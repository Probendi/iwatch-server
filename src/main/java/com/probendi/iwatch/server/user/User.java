package com.probendi.iwatch.server.user;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.bson.Document;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * A user who can send reports to the iWatch server.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
public class User implements Serializable {

    protected String id;
    protected String platform;
    protected String registrationId;
    protected String mobile;
    protected String firstname;
    protected String lastname;
    protected Date dateOfBirth;
    protected String placeOfBirth;
    protected String address;
    protected String cap;
    protected String city;
    protected Location location;
    // the list of new or updated messages to be read
    protected List<String> messages;
    // the list of new or updated reports to be read
    protected List<String> reports;
    protected String municipality;

    /**
     * Creates a new {@code User} object.
     */
    public User() {
    }

    /**
     * Creates a new {@code User} object from the given document.
     *
     * @param doc a {@link Document} instance
     */
    @SuppressWarnings("unchecked")
    public User(final @NotNull Document doc) {
        id = doc.getString("_id");
        platform = doc.getString("platform");
        if (platform == null) {
            platform = "";
        }
        registrationId = doc.getString("registrationId");
        if (registrationId == null) {
            registrationId = "";
        }
        mobile = doc.getString("mobile");
        firstname = doc.getString("firstname");
        lastname = doc.getString("lastname");
        dateOfBirth = doc.getDate("dateOfBirth");
        placeOfBirth = doc.getString("placeOfBirth");
        address = doc.getString("address");
        cap = doc.getString("cap");
        city = doc.getString("city");
        if (doc.get("location") != null) {
            location = new Location(doc.get("location", Document.class));
        }
        messages = (List<String>) doc.get("messages");
        if (messages == null) {
            messages = new LinkedList<>();
        }
        reports = (List<String>) doc.get("reports");
        if (reports == null) {
            reports = new LinkedList<>();
        }
        municipality = doc.getString("municipality");
    }

    /**
     * Creates a new {@code User} object from the given builder.
     *
     * @param builder the builder
     */
    private User(final Builder builder) {
        id = builder.id;
        platform = builder.platform;
        registrationId = builder.registrationId;
        mobile = builder.mobile;
        firstname = builder.firstname;
        lastname = builder.lastname;
        dateOfBirth = builder.dateOfBirth;
        placeOfBirth = builder.placeOfBirth;
        address = builder.address;
        cap = builder.cap;
        city = builder.city;
        location = builder.location;
        messages = builder.messages;
        if (messages == null) {
            messages = new LinkedList<>();
        }
        reports = builder.reports;
        if (reports == null) {
            reports = new LinkedList<>();
        }
        municipality = builder.municipality;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(final String platform) {
        this.platform = platform;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(final String registrationId) {
        this.registrationId = registrationId;
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

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(final Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(final String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public String getCap() {
        return cap;
    }

    public void setCap(final String cap) {
        this.cap = cap;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(final Location location) {
        this.location = location;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(final List<String> messages) {
        this.messages = messages;
    }

    public List<String> getReports() {
        return reports;
    }

    public void setReports(final List<String> reports) {
        this.reports = reports;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(final String municipality) {
        this.municipality = municipality;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(platform, user.platform) &&
                Objects.equals(registrationId, user.registrationId) &&
                Objects.equals(mobile, user.mobile) &&
                Objects.equals(firstname, user.firstname) &&
                Objects.equals(lastname, user.lastname) &&
                Objects.equals(dateOfBirth, user.dateOfBirth) &&
                Objects.equals(placeOfBirth, user.placeOfBirth) &&
                Objects.equals(address, user.address) &&
                Objects.equals(cap, user.cap) &&
                Objects.equals(city, user.city) &&
                Objects.equals(location, user.location) &&
                Objects.equals(messages, user.messages) &&
                Objects.equals(reports, user.reports) &&
                Objects.equals(municipality, user.municipality);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, platform, registrationId, mobile, firstname, lastname, dateOfBirth, placeOfBirth,
                address, cap, city, location, messages, reports, municipality);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", platform='" + platform + '\'' +
                ", registrationId='" + registrationId + '\'' +
                ", mobile='" + mobile + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", placeOfBirth='" + placeOfBirth + '\'' +
                ", address='" + address + '\'' +
                ", cap='" + cap + '\'' +
                ", city='" + city + '\'' +
                ", location='" + location + '\'' +
                ", messages='" + messages + '\'' +
                ", reports='" + reports + '\'' +
                ", municipality='" + municipality + '\'' +
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
                .append("platform", platform)
                .append("registrationId", registrationId)
                .append("mobile", mobile)
                .append("firstname", firstname)
                .append("lastname", lastname)
                .append("dateOfBirth", dateOfBirth)
                .append("placeOfBirth", placeOfBirth)
                .append("address", address)
                .append("cap", cap)
                .append("city", city)
                .append("location", location)
                .append("messages", messages)
                .append("reports", reports)
                .append("municipality", municipality);
    }

    /**
     * {@code User} builder static inner class.
     */
    public static class Builder {
        private String id;
        private String platform;
        private String registrationId;
        private String mobile;
        private String firstname;
        private String lastname;
        private Date dateOfBirth;
        private String placeOfBirth;
        private String address;
        private String cap;
        private String city;
        private Location location;
        private List<String> messages;
        private List<String> reports;
        private String municipality;

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
        public Builder platform(final @NotNull String val) {
            platform = val;
            return this;
        }

        @NotNull
        public Builder registrationId(final @NotNull String val) {
            registrationId = val;
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

        public Builder dateOfBirth(final @NotNull Date val) {
            dateOfBirth = val;
            return this;
        }

        public Builder placeOfBirth(final @NotNull String val) {
            placeOfBirth = val;
            return this;
        }

        @NotNull
        public Builder address(final @NotNull String val) {
            address = val;
            return this;
        }

        @NotNull
        public Builder cap(final @NotNull String val) {
            cap = val;
            return this;
        }

        @NotNull
        public Builder city(final @NotNull String val) {
            city = val;
            return this;
        }

        @NotNull
        public Builder location(final @NotNull Location val) {
            location = val;
            return this;
        }

        @NotNull
        public Builder messages(final @NotNull List<String> val) {
            messages = val;
            return this;
        }

        @NotNull
        public Builder reports(final @NotNull List<String> val) {
            reports = val;
            return this;
        }

        @NotNull
        public Builder municipality(final @NotNull String val) {
            municipality = val;
            return this;
        }

        @NotNull
        public User build() {
            return new User(this);
        }
    }
}
