package com.probendi.iwatch.server.user;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.probendi.iwatch.server.db.DaoException;
import com.probendi.iwatch.server.db.EntityNotFoundException;
import com.probendi.iwatch.server.jms.consumer.Platform;
import com.probendi.iwatch.server.message.Message;
import com.probendi.iwatch.server.report.Report;

/**
 * Data Access Object for a {@link User}.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
public interface UserDao {

    /**
     * Adds a message to the list of unread messages of its recipients.
     *
     * @param message the message to be added
     */
    void addMessage(final @NotNull Message message);

    /**
     * Adds a report to the list of unseen reports of its watchers.
     *
     * @param report the report to be added
     */
    void addReport(final @NotNull Report report);

    /**
     * Adds a report to the list of unseen reports of the given watcher.
     *
     * @param report  the report to be added
     * @param watcher the watcher's id
     */
    void addReport(final @NotNull String report, final @NotNull String watcher);

    /**
     * Deletes the user with the given id.
     *
     * @param id the user's id
     * @throws DaoException if the user could not be deleted
     */
    void delete(final @NotNull String id) throws DaoException;

    /**
     * Deletes all users.
     *
     * @throws DaoException if the users could not be deleted
     */
    void deleteAll() throws DaoException;

    /**
     * Deletes a message from the list of unread messages of all users.
     *
     * @param message the  message to be deleted
     * @throws DaoException if the message could not be deleted
     */
    void deleteMessage(final @NotNull String message) throws DaoException;

    /**
     * Deletes a message from the list of unread messages of the given user.
     *
     * @param id      the user's id
     * @param message the message to be deleted
     * @throws DaoException if the message could not be deleted
     */
    void deleteMessage(final @NotNull String id, final @NotNull String message) throws DaoException;

    /**
     * Deletes a report from the list if unseen reports of the given user.
     *
     * @param report  the report to be deleted
     * @param watcher the watcher to be notified
     * @throws DaoException if the report could not be deleted
     */
    void deleteReport(final @NotNull String report, final @NotNull String watcher) throws DaoException;

    /**
     * Returns the user with the given id.
     *
     * @param id the user's id
     * @return the user with the given id
     * @throws EntityNotFoundException if no user was found. The id is returned in the exception's message.
     */
    User find(final @NotNull String id) throws EntityNotFoundException;

    /**
     * Returns all users.
     *
     * @return all users
     */
    List<User> findAll();

    /**
     * Returns all users of the given municipality.
     *
     * @param municipality the municipality's id
     * @return all users of the given municipality
     */
    List<User> findAll(final @NotNull String municipality);

    /**
     * Returns the registrationIds of the recipient of the given message for the given platform.
     *
     * @param message  the message
     * @param platform the mobile's device platform
     * @return the registrationIds of the given municipality and platform
     */
    List<String> findRecipients(final @NotNull Message message, final @NotNull Platform platform);

    /**
     * Returns the registrationIds of the watchers of the given report for the given platform.
     *
     * @param report   the report
     * @param platform the mobile's device platform
     * @param watcher  the id of the watcher who created the activity
     * @return the registrationIds of the given municipality and platform
     */
    List<String> findRecipients(final @NotNull Report report, final @NotNull String watcher, final @NotNull Platform platform);

    /**
     * Sets the location to the given user.
     *
     * @param id       the user's id
     * @param location the location to be added
     * @throws DaoException if the location could not be added
     */
    void setLocation(final @NotNull String id, final @NotNull Location location) throws DaoException;

    /**
     * Upserts the given user.
     *
     * @param user the user to be upserted
     * @return {@code true} if the user was updated of {@code false} if it was inserted
     * @throws DaoException if the user could not be upserted
     */
    boolean upsert(final @NotNull User user) throws DaoException;
}
