package com.gazelle.amq.service;

import org.bson.types.ObjectId;

import java.util.Date;

/**
 * Created by msimmons on 6/17/15.
 */
public class MessageHistoryResult {

    private String collection;

    private ObjectId objectId;

    private Date createdAt;

    private String summary;

    private String message;

    public MessageHistoryResult(String collection, ObjectId objectId, Date createdAt, String summary, String message) {
        this.collection = collection;
        this.objectId = objectId;
        this.createdAt = createdAt;
        this.summary = summary;
        this.message = message;
    }

    public String getCollection() {
        return collection;
    }

    public ObjectId getObjectId() {
        return objectId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getSummary() {
        return summary;
    }

    public String getMessage() {
        return message;
    }
}
