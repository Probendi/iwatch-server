package com.probendi.iwatch.server.jms.consumer;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.MessageDriven;

import org.jetbrains.annotations.NotNull;

import com.notnoop.apns.APNS;
import com.notnoop.exceptions.NetworkIOException;

import com.probendi.iwatch.server.db.EntityNotFoundException;
import com.probendi.iwatch.server.jms.producer.MessageProducer;
import com.probendi.iwatch.server.message.Message;
import com.probendi.iwatch.server.report.Report;
import com.probendi.iwatch.server.user.User;
import com.probendi.iwatch.server.util.PropertiesReader;

/**
 * Consumes the message from {@code jms/iWatchApnQueue}.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
@MessageDriven(mappedName = "jms/iWatchApnQueue")
public class MessageConsumerApnImpl extends MessageConsumer {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @EJB
    private ApnServiceProvider apnServiceProvider;

    @EJB(beanName = "MessageProducerApnImpl")
    private MessageProducer producer;

    @Override
    public void sendNotification(final @NotNull String name, final @NotNull Message message) throws PushNotificationException {
        logger.entering(this.getClass().getName(), "sendNotification", new Object[]{name, message});

        // set the recipients
        final List<String> registrationIds = userDao.findRecipients(message, Platform.IOS);
        if (registrationIds.isEmpty()) {
            logger.log(Level.INFO, "A notification for message {0} was not sent [no registered devices]", message.getId());
        } else {
            // set the payload
            final String payload = APNS.newPayload().alertTitle(name).alertBody(message.getHeader()).build();
            final Date expiry = new Date(System.currentTimeMillis() + new PropertiesReader().getNotificationValidity());
            try {
                apnServiceProvider.getService().push(registrationIds, payload, expiry);
            } catch (final NetworkIOException e) {
                logger.log(Level.SEVERE, "Failed to sent notification for message " + message.getId(), e);
                final PushNotificationException ex = new PushNotificationException();
                logger.throwing(this.getClass().getName(), "sendNotification", ex);
                throw ex;
            }
        }

        logger.exiting(this.getClass().getName(), "sendNotification");
    }

    @Override
    public void sendNotification(final @NotNull String name, final @NotNull Report report,
                                 final @NotNull String watcher) throws PushNotificationException {
        logger.entering(this.getClass().getName(), "sendNotification", new Object[]{name, report, watcher});

        // set the recipients
        final List<String> registrationIds = userDao.findRecipients(report, watcher, Platform.IOS);
        if (registrationIds.isEmpty()) {
            logger.log(Level.INFO, "A notification for report {0} was not sent [no registered devices]", report.getId());
        } else {
            // set the payload
            final String payload = APNS.newPayload().alertTitle(name)
                    .alertBody("Segnalazione " + report.getId().substring(7) + ": c'è una nuova attività").build();
            final Date expiry = new Date(System.currentTimeMillis() + new PropertiesReader().getNotificationValidity());
            try {
                apnServiceProvider.getService().push(registrationIds, payload, expiry);
            } catch (final NetworkIOException e) {
                logger.log(Level.SEVERE, "Failed to sent notification for report " + report.getId(), e);
                final PushNotificationException ex = new PushNotificationException();
                logger.throwing(this.getClass().getName(), "sendNotification", ex);
                throw ex;
            }
        }

        logger.exiting(this.getClass().getName(), "sendNotification");
    }

    @Override
    public void sendNotification(final @NotNull String name, final @NotNull String watcher,
                                 final @NotNull String text) throws PushNotificationException {
        logger.entering(this.getClass().getName(), "sendNotification", new Object[]{name, watcher, text});

        // set the payload
        try {
            final User user = userDao.find(watcher);
            if (!Platform.IOS.getPlatform().equals(user.getPlatform()) || user.getRegistrationId().isEmpty()) {
                logger.log(Level.INFO, "Notification was not pushed because there are no registered devices", user.getId());
            } else {
                logger.log(Level.INFO, "Added user " + user.getId() + " to recipients list (" + user.getRegistrationId() + ")");
                final String payload = APNS.newPayload().alertTitle(name).alertBody(text).build();
                final Date expiry = new Date(System.currentTimeMillis() + new PropertiesReader().getNotificationValidity());
                try {
                    apnServiceProvider.getService().push(user.getRegistrationId(), payload, expiry);
                } catch (final NetworkIOException e) {
                    logger.log(Level.SEVERE, "Failed to push notification to watcher " + user.getId(), e);
                    final PushNotificationException ex = new PushNotificationException();
                    logger.throwing(this.getClass().getName(), "sendNotification", ex);
                    throw ex;
                }
            }
        } catch (final EntityNotFoundException e) {
            logger.log(Level.INFO, "Notification was not pushed because the watcher cannot be found", e.getMessage());
            logger.exiting(this.getClass().getName(), "sendNotification", true);
            return;
        }


        logger.exiting(this.getClass().getName(), "sendNotification");
    }

    @Override
    public MessageProducer getMessageProducer() {
        return producer;
    }
}
