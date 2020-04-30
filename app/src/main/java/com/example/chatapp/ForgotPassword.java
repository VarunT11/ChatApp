package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

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

public class ForgotPassword extends AppCompatActivity {

    EditText etUsername, etDOB, etPassword, etConfirmPassword;
    Button btnSubmit, btnReset;
    boolean UserExists;

    String CurrentUsername;

    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference dbRef=database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etUsername=findViewById(R.id.etForgotPasswordUsername);
        etDOB=findViewById(R.id.etForgotPasswordDOB);

        etPassword=findViewById(R.id.etForgotPasswordInputPassword);
        etConfirmPassword=findViewById(R.id.etForgotPasswordConfirmPassword);


        btnSubmit=findViewById(R.id.btnForgotPasswordSubmit);
        btnReset=findViewById(R.id.btnForgotPasswordResetButton);

        FragmentManager manager=ForgotPassword.this.getSupportFragmentManager();
        manager.beginTransaction()
                .hide(manager.findFragmentById(R.id.fragment3))
                .commit();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username=etUsername.getText().toString();
                final String dob=etDOB.getText().toString();

                if(username.isEmpty() || dob.isEmpty()){
                    Toast.makeText(ForgotPassword.this,"Please fill all the fields",Toast.LENGTH_SHORT).show();
                }
                else{
                    dbRef.child(DbVariables.KEY_USER).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            UserExists=false;
                            for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                                if(dataSnapshot1.getKey().equals(username)){
                                    if(dataSnapshot1.child(DbVariables.KEY_USER_DOB).getValue().equals(dob)){
                                        UserExists=true;
                                    }
                                    break;
                                }
                            }

                            if(UserExists){
                                etUsername.setText("");
                                etDOB.setText("");
                                CurrentUsername=username;
                                FragmentManager manager=ForgotPassword.this.getSupportFragmentManager();
                                manager.beginTransaction()
                                        .show(manager.findFragmentById(R.id.fragment3))
                                        .commit();
                            }
                            else{
                                Toast.makeText(ForgotPassword.this,"Incorrect Credentials",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password=etPassword.getText().toString();
                String confirmPassword=etConfirmPassword.getText().toString();

                if(password.isEmpty() || confirmPassword.isEmpty()){
                    Toast.makeText(ForgotPassword.this,"Please Enter all the Fields",Toast.LENGTH_SHORT).show();
                }
                else{
                    if(!password.equals(confirmPassword)){
                        Toast.makeText(ForgotPassword.this,"Passwords do not Match",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        dbRef.child(DbVariables.KEY_USER).child(CurrentUsername).child(DbVariables.KEY_USER_PASSWORD).setValue(password);
                        Toast.makeText(ForgotPassword.this,"Password Reset Successful",Toast.LENGTH_LONG).show();
                        ForgotPassword.this.finish();
                    }
                }

            }
        });

    }
}
