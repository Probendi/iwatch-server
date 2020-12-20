package com.probendi.iwatch.server.user;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.probendi.iwatch.server.db.DaoException;
import com.probendi.iwatch.server.db.EntityNotFoundException;

/**
 * Data Access Object for a {@link Administrator}.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
public interface AdministratorDao {

    /**
     * Deletes the administrator with the given id.
     *
     * @param id the administrator's id
     * @throws DaoException if the administrator could not be deleted
     */
    void delete(final @NotNull String id) throws DaoException;

    /**
     * Deletes all administrators.
     *
     * @throws DaoException if the administrators could not be deleted
     */
    void deleteAll() throws DaoException;

    /**
     * Returns the administrator with the given id.
     *
     * @param id the administrator's id
     * @return the administrator with the given id
     * @throws EntityNotFoundException if no administrator was found
     */
    Administrator find(final @NotNull String id) throws EntityNotFoundException;

    /**
     * Returns the administrator with the given credentials.
     *
     * @param id       the administrator's id
     * @param password the administrator's password
     * @return the administrator with the given credentials
     * @throws EntityNotFoundException if no administrator was found
     */
    Administrator find(final @NotNull String id, final @NotNull String password) throws EntityNotFoundException;

    /**
     * Returns all administrators.
     *
     * @return all administrators
     */
    List<Administrator> findAll();

    /**
     * Returns the administrators of the given municipality.
     *
     * @param municipality the municipality
     * @return the administrators wof the given municipality
     * =
     */
    List<Administrator> findAll(final @NotNull String municipality);

    /**
     * Inserts the given administrator.
     *
     * @param administrator the administrator to be inserted
     * @throws DaoException if the administrator could not be inserted
     */
    void insert(final @NotNull Administrator administrator) throws DaoException;

    /**
     * Sets the registrationId of the given administrator.
     *
     * @param id             the administrator's id
     * @param registrationId the administrator's registrationId
     * @throws EntityNotFoundException if no administrator was found
     */
    void setRegistrationId(final @NotNull String id, final @NotNull String registrationId) throws DaoException;

    /**
     * Updates the given administrator.
     *
     * @param administrator the administrator to be updated
     * @throws DaoException if the administrator could not be updated
     */
    void update(final @NotNull Administrator administrator) throws DaoException;
}
