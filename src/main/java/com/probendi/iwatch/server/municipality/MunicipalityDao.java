package com.probendi.iwatch.server.municipality;

import java.util.SortedSet;

import org.jetbrains.annotations.NotNull;

import com.probendi.iwatch.server.db.DaoException;
import com.probendi.iwatch.server.db.EntityNotFoundException;

/**
 * Data Access Object for a {@link Municipality}.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
public interface MunicipalityDao {

    /**
     * Deletes all municipalities.
     *
     * @throws DaoException if the municipalities could not be deleted
     */
    void deleteAll() throws DaoException;

    /**
     * Returns the municipality with the given id.
     *
     * @param id the municipality's id
     * @return the message with the given id
     * @throws EntityNotFoundException if no municipality was found
     */
    Municipality find(final @NotNull String id) throws EntityNotFoundException;

    /**
     * Returns all municipalities.
     *
     * @return all municipalities
     */
    SortedSet<Region> findAll();

    /**
     * Returns the next ticket number of the municipality with the given id.
     *
     * @param id the municipality's id
     * @return the next ticket number of the municipality with the given id
     * @throws EntityNotFoundException if no municipality was found
     * @throws DaoException            if the next ticket could not be generated
     */
    String nextTicketNumber(final @NotNull String id) throws DaoException;

    /**
     * Inserts the given municipality.
     *
     * @param municipality the municipality to be inserted
     * @throws DaoException if the municipality could not be inserted
     */
    void insert(final @NotNull Municipality municipality) throws DaoException;

    /**
     * Update the given municipality.
     *
     * @param municipality the municipality to be updated
     * @throws DaoException            if the municipality could not be updated
     * @throws EntityNotFoundException if no message was found
     */
    void update(final @NotNull Municipality municipality) throws DaoException;
}
