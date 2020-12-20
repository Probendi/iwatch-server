package com.probendi.iwatch.server.municipality;

import java.io.Serializable;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jetbrains.annotations.NotNull;

/**
 * A province.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
public class Province implements Comparable<Province>, Serializable {

    private String name;
    private SortedSet<City> cities;

    /**
     * Creates a new {@code Province} object.
     */
    public Province() {
        cities = new TreeSet<>();
    }

    /**
     * Creates a new {@code Province} object with the given name.
     *
     * @param name the name
     */
    public Province(final String name) {
        this.name = name;
        cities = new TreeSet<>();
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public SortedSet<City> getCities() {
        return cities;
    }

    public void setCities(final SortedSet<City> cities) {
        this.cities = cities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Province)) return false;
        Province province = (Province) o;
        return Objects.equals(name, province.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, cities);
    }

    @Override
    public int compareTo(@NotNull Province o) {
        return name.compareTo(o.getName());
    }

    @Override
    public String toString() {
        return "Province{" +
                "name='" + name + '\'' +
                ", cities=" + cities +
                '}';
    }

    /**
     * Adds the given {@code city} to this province.
     *
     * @param city the city to be added
     */
    public void addCity(final @NotNull City city) {
        cities.add(city);
    }
}
