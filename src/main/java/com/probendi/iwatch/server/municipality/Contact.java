package com.probendi.iwatch.server.municipality;

import java.io.Serializable;
import java.util.Objects;

import org.bson.Document;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * The contact details of a {@link Municipality}.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
public class Contact implements Serializable {

    String name;
    String telephone;
    String email;

    /**
     * Creates a new {@code Contact} object.
     */
    public Contact() {
    }

    /**
     * Creates a new {@code Contact} object from the given document.
     *
     * @param doc a {@link Document} instance
     */
    public Contact(final @NotNull Document doc) {
        name = doc.getString("name");
        telephone = doc.getString("telephone");
        email = doc.getString("email");
    }

    /**
     * Creates a new {@code Contact} object from the given builder.
     *
     * @param builder the builder
     */
    private Contact(final Builder builder) {
        name = builder.name;
        telephone = builder.telephone;
        email = builder.email;
    }

    @Contract(" -> !null")
    public static Builder newBuilder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(final String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contact)) return false;
        Contact contact = (Contact) o;
        return Objects.equals(name, contact.name) &&
                Objects.equals(telephone, contact.telephone) &&
                Objects.equals(email, contact.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, telephone, email);
    }

    @Override
    public String toString() {
        return "Contact{" +
                "name='" + name + '\'' +
                ", telephone='" + telephone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    /**
     * Returns the representation of this contact as a {@link Document}.
     *
     * @return the representation of this contact as a {@link Document}
     */
    public Document toDocument() {
        return new Document("name", name).append("telephone", telephone).append("email", email);
    }

    /**
     * {@code Contact} builder static inner class.
     */
    public static final class Builder {
        private String name;
        private String telephone;
        private String email;

        private Builder() {
        }

        public Builder name(final @NotNull String val) {
            name = val;
            return this;
        }

        public Builder telephone(final @NotNull String val) {
            telephone = val;
            return this;
        }

        public Builder email(final @NotNull String val) {
            email = val;
            return this;
        }

        @Contract(" -> !null")
        public Contact build() {
            return new Contact(this);
        }
    }
}
