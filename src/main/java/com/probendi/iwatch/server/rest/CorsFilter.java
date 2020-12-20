package com.probendi.iwatch.server.rest;

import java.util.logging.Logger;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

/**
 * Adds the {@code "Access-Control-Allow-Origin"} header for CORS requests.
 *
 * @see javax.ws.rs.container.ContainerResponseFilter
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
@Provider
public class CorsFilter implements ContainerResponseFilter {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        logger.entering(this.getClass().getName(), "filter");

        final MultivaluedMap<String, Object> headers = responseContext.getHeaders();
        headers.add("Access-Control-Allow-Headers", "Authorization, Origin, X-Requested-With, Content-Type");
        headers.add("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE");
        headers.add("Access-Control-Allow-Origin", "*");

        logger.exiting(this.getClass().getName(), "filter");
    }
}
