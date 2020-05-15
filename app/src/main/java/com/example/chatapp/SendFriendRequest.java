package com.example.chatapp;

import android.app.ProgressDialog;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SendFriendRequest {

    String destinationUid,sourceUid;
    SendRequestInterface requestInterface;
    ProgressDialog progressDialog;

    public interface SendRequestInterface{
        public void OnRequestSent();
    }

    public SendFriendRequest(SendRequestInterface requestInterface, String destinationUid, ProgressDialog progressDialog){
        this.requestInterface=requestInterface;
        this.destinationUid=destinationUid;
        this.progressDialog=progressDialog;
        SendRequest();
    }

    public void SendRequest(){
        sourceUid=CurrentUserData.uId;
        progressDialog.setTitle("Synchronizing with Database");
        progressDialog.setMessage("Sending Friend Request");
        progressDialog.show();
        DatabaseReference RequestRef=FirebaseDatabase.getInstance().getReference().child(FirebaseHandler.DB_KEY_FRIEND_REQUESTS).child(sourceUid);
        RequestRef.child(destinationUid).child(FirebaseHandler.DB_KEY_REQUEST_SEEN).setValue(true);
        RequestRef.child(destinationUid).child(FirebaseHandler.DB_KEY_FRIEND_REQUESTS_STATUS).setValue(FirebaseHandler.DB_VALUE_REQUEST_SENT).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                DatabaseReference RequestRef;
                RequestRef=FirebaseDatabase.getInstance().getReference().child(FirebaseHandler.DB_KEY_FRIEND_REQUESTS).child(destinationUid);
                RequestRef.child(sourceUid).child(FirebaseHandler.DB_KEY_REQUEST_SEEN).setValue(false);
                RequestRef.child(sourceUid).child(FirebaseHandler.DB_KEY_FRIEND_REQUESTS_STATUS).setValue(FirebaseHandler.DB_VALUE_REQUEST_RECEIVED);
                progressDialog.dismiss();
                requestInterface.OnRequestSent();
            }
        });
    }
}
