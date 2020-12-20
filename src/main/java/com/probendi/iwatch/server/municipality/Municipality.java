package com.probendi.iwatch.server.municipality;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bson.Document;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import io.jsonwebtoken.lang.Collections;

/**
 * A municipality.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
public class Municipality implements Comparable<Municipality>, Serializable {

    public enum Feature {
        REGISTRATION, REPORTING, TRACKING
    }

    static final String TICKET_NUMBER = "%s_%d_%04d";

    private String id;
    private String name;
    private String province;
    private String region;
    private String header;
    private String logo;
    private double latitude;
    private double longitude;
    private int zoom;
    private String ticketNumber;
    private boolean active;
    private List<String> features;
    private List<String> categories;
    private List<String> interests;
    private List<Contact> contacts;
    private long tokenValidity;

    /**
     * Creates a new {@code Municipality} object.
     */
    public Municipality() {
    }

    /**
     * Creates a new {@code Municipality} object from the given document.
     *
     * @param doc a {@link Document} instance
     */
    @SuppressWarnings("unchecked")
    public Municipality(final @NotNull Document doc) {
        id = doc.getString("_id");
        name = doc.getString("name");
        province = doc.getString("province");
        region = doc.getString("region");
        header = doc.getString("header");
        logo = doc.getString("logo");
        latitude = doc.getDouble("latitude");
        longitude = doc.getDouble("longitude");
        zoom = doc.getInteger("zoom");
        ticketNumber = doc.getString("ticketNumber");
        active = doc.getBoolean("active");
        features = (List<String>) doc.get("features");
        if (features == null) {
            features = Collections.arrayToList(Feature.REGISTRATION.toString());
        }
        categories = (List<String>) doc.get("categories");
        if (categories == null) {
            categories = new LinkedList<>();
        }
        interests = (List<String>) doc.get("interests");
        if (interests == null) {
            interests = new LinkedList<>();
        }
        contacts = new LinkedList<>();
        final List<Document> documents = (List<Document>) doc.get("contacts");
        if (documents != null) {
            contacts.addAll(documents.stream().map(Contact::new).collect(Collectors.toList()));
        }
        tokenValidity = doc.getLong("tokenValidity");
    }

    /**
     * Creates a new {@code Municipality} object from the given builder.
     *
     * @param builder the builder
     */
    private Municipality(final Builder builder) {
        id = builder.id;
        name = builder.name;
        province = builder.province;
        region = builder.region;
        header = builder.header;
        logo = builder.logo;
        latitude = builder.latitude;
        longitude = builder.longitude;
        zoom = builder.zoom;
        ticketNumber = builder.ticketNumber;
        active = builder.active;
        features = builder.features;
        if (features == null) {
            features = new LinkedList<>();
        }
        categories = builder.categories;
        if (categories == null) {
            categories = new LinkedList<>();
        }
        interests = builder.interests;
        if (interests == null) {
            interests = new LinkedList<>();
        }
        contacts = builder.contacts;
        if (contacts == null) {
            contacts = new LinkedList<>();
        }
        tokenValidity = builder.tokenValidity;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(final String province) {
        this.province = province;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(final String region) {
        this.region = region;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(final String header) {
        this.header = header;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(final String logo) {
        this.logo = logo;
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

    public int getZoom() {
        if (zoom < 0 || zoom > 15) {
            throw new IllegalArgumentException("zoom must be between 0 and 15 inclusive");
        }
        return zoom;
    }

    public void setZoom(final int zoom) {
        this.zoom = zoom;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(final String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(final List<String> features) {
        this.features = features;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(final List<String> categories) {
        this.categories = categories;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(final List<String> interests) {
        this.interests = interests;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(final List<Contact> contacts) {
        this.contacts = contacts;
    }

    public long getTokenValidity() {
        return tokenValidity;
    }

    public void setTokenValidity(final long tokenValidity) {
        this.tokenValidity = tokenValidity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Municipality)) return false;
        Municipality that = (Municipality) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, province, region, header, logo, latitude, longitude, zoom, ticketNumber, active,
                features, categories, interests, contacts, tokenValidity);
    }

    @Override
    public String toString() {
        return "Municipality{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", province='" + province + '\'' +
                ", region='" + region + '\'' +
                ", header='" + header + '\'' +
                ", logo='" + logo + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", zoom=" + zoom +
                ", ticketNumber='" + ticketNumber + '\'' +
                ", features=" + features +
                ", active=" + active +
                ", categories=" + categories +
                ", interests=" + interests +
                ", contacts=" + contacts +
                ", tokenValidity=" + tokenValidity +
                '}';
    }

    @Contract(" -> !null")
    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Returns the representation of this municipality as a {@link Document}.
     *
     * @return the representation of this municipality as a {@link Document}
     */
    public Document toDocument() {
        return new Document("_id", id)
                .append("name", name)
                .append("province", province)
                .append("region", region)
                .append("header", header)
                .append("logo", logo)
                .append("latitude", latitude)
                .append("longitude", longitude)
                .append("zoom", zoom)
                .append("ticketNumber", ticketNumber)
                .append("active", active)
                .append("features", features)
                .append("categories", categories)
                .append("interests", interests)
                .append("contacts", contacts)
                .append("tokenValidity", tokenValidity);
    }

    @Override
    public int compareTo(@NotNull Municipality o) {
        return name.compareTo(o.getName());
    }

    /**
     * {@code Municipality} builder static inner class.
     */
    public static final class Builder {
        private String id;
        private String name;
        private String province;
        private String region;
        private String header;
        private String logo;
        private double latitude;
        private double longitude;
        private int zoom;
        private String ticketNumber;
        private boolean active;
        private List<String> features;
        private List<String> categories;
        private List<String> interests;
        private List<Contact> contacts;
        private long tokenValidity;

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
        public Builder name(final @NotNull String val) {
            name = val;
            return this;
        }

        @NotNull
        public Builder province(final @NotNull String val) {
            province = val;
            return this;
        }

        @NotNull
        public Builder region(final @NotNull String val) {
            region = val;
            return this;
        }

        @NotNull
        public Builder header(final @NotNull String val) {
            header = val;
            return this;
        }

        @NotNull
        public Builder logo(final @NotNull String val) {
            logo = val;
            return this;
        }

        @NotNull
        public Builder latitude(final double val) {
            if (latitude < -90 || latitude > 90) {
                throw new IllegalArgumentException("latitude must be between -90 and 90 inclusive");
            }
            latitude = val;
            return this;
        }

        @NotNull
        public Builder longitude(final double val) {
            if (longitude < -180 || longitude > 180) {
                throw new IllegalArgumentException("longitude must be between -180 and 180 inclusive");
            }
            longitude = val;
            return this;
        }

        @NotNull
        public Builder zoom(final int val) {
            if (zoom < 0 || zoom > 15) {
                throw new IllegalArgumentException("zoom must be between 0 and 15 inclusive");
            }
            zoom = val;
            return this;
        }

        @NotNull
        public Builder ticketNumber(final @NotNull String val) {
            ticketNumber = val;
            return this;
        }

        @NotNull
        public Builder active(final boolean val) {
            active = val;
            return this;
        }

        @NotNull
        public Builder features(final @NotNull List<String> val) {
            features = val;
            return this;
        }

        @NotNull
        public Builder categories(final @NotNull List<String> val) {
            categories = val;
            return this;
        }

        @NotNull
        public Builder interests(final @NotNull List<String> val) {
            interests = val;
            return this;
        }

        @NotNull
        public Builder contacts(final @NotNull List<Contact> val) {
            contacts = val;
            return this;
        }

        @NotNull
        public Builder tokenValidity(final long val) {
            tokenValidity = val;
            return this;
        }

        @NotNull
        public Municipality build() {
            return new Municipality(this);
        }
    }
}
