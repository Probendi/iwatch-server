package com.probendi.iwatch.server.user;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

/**
 * A {@code BSON} {@link Codec} for {@link Watcher} instances.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
public class WatcherCodec implements Codec<Watcher> {

    @Override
    public Watcher decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        final String id = reader.readString("_id");
        final String mobile = reader.readString("mobile");
        final String firstname = reader.readString("firstname");
        final String lastname = reader.readString("lastname");
        final boolean creator = reader.readBoolean("creator");
        reader.readEndDocument();
        return Watcher.newBuilder().mobile(mobile).firstname(firstname).lastname(lastname).creator(creator).build();
    }

    @Override
    public void encode(BsonWriter writer, Watcher value, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString("_id", value.getId());
        writer.writeString("mobile", value.getMobile());
        writer.writeString("firstname", value.getFirstname());
        writer.writeString("lastname", value.getLastname());
        writer.writeBoolean("creator", value.isCreator());
        writer.writeEndDocument();
    }

    @Override
    public Class<Watcher> getEncoderClass() {
        return Watcher.class;
    }
}
