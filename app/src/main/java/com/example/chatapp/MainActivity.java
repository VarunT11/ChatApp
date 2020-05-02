package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.CursorIndexOutOfBoundsException;
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

import java.net.Inet4Address;

public class MainActivity extends AppCompatActivity {

    EditText etUsername,etPassword;
    Button btnSubmit, btnRegisterNewUser, btnForgotPassword;
    int REQUEST_CODE_REGISTER=1001;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference dbRef=database.getReference();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_CODE_REGISTER){
            if(resultCode==RESULT_OK){
                Toast.makeText(MainActivity.this,"User Registered Successfully",Toast.LENGTH_LONG).show();
            }

            if(resultCode==RESULT_CANCELED){
                Toast.makeText(MainActivity.this,"User Registration Cancelled",Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsername=findViewById(R.id.etUsername);
        etPassword=findViewById(R.id.etPassword);
        btnSubmit=findViewById(R.id.btnSubmit);
        btnRegisterNewUser=findViewById(R.id.btnRegisterNewUser);
        btnForgotPassword=findViewById(R.id.btnForgotPassword);

        if(CurrentUserInfo.LoggedIn){
            startActivity(new Intent(MainActivity.this,com.example.chatapp.ChatActivity.class));
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ApplicationClass.isInternetConnection(MainActivity.this)) {

                    dbRef.child(DbVariables.KEY_USER).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ApplicationClass.userList.clear();
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                ApplicationClass.userList.add(new User(dataSnapshot1.getKey(), dataSnapshot1.child(DbVariables.KEY_USER_NAME).getValue().toString()));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    final String username, password;
                    username = etUsername.getText().toString();
                    password = etPassword.getText().toString();
                    if (username.isEmpty() || password.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please Fill the Login Credentials", Toast.LENGTH_SHORT).show();
                    } else {
                        dbRef.child(DbVariables.KEY_USER).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                boolean UserAuth = false;
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    if (dataSnapshot1.getKey().equals(username)) {
                                        if (dataSnapshot1.child(DbVariables.KEY_USER_PASSWORD).getValue().equals(password)) {
                                            UserAuth = true;
                                            CurrentUserInfo.LoggedIn = true;
                                            CurrentUserInfo.Name = dataSnapshot1.child(DbVariables.KEY_USER_NAME).getValue().toString();
                                            CurrentUserInfo.Username = username;
                                        }
                                        break;
                                    }
                                }
                                if (UserAuth) {
                                    etUsername.setText("");
                                    etPassword.setText("");
                                    Toast.makeText(MainActivity.this, "Login Successful for " + CurrentUserInfo.Name, Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(MainActivity.this, com.example.chatapp.ChatActivity.class);

                                    startActivity(intent);
                                } else {
                                    Toast.makeText(MainActivity.this, "Incorrect Login Credentials", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
                else {
                    Toast.makeText(MainActivity.this,"Please Connect to Internet",Toast.LENGTH_LONG).show();
                }
            }
        });


        btnRegisterNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ApplicationClass.isInternetConnection(MainActivity.this)) {
                    Intent intent = new Intent(MainActivity.this, com.example.chatapp.RegisterNewUser.class);
                    startActivityForResult(intent, REQUEST_CODE_REGISTER);
                }
                else {
                    Toast.makeText(MainActivity.this,"Please Connect to Internet",Toast.LENGTH_LONG).show();
                }
            }
        });

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ApplicationClass.isInternetConnection(MainActivity.this)) {
                    startActivity(new Intent(MainActivity.this, com.example.chatapp.ForgotPassword.class));
                }
                else {
                    Toast.makeText(MainActivity.this,"Please Connect to Internet",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
