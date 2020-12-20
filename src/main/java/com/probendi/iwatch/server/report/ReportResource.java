package com.probendi.iwatch.server.report;

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

import com.probendi.iwatch.server.db.DaoException;
import com.probendi.iwatch.server.db.EntityNotFoundException;
import com.probendi.iwatch.server.jms.producer.MessageProducer;
import com.probendi.iwatch.server.municipality.Municipality;
import com.probendi.iwatch.server.municipality.MunicipalityDao;
import com.probendi.iwatch.server.user.AdministratorDao;
import com.probendi.iwatch.server.user.UserDao;
import com.probendi.iwatch.server.user.Watcher;
import com.probendi.iwatch.server.util.PropertiesReader;
import com.probendi.iwatch.server.util.UploadService;
import com.probendi.iwatch.server.websocket.WebSocketServer;

/**
 * Exposes the business methods of {@link Report} through RESTful web services.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
@Path("/reports")
public class ReportResource {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @EJB(beanName = "MessageProducerApnImpl")
    private MessageProducer apnMessageProducer;

    @EJB(beanName = "MessageProducerFcmImpl")
    private MessageProducer fcmMessageProducer;

    @EJB(beanName = "AdministratorDaoMongoImpl")
    AdministratorDao administratorDao;

    @EJB(beanName = "MunicipalityDaoMongoImpl")
    MunicipalityDao municipalityDao;

    @EJB(beanName = "ReportDaoMongoImpl")
    ReportDao reportDao;

    @EJB(beanName = "UserDaoMongoImpl")
    UserDao userDao;

    @EJB(beanName = "WebSocketServer")
    WebSocketServer webSocketServer;

    /**
     * Handles the HTTP PUT requests that add the given watcher.
     *
     * @param id      the report's id path parameter
     * @param watcher the watcher to be added
     * @return a {@link Response} object
     */
    @PUT
    @Path("/{id}/watchers")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addWatcher(final @PathParam("id") String id, final Watcher watcher) {
        logger.entering(this.getClass().getName(), "addWatcher", new Object[]{id, watcher});

        try {
            final Report report = reportDao.find(id);
            // An administrator can be a watcher only if the administrator is also the creator of the report.
            // In any case, if trying to add an administrator, an EntityNotFoundException will be thrown.
            userDao.find(watcher.getId());
            if (report.getWatchers().contains(watcher)) {
                logger.log(Level.WARNING, "Duplicate watcher " + id);
                final WebApplicationException ex = new WebApplicationException(Response.Status.CONFLICT);
                logger.throwing(this.getClass().getName(), "addWatcher", ex);
                throw ex;
            }
            reportDao.addWatcher(id, watcher);
            if (new PropertiesReader().isSendPushNotification()) {
                final String name = municipalityDao.find(reportDao.find(id).getMunicipality()).getName();
                apnMessageProducer.notifyWatcher(name, watcher.getId(), report.getId(), true, 0, 1);
                fcmMessageProducer.notifyWatcher(name, watcher.getId(), report.getId(), true, 0, 1);
            }
            logger.log(Level.INFO, "Watcher {0} added", watcher);
        } catch (final DaoException e) {
            logger.log(Level.SEVERE, "Failed to add watcher to report " + id, e);
            final WebApplicationException ex =
                    e instanceof EntityNotFoundException ? new NotFoundException() : new InternalServerErrorException();
            logger.throwing(this.getClass().getName(), "addWatcher", ex);
            throw ex;
        }

        // build the response
        final CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        final String location = "/reports/" + id + "/watcher/" + watcher.getId();
        final Response response = Response.status(Response.Status.CREATED)
                .entity(watcher)
                .header(HttpHeaders.LOCATION, location)
                .cacheControl(cacheControl)
                .build();

        logger.exiting(this.getClass().getName(), "addWatcher", response);
        return response;
    }

    /**
     * Handles the HTTP POST requests that create the given activity.
     *
     * @param id       the report's id path parameter
     * @param activity the activity to be created
     * @return a {@link Response} object
     */
    @POST
    @Path("/{id}/activities")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createActivity(final @PathParam("id") String id, final Activity activity) {
        logger.entering(this.getClass().getName(), "createActivity", new Object[]{id, activity});

        try {
            activity.setDate(new Date());
            activity.setMimeType(UploadService.mimeType(activity.getAttachment()));
            reportDao.addActivity(id, activity);
            // if the action has been created by an administrator then the actionRequired field can be cleared
            final boolean actionRequired = activity.getWatcher().isUser();
            reportDao.setActionRequired(id, actionRequired);
            // if the action has been created by an app, then the report shall be reopened if closed
            if (actionRequired) {
                reportDao.reopen(id);
            }
            logger.log(Level.INFO, "Report {0}: activity created", id);

            final boolean sendPushNotification = new PropertiesReader().isSendPushNotification();
            final Report report = reportDao.find(id);
            final String municipality = report.getMunicipality();

            // notify administrators if the activity was created an app
            if (sendPushNotification && actionRequired) {
                final long count = reportDao.countReportsToBeProcessed(municipality);
                webSocketServer.sendMessage(municipality, id, count);
            }

            // notify all mobile watchers
            if (sendPushNotification) {
                final String name = municipalityDao.find(municipality).getName();
                apnMessageProducer.notifyNewActivity(name, report, activity.getWatcher().getId(), 0, 1);
                fcmMessageProducer.notifyNewActivity(name, report, activity.getWatcher().getId(), 0, 1);
            }

            // build the response
            final CacheControl cacheControl = new CacheControl();
            cacheControl.setNoCache(true);
            final String location = "/reports/" + id + "/activities/" + activity.getDate().getTime();
            final Response response = Response.status(Response.Status.CREATED)
                    .entity(activity.getDate().getTime())
                    .header(HttpHeaders.LOCATION, location)
                    .cacheControl(cacheControl)
                    .build();

            logger.exiting(this.getClass().getName(), "createActivity", response);
            return response;
        } catch (final DaoException | IllegalStateException e) {
            logger.log(Level.SEVERE, "Failed to create activity for report " + id, e);
            final WebApplicationException ex =
                    e instanceof EntityNotFoundException ? new NotFoundException() : new InternalServerErrorException();
            logger.throwing(this.getClass().getName(), "createActivity", ex);
            throw ex;
        }
    }

    /**
     * Handles the HTTP POST requests that create the given report.
     *
     * @param report the report to be created
     * @return a {@link Response} object
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createReport(final Report report) {
        logger.entering(this.getClass().getName(), "createReport", report);

        try {
            final Municipality municipality = municipalityDao.find(report.getMunicipality());
            if (!municipality.getFeatures().contains(Municipality.Feature.REPORTING.toString())) {
                throw new DaoException("Reporting not activated");
            }

            final String id = municipalityDao.nextTicketNumber(report.getMunicipality());
            report.setDate(new Date());
            report.setId(id);
            report.setStatus("CREATA");
            // a report MUST have at least one watcher
            final boolean actionRequired = report.getWatchers().get(0).isUser();
            report.setActionRequired(actionRequired);
            report.setMimeType(UploadService.mimeType(report.getAttachment()));

            reportDao.insert(report);
            logger.log(Level.INFO, "Report {0} created", id);
            // notify active administrators only in production and if the report was created by a user
            if (actionRequired && new PropertiesReader().isSendPushNotification()) {
                final int count = reportDao.countReportsToBeProcessed(report.getMunicipality());
                webSocketServer.sendMessage(report.getMunicipality(), id, count);
            }

            // build the response
            final CacheControl cacheControl = new CacheControl();
            cacheControl.setNoCache(true);
            final String location = "/reports/" + id;
            final int index = id.indexOf('_');
            final Response response = Response.status(Response.Status.CREATED)
                    .entity(id.substring(index + 1))
                    .header(HttpHeaders.LOCATION, location)
                    .cacheControl(cacheControl)
                    .build();

            logger.exiting(this.getClass().getName(), "createReport", response);
            return response;
        } catch (final DaoException e) {
            logger.log(Level.SEVERE, "Failed to create report", e);
            final WebApplicationException ex = new InternalServerErrorException();
            logger.throwing(this.getClass().getName(), "createReport", ex);
            throw ex;
        }
    }

    /**
     * Handles the HTTP DELETE requests that delete reports.
     *
     * @param id      the id path parameter
     * @param watcher the watcher to be removed
     * @return a {@link Response} object
     */
    @DELETE
    @Path("/{id}/watchers/{watcher}")
    public Response deleteWatcher(final @PathParam("id") String id, final @PathParam("watcher") String watcher) {
        logger.entering(this.getClass().getName(), "deleteWatcher", new String[]{id, watcher});

        try {
            final Report report = reportDao.find(id);
            reportDao.deleteWatcher(id, watcher);
            if (new PropertiesReader().isSendPushNotification()) {
                final String name = municipalityDao.find(report.getMunicipality()).getName();
                apnMessageProducer.notifyWatcher(name, watcher, report.getId(), false, 0, 1);
                fcmMessageProducer.notifyWatcher(name, watcher, report.getId(), false, 0, 1);
            }
            logger.log(Level.INFO, "Watcher {0} deleted", watcher);

            // build the response
            final CacheControl cacheControl = new CacheControl();
            cacheControl.setNoCache(true);
            final Response response = Response.status(Response.Status.NO_CONTENT).cacheControl(cacheControl).build();

            logger.exiting(this.getClass().getName(), "deleteWatcher", response);
            return response;
        } catch (final DaoException e) {
            logger.log(Level.SEVERE, "Failed to delete watcher " + watcher, e);
            final WebApplicationException ex =
                    e instanceof EntityNotFoundException ? new NotFoundException() : new InternalServerErrorException();
            logger.throwing(this.getClass().getName(), "deleteWatcher", ex);
            throw ex;
        }
    }

    /**
     * Handles the HTTP GET requests that return the report with the given id.
     *
     * @param id the id path parameter
     * @return the report with the given id
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Report getReport(final @PathParam("id") String id) {
        logger.entering(this.getClass().getName(), "getReport", id);

        try {
            final Report report = reportDao.find(id);

            // build the response
            logger.exiting(this.getClass().getName(), "getReport", report);
            return report;
        } catch (final EntityNotFoundException e) {
            final WebApplicationException ex = new NotFoundException();
            logger.throwing(this.getClass().getName(), "getReport", ex);
            throw ex;
        }
    }

    /**
     * Handles the HTTP GET requests that return the reports of the given municipality.
     *
     * @param municipality the id the municipality of the reports to be selected
     * @param watcher      a watcher of the report
     * @param status       the status of the reports to be selected
     * @return the reports of the given municipality
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public GenericEntity<List<Report>> getReports(final @DefaultValue("") @QueryParam("municipality") String municipality,
                                                  final @DefaultValue("") @QueryParam("watcher") String watcher,
                                                  final @DefaultValue("") @QueryParam("status") String status) {
        logger.entering(this.getClass().getName(), "getReports", new String[]{municipality, watcher, status});

        final List<Report> reports = reportDao.findAll(municipality, watcher, status);

        // build the response
        logger.exiting(this.getClass().getName(), "getReports", reports);
        return new GenericEntity<List<Report>>(reports) {
        };
    }

    /**
     * Handles the HTTP PUT requests that update reports.
     *
     * @param contentRange the {@code "Content-Range"} header parameter
     * @param id           the id code path parameter
     * @param report       the report to be updated
     * @return a {@link Response} object
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response updateReport(final @HeaderParam("Content-Range") String contentRange,
                                 final @PathParam("id") String id,
                                 final Report report) {
        logger.entering(this.getClass().getName(), "updateReport", new Object[]{id, report});

        if (contentRange != null && !contentRange.isEmpty()) {
            final WebApplicationException ex = new BadRequestException();
            logger.throwing(this.getClass().getName(), "updateReport", ex);
            throw ex;
        }

        try {
            report.setId(id);
            // only administrator can update reports, hence the actionRequired field can be cleared
            report.setActionRequired(false);
            reportDao.update(report);

            // build the response
            final CacheControl cacheControl = new CacheControl();
            cacheControl.setNoCache(true);
            final String location = "/reports/" + id;
            final Response response = Response.status(Response.Status.NO_CONTENT)
                    .header(HttpHeaders.LOCATION, location)
                    .cacheControl(cacheControl)
                    .build();

            logger.exiting(this.getClass().getName(), "updateReport", response);
            return response;
        } catch (final DaoException | IllegalStateException e) {
            logger.log(Level.SEVERE, "Failed to update report " + id, e);
            final WebApplicationException ex =
                    e instanceof EntityNotFoundException ? new NotFoundException() : new InternalServerErrorException();
            logger.throwing(this.getClass().getName(), "updateReport", ex);
            throw ex;
        }
    }
}

