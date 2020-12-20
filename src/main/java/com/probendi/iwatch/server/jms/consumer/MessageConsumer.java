package com.probendi.iwatch.server.jms.consumer;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.jms.JMSException;
import javax.jms.MessageListener;

import org.jetbrains.annotations.NotNull;

import com.probendi.iwatch.server.db.DaoException;
import com.probendi.iwatch.server.jms.producer.MessageProducer;
import com.probendi.iwatch.server.message.Message;
import com.probendi.iwatch.server.message.MessageDao;
import com.probendi.iwatch.server.report.Report;
import com.probendi.iwatch.server.report.ReportDao;
import com.probendi.iwatch.server.user.UserDao;
import com.probendi.iwatch.server.util.PropertiesReader;

/**
 * Consumes messages from {@code jms/iWatchApnQueue} and {@code jms/iWatchFcmQueue}.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
public abstract class MessageConsumer implements MessageListener {

    private static final String WATCHER = "Segnalazione %s: %s un osservatore";

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @EJB(beanName = "MessageDaoMongoImpl")
    protected MessageDao messageDao;

    @EJB(beanName = "ReportDaoMongoImpl")
    protected ReportDao reportDao;

    @EJB(beanName = "UserDaoMongoImpl")
    protected UserDao userDao;

    @Override
    public void onMessage(final javax.jms.Message message) {
        logger.entering(this.getClass().getName(), "onMessage", message);

        try {
            onMessageSent(message);
            logger.exiting(this.getClass().getName(), "onMessage");
            return;
        } catch (JMSException ignore) {
        }

        try {
            onNewActivity(message);
            logger.exiting(this.getClass().getName(), "onMessage");
            return;
        } catch (JMSException ignore) {
        }

        try {
            onWatcherAddedOrDeleted(message);
        } catch (JMSException e) { // this goes in the last one
            logger.log(Level.SEVERE, "Failed to get message from the JMS queue");
        }

        logger.exiting(this.getClass().getName(), "onMessage");
    }

    /**
     * Processes the JMS messages that are sent when a new message is sent.
     *
     * @param message the JMS message
     * @throws JMSException if the JMS message's body  cannot be assigned to a {@link Message} object.
     */
    private void onMessageSent(final javax.jms.Message message) throws JMSException {
        logger.entering(this.getClass().getName(), "onMessageSent", message);

        final Message iWatchMessage = message.getBody(Message.class);
        int count = message.getIntProperty("count");
        final long expireOn = message.getLongProperty("expireOn");
        final String name = message.getStringProperty("name");

        if (System.currentTimeMillis() <= message.getLongProperty("expireOn")) {
            if (iWatchMessage.getExpireOn().getTime() > System.currentTimeMillis()) {
                // add the message to the list of unread messages of its recipients
                userDao.addMessage(iWatchMessage);
                try {
                    sendNotification(name, iWatchMessage);
                    logger.log(Level.INFO, "Sent PUSH notification for message {0}", iWatchMessage.getId());
                } catch (final PushNotificationException e) {
                    if (e.getRetry() == -1) {
                        logger.log(Level.SEVERE, "Message {0} will be removed from the queue", message);
                    } else {
                        final PropertiesReader reader = new PropertiesReader();
                        final long delay = e.getRetry() == 0 ? reader.getDelay() * (1 << count++) : e.getRetry() * 1000;
                        logger.log(Level.WARNING, "Message {0} will be pushed again in {1} millis", new Object[]{message, delay});
                        getMessageProducer().send(name, iWatchMessage, delay, expireOn, count);
                    }
                }
            } else {
                logger.log(Level.INFO, "Message {0} expired", iWatchMessage.getId());
            }
        } else {
            logger.log(Level.INFO, "Notification {0} expired", iWatchMessage.getId());
        }

        logger.exiting(this.getClass().getName(), "onMessageSent");
    }

    /**
     * Processes the JMS messages that are sent when a new activity has been added to a report.
     *
     * @param message the JMS message
     * @throws JMSException if the JMS message's body cannot be assigned to a {@link Report} object.
     */
    private void onNewActivity(final javax.jms.Message message) throws JMSException {
        logger.entering(this.getClass().getName(), "onNewActivity", message);

        final Report report = message.getBody(Report.class);
        int count = message.getIntProperty("count");
        final String name = message.getStringProperty("name");
        final String watcher = message.getStringProperty("watcher");

        // add the report to the list of unseen reports of its watchers
        userDao.addReport(report);
        try {
            sendNotification(name, report, watcher);
            logger.log(Level.INFO, "Sent PUSH notification for report {0}", report.getId());
        } catch (final PushNotificationException e) {
            final PropertiesReader reader = new PropertiesReader();
            final long delay = e.getRetry() == 0 ? reader.getDelay() * (1 << count++) : e.getRetry() * 1000;
            logger.log(Level.WARNING, "Message {0} will be pushed again in {1} millis", new Object[]{message, delay});
            getMessageProducer().notifyNewActivity(name, report, watcher, delay, count);
        }

        logger.exiting(this.getClass().getName(), "onNewActivity");
    }

    /**
     * Processes the JMS message that are sent when a watcher has been added/deleted to/from a report.
     *
     * @param message the JMS message
     * @throws JMSException if the JMS message cannot be assigned to an iWatch message
     */
    private void onWatcherAddedOrDeleted(final javax.jms.Message message) throws JMSException {
        logger.entering(this.getClass().getName(), "onWatcherAddedOrDeleted", message);

        final String watcher = message.getBody(String.class);
        final boolean added = message.getBooleanProperty("added");
        int count = message.getIntProperty("count");
        final String name = message.getStringProperty("name");
        final String report = message.getStringProperty("report");

        // add/remove the report to/from the list of unseen reports of the user
        try {
            if (added) {
                userDao.addReport(report, watcher);
            } else {
                userDao.deleteReport(report, watcher);
            }
        } catch (final DaoException ignore) {
        }

        try {
            sendNotification(name, watcher, String.format(WATCHER, report.substring(7), added ? "Sei" : "Non sei più"));
        } catch (final PushNotificationException e) {
            final PropertiesReader reader = new PropertiesReader();
            final long delay = e.getRetry() == 0 ? reader.getDelay() * (1 << count++) : e.getRetry() * 1000;
            logger.log(Level.WARNING, "Message {0} will be pushed again in {1} millis", new Object[]{message, delay});
            getMessageProducer().notifyWatcher(name, watcher, report, added, delay, count);
        }
        logger.log(Level.INFO, "Sent PUSH notification for report {0}", watcher);

        logger.exiting(this.getClass().getName(), "onWatcherAddedOrDeleted");
    }

    /**
     * Sends a PUSH notification.
     *
     * @param name    the municipality's name
     * @param message the message to be pushed
     * @throws PushNotificationException if the PUSH notification could not be sent
     */
    public abstract void sendNotification(final @NotNull String name, final @NotNull Message message) throws PushNotificationException;

    /**
     * Sends a PUSH notification.
     *
     * @param name    the municipality's name
     * @param report  the report to which the activity belongs
     * @param watcher the if of the watcher who created the activity
     * @throws PushNotificationException if the PUSH notification could not be sent
     */
    public abstract void sendNotification(final @NotNull String name, final @NotNull Report report,
                                          final @NotNull String watcher) throws PushNotificationException;

    /**
     * Sends a PUSH notification.
     *
     * @param name    the municipality's name
     * @param watcher the watcher
     * @param text    the text to be sent
     * @throws PushNotificationException if the PUSH notification could not be sent
     */
    public abstract void sendNotification(final @NotNull String name, final @NotNull String watcher,
                                          final @NotNull String text) throws PushNotificationException;

    /**
     * Returns a {@link MessageProducer} object.
     *
     * @return a {@link MessageProducer} object
     */
    public abstract MessageProducer getMessageProducer();
}
