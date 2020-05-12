package com.example.chatapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class CheckUserExists {

    CheckUserCallBackInterface callBackInterface;
    ProgressDialog progressDialog;
    boolean result;
    String cUserUid;

    public CheckUserExists(CheckUserCallBackInterface callBackInterface, ProgressDialog progressDialogBox){
        this.callBackInterface=callBackInterface;
        this.progressDialog=progressDialogBox;
        cUserUid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        result=false;
        progressDialog.setTitle("Checking User");
        progressDialog.setMessage("Checking in Database");
        progressDialog.show();
        CollectionReference userListRef= FirebaseFirestore.getInstance().collection(FirebaseHandler.KEY_USER);
        if(userListRef!=null) {
            userListRef.get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            Log.d("Check User List", "Successful");
                            for (DocumentSnapshot userRef : queryDocumentSnapshots.getDocuments()) {
                                if (userRef.getId().equals(cUserUid)) {
                                    result = true;
                                    break;
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Check User List", "Error", e);
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            Log.d("Check User List", "Completed");
                            progressDialog.dismiss();
                            ReturnResult();
                        }
                    });
        }
        else
            ReturnResult();
    }

    public interface CheckUserCallBackInterface{
        public void UserExists(boolean result);
    }

    public void ReturnResult(){
        callBackInterface.UserExists(result);
    }

}