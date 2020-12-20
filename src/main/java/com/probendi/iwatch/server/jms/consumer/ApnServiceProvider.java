package com.probendi.iwatch.server.jms.consumer;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedExecutorService;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsDelegate;
import com.notnoop.apns.ApnsNotification;
import com.notnoop.apns.ApnsService;
import com.notnoop.apns.DeliveryError;

import com.probendi.iwatch.server.util.PropertiesReader;

/**
 * Provides a singleton instance of an {@link ApnsService} object.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
@Startup
@Singleton
public class ApnServiceProvider {

    private ApnsService service;

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Resource(name = "concurrent/__defaultManagedThreadFactory")
    private ManagedExecutorService executor;

    /**
     * Returns a singleton instance of {@link ApnsService}.
     *
     * @return a singleton instance of {@link ApnsService}
     */
    public ApnsService getService() {
        if (service == null) {
            final PropertiesReader propertiesReader = new PropertiesReader();
            final String certificate = propertiesReader.getApnCertificate();
            final int maxConnections = propertiesReader.getApnMaxConnections();
            final String password = propertiesReader.getApnPassword();
            final boolean production = propertiesReader.isApnProduction();

            try (final InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(certificate)) {
                service = APNS.newService().withCert(in, password).asPool(executor, maxConnections)
                        .withDelegate(new ApnsDelegateImpl()).withAppleDestination(production).build();
            } catch (final IOException e) {
                logger.log(Level.WARNING, "Failed to close input stream", e);
            }
        }
        return service;
    }

    /**
     * Implements {@link ApnsDelegate} for getting notified of the status of notification deliveries.
     */
    private static class ApnsDelegateImpl implements ApnsDelegate {

        private final Logger logger = Logger.getLogger(this.getClass().getName());

        @Override
        public void messageSent(ApnsNotification notification, boolean resent) {
            logger.log(Level.FINER, "Notification sent: {0}", notification);
        }

        @Override
        public void messageSendFailed(ApnsNotification notification, Throwable e) {
            logger.log(Level.SEVERE, "Failed to send notification: " + notification, e);
        }

        @Override
        public void connectionClosed(DeliveryError e, int id) {
            logger.log(Level.FINER, "Connection closed for notification {0} [{1}]", new Object[]{id, e.toString()});
        }

        @Override
        public void cacheLengthExceeded(int newCacheLength) {
            logger.log(Level.WARNING, "Cache length exceeded [new size of the resend cache: {0}]", newCacheLength);
        }

        @Override
        public void notificationsResent(int resendCount) {
            logger.log(Level.FINER, "There are {0} notifications being queued for resend", resendCount);
        }
    }
}
