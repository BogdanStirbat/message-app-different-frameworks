package com.bstirbat.message.dw.repository;

import com.allanbank.mongodb.MongoCollection;
import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.bson.builder.BuilderFactory;
import com.allanbank.mongodb.bson.builder.DocumentBuilder;
import com.allanbank.mongodb.bson.element.ObjectId;
import com.bstirbat.message.dw.model.Message;

import java.util.Date;


public class MessageRepositoryImpl implements MessageRepository {

    private MongoCollection mongoCollection;

    public MessageRepositoryImpl(MongoCollection mongoCollection) {
        this.mongoCollection = mongoCollection;
    }

    @Override
    public Message findById(String id) {
        ObjectId objectId = new ObjectId(id);

        DocumentBuilder findDocBuilder = BuilderFactory.start();
        findDocBuilder.addObjectId("_id", objectId);
        Document foundDocument = mongoCollection.findOne(findDocBuilder);

        if (foundDocument == null) {
            return null;
        }

        Element textElement = foundDocument.get("text");
        String text = textElement.getValueAsString();

        Element createdAtElement = foundDocument.get("createdAt");
        Date createdAt = (Date) createdAtElement.getValueAsObject();

        Message message = new Message();
        message.setCreatedAt(createdAt);
        message.setText(text);
        message.setId(id);
        return message;
    }
}
