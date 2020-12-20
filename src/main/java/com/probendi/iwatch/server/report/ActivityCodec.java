package com.probendi.iwatch.server.report;

import java.util.Date;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import com.probendi.iwatch.server.user.Watcher;

/**
 * A {@code BSON} {@link Codec} for {@link Activity} instances.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
public class ActivityCodec implements Codec<Activity> {

    @Override
    public Activity decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        final Date date = new Date(reader.readDateTime("date"));
        final String comment = reader.readString("comment");
        final String attachment = reader.readString("attachment");
        final String mimeType = reader.readString("mimeType");
        final String thumbnail = reader.readString("thumbnail");
        final double latitude = reader.readDouble("latitude");
        final double longitude = reader.readDouble("longitude");
        reader.readStartDocument();
        reader.readName("watcher");
        final String id = reader.readString("_id");
        final String mobile = reader.readString("mobile");
        final String firstname = reader.readString("firstname");
        final String lastname = reader.readString("lastname");
        final boolean creator = reader.readBoolean("creator");
        reader.readEndDocument();
        reader.readEndDocument();
        final Watcher watcher = Watcher.newBuilder().id(id).mobile(mobile).lastname(lastname).firstname(firstname)
                .creator(creator).build();
        return Activity.newBuilder().date(date).comment(comment).attachment(attachment).mimeType(mimeType)
                .thumbnail(thumbnail).latitude(latitude).longitude(longitude).watcher(watcher).build();
    }

    @Override
    public void encode(BsonWriter writer, Activity value, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeDateTime("date", value.getDate().getTime());
        writer.writeString("comment", value.getComment());
        writer.writeString("attachment", value.getAttachment());
        writer.writeString("mimeType", value.getMimeType());
        writer.writeString("thumbnail", value.getThumbnail());
        writer.writeDouble("latitude", value.getLatitude());
        writer.writeDouble("longitude", value.getLongitude());
        writer.writeName("watcher");
        writer.writeStartDocument();
        writer.writeString("_id", value.getWatcher().getId());
        writer.writeString("mobile", value.getWatcher().getMobile());
        writer.writeString("firstname", value.getWatcher().getFirstname());
        writer.writeString("lastname", value.getWatcher().getLastname());
        writer.writeBoolean("creator", value.getWatcher().isCreator());
        writer.writeEndDocument();
        writer.writeEndDocument();
    }

    @Override
    public Class<Activity> getEncoderClass() {
        return Activity.class;
    }
}
