package com.probendi.iwatch.server.report;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import org.bson.Document;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import com.probendi.iwatch.server.user.Watcher;

/**
 * An activity performed on a {@link Report} by an administrator.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
public class Activity implements Serializable {

    private Date date;
    private String comment;
    private String attachment;
    private String mimeType;
    private String thumbnail;
    private double latitude;
    private double longitude;
    private Watcher watcher;

    /**
     * Creates a new {@code Activity} object.
     */
    public Activity() {
    }

    /**
     * Creates a new {@code Activity} object from the given document.
     *
     * @param doc a {@link Document} instance
     */
    public Activity(final @NotNull Document doc) {
        date = doc.getDate("date");
        comment = doc.getString("comment");
        attachment = doc.getString("attachment");
        mimeType = doc.getString("mimeType");
        thumbnail = doc.getString("thumbnail");
        latitude = doc.getDouble("latitude");
        longitude = doc.getDouble("longitude");
        if (doc.get("watcher") != null) {
            watcher = new Watcher(doc.get("watcher", Document.class));
        }
    }

    /**
     * Creates a new {@code Activity} object from the given builder.
     *
     * @param builder the builder
     */
    private Activity(final Builder builder) {
        date = builder.date;
        comment = builder.comment;
        attachment = builder.attachment;
        mimeType = builder.mimeType;
        thumbnail = builder.thumbnail;
        latitude = builder.latitude;
        longitude = builder.longitude;
        watcher = builder.watcher;
    }

    @Contract(" -> !null")
    public static Builder newBuilder() {
        return new Builder();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(final String attachment) {
        this.attachment = attachment;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(final String mimeType) {
        this.mimeType = mimeType;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(final String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(final double latitude) {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("latitude must be between -90 and 90 inclusive");
        }
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(final double longitude) {
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("longitude must be between -180 and 180 inclusive");
        }
        this.longitude = longitude;
    }

    public Watcher getWatcher() {
        return watcher;
    }

    public void setWatcher(final Watcher watcher) {
        this.watcher = watcher;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Activity)) return false;
        Activity activity = (Activity) o;
        return Objects.equals(date, activity.date) &&
                Objects.equals(comment, activity.comment) &&
                Objects.equals(attachment, activity.attachment) &&
                Objects.equals(mimeType, activity.mimeType) &&
                Objects.equals(thumbnail, activity.thumbnail) &&
                Objects.equals(latitude, activity.latitude) &&
                Objects.equals(longitude, activity.longitude) &&
                Objects.equals(watcher, activity.watcher);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, comment, attachment, mimeType, thumbnail, latitude, longitude, watcher);
    }

    @Override
    public String toString() {
        return "Activity{" +
                "date='" + date + '\'' +
                ", comment='" + comment + '\'' +
                ", attachment='" + attachment + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", watcher=" + watcher +
                '}';
    }

    /**
     * Returns the representation of this report as a {@link Document}.
     *
     * @return the representation of this report as a {@link Document}
     */
    public Document toDocument() {
        return new Document("date", date)
                .append("comment", comment)
                .append("attachment", attachment)
                .append("mimeType", mimeType)
                .append("thumbnail", thumbnail)
                .append("latitude", latitude)
                .append("longitude", longitude)
                .append("watcher", watcher);
    }

    /**
     * {@code Activity} builder static inner class.
     */
    public static final class Builder {
        private Date date;
        private String comment;
        private String attachment;
        private String mimeType;
        private String thumbnail;
        private double latitude;
        private double longitude;
        private Watcher watcher;

        /**
         * Prevents instantiation.
         */
        private Builder() {
        }

        @NotNull
        public Builder date(final @NotNull Date val) {
            date = val;
            return this;
        }

        @NotNull
        public Builder comment(final @NotNull String val) {
            comment = val;
            return this;
        }

        @NotNull
        public Builder attachment(final @NotNull String val) {
            attachment = val;
            return this;
        }

        @NotNull
        public Builder mimeType(final @NotNull String val) {
            mimeType = val;
            return this;
        }

        @NotNull
        public Builder thumbnail(final @NotNull String val) {
            thumbnail = val;
            return this;
        }

        @NotNull
        public Builder latitude(final double val) {
            if (val < -90 || val > 90) {
                throw new IllegalArgumentException("latitude must be between -90 and 90 inclusive");
            }
            latitude = val;
            return this;
        }

        @NotNull
        public Builder longitude(final double val) {
            if (val < -180 || val > 180) {
                throw new IllegalArgumentException("longitude must be between -180 and 180 inclusive");
            }
            longitude = val;
            return this;
        }

        @NotNull
        public Builder watcher(final @NotNull Watcher val) {
            watcher = val;
            return this;
        }

        @NotNull
        public Activity build() {
            return new Activity(this);
        }
    }
}
