package com.probendi.iwatch.server.user;

import java.util.List;
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
import com.probendi.iwatch.server.municipality.Municipality;
import com.probendi.iwatch.server.municipality.MunicipalityDao;
import com.probendi.iwatch.server.rest.AuthorizationRequired;

/**
 * Exposes the business methods of {@link User} through RESTful web services.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
@Path("/")
public class UserResource {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @EJB(beanName = "MunicipalityDaoMongoImpl")
    MunicipalityDao municipalityDao;

    @EJB(beanName = "UserDaoMongoImpl")
    UserDao userDao;

    /**
     * Handles the HTTP POST requests that create the given location.
     *
     * @param id       the user's id path parameter
     * @param location the location to be created
     * @return a {@link Response} object
     */
    @POST
    @Path("/users/{id}/locations")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createLocation(final @PathParam("id") String id, final Location location) {
        logger.entering(this.getClass().getName(), "create", location);

        try {
            userDao.setLocation(id, location);

            // build the response
            final CacheControl cacheControl = new CacheControl();
            cacheControl.setNoCache(true);
            final String loc = "/users/" + id + "/activities/" + location.getDate().getTime();
            final Response response = Response.status(Response.Status.CREATED)
                    .entity(location.getDate().getTime())
                    .header(HttpHeaders.LOCATION, loc)
                    .cacheControl(cacheControl)
                    .build();

            logger.exiting(this.getClass().getName(), "create", response);
            return response;
        } catch (final DaoException | IllegalStateException e) {
            logger.log(Level.SEVERE, "Failed to create location for user " + id, e);
            final WebApplicationException ex =
                    e instanceof EntityNotFoundException ? new NotFoundException() : new InternalServerErrorException();
            logger.throwing(this.getClass().getName(), "create", ex);
            throw ex;
        }
    }
    /**
     * Handles the HTTP DELETE requests that delete users.
     *
     * @param id the id path parameter
     * @return a {@link Response} object
     */
    @DELETE
    @Path("/users/{id}")
    public Response delete(final @PathParam("id") String id) {
        logger.entering(this.getClass().getName(), "delete", id);

        try {
            userDao.delete(id);
            logger.log(Level.INFO, "User {0} deleted", id);

            // build the response
            final CacheControl cacheControl = new CacheControl();
            cacheControl.setNoCache(true);
            final Response response = Response.status(Response.Status.NO_CONTENT).cacheControl(cacheControl).build();

            logger.exiting(this.getClass().getName(), "delete", response);
            return response;
        } catch (final DaoException e) {
            logger.log(Level.SEVERE, "Failed to delete user " + id, e);
            final WebApplicationException ex = new InternalServerErrorException();
            logger.throwing(this.getClass().getName(), "delete", ex);
            throw ex;
        }
    }

    /**
     * Handles the HTTP DELETE requests that delete a message from the list of unread messages.
     *
     * @param id      the id path parameter
     * @param message the message path parameter
     * @return a {@link Response} object
     */
    @DELETE
    @Path("/users/{id}/messages/{message}")
    public Response deleteMessage(final @PathParam("id") String id, final @PathParam("message") String message) {
        logger.entering(this.getClass().getName(), "deleteMessage", new String[]{id, message});

        try {
            userDao.deleteMessage(id, message);
            logger.log(Level.INFO, "User {0}: message {1} deleted", new String[]{id, message});

            // build the response
            final CacheControl cacheControl = new CacheControl();
            cacheControl.setNoCache(true);
            final Response response = Response.status(Response.Status.NO_CONTENT).cacheControl(cacheControl).build();

            logger.exiting(this.getClass().getName(), "deleteMessage", response);
            return response;
        } catch (final DaoException e) {
            logger.log(Level.SEVERE, "Failed to delete message " + message + " of users " + id, e);
            final WebApplicationException ex = new InternalServerErrorException();
            logger.throwing(this.getClass().getName(), "deleteMessage", ex);
            throw ex;
        }
    }

    /**
     * Handles the HTTP DELETE requests that delete a message from the list of unread messages.
     *
     * @param id     the id path parameter
     * @param report the report path parameter
     * @return a {@link Response} object
     */
    @DELETE
    @Path("/users/{id}/reports/{report}")
    public Response deleteReport(final @PathParam("id") String id, final @PathParam("report") String report) {
        logger.entering(this.getClass().getName(), "deleteReport", new String[]{id, report});

        try {
            userDao.deleteReport(id, report);
            logger.log(Level.INFO, "User {0}: report {1} deleted", new String[]{id, report});

            // build the response
            final CacheControl cacheControl = new CacheControl();
            cacheControl.setNoCache(true);
            final Response response = Response.status(Response.Status.NO_CONTENT).cacheControl(cacheControl).build();

            logger.exiting(this.getClass().getName(), "deleteReport", response);
            return response;
        } catch (final DaoException e) {
            logger.log(Level.SEVERE, "Failed to delete report " + report + " of users " + id, e);
            final WebApplicationException ex = new InternalServerErrorException();
            logger.throwing(this.getClass().getName(), "deleteReport", ex);
            throw ex;
        }
    }

    /**
     * Handles the HTTP GET requests that return the {@link AppUser} with the given id.
     *
     * @param id the id path parameter
     * @return the user with the given id
     */
    @GET
    @Path("/app/users/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppUser getAppUser(final @PathParam("id") String id) {
        logger.entering(this.getClass().getName(), "getAppUser", id);

        try {
            final AppUser user = new AppUser(userDao.find(id));
            final Municipality municipality = municipalityDao.find(user.getMunicipality());
            user.setHeader(municipality.getHeader());
            user.setLogo(municipality.getLogo());
            user.setFeatures(municipality.getFeatures());

            // build the response
            logger.exiting(this.getClass().getName(), "getAppUser", user);
            return user;
        } catch (final EntityNotFoundException e) {
            final WebApplicationException ex = new NotFoundException();
            logger.throwing(this.getClass().getName(), "get", ex);
            throw ex;
        }
    }

    /**
     * Handles the HTTP GET requests that return the user with the given id.
     *
     * @param id the id path parameter
     * @return the user with the given id
     */
    @GET
    @Path("/users/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUser(final @PathParam("id") String id) {
        logger.entering(this.getClass().getName(), "getUser", id);

        try {
            final User user = userDao.find(id);

            // build the response
            logger.exiting(this.getClass().getName(), "getUser", user);
            return user;
        } catch (final EntityNotFoundException e) {
            final WebApplicationException ex = new NotFoundException();
            logger.throwing(this.getClass().getName(), "getUser", ex);
            throw ex;
        }
    }

    /**
     * Handles the HTTP GET requests that return the users of the given municipality.
     *
     * @param municipality the id the municipality of the users to be selected
     * @return the reports of the given municipality
     */
    @GET
    @Path("/users")
    @AuthorizationRequired
    @Produces(MediaType.APPLICATION_JSON)
    public GenericEntity<List<User>> getUsers(final @DefaultValue("") @QueryParam("municipality") String municipality) {
        logger.entering(this.getClass().getName(), "getUsers", municipality);

        final List<User> reports = userDao.findAll(municipality);

        // build the response
        logger.exiting(this.getClass().getName(), "getUsers", reports);
        return new GenericEntity<List<User>>(reports) {
        };
    }

    /**
     * Handles the HTTP PUT requests that upsert users.
     *
     * @param contentRange the {@code "Content-Range"} header parameter
     * @param id           the id path parameter
     * @param user         the user to be upserted
     * @return a {@link Response} object
     */
    @PUT
    @Path("/users/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response upsert(final @HeaderParam("Content-Range") String contentRange,
                           final @PathParam("id") String id,
                           final User user) {
        logger.entering(this.getClass().getName(), "upsert", new Object[]{id, user});

        if (contentRange != null && !contentRange.isEmpty()) {
            final WebApplicationException ex = new BadRequestException();
            logger.throwing(this.getClass().getName(), "upsert", ex);
            throw ex;
        }

        try {
            final boolean updated = userDao.upsert(user);
            final Municipality municipality = municipalityDao.find(user.getMunicipality());
            logger.log(Level.INFO, "User {0} upserted", id);

            // build the response
            final CacheControl cacheControl = new CacheControl();
            cacheControl.setNoCache(true);
            final String location = "/users/" + id;
            final Response response = Response.status(updated ? Response.Status.OK : Response.Status.CREATED)
                    .header(HttpHeaders.LOCATION, location)
                    .cacheControl(cacheControl)
                    .entity(municipality)
                    .build();

            logger.exiting(this.getClass().getName(), "upsert", response);
            return response;
        } catch (final DaoException e) {
            logger.log(Level.SEVERE, "Failed to upsert user {0}", id);
            final WebApplicationException ex = new InternalServerErrorException();
            logger.throwing(this.getClass().getName(), "upsert", ex);
            throw ex;
        }
    }
}
