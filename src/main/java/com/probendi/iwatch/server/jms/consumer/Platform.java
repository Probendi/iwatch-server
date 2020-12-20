package com.probendi.iwatch.server.jms.consumer;

/**
 * A mobile device's platform.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
public enum Platform {

    ANDROID("Android"), IOS("iOS");

    private String platform;

    Platform(final String platform) {
        this.platform = platform;
    }

    public String getPlatform() {
        return platform;
    }
}
