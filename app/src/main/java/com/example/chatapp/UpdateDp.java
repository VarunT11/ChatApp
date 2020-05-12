package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UpdateDp extends AppCompatActivity {

    final int RC_CHOOSE_PHOTO=1;

    ImageView imgPhoto;
    Button btnChoose,btnUpload,btnRemove;
    Uri PhotoUrl;
    ProgressDialog progressDialog;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_CHOOSE_PHOTO){
            if(resultCode==RESULT_OK && data!=null && data.getData()!=null){
                PhotoUrl=data.getData();
                Picasso.get().load(PhotoUrl).into(imgPhoto);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_dp);

        imgPhoto=findViewById(R.id.imgUpdateDpPhoto);
        btnChoose=findViewById(R.id.btnUpdateDpChoose);
        btnUpload=findViewById(R.id.btnUpdateDpUpload);
        btnRemove=findViewById(R.id.btnUpdateDpRemove);
        progressDialog=new ProgressDialog(this);

        if(CurrentUserData.photoExists)
            Picasso.get().load(CurrentUserData.photoURL).into(imgPhoto);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Choose Picture"),RC_CHOOSE_PHOTO);
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PhotoUrl!=null) {
                    StorageReference UserPhotoRef = FirebaseStorage.getInstance().getReference().child(FirebaseHandler.KEY_USER).child(CurrentUserData.uId).child("ProfileDp");
                    progressDialog.setTitle("Synchronizing with Database");
                    progressDialog.setMessage("Uploading Image");
                    progressDialog.show();
                    UserPhotoRef.putFile(PhotoUrl)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Log.d("UploadPhotoTask","Successful");
                                    Map<String,Object> updatedDataMap=new HashMap<>();
                                    updatedDataMap.put(FirebaseHandler.KEY_USER_PHOTO_EXISTS,true);
                                    DocumentReference userDbRef=FirebaseFirestore.getInstance().collection(FirebaseHandler.KEY_USER).document(CurrentUserData.uId);
                                    userDbRef.update(updatedDataMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    FinishActivity();
                                                    Log.d("UpdateDBTask","Successful");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    Log.d("UpdateDBTask","Error",e);
                                                }
                                            })
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Log.d("UpdateDBTask","Completed");
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Log.d("UploadPhotoTask","Error",e);
                                }
                            })
                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    Log.d("UploadPhotoTask","Completed");
                                    CurrentUserData.photoExists=true;
                                }
                            });
                }
                else
                    Toast.makeText(UpdateDp.this,"Please Choose Any Photo",Toast.LENGTH_SHORT).show();
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CurrentUserData.photoExists) {
                    StorageReference UserPhotoRef = FirebaseStorage.getInstance().getReference().child(FirebaseHandler.KEY_USER).child(CurrentUserData.uId).child("ProfileDp");
                    progressDialog.setTitle("Synchronizing with Database");
                    progressDialog.setMessage("Removing Image");
                    progressDialog.show();
                    UserPhotoRef.delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("RemovePhotoTask","Successful");
                                    Map<String,Object> updatedDataMap=new HashMap<>();
                                    updatedDataMap.put(FirebaseHandler.KEY_USER_PHOTO_EXISTS,false);
                                    DocumentReference userDbRef=FirebaseFirestore.getInstance().collection(FirebaseHandler.KEY_USER).document(CurrentUserData.uId);
                                    userDbRef.update(updatedDataMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    FinishActivity();
                                                    Log.d("UpdateDBTask","Successful");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    Log.d("UpdateDBTask","Error",e);
                                                }
                                            })
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Log.d("UpdateDBTask","Completed");
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Log.d("RemovePhotoTask", "Error", e);
                                }
                            })
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d("RemovePhotoTask","Completed");
                                    CurrentUserData.photoExists=false;
                                }
                            });
                }
                else
                    Toast.makeText(UpdateDp.this,"You Don't Have any Profile Picture",Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void FinishActivity(){
        progressDialog.dismiss();
        UpdateDp.this.finish();
    }

}
