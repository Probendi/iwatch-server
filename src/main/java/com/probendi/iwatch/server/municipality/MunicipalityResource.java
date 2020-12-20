package com.probendi.iwatch.server.municipality;

import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.probendi.iwatch.server.db.DaoException;
import com.probendi.iwatch.server.db.EntityNotFoundException;
import com.probendi.iwatch.server.rest.AuthorizationRequired;

/**
 * Exposes the business methods of {@link Municipality} through RESTful web services.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
@Path("/municipalities")
public class MunicipalityResource {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @EJB(beanName = "MunicipalityDaoMongoImpl")
    MunicipalityDao municipalityDao;

    /**
     * Handles the HTTP GET requests that return the municipalities sorted by region and province.
     *
     * @return the municipality with the given id
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public GenericEntity<SortedSet<Region>> getMunicipalities() {
        logger.entering(this.getClass().getName(), "getMunicipalities");

        final SortedSet<Region> regions = municipalityDao.findAll();

        // build the response
        logger.exiting(this.getClass().getName(), "getUsers", regions);
        return new GenericEntity<SortedSet<Region>>(regions) {
        };
    }

    /**
     * Handles the HTTP GET requests that return the municipality with the given id.
     *
     * @param id the id path parameter
     * @return the municipality with the given id
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Municipality getMunicipality(final @PathParam("id") String id) {
        logger.entering(this.getClass().getName(), "getMunicipality", id);

        try {
            final Municipality municipality = municipalityDao.find(id);

            // build the response
            logger.exiting(this.getClass().getName(), "getMunicipality", municipality);
            return municipality;
        } catch (final EntityNotFoundException e) {
            final WebApplicationException ex = new NotFoundException();
            logger.throwing(this.getClass().getName(), "getMunicipality", ex);
            throw ex;
        }
    }

    /**
     * Handles the HTTP PUT requests that update municipalities.
     *
     * @param contentRange the {@code "Content-Range"} header parameter
     * @param id           the id path parameter
     * @param municipality the municipality to be updated
     * @return a {@link Response} object
     */
    @PUT
    @AuthorizationRequired
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response update(final @HeaderParam("Content-Range") String contentRange,
                           final @PathParam("id") String id,
                           final Municipality municipality) {
        logger.entering(this.getClass().getName(), "update", new Object[]{id, municipality});

        if (contentRange != null && !contentRange.isEmpty()) {
            final WebApplicationException ex = new BadRequestException();
            logger.throwing(this.getClass().getName(), "update", ex);
            throw ex;
        }

        try {
            municipality.setId(id);
            municipalityDao.update(municipality);

            // build the response
            final CacheControl cacheControl = new CacheControl();
            cacheControl.setNoCache(true);
            final String location = "/municipalities/" + id;
            final Response response = Response.status(Response.Status.NO_CONTENT)
                    .header(HttpHeaders.LOCATION, location)
                    .cacheControl(cacheControl)
                    .build();

            logger.exiting(this.getClass().getName(), "update", response);
            return response;
        } catch (final DaoException e) {
            logger.log(Level.SEVERE, "Failed to update municipality " + id, e);
            final WebApplicationException ex =
                    e instanceof EntityNotFoundException ? new NotFoundException() : new InternalServerErrorException();
            logger.throwing(this.getClass().getName(), "update", ex);
            throw ex;
        }
    }
}
