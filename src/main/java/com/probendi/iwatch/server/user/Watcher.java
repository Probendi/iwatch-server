package com.probendi.iwatch.server.user;

import java.io.Serializable;
import java.util.Objects;

import org.bson.Document;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * A user or an administrator who created a report, or a user who can view a report created by someone else.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
public class Watcher implements Serializable {

    private String id;
    private String mobile;
    private String firstname;
    private String lastname;
    private boolean creator;

    /**
     * Creates a new {@code Watcher} object.
     */
    public Watcher() {
    }

    /**
     * Creates a new {@code Watcher} object from the given document.
     *
     * @param doc a {@link Document} instance
     */
    public Watcher(final @NotNull Document doc) {
        id = doc.getString("_id");
        mobile = doc.getString("mobile");
        firstname = doc.getString("firstname");
        lastname = doc.getString("lastname");
        creator = doc.getBoolean("creator");
    }

    /**
     * Creates a new {@code User} object from the given builder.
     *
     * @param builder the builder
     */
    private Watcher(final Builder builder) {
        id = builder.id;
        mobile = builder.mobile;
        firstname = builder.firstname;
        lastname = builder.lastname;
        creator = builder.creator;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(final String uuid) {
        this.mobile = uuid;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(final String lastname) {
        this.lastname = lastname;
    }

    public boolean isCreator() {
        return creator;
    }

    public void setCreator(boolean creator) {
        this.creator = creator;
    }

    /**
     * Returns {@code true} if this watcher is an administrator.
     *
     * @return {@code true} if this watcher is an administrator
     */
    public boolean isAdministrator() {
        return id.contains("@");
    }

    /**
     * Returns {@code true} if this watcher is a user.
     *
     * @return {@code true} if this watcher is a user
     */
    public boolean isUser() {
        return !id.contains("@");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Watcher)) return false;
        Watcher watcher = (Watcher) o;
        return creator == watcher.creator &&
                Objects.equals(id, watcher.id) &&
                Objects.equals(mobile, watcher.mobile) &&
                Objects.equals(firstname, watcher.firstname) &&
                Objects.equals(lastname, watcher.lastname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, mobile, firstname, lastname, creator);
    }

    @Override
    public String toString() {
        return "Watcher{" +
                "id='" + id + '\'' +
                ", mobile='" + mobile + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", creator=" + creator +
                '}';
    }

    @Contract(" -> !null")
    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Returns the representation of this user as a {@link Document}.
     *
     * @return the representation of this user as a {@link Document}
     */
    Document toDocument() {
        return new Document("_id", id)
                .append("mobile", mobile)
                .append("firstname", firstname)
                .append("lastname", lastname)
                .append("creator", creator);
    }

    /**
     * {@code User} builder static inner class.
     */
    public static class Builder {
        private String id;
        private String mobile;
        private String firstname;
        private String lastname;
        private boolean creator;

        /**
         * Prevents instantiation.
         */
        protected Builder() {
        }

        @NotNull
        public Builder id(final @NotNull String val) {
            id = val;
            return this;
        }

        @NotNull
        public Builder mobile(final @NotNull String val) {
            mobile = val;
            return this;
        }

        @NotNull
        public Builder firstname(final @NotNull String val) {
            firstname = val;
            return this;
        }

        @NotNull
        public Builder lastname(final @NotNull String val) {
            lastname = val;
            return this;
        }

        @NotNull
        public Builder creator(final boolean val) {
            creator = val;
            return this;
        }

        @NotNull
        public Watcher build() {
            return new Watcher(this);
        }
    }
}
