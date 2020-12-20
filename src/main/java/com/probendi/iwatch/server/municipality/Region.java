package com.probendi.iwatch.server.municipality;

import java.io.Serializable;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jetbrains.annotations.NotNull;

/**
 * A region.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
public class Region implements Comparable<Region>, Serializable {

    private String name;
    private SortedSet<Province> provinces;

    /**
     * Creates a new {@code Region} object.
     */
    public Region() {
        provinces = new TreeSet<>();
    }

    /**
     * Creates a new {@code Region} object with the given name.
     *
     * @param name the name
     */
    public Region(final String name) {
        this.name = name;
        provinces = new TreeSet<>();
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public SortedSet<Province> getProvinces() {
        return provinces;
    }

    public void setProvinces(final SortedSet<Province> provinces) {
        this.provinces = provinces;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Region)) return false;
        Region region = (Region) o;
        return Objects.equals(name, region.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, provinces);
    }

    @Override
    public int compareTo(@NotNull Region o) {
        return name.compareTo(o.getName());
    }

    @Override
    public String toString() {
        return "Region{" +
                "name='" + name + '\'' +
                ", provinces=" + provinces +
                '}';
    }

    /**
     * Adds the given {@code province} to this region.
     *
     * @param province the province to be added
     */
    public void addProvince(final @NotNull Province province) {
        provinces.add(province);
    }
}
