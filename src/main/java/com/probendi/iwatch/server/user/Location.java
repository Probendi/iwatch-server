package com.probendi.iwatch.server.user;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import org.bson.Document;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * The location details of a {@link User}.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
public class Location implements Comparable<Location>, Serializable {

    protected Date date;
    protected double latitude;
    protected double longitude;
    protected double altitude;
    protected double accuracy;
    protected double altitudeAccuracy;
    protected double heading;
    protected double speed;

    /**
     * Creates a new {@code Location} object.
     */
    public Location() {
    }

    /**
     * Creates a new {@code Location} object from the given document.
     *
     * @param doc a {@link Document} instance
     */
    public Location(final @NotNull Document doc) {
        date = doc.getDate("date");
        latitude = doc.getDouble("latitude");
        longitude = doc.getDouble("longitude");
        altitude = doc.getDouble("altitude");
        accuracy = doc.getDouble("accuracy");
        altitudeAccuracy = doc.getDouble("altitudeAccuracy");
        heading = doc.getDouble("heading");
        speed = doc.getDouble("speed");
    }

    /**
     * Creates a new {@code Location} object from the given builder.
     *
     * @param builder the builder
     */
    private Location(final Builder builder) {
        date = builder.date;
        latitude = builder.latitude;
        longitude = builder.longitude;
        altitude = builder.altitude;
        accuracy = builder.accuracy;
        altitudeAccuracy = builder.altitudeAccuracy;
        heading = builder.heading;
        speed = builder.speed;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(final double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(final double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(final double altitude) {
        this.altitude = altitude;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(final double accuracy) {
        this.accuracy = accuracy;
    }

    public double getAltitudeAccuracy() {
        return altitudeAccuracy;
    }

    public void setAltitudeAccuracy(final double altitudeAccuracy) {
        this.altitudeAccuracy = altitudeAccuracy;
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(final double heading) {
        this.heading = heading;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(final double speed) {
        this.speed = speed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        Location location = (Location) o;
        return Double.compare(location.latitude, latitude) == 0 &&
                Double.compare(location.longitude, longitude) == 0 &&
                Double.compare(location.altitude, altitude) == 0 &&
                Double.compare(location.accuracy, accuracy) == 0 &&
                Double.compare(location.altitudeAccuracy, altitudeAccuracy) == 0 &&
                Double.compare(location.heading, heading) == 0 &&
                Double.compare(location.speed, speed) == 0 &&
                Objects.equals(date, location.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, latitude, longitude, altitude, accuracy, altitudeAccuracy, heading, speed);
    }

    @Override
    public String toString() {
        return "Location{" +
                "date=" + date +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                ", accuracy=" + accuracy +
                ", altitudeAccuracy=" + altitudeAccuracy +
                ", heading=" + heading +
                ", speed=" + speed +
                '}';
    }

    /**
     * Helper method for sorting a collection of {@code Location} objects from the newest one to the oldest one.
     *
     * @param   o the {@code Location} object to be compared.
     * @return  a negative integer, zero, or a positive integer as this object
     *          is less than, equal to, or greater than the specified {@code Location} object.
     *
     * @throws NullPointerException if the specified {@code Location} object is null
     */
    @Override
    public int compareTo(@NotNull Location o) {
        return o.date.compareTo(date);
    }

    /**
     * Returns the representation of this contact as a {@link Document}.
     *
     * @return the representation of this contact as a {@link Document}
     */
    public Document toDocument() {
        return new Document("date", date).append("latitude", latitude).append("longitude", longitude)
                .append("altitude", altitude).append("accuracy", accuracy).append("altitudeAccuracy", altitudeAccuracy)
                .append("heading", heading).append("speed", speed);
    }

    /**
     * {@code Contact} builder static inner class.
     */
    public static class Builder {
        private Date date;
        private double latitude;
        private double longitude;
        private double altitude;
        private double accuracy;
        private double altitudeAccuracy;
        private double heading;
        private double speed;

        protected Builder() {
        }

        public Builder date(final @NotNull Date val) {
            date = val;
            return this;
        }

        public Builder latitude(final double val) {
            latitude = val;
            return this;
        }

        public Builder longitude(final double val) {
            longitude = val;
            return this;
        }

        public Builder altitude(final double val) {
            altitude = val;
            return this;
        }

        public Builder accuracy(final double val) {
            accuracy = val;
            return this;
        }

        public Builder altitudeAccuracy(final double val) {
            altitudeAccuracy = val;
            return this;
        }

        public Builder heading(final double val) {
            heading = val;
            return this;
        }

        public Builder speed(final double val) {
            speed = val;
            return this;
        }

        @Contract(" -> !null")
        public Location build() {
            return new Location(this);
        }
    }
}
