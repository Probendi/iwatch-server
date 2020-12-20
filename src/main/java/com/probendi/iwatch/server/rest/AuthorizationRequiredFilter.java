package com.probendi.iwatch.server.rest;

import java.util.logging.Logger;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

/**
 * The filter which ensures that only user with a valid JWT can access restricted RESTful end points.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
@Provider
@AuthorizationRequired
public class AuthorizationRequiredFilter implements ContainerRequestFilter {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void filter(ContainerRequestContext requestContext) {
        logger.entering(this.getClass().getName(), "filter", requestContext);

        final String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            final NotAuthorizedException ex = new NotAuthorizedException("Authorization header is missing or is invalid");
            logger.throwing(this.getClass().getName(), "filter", ex);
            throw ex;
        }
        final String token = authorizationHeader.substring("Bearer".length()).trim();
        try {
            Jwts.parser().setSigningKey(ApplicationConfig.getKey()).parseClaimsJws(token).getBody();
        } catch (final JwtException | IllegalArgumentException e) {
            final NotAuthorizedException ex = new NotAuthorizedException("Invalid token");
            logger.throwing(this.getClass().getName(), "filter", ex);
            throw ex;
        }

        logger.exiting(this.getClass().getName(), "filter");
    }
}
