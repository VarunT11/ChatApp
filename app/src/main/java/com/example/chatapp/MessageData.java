package com.example.chatapp;

import android.net.Uri;

public class MessageData {
    private String sourceUId;
    private String destinationUId;
    private Long timeOfMessage;
    private Uri photoUrl;
    private boolean PhotoExists;
    private String message;
    private String messageId;

    MessageData(){

    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSourceUId() {
        return sourceUId;
    }

    public void setSourceUId(String sourceUId) {
        this.sourceUId = sourceUId;
    }

    public String getDestinationUId() {
        return destinationUId;
    }

    public void setDestinationUId(String destinationUId) {
        this.destinationUId = destinationUId;
    }

    public Long getTimeOfMessage() {
        return timeOfMessage;
    }

    public void setTimeOfMessage(Long timeOfMessage) {
        this.timeOfMessage = timeOfMessage;
    }

    public Uri getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(Uri photoUrl) {
        this.photoUrl = photoUrl;
    }

    public boolean isPhotoExists() {
        return PhotoExists;
    }

    public void setPhotoExists(boolean photoExists) {
        PhotoExists = photoExists;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
