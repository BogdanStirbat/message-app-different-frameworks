package com.bstirbat.message.dw;

import co.paralleluniverse.fibers.dropwizard.FiberApplication;
import co.paralleluniverse.fibers.mongodb.FiberMongoCollectionImpl;
import co.paralleluniverse.fibers.mongodb.FiberMongoFactory;
import com.allanbank.mongodb.MongoClient;
import com.allanbank.mongodb.MongoCollection;
import com.allanbank.mongodb.MongoDatabase;
import com.bstirbat.message.dw.controller.MessageController;
import com.bstirbat.message.dw.repository.MessageRepository;
import com.bstirbat.message.dw.repository.MessageRepositoryImpl;
import com.codahale.metrics.*;
import com.fasterxml.jackson.annotation.*;
import io.dropwizard.db.*;
import io.dropwizard.setup.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.*;

public class Main extends FiberApplication<Main.JModernConfiguration> {
    public static void main(String[] args) throws Exception {
        new Main().run(new String[]{"server", System.getProperty("dropwizard.config")});
    }

    @Override
    public void initialize(Bootstrap<JModernConfiguration> bootstrap) {
    }

    @Override
    public void fiberRun(JModernConfiguration cfg, Environment env) throws ClassNotFoundException {
        JmxReporter.forRegistry(env.metrics()).build().start(); // Manually add JMX reporting (Dropwizard regression)

        MongoClient mongoClient = FiberMongoFactory.createClient("mongodb://localhost:27017/?maxConnectionCount=10");
        MongoDatabase mongoDb = mongoClient.getDatabase("message_demo");
        MongoCollection mongoCollection = (FiberMongoCollectionImpl) mongoDb.getCollection("messages");

        MessageRepository messageRepository = new MessageRepositoryImpl(mongoCollection);
        MessageController messageController = new MessageController(messageRepository);

        env.jersey().register(messageController);
    }

    // YAML Configuration
    public static class JModernConfiguration extends io.dropwizard.Configuration {
        @JsonProperty private @NotEmpty String template;
        @JsonProperty private @NotEmpty String defaultName;
        @Valid @NotNull @JsonProperty private DataSourceFactory database = new DataSourceFactory();

        public DataSourceFactory getDataSourceFactory() { return database; }
        public String getTemplate()    { return template; }
        public String getDefaultName() { return defaultName; }
    }
}
