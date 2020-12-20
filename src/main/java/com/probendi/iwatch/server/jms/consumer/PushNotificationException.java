package com.probendi.iwatch.server.jms.consumer;

/**
 * A {@code PushNotificationException} is thrown if a push notification cannot be sent.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
public class PushNotificationException extends Exception {

    final long retry;

    /**
     * Creates a new {@code PushNotificationException} object.
     */
    public PushNotificationException() {
        retry = 0;
    }

    /**
     * Creates a new {@code PushNotificationException} object.
     *
     * @param retry the retry value in seconds
     */
    public PushNotificationException(final long retry) {
        this.retry = retry;
    }

    public long getRetry() {
        return retry;
    }

}
