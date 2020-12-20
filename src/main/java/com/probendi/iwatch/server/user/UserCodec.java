package com.probendi.iwatch.server.user;

import java.util.Date;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

/**
 * A {@code BSON} {@link Codec} for {@link User} instances.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
public class UserCodec implements Codec<User> {

    @Override
    public User decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        final String id = reader.readString("_id");
        final String platform = reader.readString("platform");
        final String registrationId = reader.readString("registrationId");
        final String mobile = reader.readString("mobile");
        final String firstname = reader.readString("firstname");
        final String lastname = reader.readString("lastname");
        final Date dateOfBirth = new Date(reader.readDateTime("dateOfBirth"));
        final String placeOfBirth = reader.readString("placeOfBirth");
        final String address = reader.readString("address");
        final String cap = reader.readString("cap");
        final String city = reader.readString("city");
        final String municipality = reader.readString("municipality");
        reader.readEndDocument();
        return User.newBuilder().id(id).platform(platform).registrationId(registrationId).mobile(mobile)
                .firstname(firstname).lastname(lastname).dateOfBirth(dateOfBirth).placeOfBirth(placeOfBirth)
                .address(address).cap(cap).city(city).municipality(municipality).build();
    }

    @Override
    public void encode(BsonWriter writer, User value, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString("_id", value.getId());
        writer.writeString("platform", value.getPlatform());
        writer.writeString("registrationId", value.getRegistrationId());
        writer.writeString("mobile", value.getMobile());
        writer.writeString("firstname", value.getFirstname());
        writer.writeString("lastname", value.getLastname());
        writer.writeDateTime("dateOfBirth", value.getDateOfBirth().getTime());
        writer.writeString("placeOfBirth", value.getPlaceOfBirth());
        writer.writeString("address", value.getAddress());
        writer.writeString("cap", value.getCap());
        writer.writeString("city", value.getCity());
        writer.writeString("municipality", value.getMunicipality());
        writer.writeEndDocument();
    }

    @Override
    public Class<User> getEncoderClass() {
        return User.class;
    }
}
