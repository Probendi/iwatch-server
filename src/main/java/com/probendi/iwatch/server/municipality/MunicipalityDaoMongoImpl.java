package com.probendi.iwatch.server.municipality;

import java.time.Year;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;

import com.mongodb.Block;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.result.UpdateResult;

import com.probendi.iwatch.server.db.DaoException;
import com.probendi.iwatch.server.db.EntityNotFoundException;
import com.probendi.iwatch.server.db.Mongo;

/**
 * The MongoDB Data Access Object for a {@link Municipality}.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
@Stateless
public class MunicipalityDaoMongoImpl implements MunicipalityDao {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void deleteAll() throws DaoException {
        logger.entering(this.getClass().getName(), "deleteAll");

        try {
            Mongo.instance.getMunicipalityCollection().deleteMany(new Document());
        } catch (final MongoException e) {
            logger.log(Level.SEVERE, "Failed to delete municipalities", e);
            final DaoException ex = new DaoException();
            logger.throwing(this.getClass().getName(), "deleteAll", ex);
            throw ex;
        }

        logger.exiting(this.getClass().getName(), "deleteAll");
    }

    @Override
    public Municipality find(final @NotNull String id) throws EntityNotFoundException {
        logger.entering(this.getClass().getName(), "find", id);

        final Bson filter = new Document("_id", id);
        final Document document = Mongo.instance.getMunicipalityCollection().find(filter).first();
        if (document == null) {
            final EntityNotFoundException ex = new EntityNotFoundException();
            logger.throwing(this.getClass().getName(), "find", ex);
            throw ex;
        }
        final Municipality municipality = new Municipality(document);

        logger.exiting(this.getClass().getName(), "find", municipality);
        return municipality;
    }

    @Override
    public SortedSet<Region> findAll() {
        logger.entering(this.getClass().getName(), "find");

        final SortedSet<Region> regions = new TreeSet<>();

        final Bson filter = new Document("active", true).append("features", Municipality.Feature.REGISTRATION.toString());
        final FindIterable<Document> iterable = Mongo.instance.getMunicipalityCollection().find(filter);
        iterable.forEach((Block<Document>) document -> {
            final Municipality municipality = new Municipality(document);
            boolean regionFound = false;
            for (final Region region : regions) {
                if (region.getName().equals(municipality.getRegion())) {
                    regionFound = true;
                    final SortedSet<Province> provinces = region.getProvinces();
                    boolean provinceFound = false;
                    for (final Province province : provinces) {
                        if (province.getName().equals(municipality.getProvince())) {
                            provinceFound = true;
                            province.addCity(new City(municipality));
                            break;
                        }
                    }
                    if (!provinceFound) {
                        final Province province = new Province(municipality.getProvince());
                        province.addCity(new City(municipality));
                        region.addProvince(province);
                    }
                }
            }
            if (!regionFound) {
                final Province province = new Province(municipality.getProvince());
                province.addCity(new City(municipality));
                final Region region = new Region(municipality.getRegion());
                region.addProvince(province);
                regions.add(region);
            }
        });

        logger.exiting(this.getClass().getName(), "find", regions);
        return regions;
    }

    synchronized public String nextTicketNumber(final @NotNull String id) throws DaoException {
        logger.entering(this.getClass().getName(), "nextTicketNumber", id);

        final Municipality municipality = find(id);
        String ticketNumber = municipality.getTicketNumber();

        final int year = Year.now().getValue();
        int n = 1;
        if (!ticketNumber.isEmpty()) {
            final String[] array = ticketNumber.split("_");
            if (year == Integer.parseInt(array[1])) {
                n += Integer.parseInt(array[2]);
            }
        }
        ticketNumber = String.format(Municipality.TICKET_NUMBER, id, year, n);
        municipality.setTicketNumber(ticketNumber);
        update(municipality);

        logger.exiting(this.getClass().getName(), "nextTicketNumber", ticketNumber);
        return ticketNumber;
    }

    @Override
    public void insert(final @NotNull Municipality municipality) throws DaoException {
        logger.entering(this.getClass().getName(), "insert", municipality);

        try {
            final Document document = municipality.toDocument();
            Mongo.instance.getMunicipalityCollection().insertOne(document);
        } catch (final MongoException e) {
            logger.log(Level.SEVERE, "Failed to insert municipality " + municipality, e);
            final DaoException ex = new DaoException();
            logger.throwing(this.getClass().getName(), "insert", ex);
            throw ex;
        }

        logger.exiting(this.getClass().getName(), "insert");
    }

    @Override
    public void update(final @NotNull Municipality municipality) throws DaoException {
        logger.entering(this.getClass().getName(), "update", municipality);

        try {
            final Bson filter = new Document("_id", municipality.getId());
            final UpdateResult result = Mongo.instance.getMunicipalityCollection().replaceOne(filter, municipality.toDocument());
            if (result.getMatchedCount() == 0) {
                final EntityNotFoundException ex = new EntityNotFoundException();
                logger.throwing(this.getClass().getName(), "update", ex);
                throw ex;
            } else if (result.getModifiedCount() == 0) {
                logger.log(Level.SEVERE, "Failed to update municipality {0}", municipality.getId());
                final DaoException ex = new DaoException();
                logger.throwing(this.getClass().getName(), "update", ex);
                throw ex;
            }
        } catch (final MongoException e) {
            logger.log(Level.SEVERE, "Failed to update municipality " + municipality.getId(), e);
            final DaoException ex = new DaoException();
            logger.throwing(this.getClass().getName(), "update", ex);
            throw ex;
        }

        logger.exiting(this.getClass().getName(), "update");
    }
}
