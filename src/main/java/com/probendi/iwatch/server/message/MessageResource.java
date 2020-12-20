package com.probendi.iwatch.server.message;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.probendi.iwatch.server.db.DaoException;
import com.probendi.iwatch.server.db.EntityNotFoundException;
import com.probendi.iwatch.server.jms.producer.MessageProducer;
import com.probendi.iwatch.server.municipality.MunicipalityDao;
import com.probendi.iwatch.server.rest.AuthorizationRequired;
import com.probendi.iwatch.server.user.UserDao;
import com.probendi.iwatch.server.util.PropertiesReader;
import com.probendi.iwatch.server.util.UploadService;

/**
 * Exposes the business methods of {@link Message} through RESTful web services.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
@Path("/messages")
public class MessageResource {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @EJB(beanName = "MessageProducerApnImpl")
    private MessageProducer apnMessageProducer;

    @EJB(beanName = "MessageProducerFcmImpl")
    private MessageProducer fcmMessageProducer;

    @EJB(beanName = "MunicipalityDaoMongoImpl")
    private MunicipalityDao municipalityDao;

    @EJB(beanName = "MessageDaoMongoImpl")
    MessageDao messageDao;

    @EJB(beanName = "UserDaoMongoImpl")
    UserDao userDao;

    /**
     * Handles the HTTP POST requests that create the given message.
     *
     * @param message the message to be created
     * @return a {@link Response} object
     */
    @POST
    @AuthorizationRequired
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(final Message message) {
        logger.entering(this.getClass().getName(), "create", message);

        try {
            message.setCreatedOn(new Date());
            message.setMimeType(UploadService.mimeType(message.getAttachment()));
            final String id = messageDao.insert(message);
            logger.log(Level.INFO, "Message {0} created", id);
            final PropertiesReader reader = new PropertiesReader();
            if (reader.isSendPushNotification()) {
                final String name = municipalityDao.find(message.getMunicipality()).getName();
                final long  expireOn = System.currentTimeMillis() + reader.getNotificationValidity();
                apnMessageProducer.send(name, message, 0, expireOn, 1);
                fcmMessageProducer.send(name, message, 0, expireOn, 1);
            }

            // build the response
            final CacheControl cacheControl = new CacheControl();
            cacheControl.setNoCache(true);
            final String location = "/messages/" + id;
            final Response response = Response.status(Response.Status.CREATED)
                    .header(HttpHeaders.LOCATION, location)
                    .cacheControl(cacheControl)
                    .build();

            logger.exiting(this.getClass().getName(), "create", response);
            return response;
        } catch (final DaoException e) {
            logger.log(Level.SEVERE, "Failed to create message", e);
            final WebApplicationException ex = new InternalServerErrorException();
            logger.throwing(this.getClass().getName(), "create", ex);
            throw ex;
        }
    }

    /**
     * Handles the HTTP GET requests that return the message with the given id.
     *
     * @param id the id path parameter
     * @return the message with the given id
     */
    @GET
    @AuthorizationRequired
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Message get(final @PathParam("id") String id) {
        logger.entering(this.getClass().getName(), "get", id);

        try {
            final Message message = messageDao.find(id);

            // build the response
            logger.exiting(this.getClass().getName(), "get", message);
            return message;
        } catch (final EntityNotFoundException e) {
            final WebApplicationException ex = new NotFoundException();
            logger.throwing(this.getClass().getName(), "get", ex);
            throw ex;
        }
    }

    /**
     * Handles the HTTP GET requests that return the messages which match the given criteria.
     *
     * @param municipality the id of the municipality of the messages to be selected
     * @param interest     the message's interest
     * @param user         the user's id
     * @return the messages which match the given criteria
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public GenericEntity<List<Message>> get(final @DefaultValue("") @QueryParam("municipality") String municipality,
                                            final @DefaultValue("") @QueryParam("interest") String interest,
                                            final @DefaultValue("") @QueryParam("user") String user) {

        logger.entering(this.getClass().getName(), "get", new Object[]{municipality, interest, user});

        // this is the query from the desktop application
        if (user.isEmpty()) {
            final List<Message> messages = messageDao.find(municipality, interest, "");

            // build the response
            logger.exiting(this.getClass().getName(), "get", messages);
            return new GenericEntity<List<Message>>(messages) {
            };
        }

        // if interest is empty then return the user's unread messages, otherwise the messages for the given interest
        final List<Message> messages = new LinkedList<>();
        if (interest.isEmpty()) {
            try {
                final long now = System.currentTimeMillis() / Message.MILLIS_IN_ONE_DAY * Message.MILLIS_IN_ONE_DAY;
                for (final String unreadMessage : userDao.find(user).getMessages()) {
                    final Message message = messageDao.find(unreadMessage);
                    if (message.getExpireOn().getTime() >= now) {
                        messages.add(message);
                    } else { // remove unread expired messages
                        try {
                            userDao.deleteMessage(user, unreadMessage);
                        } catch (final DaoException e) {
                            logger.log(Level.WARNING, "Failed to delete expired message '" + unreadMessage + "' of user " + user, e);
                        }
                    }
                }
            } catch (EntityNotFoundException e) {
                logger.log(Level.WARNING, "User not found '" + user + "'", e);
            }
        } else {
            messages.addAll(messageDao.find(municipality, interest, user));
        }
        Collections.sort(messages);


        // build the response
        logger.exiting(this.getClass().getName(), "get", messages);
        return new GenericEntity<List<Message>>(messages) {
        };
    }
}
