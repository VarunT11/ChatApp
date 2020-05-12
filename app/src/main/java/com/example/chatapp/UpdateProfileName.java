package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UpdateProfileName extends AppCompatActivity {

    EditText etUserName;
    Button btnUpdateName;
    String newUserName;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile_name);

        etUserName=findViewById(R.id.etNewUserName);
        btnUpdateName=findViewById(R.id.btnUpdateUsername);
        progressDialog=new ProgressDialog(this);

        etUserName.setText(CurrentUserData.DisplayName);

        btnUpdateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newUserName=etUserName.getText().toString();
                if(newUserName.isEmpty()){
                    Toast.makeText(UpdateProfileName.this,"User Name can't be Empty",Toast.LENGTH_SHORT).show();
                }
                else {
                    progressDialog.setTitle("Updating Database");
                    progressDialog.setMessage("Updating User's Display Name");
                    progressDialog.show();
                    DocumentReference userDataRef=FirebaseFirestore.getInstance().collection(FirebaseHandler.KEY_USER).document(CurrentUserData.uId);
                    Map<String,Object> newMappedData=new HashMap<>();
                    newMappedData.put(FirebaseHandler.KEY_USER_NAME,newUserName);
                    userDataRef.update(newMappedData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("UpdateDBTask","Successful");
                                    CurrentUserData.DisplayName=newUserName;
                                    FinishActivity();
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
            }
        });

    }

    public void FinishActivity(){
        progressDialog.dismiss();
        UpdateProfileName.this.finish();
    }
}
