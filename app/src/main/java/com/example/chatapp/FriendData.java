package com.example.chatapp;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

public class FriendData {

    private String UId;
    private String DisplayName;
    private String EmailId;
    private boolean PhotoExists;
    private Uri PhotoURL;
    private ArrayList<MessageData> MessageList;

    public FriendData(String UID) {
        this.UId = UID;

        DocumentReference UserRef= FirebaseFirestore.getInstance().collection(FirebaseHandler.KEY_USER).document(UId);
        UserRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String,Object> userData=documentSnapshot.getData();
                        DisplayName=userData.get(FirebaseHandler.KEY_USER_NAME).toString();
                        EmailId=userData.get(FirebaseHandler.KEY_USER_EMAIL_ID).toString();
                        PhotoExists=(Boolean)userData.get(FirebaseHandler.KEY_USER_PHOTO_EXISTS);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(PhotoExists){
                            StorageReference storageReference= FirebaseStorage.getInstance().getReference(FirebaseHandler.KEY_USER).child(UId).child("ProfileDp");
                            storageReference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            PhotoURL=uri;
                                        }
                                    })
                                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            MessageList=new ArrayList<MessageData>();
                                        }
                                    });
                        }
                    }
                });
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

    public boolean isPhotoExists() {
        return PhotoExists;
    }

    public void setPhotoExists(boolean photoExists) {
        PhotoExists = photoExists;
    }

    public Uri getPhotoURL() {
        return PhotoURL;
    }

    public void setPhotoURL(Uri photoURL) {
        PhotoURL = photoURL;
    }

    public ArrayList<MessageData> getMessageList() {
        return MessageList;
    }

    public void setMessageList(ArrayList<MessageData> messageList) {
        MessageList = messageList;
    }
}
