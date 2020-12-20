package com.probendi.iwatch.server.message;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import com.mongodb.Block;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;

import com.probendi.iwatch.server.db.DaoException;
import com.probendi.iwatch.server.db.EntityNotFoundException;
import com.probendi.iwatch.server.db.Mongo;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;

/**
 * The MongoDB Data Access Object for a {@link Message}.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
@Stateless
public class MessageDaoMongoImpl implements MessageDao {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void delete(final @NotNull String id) throws DaoException {
        logger.entering(this.getClass().getName(), "delete", id);

        try {
            final Bson filter = new Document("_id", new ObjectId(id));
            if (Mongo.instance.getMessageCollection().deleteOne(filter).getDeletedCount() == 0) {
                logger.log(Level.SEVERE, "Failed to delete message {0}", id);
                final DaoException ex = new DaoException();
                logger.throwing(this.getClass().getName(), "delete", ex);
                throw ex;
            }
        } catch (final MongoException e) {
            logger.log(Level.SEVERE, "Failed to delete message " + id, e);
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
            Mongo.instance.getMessageCollection().deleteMany(new Document());
        } catch (final MongoException e) {
            logger.log(Level.SEVERE, "Failed to delete messages", e);
            final DaoException ex = new DaoException();
            logger.throwing(this.getClass().getName(), "deleteAll", ex);
            throw ex;
        }

        logger.exiting(this.getClass().getName(), "deleteAll");
    }

    @Override
    public Message find(final @NotNull String id) throws EntityNotFoundException {
        logger.entering(this.getClass().getName(), "find", id);

        final Bson filter = new Document("_id", new ObjectId(id));
        final Document document = Mongo.instance.getMessageCollection().find(filter).first();
        if (document == null) {
            final EntityNotFoundException ex = new EntityNotFoundException(id);
            logger.throwing(this.getClass().getName(), "find", ex);
            throw ex;
        }
        final Message message = new Message(document);

        logger.exiting(this.getClass().getName(), "find", message);
        return message;
    }

    @Override
    public List<Message> find(final @NotNull String municipality, final @NotNull String interest, final @NotNull String user) {
        logger.entering(this.getClass().getName(), "find", new String[]{municipality, interest, user});

        final List<Message> messages = new LinkedList<>();

        // build the filters
        final List<Bson> filters = new LinkedList<>();
        filters.add(eq("municipality", municipality));
        if (!interest.isEmpty()) {
            filters.add(eq("interest", interest));
        }
        if (!user.isEmpty()) {
            filters.add(Filters.in("recipients", user));
        }
        final long time = System.currentTimeMillis() / Message.MILLIS_IN_ONE_DAY * Message.MILLIS_IN_ONE_DAY;
        filters.add(gte("expireOn", new Date(time)));

        final FindIterable<Document> iterable = Mongo.instance.getMessageCollection().find(and(filters));
        iterable.forEach((Block<Document>) document -> messages.add(new Message(document)));

        logger.exiting(this.getClass().getName(), "find", messages);
        return messages;
    }

    @Override
    public List<Message> findAll() {
        logger.entering(this.getClass().getName(), "find");

        final List<Message> messages = new LinkedList<>();

        final FindIterable<Document> iterable = Mongo.instance.getMessageCollection().find();
        iterable.forEach((Block<Document>) document -> messages.add(new Message(document)));

        logger.exiting(this.getClass().getName(), "find", messages);
        return messages;
    }

    @Override
    public String insert(final @NotNull Message message) throws DaoException {
        logger.entering(this.getClass().getName(), "insert", message);

        try {
            final Document document = message.toDocument();
            Mongo.instance.getMessageCollection().insertOne(document);
            message.setId(document.getObjectId("_id").toString());
        } catch (final MongoException e) {
            logger.log(Level.SEVERE, "Failed to insert message " + message, e);
            final DaoException ex = new DaoException();
            logger.throwing(this.getClass().getName(), "insert", ex);
            throw ex;
        }

        logger.exiting(this.getClass().getName(), "insert", message.getId());
        return message.getId();
    }
}
