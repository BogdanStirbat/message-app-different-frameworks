package com.bstirbat.message.dw.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class Message {
    private String id;

    private String text;

    private Date createdAt = new Date();

    public Message() {

    }

    public Message(String text) {
        this.text = text;
    }

    @JsonProperty
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @JsonProperty
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
