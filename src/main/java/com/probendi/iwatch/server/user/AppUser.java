package com.probendi.iwatch.server.user;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.bson.Document;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Helper class for enabling app users to retrieve their profile and header, logo and features of their municipality
 * in a single REST call.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
public class AppUser extends User {

    private String header;
    private String logo;
    private List<String> features;

    /**
     * Creates a new {@code AppUser} object.
     */
    public AppUser() {
    }

    /**
     * Creates a new {@code AppUser} object from the given document.
     *
     * @param doc a {@link Document} instance
     */
    @SuppressWarnings("unchecked")
    public AppUser(final @NotNull Document doc) {
        header = doc.getString("header");
        logo = doc.getString("logo");
        features = (List<String>) doc.get("features");
        if (features == null) {
            features = new LinkedList<>();
        }
    }

    /**
     * Creates a new {@code AppUser} object from the given user.
     *
     * @param user a {@link User} instance
     */
    public AppUser(final @NotNull User user) {
        id = user.getId();
        platform = user.platform;
        registrationId = user.registrationId;
        mobile = user.mobile;
        firstname = user.firstname;
        lastname = user.lastname;
        dateOfBirth = user.dateOfBirth;
        placeOfBirth = user.placeOfBirth;
        address = user.address;
        cap = user.cap;
        city = user.city;
        messages = user.messages;
        reports = user.reports;
        municipality = user.municipality;
        header = "";
        logo = "";
        features = new LinkedList<>();
    }

    /**
     * Creates a new {@code AppUser} object from the given builder.
     *
     * @param builder the builder
     */
    private AppUser(final Builder builder) {
        header = builder.header;
        logo = builder.logo;
        features = builder.features;
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

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(final List<String> features) {
        this.features = features;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppUser)) return false;
        if (!super.equals(o)) return false;
        AppUser appUser = (AppUser) o;
        return Objects.equals(header, appUser.header) &&
                Objects.equals(logo, appUser.logo) &&
                Objects.equals(features, appUser.features);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), header, logo, features);
    }

    @Override
    public String toString() {
        String replacement = ", header='" + header + '\'' + ", logo='" + logo + '\'' + ", features='" + features + "'}";
        return super.toString().replace("}", replacement);
    }

    @Contract(" -> !null")
    public static User.Builder newBuilder() {
        return new Builder();
    }

    /**
     * Returns the representation of this user as a {@link Document}.
     *
     * @return the representation of this user as a {@link Document}
     */
    protected Document toDocument() {
        return super.toDocument().append("logo", logo).append("features", features);
    }

    /**
     * {@code User} builder static inner class.
     */
    public static final class Builder extends User.Builder {
        private String header;
        private String logo;
        private List<String> features;

        /**
         * Prevents instantiation.
         */
        protected Builder() {
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
        public Builder features(final @NotNull List<String> val) {
            features = val;
            return this;
        }

        @NotNull
        public AppUser build() {
            return new AppUser(this);
        }
    }
}
