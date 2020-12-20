package com.probendi.iwatch.server.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class for reading the properties file.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
public class PropertiesReader {

    private Properties properties = new Properties();

    public PropertiesReader() {
        try (final InputStream in = getClass().getClassLoader().getResourceAsStream("iwatch.properties");
             final InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(in), StandardCharsets.UTF_8)) {
            properties.load(reader);
        } catch (final IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Failed to read properties file", e);
        }
    }


    public String getApnCertificate() {
        return properties.getProperty(isApnProduction() ? "apn.certificate.production" : "apn.certificate.sandbox");
    }

    public int getApnMaxConnections() {
        return Integer.parseInt(properties.getProperty("apn.max.connections"));
    }

    public String getApnPassword() {
        return properties.getProperty("apn.password");
    }

    public String getAudioMessageCsvHeader() {
        return properties.getProperty("audio-message.csv.header") + "\r\n";
    }

    public SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat(properties.getProperty("date.format"));
    }

    public SimpleDateFormat getDateTimeFormat() {
        return new SimpleDateFormat(properties.getProperty("datetime.format"));
    }

    public int getDelay() {
        return Integer.parseInt(properties.getProperty("delay"));
    }

    public String getFcmKey() {
        return properties.getProperty("fcm.key");
    }

    public int getFcmMaxRecipients() {
        return Integer.parseInt(properties.getProperty("fcm.max.recipients"));
    }

    public String getFcmUrl() {
        return properties.getProperty("fcm.url");
    }

    public String getMessageCsvHeader() {
        return properties.getProperty("message.csv.header") + "\r\n";
    }

    public int getNotificationValidity() {
        return Integer.parseInt(properties.getProperty("notification.validity"));
    }

    public String getReportCsvHeader() {
        return properties.getProperty("report.csv.header") + "\r\n";
    }

    public int getThumbnailSize() {
        return Integer.parseInt(properties.getProperty("thumbnail.size"));
    }

    public String getUploadsPath() {
        return properties.getProperty("uploads.path");
    }

    public String getUserCsvHeader() {
        return properties.getProperty("user.csv.header") + "\r\n";
    }

    public boolean isApnProduction() {
        return Boolean.parseBoolean(properties.getProperty("apn.production"));
    }

    public boolean isSendPushNotification() {
        return Boolean.parseBoolean(properties.getProperty("sendPushNotification"));
    }
}
