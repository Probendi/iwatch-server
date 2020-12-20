package com.probendi.iwatch.server.message;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.bson.Document;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * A published message.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
public class Message implements Comparable<Message>, Serializable {

    static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    static final long MILLIS_IN_ONE_DAY = 24 * 3600 * 1000L;

    private String id;
    private String header;
    private String text;
    private String interest;
    private Date createdOn;
    private Date expireOn;
    private String attachment;
    private String mimeType;
    private String thumbnail;
    private String municipality;
    private List<String> recipients;

    /**
     * Creates a new {@code Message} object.
     */
    public Message() {
    }

    /**
     * Creates a new {@code Message} object from the given document.
     *
     * @param doc a {@link Document} instance
     */
    @SuppressWarnings("unchecked")
    public Message(final @NotNull Document doc) {
        id = doc.getObjectId("_id").toString();
        header = doc.getString("header");
        text = doc.getString("text");
        interest = doc.getString("interest");
        createdOn = doc.getDate("createdOn");
        expireOn = doc.getDate("expireOn");
        attachment = doc.getString("attachment");
        mimeType = doc.getString("mimeType");
        thumbnail = doc.getString("thumbnail");
        municipality = doc.getString("municipality");
        recipients = (List<String>) doc.get("recipients");
        if (recipients == null) {
            recipients = new LinkedList<>();
        }
    }

    /**
     * Creates a new {@code Message} object from the given message. The new message will contains only a subset of the
     * original data because:
     * <ul>
     *     <li>the whole set of recipients shall not be sent to mobile users;</li>
     *     <li>the size of the collections shall be kept to a minimum</li>
     * </ul>
     *
     * @param message the source message
     */
    @SuppressWarnings("CopyConstructorMissesField")
    public Message(final @NotNull Message message) {
        id = message.id;
        header = message.header;
        text = message.text;
        createdOn = message.createdOn;
        attachment = message.attachment;
        mimeType = message.mimeType;
        thumbnail = message.thumbnail;
    }

    /**
     * Creates a new {@code Message} object from the given builder.
     *
     * @param builder the builder
     */
    private Message(final @NotNull Builder builder) {
        header = builder.header;
        text = builder.text;
        interest = builder.interest;
        createdOn = builder.createdOn;
        expireOn = builder.expireOn;
        attachment = builder.attachment;
        mimeType = builder.mimeType;
        thumbnail = builder.thumbnail;
        municipality = builder.municipality;
        recipients = builder.recipients;
        if (recipients == null) {
            recipients = new LinkedList<>();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(final String header) {
        this.header = header;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(final String interest) {
        this.interest = interest;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(final Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getExpireOn() {
        return expireOn;
    }

    public void setExpireOn(final Date expireOn) {
        this.expireOn = expireOn;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(final String attachment) {
        this.attachment = attachment;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(final String mimeType) {
        this.mimeType = mimeType;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(final String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(final String municipality) {
        this.municipality = municipality;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(final List<String> recipients) {
        this.recipients = recipients;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Message)) {
            return false;
        }
        Message that = (Message) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(header, that.header) &&
                Objects.equals(text, that.text) &&
                Objects.equals(interest, that.interest) &&
                Objects.equals(createdOn, that.createdOn) &&
                Objects.equals(expireOn, that.expireOn) &&
                Objects.equals(attachment, that.attachment) &&
                Objects.equals(mimeType, that.mimeType) &&
                Objects.equals(thumbnail, that.thumbnail) &&
                Objects.equals(municipality, that.municipality) &&
                Objects.equals(recipients, that.recipients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, header, text, interest, createdOn,
                expireOn, attachment, mimeType, thumbnail, municipality, recipients);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", header='" + header + '\'' +
                ", text='" + text + '\'' +
                ", interest='" + interest + '\'' +
                ", createdOn=" + createdOn +
                ", expireOn=" + expireOn +
                ", attachment='" + attachment + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", municipality='" + municipality + '\'' +
                ", recipients=" + recipients +
                '}';
    }

    @Override
    public int compareTo(@NotNull Message o) {
        return createdOn.compareTo(o.getCreatedOn());
    }

    @Contract(" -> !null")
    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Returns the representation of this message as a {@link Document}.
     *
     * @return the representation of this message as a {@link Document}
     */
    public Document toDocument() {
        return new Document("header", header)
                .append("text", text)
                .append("interest", interest)
                .append("createdOn", createdOn)
                .append("expireOn", expireOn)
                .append("attachment", attachment)
                .append("mimeType", mimeType)
                .append("thumbnail", thumbnail)
                .append("municipality", municipality)
                .append("recipients", recipients);
    }

    /**
     * {@code Message} builder static inner class.
     */
    public static final class Builder {
        private String header;
        private String text;
        private String interest;
        private Date createdOn;
        private Date expireOn;
        private String attachment;
        private String mimeType;
        private String thumbnail;
        private String municipality;
        private List<String> recipients;

        /**
         * Prevents instantiation.
         */
        private Builder() {
        }

        @NotNull
        public Builder header(final @NotNull String val) {
            header = val;
            return this;
        }

        @NotNull
        public Builder text(final @NotNull String val) {
            text = val;
            return this;
        }

        @NotNull
        public Builder interest(final @NotNull String val) {
            interest = val;
            return this;
        }

        @NotNull
        public Builder createdOn(final @NotNull String val) throws ParseException {
            createdOn = dateFormat.parse(val);
            return this;
        }

        @NotNull
        public Builder expireOn(final @NotNull String val) throws ParseException {
            expireOn = dateFormat.parse(val);
            return this;
        }

        @NotNull
        public Builder attachment(final @NotNull String val) {
            attachment = val;
            return this;
        }

        @NotNull
        public Builder mimeType(@NotNull String val) {
            mimeType = val;
            return this;
        }

        @NotNull
        public Builder thumbnail(final @NotNull String val) {
            thumbnail = val;
            return this;
        }

        @NotNull
        public Builder municipality(final @NotNull String val) {
            municipality = val;
            return this;
        }

        @NotNull
        public Builder recipients(final @NotNull List<String> val) {
            recipients = val;
            return this;
        }

        @NotNull
        public Message build() {
            return new Message(this);
        }
    }
}
