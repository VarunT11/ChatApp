package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterNewUser extends AppCompatActivity {

    EditText etName,etUsername,etPassword,etConfirmPassword,etDOB;
    Button btnSubmit;
    boolean UserExist=false;

    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference myRef=database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_user);

        etName=findViewById(R.id.etRegisterUserName);
        etUsername=findViewById(R.id.etRegisterUserUsername);
        etPassword=findViewById(R.id.etRegisterPassword);
        etConfirmPassword=findViewById(R.id.etRegisterPasswordConfirm);
        etDOB=findViewById(R.id.etRegisterUserDOB);
        btnSubmit=findViewById(R.id.btnRegisterSubmit);

        final Intent intent=getIntent();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name,username,password,passwordConfirm,dob;
                name=etName.getText().toString();
                username=etUsername.getText().toString();
                password=etPassword.getText().toString();
                passwordConfirm=etConfirmPassword.getText().toString();
                dob=etDOB.getText().toString();

                if(name.isEmpty() || username.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty() || dob.isEmpty()){
                    Toast.makeText(RegisterNewUser.this,"Please Fill all the Details",Toast.LENGTH_SHORT).show();
                }
                else{
                    if(password.equals(passwordConfirm)){
                        myRef.child(DbVariables.KEY_USER).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                                    if(dataSnapshot1.getKey().equals(username)){
                                        UserExist=true;
                                        break;
                                    }
                                }
                                if(UserExist){
                                    Toast.makeText(RegisterNewUser.this,"Username Already Exists!\nPlease try with a different Username",Toast.LENGTH_SHORT).show();
                                    UserExist=false;
                                }
                                else{
                                    DatabaseReference dbRef=myRef.child(DbVariables.KEY_USER).child(username);
                                    dbRef.child(DbVariables.KEY_USER_NAME).setValue(name);
                                    dbRef.child(DbVariables.KEY_USER_PASSWORD).setValue(password);
                                    dbRef.child(DbVariables.KEY_USER_DOB).setValue(dob);
                                    ApplicationClass.userList.add(new User(username,name));
                                    UserExist=false;
                                    setResult(RESULT_OK,intent);
                                    RegisterNewUser.this.finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    else{
                        Toast.makeText(RegisterNewUser.this,"Passwords Do not Match",Toast.LENGTH_SHORT).show();
                        etPassword.setText("");
                        etConfirmPassword.setText("");
                    }
                }
            }
        });

    }
}
