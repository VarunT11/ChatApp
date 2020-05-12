package com.example.chatapp;

import android.app.ProgressDialog;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;

public class GetPhotoUrlFromUId {

    String UId;
    Uri PhotoUrl;
    PhotoUrlCallBackInterface callBackInterface;
    ProgressDialog progressDialogBox;

    public GetPhotoUrlFromUId(PhotoUrlCallBackInterface photoUrlCallBackInterface,String uId, ProgressDialog progressDialog){
        progressDialogBox=progressDialog;
        progressDialogBox.setTitle("Synchronizing With Database");
        progressDialogBox.setMessage("Getting User's Photo");
        progressDialogBox.show();
        callBackInterface=photoUrlCallBackInterface;
        UId=uId;
        DocumentReference userRef= FirebaseFirestore.getInstance().collection(FirebaseHandler.KEY_USER).document(UId);
        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String,Object> userData=documentSnapshot.getData();
                        if(userData.get(FirebaseHandler.KEY_USER_PHOTO_EXISTS).equals(true)){
                            StorageReference storageReference=FirebaseStorage.getInstance().getReference().child(FirebaseHandler.KEY_USER).child(UId).child("ProfileDp");
                            storageReference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.d("PhotoGetTask","Successful");
                                            PhotoUrl=uri;
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("PhotoGetTask","Error",e);
                                        }
                                    })
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.d("PhotoGetTask","Completed");
                                            ReturnResult();
                                        }
                                    });
                        }
                        else {
                            PhotoUrl=null;
                            ReturnResult();
                        }
                        Log.d("PhotoUrlTask","Successful");
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Log.d("PhotoUrlTask","Completed");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("PhotoUrlTask","Error",e);
                    }
                });
    }


    private void ReturnResult(){
        progressDialogBox.dismiss();
        callBackInterface.PhotoResult(PhotoUrl);
    }

    public interface PhotoUrlCallBackInterface{
        public void PhotoResult(Uri PhotoUrl);
    }

}
