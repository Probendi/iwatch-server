package com.probendi.iwatch.server.jms.producer;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Queue;

import org.jetbrains.annotations.NotNull;

import com.probendi.iwatch.server.message.Message;
import com.probendi.iwatch.server.report.Report;

/**
 * An implementation of {@link MessageProducer} for sending messages to {@code jms/iWatchApnQueue}.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
@Stateless
public class MessageProducerApnImpl implements MessageProducer {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Inject
    @JMSConnectionFactory("java:comp/DefaultJMSConnectionFactory")
    private JMSContext jmsContext;

    @Resource(lookup = "jms/iWatchApnQueue")
    private Queue queue;

    @Override
    public void send(final @NotNull String name, final @NotNull Message message, final long delay, final long expireOn, final int count) {
        logger.entering(this.getClass().getName(), "send", new Object[]{name, message, delay, expireOn, count});

        jmsContext.createProducer().setDeliveryDelay(delay).setProperty("name", name)
                .setProperty("expireOn", expireOn).setProperty("count", count).send(queue, message);
        logger.log(Level.FINER, "Sent JMS message for Message {0}", message.getId());

        logger.exiting(this.getClass().getName(), "send");
    }

    @Override
    public void notifyNewActivity(final @NotNull String name, final @NotNull Report report,
                                  final @NotNull String watcher, final long delay, final int count) {
        logger.entering(this.getClass().getName(), "notifyNewActivity", new Object[]{name, report, delay, count});

        jmsContext.createProducer().setDeliveryDelay(delay).setProperty("name", name).setProperty("count", count)
                .setProperty("watcher", watcher).send(queue, report);
        logger.log(Level.FINER, "Sent JMS message for new activity of report {0}", report);

        logger.exiting(this.getClass().getName(), "notifyNewActivity");
    }

    @Override
    public void notifyWatcher(final @NotNull String name, final @NotNull String watcher, final @NotNull String report,
                              final boolean added, final long delay, final int count) {
        logger.entering(this.getClass().getName(), "notifyWatcher", new Object[]{name, watcher, report, added, delay, count});

        jmsContext.createProducer().setDeliveryDelay(delay).setProperty("name", name).setProperty("report", report)
                .setProperty("added", added).setProperty("count", count).send(queue, watcher);
        logger.log(Level.FINER, "Sent JMS message for watcher {0}", watcher);

        logger.exiting(this.getClass().getName(), "notifyWatcher");
    }
}
