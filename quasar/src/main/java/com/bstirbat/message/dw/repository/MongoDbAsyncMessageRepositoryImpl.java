package com.bstirbat.message.dw.repository;


import co.paralleluniverse.fibers.FiberAsync;
import co.paralleluniverse.fibers.Suspendable;
import com.bstirbat.message.dw.model.Message;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Date;

public class MongoDbAsyncMessageRepositoryImpl implements MessageRepository {

    private MongoCollection<Document> collection;

    public MongoDbAsyncMessageRepositoryImpl(MongoCollection<Document> collection) {
        this.collection = collection;
    }

    @Override
    @Suspendable
    public Message findById(final String id) {
        try {
            return find(id);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    @Suspendable
    public Message find(final String id) throws InterruptedException {
        Document document = null;

        try {
            document = new FiberAsync<Document, Throwable>() {

                @Override
                protected void requestAsync() {
                    ObjectId objectId = new ObjectId(id);
                    Document findDocument = new Document();
                    findDocument.append("_id", objectId);

                    collection.find(findDocument).first(new SingleResultCallback<Document>() {

                        @Override
                        public void onResult(Document document, Throwable throwable) {
                            if (throwable != null) {
                                asyncFailed(throwable);
                            }

                            asyncCompleted(document);
                        }
                    });
                }
            }.run();
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }

        if (document == null) {
            return null;
        }

        String text = document.getString("text");
        Date createdAt = document.getDate("createdAt");

        Message message = new Message();
        message.setId(id);
        message.setText(text);
        message.setCreatedAt(createdAt);

        return message;
    }
}
