package com.probendi.iwatch.server.user;

import java.io.Serializable;
import java.util.Objects;

import org.bson.Document;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * An administrator.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
public class Administrator implements Serializable {

    private String id;
    private String firstname;
    private String lastname;
    private String password;
    private String municipality;
    private boolean superuser;

    /**
     * Creates a new {@code Administrator} object.
     */
    public Administrator() {
    }

    /**
     * Creates a new {@code Administrator} object from the given document.
     *
     * @param doc a {@link Document} instance
     */
    public Administrator(final @NotNull Document doc) {
        id = doc.getString("_id");
        firstname = doc.getString("firstname");
        lastname = doc.getString("lastname");
        password = doc.getString("password");
        municipality = doc.getString("municipality");
        superuser = doc.getBoolean("superuser");
    }

    /**
     * Creates a new {@code Administrator} object from the given builder.
     *
     * @param builder the builder
     */
    private Administrator(final Builder builder) {
        id = builder.id;
        firstname = builder.firstname;
        lastname = builder.lastname;
        password = builder.password;
        municipality = builder.municipality;
        superuser = builder.superuser;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(final String municipality) {
        this.municipality = municipality;
    }

    public boolean isSuperuser() {
        return superuser;
    }

    public void setSuperuser(final boolean superuser) {
        this.superuser = superuser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Administrator)) return false;
        Administrator administrator = (Administrator) o;
        return superuser == administrator.superuser &&
                Objects.equals(id, administrator.id) &&
                Objects.equals(firstname, administrator.firstname) &&
                Objects.equals(lastname, administrator.lastname) &&
                Objects.equals(password, administrator.password) &&
                Objects.equals(municipality, administrator.municipality);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, password, municipality);
    }

    @Override
    public String toString() {
        return "Administrator{" +
                "id='" + id + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", password='" + password + '\'' +
                ", municipality='" + municipality + '\'' +
                ", superuser=" + superuser +
                '}';
    }

    @Contract(" -> !null")
    static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Returns the representation of this administrator as a {@link Document}.
     *
     * @return the representation of this administrator as a {@link Document}
     */
    Document toDocument() {
        return new Document("_id", id)
                .append("firstname", firstname)
                .append("lastname", lastname)
                .append("password", password)
                .append("municipality", municipality)
                .append("superuser", superuser);
    }

    /**
     * {@code Administrator} builder static inner class.
     */
    public static final class Builder {
        private String id;
        private String firstname;
        private String lastname;
        private String password;
        private String municipality;
        private boolean superuser;

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
        public Builder password(final @NotNull String val) {
            password = val;
            return this;
        }

        @NotNull
        Builder municipality(final @NotNull String val) {
            municipality = val;
            return this;
        }

        @NotNull
        Builder superuser(final boolean val) {
            superuser = val;
            return this;
        }

        @NotNull
        Administrator build() {
            return new Administrator(this);
        }
    }
}
