package com.probendi.iwatch.server.municipality;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

/**
 * A {@code BSON} {@link Codec} for {@link Contact} instances.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
public class ContactCodec implements Codec<Contact> {

    @Override
    public Contact decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        final String name = reader.readString("name");
        final String telephone = reader.readString("telephone");
        final String email = reader.readString("email");
        reader.readEndDocument();
        return Contact.newBuilder().name(name).telephone(telephone).email(email).build();
    }

    @Override
    public void encode(BsonWriter writer, Contact value, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString("name", value.getName());
        writer.writeString("telephone", value.getTelephone());
        writer.writeString("email", value.getEmail());
        writer.writeEndDocument();
    }

    @Override
    public Class<Contact> getEncoderClass() {
        return Contact.class;
    }
}
