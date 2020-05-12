package com.example.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class UpdateCurrentUserData {

    UpdateDataCallBackInterface callBackInterface;
    ProgressDialog progressDialogBox;

    public interface UpdateDataCallBackInterface{
        public void DataUpdated();
    }

    public UpdateCurrentUserData(UpdateDataCallBackInterface aCallBackInterface, ProgressDialog progressDialog){
        this.callBackInterface=aCallBackInterface;
        progressDialogBox=progressDialog;
        FirebaseUser cUser= FirebaseAuth.getInstance().getCurrentUser();
        if(cUser!=null) {
            progressDialogBox.setTitle("Updating Data");
            progressDialogBox.setMessage("Synchronizing with Database");
            progressDialogBox.show();
            CurrentUserData.uId=cUser.getUid();
            DocumentReference cUserDataRef = FirebaseFirestore.getInstance().collection(FirebaseHandler.KEY_USER).document(cUser.getUid());
            cUserDataRef.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Map<String,Object> userData=documentSnapshot.getData();
                            CurrentUserData.DisplayName=userData.get(FirebaseHandler.KEY_USER_NAME).toString();
                            CurrentUserData.EmailId=userData.get(FirebaseHandler.KEY_USER_EMAIL_ID).toString();
                            CurrentUserData.photoExists=(Boolean)userData.get(FirebaseHandler.KEY_USER_PHOTO_EXISTS);
                            CurrentUserData.NumFriends=(Long)userData.get(FirebaseHandler.KEY_USER_NUM_FRIENDS);
                            Log.d("Updating User Data","Update of Current User Data Successful");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Updating User Data","Error in Updating Data, Exception",e);
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            Log.d("Updating User Data","Task Completed");
                            progressDialogBox.dismiss();
                            callBackInterface.DataUpdated();
                        }
                    });
        }
        else
            callBackInterface.DataUpdated();
    }
}