package com.probendi.iwatch.server.municipality;

import java.io.Serializable;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

/**
 * A minimized version of a {@link Municipality}.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
public class City implements Comparable<City>, Serializable {

    private String id;
    private String name;

    /**
     * Creates a new {@code City} object.
     */
    public City() {
        this.id = "";
        this.name = "";
    }

    /**
     * Creates a new {@code City} object from the given municipality.
     *
     * @param municipality the municipality
     */
    public City(final @NotNull Municipality municipality) {
        this.id = municipality.getId();
        this.name = municipality.getName();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof City)) return false;
        City city = (City) o;
        return Objects.equals(id, city.id) &&
                Objects.equals(name, city.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public int compareTo(@NotNull City o) {
        return name.compareTo(o.getName());
    }

    @Override
    public String toString() {
        return "City{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
