package com.probendi.iwatch.server.rest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.ws.rs.NameBinding;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Defines the name-binding annotation that decorates the {@link AuthorizationRequiredFilter} filter and that it is
 * applied to the resource method(s) that can be invoked only by authenticated users.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
@NameBinding
@Retention(RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface AuthorizationRequired {
}