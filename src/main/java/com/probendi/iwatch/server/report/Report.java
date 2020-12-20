package com.probendi.iwatch.server.report;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bson.Document;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import com.probendi.iwatch.server.user.User;
import com.probendi.iwatch.server.user.Watcher;

/**
 * A report created by a {@link User}.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
public class Report implements Serializable {

    private String id;
    private String category;
    private String description;
    private Date date;
    private String attachment;
    private String mimeType;
    private String thumbnail;
    private double latitude;
    private double longitude;
    private String municipality;
    private String status;
    private boolean actionRequired;
    private List<Watcher> watchers;
    private List<Activity> activities;

    /**
     * Creates a new {@code Report} object.
     */
    public Report() {
    }

    /**
     * Creates a new {@code Report} object from the given document.
     *
     * @param doc a {@link Document} instance
     */
    @SuppressWarnings("unchecked")
    public Report(final @NotNull Document doc) {
        id = doc.getString("_id");
        category = doc.getString("category");
        description = doc.getString("description");
        date = doc.getDate("date");
        attachment = doc.getString("attachment");
        mimeType = doc.getString("mimeType");
        thumbnail = doc.getString("thumbnail");
        latitude = doc.getDouble("latitude");
        longitude = doc.getDouble("longitude");
        municipality = doc.getString("municipality");
        status = doc.getString("status");
        actionRequired = doc.getBoolean("actionRequired");
        watchers = new LinkedList<>();
        List<Document> documents = (List<Document>) doc.get("watchers");
        if (documents != null) {
            watchers.addAll(documents.stream().map(Watcher::new).collect(Collectors.toList()));
        }
        activities = new LinkedList<>();
        documents = (List<Document>) doc.get("activities");
        if (documents != null) {
            activities.addAll(documents.stream().map(Activity::new).collect(Collectors.toList()));
        }
    }

    /**
     * Creates a new {@code Report} object from the given builder.
     *
     * @param builder the builder
     */
    private Report(final Builder builder) {
        id = builder.id;
        category = builder.category;
        description = builder.description;
        date = builder.date;
        attachment = builder.attachment;
        mimeType = builder.mimeType;
        thumbnail = builder.thumbnail;
        latitude = builder.latitude;
        longitude = builder.longitude;
        municipality = builder.municipality;
        status = builder.status;
        actionRequired = builder.actionRequired;
        watchers = builder.watchers;
        if (watchers == null) {
            watchers = new LinkedList<>();
        }
        activities = builder.activities;
        if (activities == null) {
            activities = new LinkedList<>();
        }
    }

    @Contract(" -> !null")
    public static Builder newBuilder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(final String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        this.date = date;
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

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(final String municipality) {
        this.municipality = municipality;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public boolean isActionRequired() {
        return actionRequired;
    }

    public void setActionRequired(final boolean actionRequired) {
        this.actionRequired = actionRequired;
    }

    public List<Watcher> getWatchers() {
        return watchers;
    }

    public void setWatchers(final List<Watcher> watchers) {
        this.watchers = watchers;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(final List<Activity> activities) {
        this.activities = activities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Report)) return false;
        Report report = (Report) o;
        return Objects.equals(id, report.id) &&
                Objects.equals(category, report.category) &&
                Objects.equals(description, report.description) &&
                Objects.equals(date, report.date) &&
                Objects.equals(attachment, report.attachment) &&
                Objects.equals(mimeType, report.mimeType) &&
                Objects.equals(thumbnail, report.thumbnail) &&
                Objects.equals(latitude, report.latitude) &&
                Objects.equals(longitude, report.longitude) &&
                Objects.equals(municipality, report.municipality) &&
                Objects.equals(status, report.status) &&
                Objects.equals(actionRequired, report.actionRequired) &&
                Objects.equals(watchers, report.watchers) &&
                Objects.equals(activities, report.activities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, category, description, date, attachment, mimeType, thumbnail, latitude, longitude,
                municipality, status, actionRequired, watchers, activities);
    }

    @Override
    public String toString() {
        return "Report{" +
                "id='" + id + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", attachment='" + attachment + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", municipality='" + municipality + '\'' +
                ", status='" + status + '\'' +
                ", actionRequired='" + actionRequired + '\'' +
                ", watchers='" + watchers + '\'' +
                ", activities='" + activities + '\'' +
                '}';
    }

    /**
     * Returns the representation of this report as a {@link Document}.
     *
     * @return the representation of this report as a {@link Document}
     */
    public Document toDocument() {
        return new Document("_id", id)
                .append("category", category)
                .append("description", description)
                .append("date", date)
                .append("attachment", attachment)
                .append("mimeType", mimeType)
                .append("thumbnail", thumbnail)
                .append("latitude", latitude)
                .append("longitude", longitude)
                .append("municipality", municipality)
                .append("status", status)
                .append("actionRequired", actionRequired)
                .append("watchers", watchers)
                .append("activities", activities);
    }

    /**
     * {@code Report} builder static inner class.
     */
    public static final class Builder {
        private String id;
        private String category;
        private String description;
        private Date date;
        private String attachment;
        private String mimeType;
        private String thumbnail;
        private double latitude;
        private double longitude;
        private String municipality;
        private String status;
        private boolean actionRequired;
        private List<Watcher> watchers;
        private List<Activity> activities;

        /**
         * Prevents instantiation.
         */
        private Builder() {
        }

        @NotNull
        public Builder id(final @NotNull String val) {
            id = val;
            return this;
        }

        @NotNull
        public Builder category(final @NotNull String val) {
            category = val;
            return this;
        }

        @NotNull
        public Builder description(final @NotNull String val) {
            description = val;
            return this;
        }

        @NotNull
        public Builder date(final @NotNull Date val) {
            date = val;
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
        public Builder municipality(final @NotNull String val) {
            municipality = val;
            return this;
        }

        @NotNull
        public Builder status(final @NotNull String val) {
            status = val;
            return this;
        }

        @NotNull
        public Builder actionRequired(final boolean val) {
            actionRequired = val;
            return this;
        }

        @NotNull
        public Builder watchers(final @NotNull List<Watcher> val) {
            watchers = val;
            return this;
        }

        @NotNull
        public Builder activities(final @NotNull List<Activity> val) {
            activities = val;
            return this;
        }

        @NotNull
        public Report build() {
            return new Report(this);
        }
    }
}
