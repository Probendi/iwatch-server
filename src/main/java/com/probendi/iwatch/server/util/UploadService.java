package com.probendi.iwatch.server.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.imgscalr.Scalr;
import org.jetbrains.annotations.NotNull;

/**
 * The RESTful web service for managing uploaded files.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
@Path("/uploads")
public class UploadService {

    /**
     * Returns the mime type for the given file.
     *
     * @param file the name of the file
     * @return the mime type for the given file
     */
    @NotNull
    public static String mimeType(final String file) {
        if (file == null) {
            return "";
        }
        final String string = file.toLowerCase();
        if (string.endsWith(".jpg") || string.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (string.endsWith(".png")) {
            return "image/png";
        } else if (string.endsWith(".pdf")) {
            return "application/pdf";
        } else if (string.endsWith(".mp3")) {
            return "audio/mpeg3";
        } else if (string.endsWith(".3gp")) {
            return "video/3gpp";
        } else if (string.endsWith(".mp4")) {
            return "video/mp4";
        } else if (string.endsWith(".mov")) {
            return "video/quicktime";
        } else if (string.endsWith(".amr")) {
            return "audio/amr";
        } else if (string.endsWith(".m4a")) {
            return "audio/m4a";
        } else {
            return "";
        }
    }

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Context
    private UriInfo uriInfo;

    /**
     * Handles the HTTP GET requests that return media captures.
     *
     * @param filename the filename path parameter
     * @return the captured media with the given file name
     */
    @GET
    @Path("/{filename}")
    public Response getMediaCapture(final @PathParam("filename") String filename) {
        logger.entering(this.getClass().getName(), "getFile", filename);

        final String path = new PropertiesReader().getUploadsPath() + "/";
        final File file = new File(path + filename);
        final long bytes = file.length();
        final String mediaType = mimeType(filename);
        final Response response = Response.ok(file, mediaType).header("Accept-Ranges", "bytes")
                .header("Content-Range", "bytes 0-" + bytes +"/" + (bytes -1)).build();

        logger.exiting(this.getClass().getName(), "getFile", response);
        return response;
    }

    /**
     * Handles the HTTP POST requests that upload media captures.
     *
     * @param enabled {@code true} if enabled
     * @param in      the input stream
     * @param fd      the file's metadata
     * @return a {@link Response} object
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response upload(final @DefaultValue("true") @FormDataParam("enabled") boolean enabled,
                           final @FormDataParam("file") InputStream in,
                           final @FormDataParam("file") FormDataContentDisposition fd) {
        logger.entering(this.getClass().getName(), "uploadMediaCapture", new Object[]{enabled, in, fd});

        final PropertiesReader propertiesReader = new PropertiesReader();
        final String path = propertiesReader.getUploadsPath() + "/";
        final int size = propertiesReader.getThumbnailSize();

        final String attachment = System.currentTimeMillis() + "_" + fd.getFileName().replace(' ', '_');
        final String thumbnail = mimeType(attachment).startsWith("image/") ? "t_" + attachment : "";
        final File file = new File(path + attachment);
        if (file.exists()) {
            logger.log(Level.INFO, "File {0} already exists", file.getName());
            final WebApplicationException ex = new WebApplicationException(Response.Status.CONFLICT);
            logger.throwing(this.getClass().getName(), "uploadMediaCapture", ex);
            throw ex;
        }

        try {
            Files.copy(in, file.toPath());
            if (!thumbnail.isEmpty()) {
                createThumbnail(file, path, thumbnail, size);
            }
            logger.log(Level.INFO, "File {0} uploaded", file.getName());
        } catch (final IOException e) {
            logger.log(Level.SEVERE, "Failed to upload " + file.getName(), e);
            final WebApplicationException ex = new InternalServerErrorException();
            logger.throwing(this.getClass().getName(), "uploadMediaCapture", ex);
            throw ex;
        }

        // build the response
        final CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        final String location = "/uploads/" + attachment;
        final UploadedFile image = UploadedFile.newBuilder().attachment(attachment).thumbnail(thumbnail).build();
        final Response response = Response.status(Response.Status.CREATED).entity(image)
                .header(HttpHeaders.LOCATION, location).cacheControl(cacheControl).build();

        logger.exiting(this.getClass().getName(), "uploadMediaCapture", response);
        return response;
    }

    /**
     * Creates a thumbnail of a JPEG or PNG file.
     *
     * @param source    the source file
     * @param path      the path where the thumbnail shall be created
     * @param thumbnail the thumbnail's name
     * @param size      the thumbnail's size
     */
    void createThumbnail(final @NotNull File source, final @NotNull String path, final @NotNull String thumbnail, final int size) {
        logger.entering(this.getClass().getName(), "createThumbnail", new Object[]{source, path, thumbnail, size});

        try {
            final BufferedImage image = ImageIO.read(source);
            final BufferedImage thumbnailImage = Scalr.resize(image, Scalr.Method.QUALITY, size, Scalr.OP_ANTIALIAS);
            final File target = new File(path + thumbnail);
            ImageIO.write(thumbnailImage, "jpg", target);
        } catch (final Throwable t) {
            logger.log(Level.WARNING, "Failed to create thumbnail {0} [{1}]", new Object[]{thumbnail, t});
        }

        logger.exiting(this.getClass().getName(), "createThumbnail");
    }
}