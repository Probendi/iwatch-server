package com.probendi.iwatch.server.user;

import java.util.LinkedList;
import java.util.List;
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
 * The MongoDB Data Access Object for a {@link Administrator}.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
@Stateless
public class AdministratorDaoMongoImpl implements AdministratorDao {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void delete(final @NotNull String id) throws DaoException {
        logger.entering(this.getClass().getName(), "delete", id);

        try {
            final Bson filter = new Document("_id", id);
            if (Mongo.instance.getAdministratorCollection().deleteOne(filter).getDeletedCount() == 0) {
                logger.log(Level.SEVERE, "Failed to delete administrator {0}", id);
                final DaoException ex = new DaoException();
                logger.throwing(this.getClass().getName(), "delete", ex);
                throw ex;
            }
        } catch (final MongoException e) {
            logger.log(Level.SEVERE, "Failed to delete administrator " + id, e);
            final DaoException ex = new DaoException();
            logger.throwing(this.getClass().getName(), "delete", ex);
            throw ex;
        }

        logger.exiting(this.getClass().getName(), "delete");
    }

    @Override
    public void deleteAll() throws DaoException {
        logger.entering(this.getClass().getName(), "deleteAll");

        try {
            Mongo.instance.getAdministratorCollection().deleteMany(new Document());
        } catch (final MongoException e) {
            logger.log(Level.SEVERE, "Failed to delete administrators", e);
            final DaoException ex = new DaoException();
            logger.throwing(this.getClass().getName(), "deleteAll", ex);
            throw ex;
        }

        logger.exiting(this.getClass().getName(), "deleteAll");
    }

    @Override
    public Administrator find(final @NotNull String id) throws EntityNotFoundException {
        logger.entering(this.getClass().getName(), "find", id);

        final Bson filter = new Document("_id", id);
        final Document document = Mongo.instance.getAdministratorCollection().find(filter).first();
        if (document == null) {
            final EntityNotFoundException ex = new EntityNotFoundException();
            logger.throwing(this.getClass().getName(), "find", ex);
            throw ex;
        }
        final Administrator administrator = new Administrator(document);

        logger.exiting(this.getClass().getName(), "find", administrator);
        return administrator;
    }

    @Override
    public Administrator find(final @NotNull String id, final @NotNull String password) throws EntityNotFoundException {
        logger.entering(this.getClass().getName(), "find", new String[]{id, password});

        final Bson filter = new Document("_id", id).append("password", password);
        final Document document = Mongo.instance.getAdministratorCollection().find(filter).first();
        if (document == null) {
            final EntityNotFoundException ex = new EntityNotFoundException("administrator " + id + " not found");
            logger.throwing(this.getClass().getName(), "find", ex);
            throw ex;
        }
        final Administrator administrator = new Administrator(document);

        // ensure municipality is active
        final String mid = administrator.getMunicipality();
        final Bson mfilter = new Document("_id", mid).append("active", true);
        final Document mdocument = Mongo.instance.getMunicipalityCollection().find(mfilter).first();
        if (mdocument == null) {
            final EntityNotFoundException ex = new EntityNotFoundException("municipality " + id + " not found");
            logger.throwing(this.getClass().getName(), "find", ex);
            throw ex;
        }

        logger.exiting(this.getClass().getName(), "find", administrator);
        return administrator;
    }

    @Override
    public List<Administrator> findAll() {
        logger.entering(this.getClass().getName(), "find");

        final List<Administrator> administrators = new LinkedList<>();

        final FindIterable<Document> iterable = Mongo.instance.getAdministratorCollection().find();
        iterable.forEach((Block<Document>) document -> administrators.add(new Administrator(document)));

        logger.exiting(this.getClass().getName(), "find", administrators);
        return administrators;
    }

    @Override
    public List<Administrator> findAll(final @NotNull String municipality) {
        logger.entering(this.getClass().getName(), "find");

        final List<Administrator> administrators = new LinkedList<>();

        final Bson filter = new Document("municipality", municipality);
        final FindIterable<Document> iterable = Mongo.instance.getAdministratorCollection().find(filter);
        iterable.forEach((Block<Document>) document -> administrators.add(new Administrator(document)));

        logger.exiting(this.getClass().getName(), "find", administrators);
        return administrators;
    }

    @Override
    public void insert(final @NotNull Administrator administrator) throws DaoException {
        logger.entering(this.getClass().getName(), "insert", administrator);

        try {
            final Document document = administrator.toDocument();
            Mongo.instance.getAdministratorCollection().insertOne(document);
        } catch (final MongoException e) {
            logger.log(Level.SEVERE, "Failed to insert administrator " + administrator, e);
            final DaoException ex = new DaoException();
            logger.throwing(this.getClass().getName(), "insert", ex);
            throw ex;
        }

        logger.exiting(this.getClass().getName(), "insert");
    }

    @Override
    public void setRegistrationId(final @NotNull String id, final @NotNull String registrationId) throws DaoException {
        logger.entering(this.getClass().getName(), "setRegistrationId", new String[]{id, registrationId});

        try {
            final Bson filter = new Document("_id", id);
            final Document document = new Document("registrationId", registrationId);
            final UpdateResult result = Mongo.instance.getAdministratorCollection().updateOne(filter, new Document("$set", document));
            if (result.getMatchedCount() == 0) {
                final EntityNotFoundException ex = new EntityNotFoundException();
                logger.throwing(this.getClass().getName(), "update", ex);
                throw ex;
            }
        } catch (final MongoException e) {
            logger.log(Level.SEVERE, "Failed to set registration id of administrator " + id, e);
            final DaoException ex = new DaoException();
            logger.throwing(this.getClass().getName(), "update", ex);
            throw ex;
        }

        logger.exiting(this.getClass().getName(), "setRegistrationId");
    }

    @Override
    public void update(final @NotNull Administrator administrator) throws DaoException {
        logger.entering(this.getClass().getName(), "update", administrator);

        try {
            final Bson filter = new Document("_id", administrator.getId());
            final UpdateResult result = Mongo.instance.getAdministratorCollection().replaceOne(filter, administrator.toDocument());
            if (result.getMatchedCount() == 0) {
                final EntityNotFoundException ex = new EntityNotFoundException();
                logger.throwing(this.getClass().getName(), "update", ex);
                throw ex;
            } else if (result.getModifiedCount() == 0) {
                logger.log(Level.SEVERE, "Failed to update administrator {0}", administrator.getId());
                final DaoException ex = new DaoException();
                logger.throwing(this.getClass().getName(), "update", ex);
                throw ex;
            }
        } catch (final MongoException e) {
            logger.log(Level.SEVERE, "Failed to update administrator " + administrator.getId(), e);
            final DaoException ex = new DaoException();
            logger.throwing(this.getClass().getName(), "update", ex);
            throw ex;
        }

        logger.exiting(this.getClass().getName(), "update");
    }
}
