package com.probendi.iwatch.server.websocket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Singleton;
import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

/**
 * The web socket server.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
@Singleton
@ServerEndpoint("/websocket/reports/{id}")
public class WebSocketServer {

    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Adds the given session to {@link WebSocketServer#sessions}.
     *
     * @param id      the id path parameter
     * @param session the session to be added
     */
    @OnOpen
    public void onOpen(final @PathParam("id") String id, final Session session) {
        logger.entering(this.getClass().getName(), "onOpen", session);

        sessions.add(session);
        logger.info("Session added: " + session + " [id: " + id + "]");

        logger.exiting(this.getClass().getName(), "onOpen");
    }

    /**
     * Removes the given session from {@link WebSocketServer#sessions}.
     *
     * @param session the session to be removed
     */
    @OnClose
    public void onClose(final Session session) {
        logger.entering(this.getClass().getName(), "onClose", session);

        sessions.remove(session);
        logger.info("Session removed: " + session);

        logger.exiting(this.getClass().getName(), "onClose");
    }

    /**
     * Implements a trivial echo service.
     *
     * @param text    the text of the message
     * @param session the remote session
     */
    @OnMessage
    public void onMessage(final String text, final Session session) {
        logger.entering(this.getClass().getName(), "onMessage", text);

        logger.info("Received message \"" + text + "\" from session " + session);
        try {
            session.getBasicRemote().sendText(text);
        } catch (final IOException e) {
            logger.log(Level.SEVERE, "Failed to send message \"" + text + "\" to session " + session, e);
        }

        logger.exiting(this.getClass().getName(), "onMessage");
    }

    /**
     * Sends a message to the open sessions of the given municipality.
     *
     * @param municipality the municipality
     * @param id           the id of the last report which requires to be processed
     * @param count        the number of reports which require to be processed
     */
    public void sendMessage(final String municipality, final String id, final long count) {
        logger.entering(this.getClass().getName(), "sendMessage", new Object[]{municipality, id, count});

        int n = 0;
        final JsonObject json = Json.createObjectBuilder().add("id", id).add("count", count).build();
        for (final Session session : sessions) {
            if (session.getRequestURI().toString().endsWith("/" + municipality)) {
                try {
                    session.getBasicRemote().sendText(json.toString());
                    ++n;
                    logger.log(Level.INFO, "Sent message \"" + json + "\" to session " + session);
                } catch (final IOException e) {
                    logger.log(Level.SEVERE, "Failed to send message \"" + json + "\" to session " + session, e);
                }
            }
        }
        logger.log(Level.INFO, "Sent " + n + " messages");

        logger.exiting(this.getClass().getName(), "sendMessage");
    }
}