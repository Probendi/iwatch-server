package com.probendi.iwatch.server.user;

import java.util.Date;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

/**
 * A {@code BSON} {@link Codec} for {@link Location} instances.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
public class LocationCodec implements Codec<Location> {

    @Override
    public Location decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        final Date date = new Date(reader.readDateTime("date"));
        final double latitude = reader.readDouble("latitude");
        final double longitude = reader.readDouble("longitude");
        final double altitude = reader.readDouble("altitude");
        final double accuracy = reader.readDouble("accuracy");
        final double altitudeAccuracy = reader.readDouble("altitudeAccuracy");
        final double heading = reader.readDouble("heading");
        final double speed = reader.readDouble("speed");
        reader.readEndDocument();
        return Location.newBuilder().date(date).latitude(latitude).longitude(longitude).altitude(altitude)
                .accuracy(accuracy).altitudeAccuracy(altitudeAccuracy).heading(heading).speed(speed).build();
    }

    @Override
    public void encode(BsonWriter writer, Location value, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeDateTime("date", value.getDate().getTime());
        writer.writeDouble("latitude", value.getLatitude());
        writer.writeDouble("longitude", value.getLongitude());
        writer.writeDouble("altitude", value.getAltitude());
        writer.writeDouble("accuracy", value.getAccuracy());
        writer.writeDouble("altitudeAccuracy", value.getAltitudeAccuracy());
        writer.writeDouble("heading", value.getHeading());
        writer.writeDouble("speed", value.getSpeed());
        writer.writeEndDocument();
    }

    @Override
    public Class<Location> getEncoderClass() {
        return Location.class;
    }
}
