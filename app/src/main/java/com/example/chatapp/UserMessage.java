package com.example.chatapp;

import java.util.Date;

public class UserMessage {
    private String id;
    private String sourceName;
    private String destinationName;
    private String sourceUsername;
    private String destinationUsername;
    private String data;
    private Date timeOfMessage;

    UserMessage(){

    }

    public UserMessage(String sourceName, String destinationName, String sourceUsername, String destinationUsername, String data, Date timeOfMessage) {
        this.sourceName = sourceName;
        this.destinationName = destinationName;
        this.sourceUsername = sourceUsername;
        this.destinationUsername = destinationUsername;
        this.data = data;
        this.timeOfMessage = timeOfMessage;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public String getSourceUsername() {
        return sourceUsername;
    }

    public void setSourceUsername(String sourceUsername) {
        this.sourceUsername = sourceUsername;
    }

    public String getDestinationUsername() {
        return destinationUsername;
    }

    public void setDestinationUsername(String destinationUsername) {
        this.destinationUsername = destinationUsername;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Date getTimeOfMessage() {
        return timeOfMessage;
    }

    public void setTimeOfMessage(Date timeOfMessage) {
        this.timeOfMessage = timeOfMessage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
