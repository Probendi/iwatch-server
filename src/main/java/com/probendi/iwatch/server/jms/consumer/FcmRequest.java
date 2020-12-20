package com.probendi.iwatch.server.jms.consumer;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

/**
 * The FCM request.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
public class FcmRequest {

    private Data data;
    private List<String> registration_ids;

    public Data getData() {
        return data;
    }

    public void setData(final Data data) {
        this.data = data;
    }

    public List<String> getRegistration_ids() {
        return registration_ids;
    }

    public void setRegistration_ids(final List<String> registration_ids) {
        this.registration_ids = registration_ids;
    }

    public FcmRequest data(final @NotNull Data data) {
        this.data = data;
        return this;
    }

    public FcmRequest registration_ids(final @NotNull String registration_id) {
        this.registration_ids = Collections.singletonList(registration_id);
        return this;
    }

    public FcmRequest registration_ids(final @NotNull List<String> registration_ids) {
        this.registration_ids = registration_ids;
        return this;
    }

    @Override
    public String toString() {
        return "FcmRequest{" +
                "data=" + data +
                ", registration_ids=" + registration_ids +
                '}';
    }

    static class Data {
        private String image;
        private String title;
        private String body;
        private String sound;

        /**
         * Creates a new {@code Data} object with the {@code default} sound.
         */
        public Data() {
            this.sound = "default";
        }

        public String getImage() {
            return image;
        }

        public void setImage(final String image) {
            this.image = image;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(final String title) {
            this.title = title;
        }

        public String getBody() {
            return body;
        }

        public void setBody(final String body) {
            this.body = body;
        }

        public String getSound() {
            return sound;
        }

        public void setSound(final String sound) {
            this.sound = sound;
        }

        public Data image(final @NotNull String image) {
            this.image = image;
            return this;
        }

        public Data title(final @NotNull String title) {
            this.title = title;
            return this;
        }

        public Data body(final @NotNull String body) {
            this.body = body;
            return this;
        }

        public Data sound(final @NotNull String sound) {
            this.sound = sound;
            return this;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "image='" + image + '\'' +
                    ", title='" + title + '\'' +
                    ", body='" + body + '\'' +
                    ", sound='" + sound + '\'' +
                    '}';
        }
    }
}
