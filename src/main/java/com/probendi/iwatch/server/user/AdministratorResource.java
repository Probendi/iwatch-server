package com.probendi.iwatch.server.user;

import java.util.Date;
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

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import com.probendi.iwatch.server.db.DaoException;
import com.probendi.iwatch.server.db.EntityNotFoundException;
import com.probendi.iwatch.server.municipality.MunicipalityDao;
import com.probendi.iwatch.server.report.ReportDao;
import com.probendi.iwatch.server.rest.ApplicationConfig;
import com.probendi.iwatch.server.rest.AuthorizationRequired;

/**
 * Exposes the business methods of {@link Administrator} through RESTful web services.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
@Path("/")
public class AdministratorResource {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @EJB(beanName = "AdministratorDaoMongoImpl")
    AdministratorDao administratorDao;

    @EJB(beanName = "MunicipalityDaoMongoImpl")
    MunicipalityDao municipalityDao;

    @EJB(beanName = "ReportDaoMongoImpl")
    ReportDao reportDao;

    /**
     * Handles the HTTP POST requests that authenticate administrators.
     *
     * @param administrator the administrator to be authenticated
     * @return {@code 200 "OK"} if the administrator was authenticated, or {@code 401 "Unauthorized"} otherwise
     */
    @POST
    @Path("/authentication")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Token authentication(Administrator administrator) {
        logger.entering(this.getClass().getName(), "authentication", administrator);

        if (administrator.getId() == null) {
            final WebApplicationException ex = new NotAuthorizedException(Response.Status.UNAUTHORIZED);
            logger.throwing(this.getClass().getName(), "authentication", ex);
            throw ex;
        }

        try {
            administrator = administratorDao.find(administrator.getId(), administrator.getPassword());
            final long tokenValidity = municipalityDao.find(administrator.getMunicipality()).getTokenValidity() * 1000;
            final int count = reportDao.countReportsToBeProcessed(administrator.getMunicipality());
            logger.log(Level.INFO, "Administrator {0} authenticated", administrator.getId());

            // issue a new token
            final String jwt = Jwts.builder()
                    .setIssuer("iwatch-server")
                    .setSubject(administrator.getId())
                    .setAudience(administrator.getMunicipality())
                    .claim("superuser", administrator.isSuperuser())
                    .claim("firstname", administrator.getFirstname())
                    .claim("lastname", administrator.getLastname())
                    .claim("count", count)
                    .setExpiration(new Date(System.currentTimeMillis() + tokenValidity))
                    .setIssuedAt(new Date())
                    .signWith(SignatureAlgorithm.HS512, ApplicationConfig.getKey())
                    .compact();
            final Token token = Token.newBuilder().access_token(jwt).expires_in(tokenValidity / 1000).id_token(jwt).build();

            logger.exiting(this.getClass().getName(), "authentication", token);
            return token;
        } catch (final EntityNotFoundException e) {
            final WebApplicationException ex = new NotAuthorizedException(Response.Status.UNAUTHORIZED);
            logger.throwing(this.getClass().getName(), "authentication", ex);
            throw ex;
        }
    }

    /**
     * Handles the HTTP DELETE requests that delete administrators.
     *
     * @param id the id path parameter
     * @return a {@link Response} object
     */
    @DELETE
    @AuthorizationRequired
    @Path("/administrators/{id}")
    public Response delete(final @PathParam("id") String id) {
        logger.entering(this.getClass().getName(), "delete", id);

        try {
            administratorDao.delete(id);
            logger.log(Level.INFO, "Administrator {0} deleted", id);

            // build the response
            final CacheControl cacheControl = new CacheControl();
            cacheControl.setNoCache(true);
            final Response response = Response.status(Response.Status.NO_CONTENT).cacheControl(cacheControl).build();

            logger.exiting(this.getClass().getName(), "delete", response);
            return response;
        } catch (final DaoException e) {
            logger.log(Level.SEVERE, "Failed to delete administrator " + id, e);
            final WebApplicationException ex = new InternalServerErrorException();
            logger.throwing(this.getClass().getName(), "delete", ex);
            throw ex;
        }
    }

    /**
     * Handles the HTTP GET requests that return the administrator with the given id.
     *
     * @param id the id path parameter
     * @return the administrator with the given id
     */
    @GET
    @AuthorizationRequired
    @Path("/administrators/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Administrator getAdministrator(final @PathParam("id") String id) {
        logger.entering(this.getClass().getName(), "getAdministrator", id);

        try {
            final Administrator administrator = administratorDao.find(id);

            // build the response
            logger.exiting(this.getClass().getName(), "getAdministrator", administrator);
            return administrator;
        } catch (final EntityNotFoundException e) {
            final WebApplicationException ex = new NotFoundException();
            logger.throwing(this.getClass().getName(), "getAdministrator", ex);
            throw ex;
        }
    }

    /**
     * Handles the HTTP GET requests that return the administrators of the given municipality.
     *
     * @param municipality the id of the municipality of the messages to be selected
     * @return the messages which match the given criteria
     */
    @GET
    @Path("/administrators")
    @Produces(MediaType.APPLICATION_JSON)
    public GenericEntity<List<Administrator>> getAdministrators(
            final @DefaultValue("") @QueryParam("municipality") String municipality) {

        logger.entering(this.getClass().getName(), "getAdministrators", municipality);

        final List<Administrator> administrators = administratorDao.findAll(municipality);

        // build the response
        logger.exiting(this.getClass().getName(), "getAdministrators", administrators);
        return new GenericEntity<List<Administrator>>(administrators) {
        };
    }

    /**
     * Handles the HTTP PUT requests that create or update administrators, i.e. their password is changed.
     *
     * @param contentRange  the {@code "Content-Range"} header parameter
     * @param id            the id path parameter
     * @param administrator the administrator to be created or updated
     * @return a {@link Response} object
     */
    @PUT
    @AuthorizationRequired
    @Path("/administrators/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response upsert(final @HeaderParam("Content-Range") String contentRange,
                           final @PathParam("id") String id,
                           final Administrator administrator) {
        logger.entering(this.getClass().getName(), "upsert", new Object[]{id, administrator});

        if (contentRange != null && !contentRange.isEmpty()) {
            final WebApplicationException ex = new BadRequestException();
            logger.throwing(this.getClass().getName(), "upsert", ex);
            throw ex;
        }

        // if the administrator already exists then it is completely replaced, otherwise it is created
        administrator.setId(id);

        Response.Status status;
        try {
            try {
                final Administrator administratorFound = administratorDao.find(id);
                if (!administratorFound.getMunicipality().equals(administrator.getMunicipality())) {
                    logger.info("Administrator " + id + " already exists");
                    final WebApplicationException ex = new WebApplicationException(Response.Status.CONFLICT);
                    logger.throwing(this.getClass().getName(), "upsert", ex);
                    throw ex;
                }
                status = Response.Status.NO_CONTENT;
                administratorDao.update(administrator);
                logger.log(Level.INFO, "Administrator {0} updated", id);
            } catch (final EntityNotFoundException ignore) {
                status = Response.Status.CREATED;
                administratorDao.insert(administrator);
                logger.log(Level.INFO, "Administrator {0} created", id);
            }
        } catch (final DaoException e) {
            logger.log(Level.SEVERE, "Failed to upsert administrator {0}", id);
            final WebApplicationException ex = new InternalServerErrorException();
            logger.throwing(this.getClass().getName(), "upsert", ex);
            throw ex;
        }

        // build the response
        final CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        final String location = "/administrators/" + id;
        final Response response = Response.status(status)
                .header(HttpHeaders.LOCATION, location)
                .cacheControl(cacheControl)
                .build();

        logger.exiting(this.getClass().getName(), "upsert", response);
        return response;
    }
}

