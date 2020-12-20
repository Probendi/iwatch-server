package com.probendi.iwatch.server.message;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.probendi.iwatch.server.db.DaoException;
import com.probendi.iwatch.server.db.EntityNotFoundException;

/**
 * Data Access Object for a {@link Message}.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
public interface MessageDao {

    /**
     * Deletes the message with the given id.
     *
     * @param id the message's id
     * @throws DaoException if the message could not be deleted
     */
    void delete(final @NotNull String id) throws DaoException;

    /**
     * Deletes all messages.
     *
     * @throws DaoException if the messages could not be deleted
     */
    void deleteAll() throws DaoException;

    /**
     * Returns the message with the given id.
     *
     * @param id the message's id
     * @return the message with the given id
     * @throws EntityNotFoundException if no message was found. The id is returned in the exception's message.
     */
    Message find(final @NotNull String id) throws EntityNotFoundException;

    /**
     * Returns the messages which match the given criteria.
     *
     * @param municipality the municipality's id
     * @param interest     the message's interest
     * @param user         the user's id
     * @return the message which match the given criteria
     */
    List<Message> find(final @NotNull String municipality, final @NotNull String interest, final @NotNull String user);

    /**
     * Returns all messages.
     *
     * @return all messages
     */
    List<Message> findAll();

    /**
     * Inserts the given message.
     *
     * @param message the message to be inserted
     * @return the id of the message inserted
     * @throws DaoException if the message could not be inserted
     */
    String insert(final @NotNull Message message) throws DaoException;
}
