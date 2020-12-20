package com.probendi.iwatch.server.db;

/**
 * A {@code DaoException} is thrown if a DAO operation fails.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
public class DaoException extends Exception {

    /**
     * Creates a new {@code DaoException} object.
     */
    public DaoException() {
    }

    /**
     * Creates a new {@code DaoException} object with the given detail message.
     *
     * @param message the detail message
     */
    public DaoException(final String message) {
        super(message);
    }
}
