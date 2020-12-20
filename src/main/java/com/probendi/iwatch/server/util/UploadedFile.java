package com.probendi.iwatch.server.util;

import java.io.Serializable;
import java.util.Objects;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * An uploaded file.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
public class UploadedFile implements Serializable {

    private String attachment;
    private String thumbnail;

    /**
     * Creates a new {@code UploadedFile} object.
     */
    public UploadedFile() {
    }

    /**
     * Creates a new {@code UploadedFile} object from the given builder.
     *
     * @param builder the builder
     */
    private UploadedFile(final Builder builder) {
        this.attachment = builder.attachment;
        this.thumbnail = builder.thumbnail;
    }

    @Contract(" -> !null")
    public static Builder newBuilder() {
        return new Builder();
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(final String attachment) {
        this.attachment = attachment;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(final String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UploadedFile)) return false;
        UploadedFile that = (UploadedFile) o;
        return Objects.equals(attachment, that.attachment) &&
                Objects.equals(thumbnail, that.thumbnail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attachment, thumbnail);
    }

    @Override
    public String toString() {
        return "UploadedMediaCapture{" +
                "attachment='" + attachment + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                '}';
    }

    /**
     * {@code UploadedFile} builder static inner class.
     */
    public static final class Builder {
        private String attachment;
        private String thumbnail;

        private Builder() {
        }

        /**
         * Sets the {@code capture} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code capture} to set
         * @return a reference to this Builder
         */
        public Builder attachment(final @NotNull String val) {
            attachment = val;
            return this;
        }

        /**
         * Sets the {@code thumbnail} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code thumbnail} to set
         * @return a reference to this Builder
         */
        public Builder thumbnail(final @NotNull String val) {
            thumbnail = val;
            return this;
        }

        /**
         * Returns a {@code UploadedFile} built from the parameters previously set.
         *
         * @return a {@code UploadedFile} built with parameters of this {@code UploadedMediaCapture.Builder}
         */
        @Contract(" -> !null")
        public UploadedFile build() {
            return new UploadedFile(this);
        }
    }
}
