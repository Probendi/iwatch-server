package com.probendi.iwatch.server.rest;

import java.security.Key;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

import io.jsonwebtoken.impl.crypto.MacProvider;

import com.probendi.iwatch.server.message.MessageResource;
import com.probendi.iwatch.server.municipality.MunicipalityResource;
import com.probendi.iwatch.server.report.ReportResource;
import com.probendi.iwatch.server.user.AdministratorResource;
import com.probendi.iwatch.server.user.UserResource;
import com.probendi.iwatch.server.util.UploadService;

/**
 * Helper class for configuring the REST application.
 *
 * @see javax.ws.rs.core.Application
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
@ApplicationPath("/")
public class ApplicationConfig extends Application {

    private static final Key key = MacProvider.generateKey();

    public static Key getKey() {
        return key;
    }

    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> resources = new HashSet<>();
        resources.add(AdministratorResource.class);
        resources.add(MessageResource.class);
        resources.add(MunicipalityResource.class);
        resources.add(MultiPartFeature.class);
        resources.add(ReportResource.class);
        resources.add(UploadService.class);
        resources.add(UserResource.class);
        return resources;
    }

    @Override
    public Map<String, Object> getProperties() {
        final Map<String, Object> properties = new HashMap<>();
        properties.put("jersey.config.server.provider.packages", "com.probendi.iwatch.server.rest");
        return properties;
    }
}
