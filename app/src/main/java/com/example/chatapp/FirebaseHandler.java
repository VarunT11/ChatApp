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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

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
    public final static String DB_VALUE_REQUEST_SENT="Sent";
    public final static String DB_VALUE_REQUEST_RECEIVED="Received";
    public final static String DB_VALUE_REQUEST_ACCEPTED="Accepted";

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
        CurrentUserData.uId=null;
        CurrentUserData.DisplayName=null;
        CurrentUserData.EmailId=null;
        CurrentUserData.NumFriends=0;
        CurrentUserData.photoExists=false;
    }

    public static void AddNewFriend(String FriendUId){

    }

}
