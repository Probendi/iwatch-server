package com.probendi.iwatch.server.jms.producer;

import org.jetbrains.annotations.NotNull;

import com.probendi.iwatch.server.message.Message;
import com.probendi.iwatch.server.report.Activity;
import com.probendi.iwatch.server.report.Report;

/**
 * Produces JMS messages which will trigger push notifications to mobile clients.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
public interface MessageProducer {

    /**
     * Sends a JMS message for a new {@link Message}.
     *
     * @param name     the municipality's name
     * @param message  a {@link Message} object
     * @param delay    the message's delivery delay in millis
     * @param expireOn the expiration date in millis
     * @param count    the number of delivery attempts
     */
    void send(final @NotNull String name, final @NotNull Message message, final long delay, final long expireOn, final int count);

    /**
     * Sends a JMS message when a new {@link Activity} has been added to the given report.
     *
     * @param name    the municipality's name
     * @param report  a {@link Report} object
     * @param watcher the id of the watcher who created the activity
     * @param delay   the message's delivery delay in millis
     * @param count   the number of delivery attempts
     */
    void notifyNewActivity(final @NotNull String name, final @NotNull Report report,
                           final @NotNull String watcher, final long delay, final int count);

    /**
     * Sends a JMS message when a user who has been added/deleted as a watcher to/from the given report.
     *
     * @param name    the municipality's name
     * @param watcher the watcher's id
     * @param report  the report's id
     * @param added   {@code true} if the watcher was added or {@code false} if the watcher was deleted
     * @param delay   the message's delivery delay in millis
     * @param count   the number of delivery attempts
     */
    void notifyWatcher(final @NotNull String name, final String watcher, final @NotNull String report,
                       final boolean added, final long delay, final int count);
}
