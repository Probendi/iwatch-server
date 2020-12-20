package com.probendi.iwatch.server.user;

import java.util.Objects;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * An authentication token.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
public class Token {

    private String access_token;
    private String token_type;
    private long expires_in;
    private String id_token;
    private String refresh_token;

    /**
     * Creates a new {@code Token} object.
     */
    public Token() {
    }

    /**
     * Creates a new {@code Token} object from the given builder.
     *
     * @param builder the builder
     */
    private Token(final Builder builder) {
        access_token = builder.access_token;
        token_type = "Bearer";
        expires_in = builder.expires_in;
        id_token = builder.id_token;
        refresh_token = builder.refresh_token;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(final String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(final String token_type) {
        this.token_type = token_type;
    }

    public long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(final int expires_in) {
        this.expires_in = expires_in;
    }

    public String getId_token() {
        return id_token;
    }

    public void setId_token(final String id_token) {
        this.id_token = id_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(final String refresh_token) {
        this.refresh_token = refresh_token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Token)) return false;
        Token token = (Token) o;
        return expires_in == token.expires_in &&
                Objects.equals(access_token, token.access_token) &&
                Objects.equals(token_type, token.token_type) &&
                Objects.equals(id_token, token.id_token) &&
                Objects.equals(refresh_token, token.refresh_token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(access_token, token_type, expires_in, id_token, refresh_token);
    }

    @Override
    public String toString() {
        return "Token{" +
                "access_token='" + access_token + '\'' +
                ", token_type='" + token_type + '\'' +
                ", expires_in=" + expires_in +
                ", id_token='" + id_token + '\'' +
                ", refresh_token='" + refresh_token + '\'' +
                '}';
    }

    @Contract(" -> !null")
    static Builder newBuilder() {
        return new Builder();
    }

    /**
     * {@code Token} builder static inner class.
     */
    public static final class Builder {
        private String access_token;
        private String token_type;
        private long expires_in;
        private String id_token;
        private String refresh_token;
        // the number of reports which need to be processed
        private int count;

        /**
         * Prevents instantiation.
         */
        private Builder() {
        }

        @NotNull
        public Builder access_token(final @NotNull String val) {
            access_token = val;
            return this;
        }

        @NotNull
        Builder expires_in(final long val) {
            expires_in = val;
            return this;
        }

        @NotNull
        public Builder id_token(final @NotNull String val) {
            id_token = val;
            return this;
        }

        @NotNull
        public Builder refresh_token(final @NotNull String val) {
            token_type = val;
            return this;
        }

        @NotNull Token build() {
            return new Token(this);
        }
    }
}
