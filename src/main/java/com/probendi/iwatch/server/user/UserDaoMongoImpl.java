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
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;

import com.probendi.iwatch.server.db.DaoException;
import com.probendi.iwatch.server.db.EntityNotFoundException;
import com.probendi.iwatch.server.db.Mongo;
import com.probendi.iwatch.server.jms.consumer.Platform;
import com.probendi.iwatch.server.message.Message;
import com.probendi.iwatch.server.report.Report;

import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

/**
 * The MongoDB Data Access Object for a {@link User}.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
@Stateless
public class UserDaoMongoImpl implements UserDao {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void addMessage(final @NotNull Message message) {
        logger.entering(this.getClass().getName(), "addMessage", message);

        final String municipality = message.getMunicipality();
        try {
            final Bson filter = new Document("_id", new Document("$in", message.getRecipients()));
            final Document document = new Document("messages", message.getId());
            final UpdateResult result = Mongo.instance.getUserCollection().updateMany(filter, new Document("$addToSet", document));
            if (result.getMatchedCount() == 0) {
                logger.log(Level.WARNING, "Failed to add message " + message.getId() + " to users of municipality " + municipality);
            }
        } catch (final MongoException e) {
            logger.log(Level.WARNING, "Failed to add message " + message.getId() + " to users of municipality " + municipality, e);
        }

        logger.exiting(this.getClass().getName(), "addMessage");
    }

    @Override
    public void addReport(final @NotNull Report report) {
        logger.entering(this.getClass().getName(), "addReport", report);

        final String reportId = report.getId();
        final List<String> watchers = new LinkedList<>();
        for (final Watcher watcher : report.getWatchers()) {
            if (watcher.isAdministrator()) continue;
            watchers.add(watcher.getId());
        }
        try {
            final Bson filter = new Document("_id", new Document("$in", watchers));
            final Document document = new Document("reports", reportId);
            final UpdateResult result = Mongo.instance.getUserCollection().updateMany(filter, new Document("$addToSet", document));
            if (result.getMatchedCount() == 0) {
                logger.log(Level.WARNING, "Failed to add report " + reportId + " to watchers");
            }
        } catch (final MongoException e) {
            logger.log(Level.WARNING, "Failed to add report " + reportId + " to watchers", e);
        }

        logger.exiting(this.getClass().getName(), "addReport");
    }

    @Override
    public void addReport(final @NotNull String report, final @NotNull String watcher) {
        logger.entering(this.getClass().getName(), "addReport", new Object[]{report, watcher});

        try {
            final Bson filter = new Document("_id", watcher);
            final Document document = new Document("reports", report);
            final UpdateResult result = Mongo.instance.getUserCollection().updateMany(filter, new Document("$addToSet", document));
            if (result.getMatchedCount() == 0) {
                logger.log(Level.WARNING, "Failed to add report " + report + " to watcher " + watcher);
            }
        } catch (final MongoException e) {
            logger.log(Level.WARNING, "Failed to add report " + report + " to watcher " + watcher, e);
        }

        logger.exiting(this.getClass().getName(), "addReport");
    }

    @Override
    public void delete(final @NotNull String id) throws DaoException {
        logger.entering(this.getClass().getName(), "delete", id);

        try {
            final Bson filter = new Document("_id", id);
            if (Mongo.instance.getUserCollection().deleteOne(filter).getDeletedCount() == 0) {
                logger.log(Level.SEVERE, "Failed to delete user {0}", id);
                final DaoException ex = new DaoException();
                logger.throwing(this.getClass().getName(), "delete", ex);
                throw ex;
            }
        } catch (final MongoException e) {
            logger.log(Level.SEVERE, "Failed to delete user " + id, e);
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
            Mongo.instance.getUserCollection().deleteMany(new Document());
        } catch (final MongoException e) {
            logger.log(Level.SEVERE, "Failed to delete users", e);
            final DaoException ex = new DaoException();
            logger.throwing(this.getClass().getName(), "deleteAll", ex);
            throw ex;
        }

        logger.exiting(this.getClass().getName(), "deleteAll");
    }

    @Override
    public void deleteMessage(final @NotNull String message) {
        logger.entering(this.getClass().getName(), "deleteMessage", message);

        try {
            final Bson filter = new Document();
            final Document document = new Document("messages", message);
            Mongo.instance.getUserCollection().updateMany(filter, new Document("$pull", document));
        } catch (final MongoException e) {
            logger.log(Level.WARNING, "Failed to delete message " + message + " from users", e);
        }

        logger.exiting(this.getClass().getName(), "deleteMessage");
    }

    @Override
    public void deleteMessage(final @NotNull String id, final @NotNull String message) throws DaoException {
        logger.entering(this.getClass().getName(), "deleteMessage", new String[]{id, message});

        try {
            final Bson filter = new Document("_id", id);
            final Document document = new Document("messages", message);
            final UpdateResult result = Mongo.instance.getUserCollection().updateOne(filter, new Document("$pull", document));
            if (result.getMatchedCount() == 0) {
                final EntityNotFoundException ex = new EntityNotFoundException();
                logger.throwing(this.getClass().getName(), "deleteMessage", ex);
                throw ex;
            }
        } catch (final MongoException e) {
            logger.log(Level.SEVERE, "Failed to delete message " + message + " of user " + id, e);
            final DaoException ex = new DaoException();
            logger.throwing(this.getClass().getName(), "deleteMessage", ex);
            throw ex;
        }

        logger.exiting(this.getClass().getName(), "deleteMessage");
    }

    @Override
    public void deleteReport(final @NotNull String id, final @NotNull String report) throws DaoException {
        logger.entering(this.getClass().getName(), "deleteReport", new String[]{id, report});

        try {
            final Bson filter = new Document("_id", id);
            final Document document = new Document("reports", report);
            final UpdateResult result = Mongo.instance.getUserCollection().updateOne(filter, new Document("$pull", document));
            if (result.getMatchedCount() == 0) {
                final EntityNotFoundException ex = new EntityNotFoundException();
                logger.throwing(this.getClass().getName(), "deleteReport", ex);
                throw ex;
            }
        } catch (final MongoException e) {
            logger.log(Level.SEVERE, "Failed to delete report " + report + " of user " + id, e);
            final DaoException ex = new DaoException();
            logger.throwing(this.getClass().getName(), "deleteReport", ex);
            throw ex;
        }

        logger.exiting(this.getClass().getName(), "deleteReport");
    }

    @Override
    public User find(final @NotNull String id) throws EntityNotFoundException {
        logger.entering(this.getClass().getName(), "find", id);

        final Bson filter = new Document("_id", id);
        final Document document = Mongo.instance.getUserCollection().find(filter).first();
        if (document == null) {
            final EntityNotFoundException ex = new EntityNotFoundException(id);
            logger.throwing(this.getClass().getName(), "find", ex);
            throw ex;
        }
        final User user = new User(document);

        logger.exiting(this.getClass().getName(), "find", user);
        return user;
    }

    @Override
    public List<User> findAll() {
        logger.entering(this.getClass().getName(), "findAll");

        final List<User> users = new LinkedList<>();

        final FindIterable<Document> iterable = Mongo.instance.getUserCollection().find();
        iterable.forEach((Block<Document>) document -> users.add(new User(document)));

        logger.exiting(this.getClass().getName(), "find", users);
        return users;
    }

    @Override
    public List<User> findAll(final @NotNull String municipality) {
        logger.entering(this.getClass().getName(), "findAll");

        final List<User> users = new LinkedList<>();

        // build the filters
        final Bson filter = new Document("municipality", municipality);
        final FindIterable<Document> iterable = Mongo.instance.getUserCollection().find(filter);
        iterable.forEach((Block<Document>) document -> users.add(new User(document)));

        logger.exiting(this.getClass().getName(), "find", users);
        return users;
    }

    @Override
    public List<String> findRecipients(final @NotNull Message message, final @NotNull Platform platform) {
        logger.entering(this.getClass().getName(), "findRecipients", new Object[]{message, platform});

        final List<String> registrationIds = new LinkedList<>();

        // build the filters
        final Bson filter = new Document("municipality", message.getMunicipality())
                .append("platform", platform.getPlatform()).append("_id", new Document("$in", message.getRecipients()));
        final Bson projection = fields(include("registrationId"), excludeId());
        final FindIterable<Document> iterable = Mongo.instance.getUserCollection().find(filter).projection(projection);
        iterable.forEach((Block<Document>) document -> registrationIds.add(document.getString("registrationId")));

        logger.exiting(this.getClass().getName(), "findRecipients", registrationIds);
        return registrationIds;
    }

    @Override
    public List<String> findRecipients(final @NotNull Report report, final @NotNull String watcher, final @NotNull Platform platform) {
        logger.entering(this.getClass().getName(), "findRecipients", new Object[]{report, platform});

        final List<String> registrationIds = new LinkedList<>();

        // build the filters
        final List<String> watchers = new LinkedList<>();
        for (final Watcher aWatcher : report.getWatchers()) {
            if (aWatcher.isAdministrator() || aWatcher.getId().equals(watcher)) continue;
            watchers.add(aWatcher.getId());
        }
        final Bson filter = new Document("municipality", report.getMunicipality())
                .append("platform", platform.getPlatform()).append("_id", new Document("$in", watchers));
        final Bson projection = fields(include("registrationId"), excludeId());
        final FindIterable<Document> iterable = Mongo.instance.getUserCollection().find(filter).projection(projection);
        iterable.forEach((Block<Document>) document -> registrationIds.add(document.getString("registrationId")));

        logger.exiting(this.getClass().getName(), "findRecipients", registrationIds);
        return registrationIds;
    }

    @Override
    public void setLocation(final @NotNull String id, final @NotNull Location location) throws DaoException {
        logger.entering(this.getClass().getName(), "setLocation", new Object[]{id, location});

        try {
            final Bson filter = new Document("_id", id);
            final Document document = new Document("location", location.toDocument());
            final UpdateResult result = Mongo.instance.getUserCollection().updateOne(filter, new Document("$set", document));
            if (result.getMatchedCount() == 0) {
                final EntityNotFoundException ex = new EntityNotFoundException();
                logger.throwing(this.getClass().getName(), "setLocation", ex);
                throw ex;
            }
            logger.info("User: \"" + id + "\"; location: " + location);
        } catch (final MongoException e) {
            logger.log(Level.SEVERE, "Failed to set location of user " + id, e);
            final DaoException ex = new DaoException();
            logger.throwing(this.getClass().getName(), "setLocation", ex);
            throw ex;
        }

        logger.exiting(this.getClass().getName(), "setLocation");
    }

    @Override
    public boolean upsert(final @NotNull User user) throws DaoException {
        logger.entering(this.getClass().getName(), "upsert", user);

        boolean updated;
        try {
            final Document document = user.toDocument();
            final Bson filter = new Document("_id", user.getId());
            final UpdateOptions options = (new UpdateOptions()).upsert(true);
            final UpdateResult result = Mongo.instance.getUserCollection().replaceOne(filter, document, options);
            updated = result.getUpsertedId() == null;
        } catch (final MongoException e) {
            logger.log(Level.SEVERE, "Failed to upsert user " + user, e);
            final DaoException ex = new DaoException();
            logger.throwing(this.getClass().getName(), "upsert", ex);
            throw ex;
        }

        logger.exiting(this.getClass().getName(), "upsert", updated);
        return updated;
    }
}
