package com.probendi.iwatch.server.db;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import com.probendi.iwatch.server.municipality.ContactCodec;
import com.probendi.iwatch.server.report.ActivityCodec;
import com.probendi.iwatch.server.user.LocationCodec;
import com.probendi.iwatch.server.user.UserCodec;
import com.probendi.iwatch.server.user.WatcherCodec;

/**
 * Helper class for interacting with the Mongo database through a connection pool.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
public enum Mongo {

    instance;

    private final MongoDatabase database;

    /**
     * Creates a new {@code Mongo} object.
     */
    Mongo() {
        final CodecRegistry registry = CodecRegistries.fromCodecs(new ActivityCodec(), new ContactCodec(),
                new LocationCodec(), new UserCodec(), new WatcherCodec());
        final CodecRegistry codecRegistry = CodecRegistries.fromRegistries(registry, MongoClient.getDefaultCodecRegistry());
        final MongoClientOptions options = MongoClientOptions.builder().codecRegistry(codecRegistry).build();
        final MongoClient client = new MongoClient(new ServerAddress(), options);
        database = client.getDatabase("iwatch");
    }

    /**
     * Returns the {@code administrator} collection.
     *
     * @return the {@code administrator} collection
     */
    public MongoCollection<Document> getAdministratorCollection() {
        return database.getCollection("administrator");
    }

    /**
     * Returns the {@code message} collection.
     *
     * @return the {@code message} collection
     */
    public MongoCollection<Document> getMessageCollection() {
        return database.getCollection("message");
    }

    /**
     * Returns the {@code municipality} collection.
     *
     * @return the {@code municipality} collection
     */
    public MongoCollection<Document> getMunicipalityCollection() {
        return database.getCollection("municipality");
    }

    /**
     * Returns the {@code report} collection.
     *
     * @return the {@code report} collection
     */
    public MongoCollection<Document> getReportCollection() {
        return database.getCollection("report");
    }

    /**
     * Returns the {@code user} collection.
     *
     * @return the {@code user} collection
     */
    public MongoCollection<Document> getUserCollection() {
        return database.getCollection("user");
    }
}
