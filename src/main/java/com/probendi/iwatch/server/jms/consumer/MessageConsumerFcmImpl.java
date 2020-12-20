package com.probendi.iwatch.server.jms.consumer;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jetbrains.annotations.NotNull;

import com.probendi.iwatch.server.db.EntityNotFoundException;
import com.probendi.iwatch.server.jms.producer.MessageProducer;
import com.probendi.iwatch.server.message.Message;
import com.probendi.iwatch.server.report.Report;
import com.probendi.iwatch.server.user.User;
import com.probendi.iwatch.server.util.PropertiesReader;

/**
 * Consumes the message from {@code jms/iWatchFcmQueue}.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
@MessageDriven(mappedName = "jms/iWatchFcmQueue")
public class MessageConsumerFcmImpl extends MessageConsumer {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @EJB(beanName = "MessageProducerFcmImpl")
    private MessageProducer producer;

    @Override
    public void sendNotification(final @NotNull String name, final @NotNull Message message) throws PushNotificationException {
        logger.entering(this.getClass().getName(), "sendNotification", new Object[]{name, message});

        final PropertiesReader propertiesReader = new PropertiesReader();
        final int maxRecipients = propertiesReader.getFcmMaxRecipients();
        final String key = "key=" + propertiesReader.getFcmKey();
        final String url = propertiesReader.getFcmUrl();

        // set the recipients
        final List<String> registrationIds = userDao.findRecipients(message, Platform.ANDROID);
        final int size = registrationIds.size();
        if (size == 0) {
            logger.log(Level.INFO, "A notification for message {0} was not sent [no registered devices]", message.getId());
            logger.exiting(this.getClass().getName(), "sendNotification", true);
            return;
        }

        // create the request bodies
        final List<FcmRequest> fcmRequests = new LinkedList<>();
        final FcmRequest.Data data = new FcmRequest.Data().image(message.getThumbnail()).title(name).body(message.getHeader());

        // add the recipients in blocks of maxRecipients
        final int div = size / maxRecipients;
        final int mod = size % maxRecipients;
        for (int i = 0; i < div; i++) {
            final int from = i * maxRecipients;
            final int to = from + maxRecipients;
            fcmRequests.add(new FcmRequest().data(data).registration_ids(registrationIds.subList(from, to)));
        }

        // add the last notification with the remainder recipients
        if (mod > 0) {
            final int from = div * maxRecipients;
            fcmRequests.add(new FcmRequest().data(data).registration_ids(registrationIds.subList(from, size)));
        }

        // send the HTTP POST requests
        try {
            for (final FcmRequest fcmRequest : fcmRequests) {
                sendRequest(fcmRequest, key, url);
            }
        } catch (final ProcessingException e) {
            logger.log(Level.SEVERE, "Failed to sent notification for message " + message.getId(), e);
            final PushNotificationException ex = new PushNotificationException();
            logger.throwing(this.getClass().getName(), "sendNotification", ex);
            throw ex;
        }

        logger.exiting(this.getClass().getName(), "sendNotification");
    }

    @Override
    public void sendNotification(final @NotNull String name, final @NotNull Report report,
                                 final @NotNull String watcher) throws PushNotificationException {
        logger.entering(this.getClass().getName(), "sendNotification", new Object[]{name, report, watcher});

        final PropertiesReader propertiesReader = new PropertiesReader();
        final int maxRecipients = propertiesReader.getFcmMaxRecipients();
        final String key = "key=" + propertiesReader.getFcmKey();
        final String url = propertiesReader.getFcmUrl();

        final List<String> registrationIds = userDao.findRecipients(report, watcher, Platform.ANDROID);
        final int size = registrationIds.size();
        if (size == 0) {
            logger.log(Level.INFO, "A notification for report {0} was not sent [no registered devices]", report.getId());
            logger.exiting(this.getClass().getName(), "sendNotification", true);
            return;
        }

        // create the request bodies
        final List<FcmRequest> fcmRequests = new LinkedList<>();
        final FcmRequest.Data data = new FcmRequest.Data().title(name)
                .body("Segnalazione " + report.getId().substring(7) + ": c'è una nuova attività");

        // add the recipients in blocks of maxRecipients
        final int div = size / maxRecipients;
        final int mod = size % maxRecipients;
        for (int i = 0; i < div; i++) {
            final int from = i * maxRecipients;
            final int to = from + maxRecipients;
            fcmRequests.add(new FcmRequest().data(data).registration_ids(registrationIds.subList(from, to)));
        }

        // add the last notification with the remainder recipients
        if (mod > 0) {
            final int from = div * maxRecipients;
            fcmRequests.add(new FcmRequest().data(data).registration_ids(registrationIds.subList(from, size)));
        }

        // send the HTTP POST requests
        try {
            for (final FcmRequest fcmRequest : fcmRequests) {
                sendRequest(fcmRequest, key, url);
            }
        } catch (final ProcessingException e) {
            logger.log(Level.SEVERE, "Failed to sent notification for message " + report.getId(), e);
            final PushNotificationException ex = new PushNotificationException();
            logger.throwing(this.getClass().getName(), "sendNotification", ex);
            throw ex;
        }

        logger.exiting(this.getClass().getName(), "sendNotification");
    }

    @Override
    public void sendNotification(final @NotNull String name, final @NotNull String watcher,
                                 final @NotNull String text) throws PushNotificationException {
        logger.entering(this.getClass().getName(), "sendNotification", new Object[]{name, watcher, text});

        final PropertiesReader propertiesReader = new PropertiesReader();
        final String key = "key=" + propertiesReader.getFcmKey();
        final String url = propertiesReader.getFcmUrl();

        // set the payload
        try {
            final User user = userDao.find(watcher);
            if (!Platform.ANDROID.getPlatform().equals(user.getPlatform()) || user.getRegistrationId().isEmpty()) {
                logger.log(Level.INFO, "Notification was not pushed because there are no registered devices", user.getId());
                logger.exiting(this.getClass().getName(), "sendNotification", true);
                return;
            }
            logger.log(Level.INFO, "Added user " + user.getId() + " to recipients list (" + user.getRegistrationId() + ")");

            // create the request bodies
            final FcmRequest.Data data = new FcmRequest.Data().title(name).body(text);
            final FcmRequest fcmRequest = new FcmRequest().data(data).registration_ids(user.getRegistrationId());

            // send the HTTP POST requests
            try {
                sendRequest(fcmRequest, key, url);
            } catch (final ProcessingException e) {
                logger.log(Level.SEVERE, "Failed to push notification to watcher " + user.getId(), e);
                final PushNotificationException ex = new PushNotificationException();
                logger.throwing(this.getClass().getName(), "sendNotification", ex);
                throw ex;
            }
        } catch (final EntityNotFoundException e) {
            logger.log(Level.INFO, "Notification was not pushed because the watcher cannot be found", e.getMessage());
            logger.exiting(this.getClass().getName(), "sendNotification", true);
            return;
        }

        logger.exiting(this.getClass().getName(), "sendNotification");
    }

    /**
     * Sends the HTTP request.
     *
     * @param fcmRequest a {@link FcmRequest} object
     * @param key the key
     * @param url the url
     * @throws PushNotificationException if the notification cannot be sent
     */
    private void sendRequest(final FcmRequest fcmRequest, final String key, String url) throws PushNotificationException {
        final Response response = ClientBuilder.newClient().target(url).request()
                .header("Authorization", key).post(Entity.entity(fcmRequest, MediaType.APPLICATION_JSON_TYPE));
        final String string = response.readEntity(String.class);
        final int status = response.getStatus();
        if (status == 200) {
            logger.log(Level.FINER, "Request processed - {0}", string);
        } else if (status == 400) {
            logger.log(Level.SEVERE, "Invalid request: {0}", string);
            final PushNotificationException ex = new PushNotificationException(-1);
            logger.throwing(this.getClass().getName(), "sendNotification", ex);
            throw ex;
        } else if (status == 401) {
            logger.log(Level.SEVERE, "Authentication error");
            final PushNotificationException ex = new PushNotificationException(-1);
            logger.throwing(this.getClass().getName(), "sendNotification", ex);
            throw ex;
        } else {
            logger.log(Level.SEVERE, "Request failed with status {0} [{1}]", new Object[]{status,});
            long retry = 0;
            try {
                retry = Long.parseLong(response.getHeaderString("Retry-After"));
            } catch (final NumberFormatException ignore) {
            }
            final PushNotificationException ex = new PushNotificationException(retry);
            logger.throwing(this.getClass().getName(), "sendNotification", ex);
            throw ex;
        }
    }

    @Override
    public MessageProducer getMessageProducer() {
        return producer;
    }
}
