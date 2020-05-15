package com.example.chatapp;

import android.app.ProgressDialog;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FirebaseHandler {

    public static ProgressDialog progressDialogBox;
    public final static String KEY_USER="Users";
    public final static String KEY_USER_UID="UId";
    public final static String KEY_USER_NAME="UserName";
    public final static String KEY_USER_PHOTO_EXISTS="PhotoExists";
    public final static String KEY_USER_EMAIL_ID="Email-Id";
    public final static String KEY_USER_NUM_FRIENDS="NumberOfFriends";
    public final static String KEY_USER_FRIEND="Friends";
    public final static String DB_KEY_FRIEND_REQUESTS="FriendRequests";
    public final static String DB_KEY_FRIEND_REQUESTS_STATUS="Status";
    public final static String DB_VALUE_REQUEST_SENT="Sent";
    public final static String DB_VALUE_REQUEST_RECEIVED="Received";
    public final static String DB_VALUE_REQUEST_ACCEPTED="Accepted";
    public final static String DB_KEY_REQUEST_SEEN="Seen";

    public static FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();

    public static boolean IsUserLoggedIn(){
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        return (user!=null);
    }

    public static void AddNewUser(ProgressDialog progressDialog){
        FirebaseUser cUser=FirebaseAuth.getInstance().getCurrentUser();
        if(cUser!=null){
            progressDialogBox=progressDialog;
            progressDialogBox.setTitle("Updating Database");
            progressDialogBox.setMessage("Adding New User's Data");
            progressDialogBox.show();
            DocumentReference newUserRef=FirebaseFirestore.getInstance().collection(KEY_USER).document(cUser.getUid());
            Map<String, Object> newUserData=new HashMap<>();
            newUserData.put(KEY_USER_UID,cUser.getUid());
            newUserData.put(KEY_USER_NAME,cUser.getDisplayName());
            newUserData.put(KEY_USER_EMAIL_ID,cUser.getEmail());
            newUserData.put(KEY_USER_PHOTO_EXISTS,false);
            newUserData.put(KEY_USER_NUM_FRIENDS,0);
            newUserRef.set(newUserData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Adding New User","Successful");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Adding New User","Error",e);
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("Adding New User","Task Completed");
                            progressDialogBox.dismiss();
                        }
                    });
        }
    }

    public static void SignOutCurrentUser(){
        FirebaseAuth.getInstance().signOut();
        FriendRequestHandler.onSignOut();
        FriendMessageHandler.onSignOut();
        CurrentUserData.uId=null;
        CurrentUserData.DisplayName=null;
        CurrentUserData.EmailId=null;
        CurrentUserData.NumFriends=0;
        CurrentUserData.photoExists=false;
    }

    public static void AddNewMessage(MessageData newMessage){

        DatabaseReference newMessageRef1=firebaseDatabase.getReference().child(FriendMessageHandler.DB_KEY_MESSAGES)
                .child(newMessage.getSourceUId()).child(newMessage.getDestinationUId()).push();
        DatabaseReference newMessageRef2=firebaseDatabase.getReference().child(FriendMessageHandler.DB_KEY_MESSAGES)
                .child(newMessage.getDestinationUId()).child(newMessage.getSourceUId()).push();

        newMessageRef1.child(FriendMessageHandler.DB_KEY_MESSAGE_SOURCE_UID).setValue(newMessage.getSourceUId());
        newMessageRef1.child(FriendMessageHandler.DB_KEY_MESSAGE_DESTINATION_UID).setValue(newMessage.getDestinationUId());
        newMessageRef1.child(FriendMessageHandler.DB_KEY_MESSAGE_TIME_OF_MESSAGE).setValue(newMessage.getTimeOfMessage());
        newMessageRef1.child(FriendMessageHandler.DB_KEY_MESSAGE_DATA).setValue(newMessage.getMessage());
        newMessageRef1.child(FriendMessageHandler.DB_KEY_MESSAGE_SEEN_STATUS).setValue(true);
        newMessageRef1.child(FriendMessageHandler.DB_KEY_MESSAGE_PHOTO_EXISTS).setValue(false);

        newMessageRef2.child(FriendMessageHandler.DB_KEY_MESSAGE_SOURCE_UID).setValue(newMessage.getSourceUId());
        newMessageRef2.child(FriendMessageHandler.DB_KEY_MESSAGE_DESTINATION_UID).setValue(newMessage.getDestinationUId());
        newMessageRef2.child(FriendMessageHandler.DB_KEY_MESSAGE_TIME_OF_MESSAGE).setValue(newMessage.getTimeOfMessage());
        newMessageRef2.child(FriendMessageHandler.DB_KEY_MESSAGE_DATA).setValue(newMessage.getMessage());
        newMessageRef2.child(FriendMessageHandler.DB_KEY_MESSAGE_SEEN_STATUS).setValue(false);
        newMessageRef2.child(FriendMessageHandler.DB_KEY_MESSAGE_PHOTO_EXISTS).setValue(false);

    }

    public static void AddNewPhotoMessage(final String destinationUId, final Uri messageUri){
        DatabaseReference newMessageRef1=firebaseDatabase.getReference().child(FriendMessageHandler.DB_KEY_MESSAGES)
                .child(CurrentUserData.uId).child(destinationUId).push();
        DatabaseReference newMessageRef2=firebaseDatabase.getReference().child(FriendMessageHandler.DB_KEY_MESSAGES)
                .child(destinationUId).child(CurrentUserData.uId).push();

        newMessageRef1.child(FriendMessageHandler.DB_KEY_MESSAGE_SOURCE_UID).setValue(CurrentUserData.uId);
        newMessageRef1.child(FriendMessageHandler.DB_KEY_MESSAGE_DESTINATION_UID).setValue(destinationUId);
        newMessageRef1.child(FriendMessageHandler.DB_KEY_MESSAGE_TIME_OF_MESSAGE).setValue((new Date()).getTime());
        newMessageRef1.child(FriendMessageHandler.DB_KEY_MESSAGE_SEEN_STATUS).setValue(true);
        newMessageRef1.child(FriendMessageHandler.DB_KEY_MESSAGE_DATA).setValue("__");

        newMessageRef2.child(FriendMessageHandler.DB_KEY_MESSAGE_SOURCE_UID).setValue(CurrentUserData.uId);
        newMessageRef2.child(FriendMessageHandler.DB_KEY_MESSAGE_DESTINATION_UID).setValue(destinationUId);
        newMessageRef2.child(FriendMessageHandler.DB_KEY_MESSAGE_TIME_OF_MESSAGE).setValue((new Date()).getTime());
        newMessageRef2.child(FriendMessageHandler.DB_KEY_MESSAGE_SEEN_STATUS).setValue(false);
        newMessageRef2.child(FriendMessageHandler.DB_KEY_MESSAGE_DATA).setValue("__");

        final String UserKey2=newMessageRef2.getKey();
        final String UserKey1=newMessageRef1.getKey();

        StorageReference imageRef1= FirebaseStorage.getInstance().getReference().child(FirebaseHandler.KEY_USER)
                .child(CurrentUserData.uId).child("PhotoMessages").child(UserKey1);
        imageRef1.putFile(messageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d("MessageUploadTask","Photo Uploaded Successfully");
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        StorageReference imageRef2= FirebaseStorage.getInstance().getReference().child(FirebaseHandler.KEY_USER)
                                .child(destinationUId).child("PhotoMessages").child(UserKey2);
                        imageRef2.putFile(messageUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Log.d("MessageUploadTask","Photo Uploaded Successfully");
                                       firebaseDatabase.getReference().child(FriendMessageHandler.DB_KEY_MESSAGES)
                                                .child(CurrentUserData.uId).child(destinationUId).child(UserKey1).child(FriendMessageHandler.DB_KEY_MESSAGE_PHOTO_EXISTS)
                                               .setValue(true);
                                        firebaseDatabase.getReference().child(FriendMessageHandler.DB_KEY_MESSAGES)
                                                .child(destinationUId).child(CurrentUserData.uId).child(UserKey2).child(FriendMessageHandler.DB_KEY_MESSAGE_PHOTO_EXISTS)
                                                .setValue(true);
                                    }
                                });
                    }
                });

    }

}
