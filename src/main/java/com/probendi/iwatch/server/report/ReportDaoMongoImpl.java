package com.probendi.iwatch.server.report;

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
import com.probendi.iwatch.server.user.Watcher;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.elemMatch;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;

/**
 * The MongoDB Data Access Object for a {@link Report}.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
@Stateless
public class ReportDaoMongoImpl implements ReportDao {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void addActivity(final @NotNull String id, final @NotNull Activity activity) throws DaoException {
        logger.entering(this.getClass().getName(), "addActivity", new Object[]{id, activity});

        try {
            final Bson filter = new Document("_id", id);
            final Document document = new Document("activities", activity.toDocument());
            final UpdateResult result = Mongo.instance.getReportCollection().updateOne(filter, new Document("$push", document));
            if (result.getMatchedCount() == 0) {
                final EntityNotFoundException ex = new EntityNotFoundException();
                logger.throwing(this.getClass().getName(), "addActivity", ex);
                throw ex;
            } else if (result.getModifiedCount() == 0) {
                logger.log(Level.SEVERE, "Failed to add activity to report {0}", id);
                final DaoException ex = new DaoException();
                logger.throwing(this.getClass().getName(), "addActivity", ex);
                throw ex;
            }
        } catch (final MongoException e) {
            logger.log(Level.SEVERE, "Failed to add activity to report " + id, e);
            final DaoException ex = new DaoException();
            logger.throwing(this.getClass().getName(), "addActivity", ex);
            throw ex;
        }

        logger.exiting(this.getClass().getName(), "addActivity");
    }

    @Override
    public void addWatcher(final @NotNull String id, final @NotNull Watcher watcher) throws DaoException {
        logger.entering(this.getClass().getName(), "addWatcher", new Object[]{id, watcher});

        try {
            final Bson filter = new Document("_id", id);
            final Document document = new Document("watchers", watcher);
            final UpdateResult result = Mongo.instance.getReportCollection().updateOne(filter, new Document("$push", document));
            if (result.getMatchedCount() == 0) {
                final EntityNotFoundException ex = new EntityNotFoundException();
                logger.throwing(this.getClass().getName(), "addWatcher", ex);
                throw ex;
            } else if (result.getModifiedCount() == 0) {
                logger.log(Level.SEVERE, "Failed to add watcher to report {0}", id);
                final DaoException ex = new DaoException();
                logger.throwing(this.getClass().getName(), "addWatcher", ex);
                throw ex;
            }
        } catch (final MongoException e) {
            logger.log(Level.SEVERE, "Failed to add watcher to report " + id, e);
            final DaoException ex = new DaoException();
            logger.throwing(this.getClass().getName(), "addWatcher", ex);
            throw ex;
        }

        logger.exiting(this.getClass().getName(), "addWatcher");
    }

    @Override
    public int countReportsToBeProcessed(final @NotNull String municipality) {
        logger.entering(this.getClass().getName(), "countReportsToBeProcessed", municipality);

        final Bson filter = new Document("municipality", municipality).append("actionRequired", true);
        final int n = (int) Mongo.instance.getReportCollection().count(filter);

        logger.exiting(this.getClass().getName(), "countReportsToBeProcessed", n);
        return n;
    }

    @Override
    public void delete(final @NotNull String id) throws DaoException {
        logger.entering(this.getClass().getName(), "delete", id);

        try {
            final Bson filter = new Document("_id", id);
            if (Mongo.instance.getReportCollection().deleteOne(filter).getDeletedCount() == 0) {
                logger.log(Level.SEVERE, "Failed to delete report {0}", id);
                final DaoException ex = new DaoException();
                logger.throwing(this.getClass().getName(), "delete", ex);
                throw ex;
            }
        } catch (final MongoException e) {
            logger.log(Level.SEVERE, "Failed to delete report " + id, e);
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
            Mongo.instance.getReportCollection().deleteMany(new Document());
        } catch (final MongoException e) {
            logger.log(Level.SEVERE, "Failed to delete reports", e);
            final DaoException ex = new DaoException();
            logger.throwing(this.getClass().getName(), "deleteAll", ex);
            throw ex;
        }

        logger.exiting(this.getClass().getName(), "deleteAll");
    }

    @Override
    public void deleteWatcher(final @NotNull String id, final @NotNull String watcher) throws DaoException {
        logger.entering(this.getClass().getName(), "deleteWatcher", new String[]{id, watcher});

        try {
            // db.report.find({_id: 'id0_2018_0002', watchers: {$elemMatch: {_id: 'watcher_13', creator: false}}})
            final List<Bson> filters = new LinkedList<>();
            filters.add(eq("_id", id));
            filters.add(elemMatch("watchers", new Document("_id", watcher).append("creator", false)));

            Document document = new Document("watchers", new Document("_id", watcher));
            final UpdateResult result = Mongo.instance.getReportCollection().updateOne(and(filters), new Document("$pull", document));
            if (result.getMatchedCount() == 0) {
                final EntityNotFoundException ex = new EntityNotFoundException();
                logger.throwing(this.getClass().getName(), "deleteWatcher", ex);
                throw ex;
            } else if (result.getModifiedCount() == 0) {
                logger.log(Level.SEVERE, "Failed to delete watcher from report {0}", id);
                final DaoException ex = new DaoException();
                logger.throwing(this.getClass().getName(), "deleteWatcher", ex);
                throw ex;
            }
        } catch (final MongoException e) {
            logger.log(Level.SEVERE, "Failed to delete watcher from report " + id, e);
            final DaoException ex = new DaoException();
            logger.throwing(this.getClass().getName(), "deleteWatcher", ex);
            throw ex;
        }

        logger.exiting(this.getClass().getName(), "deleteWatcher");
    }

    @Override
    public Report find(final @NotNull String id) throws EntityNotFoundException {
        logger.entering(this.getClass().getName(), "find", id);

        final Bson filter = new Document("_id", id);
        final Document document = Mongo.instance.getReportCollection().find(filter).first();
        if (document == null) {
            final EntityNotFoundException ex = new EntityNotFoundException();
            logger.throwing(this.getClass().getName(), "find", ex);
            throw ex;
        }
        final Report report = new Report(document);

        logger.exiting(this.getClass().getName(), "find", report);
        return report;
    }

    @Override
    public List<Report> findAll(final @NotNull String municipality, final @NotNull String watcher, final @NotNull String status) {
        logger.entering(this.getClass().getName(), "findAll", new Object[]{municipality, watcher, status});

        final List<Report> reports = new LinkedList<>();

        // build the filters
        final List<Bson> filters = new LinkedList<>();
        filters.add(eq("municipality", municipality));
        if (!status.isEmpty()) {
            filters.add(eq("status", status));
        }
        if (!watcher.isEmpty()) {
            filters.add(in("watchers._id", watcher));
        }
        final FindIterable<Document> iterable = Mongo.instance.getReportCollection().find(and(filters));
        iterable.forEach((Block<Document>) document -> reports.add(new Report(document)));

        logger.exiting(this.getClass().getName(), "findAll", reports);
        return reports;
    }

    @Override
    public List<Report> findAll() {
        logger.entering(this.getClass().getName(), "findAll");

        final List<Report> reports = new LinkedList<>();

        final FindIterable<Document> iterable = Mongo.instance.getReportCollection().find();
        iterable.forEach((Block<Document>) document -> reports.add(new Report(document)));

        logger.exiting(this.getClass().getName(), "findAll", reports);
        return reports;
    }

    @Override
    public void insert(final @NotNull Report report) throws DaoException {
        logger.entering(this.getClass().getName(), "insert", report);

        try {
            final Document document = report.toDocument();
            Mongo.instance.getReportCollection().insertOne(document);
        } catch (final MongoException e) {
            logger.log(Level.SEVERE, "Failed to insert report " + report, e);
            final DaoException ex = new DaoException();
            logger.throwing(this.getClass().getName(), "insert", ex);
            throw ex;
        }

        logger.exiting(this.getClass().getName(), "insert");
    }

    @Override
    public void reopen(final @NotNull String id) throws DaoException {
        logger.entering(this.getClass().getName(), "reopen", id);

        try {
            final Bson filter = new Document("_id", id).append("status", "CHIUSA");
            final Document document = new Document("status", "RIAPERTA");
            Mongo.instance.getReportCollection().updateOne(filter, new Document("$set", document));
        } catch (final MongoException e) {
            logger.log(Level.SEVERE, "Failed to update report " + id, e);
            final DaoException ex = new DaoException();
            logger.throwing(this.getClass().getName(), "setActionRequired", ex);
            throw ex;
        }


        logger.exiting(this.getClass().getName(), "reopen");
    }

    @Override
    public void setActionRequired(final @NotNull String id, final boolean value) throws DaoException {
        logger.entering(this.getClass().getName(), "setActionRequired", new Object[]{id, value});

        try {
            final Bson filter = new Document("_id", id);
            final Document document = new Document("actionRequired", value);
            final UpdateResult result = Mongo.instance.getReportCollection().updateOne(filter, new Document("$set", document));
            if (result.getMatchedCount() == 0) {
                final EntityNotFoundException ex = new EntityNotFoundException();
                logger.throwing(this.getClass().getName(), "setActionRequired", ex);
                throw ex;
            }
        } catch (final MongoException e) {
            logger.log(Level.SEVERE, "Failed to update report " + id, e);
            final DaoException ex = new DaoException();
            logger.throwing(this.getClass().getName(), "setActionRequired", ex);
            throw ex;
        }

        logger.exiting(this.getClass().getName(), "setActionRequired");
    }

    @Override
    public void update(final @NotNull Report report) throws DaoException {
        logger.entering(this.getClass().getName(), "update", report);

        try {
            final Report currentReport = find(report.getId());
            ReportDao.validateStateTransition(currentReport.getStatus(), report.getStatus());

            final Bson filter = new Document("_id", report.getId());
            final Document document = new Document("status", report.getStatus()).append("actionRequired", false);
            if (currentReport.getStatus().equals("CREATA")) {
                document.append("category", report.getCategory());
            }
            final UpdateResult result = Mongo.instance.getReportCollection().updateOne(filter, new Document("$set", document));
            if (result.getMatchedCount() == 0) {
                final EntityNotFoundException ex = new EntityNotFoundException();
                logger.throwing(this.getClass().getName(), "update", ex);
                throw ex;
            }
        } catch (final MongoException e) {
            logger.log(Level.SEVERE, "Failed to update report " + report.getId(), e);
            final DaoException ex = new DaoException();
            logger.throwing(this.getClass().getName(), "update", ex);
            throw ex;
        }

        logger.exiting(this.getClass().getName(), "update");
    }
}
