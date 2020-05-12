package com.example.chatapp;

import android.net.Uri;

public class FriendListData {
    private String UId;
    private String DisplayName;
    private String EmailId;
    private String FriendStatus;
    private Uri PhotoUrl;
    private boolean PhotoExists;

    FriendListData(){}

    public boolean isPhotoExists() {
        return PhotoExists;
    }

    public void setPhotoExists(boolean photoExists) {
        PhotoExists = photoExists;
    }

    public FriendListData(String UId, String displayName, String emailId, Uri photoUrl) {
        this.UId = UId;
        DisplayName = displayName;
        EmailId = emailId;
        PhotoUrl = photoUrl;
    }

    public String getUId() {
        return UId;
    }

    public void setUId(String UId) {
        this.UId = UId;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public String getEmailId() {
        return EmailId;
    }

    public void setEmailId(String emailId) {
        EmailId = emailId;
    }

    public String getFriendStatus() {
        return FriendStatus;
    }

    public void setFriendStatus(String friendStatus) {
        FriendStatus = friendStatus;
    }

    public Uri getPhotoUrl() {
        return PhotoUrl;
    }

    public void setPhotoUrl(Uri photoUrl) {
        PhotoUrl = photoUrl;
    }
}
