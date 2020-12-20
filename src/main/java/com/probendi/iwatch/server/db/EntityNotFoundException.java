package com.probendi.iwatch.server.db;

/**
 * A {@code MessageNotFoundException} is thrown if no entity was found.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
public class EntityNotFoundException extends DaoException {

    /**
     * Creates a new {@code EntityNotFoundException} object.
     */
    public EntityNotFoundException() {
    }

    /**
     * Creates a new {@code EntityNotFoundException} object with the given detail message.
     *
     * @param message the detail message
     */
    public EntityNotFoundException(final String message) {
        super(message);
    }
}
