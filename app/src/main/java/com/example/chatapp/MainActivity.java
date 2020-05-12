package com.example.chatapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity implements CheckUserExists.CheckUserCallBackInterface, UpdateCurrentUserData.UpdateDataCallBackInterface {

    final int RC_SIGN_IN=1;
    final int RC_UPDATE_PROFILE=2;

    public final static String NOTIFICATION_CHANNEL_ID="NotificationChannelID";

    TextView tvSignInLink,tvGreetings;
    Button btnGoToChat,btnUpdateProfile,btnSignOut;
    ProgressDialog progressDialog;

    private List<AuthUI.IdpConfig> AuthProviders= Arrays.asList(
            new AuthUI.IdpConfig.GoogleBuilder().build(),
            new AuthUI.IdpConfig.EmailBuilder().build()
    );

    public void UpdateMainActivity(){
        if(FirebaseHandler.IsUserLoggedIn()){
            tvSignInLink.setVisibility(View.GONE);
            tvGreetings.setVisibility(View.VISIBLE);
            btnGoToChat.setVisibility(View.VISIBLE);
            btnUpdateProfile.setVisibility(View.VISIBLE);
            btnSignOut.setVisibility(View.VISIBLE);
            tvGreetings.setText("Hello "+CurrentUserData.DisplayName);
        }
        else {
            tvSignInLink.setVisibility(View.VISIBLE);
            tvGreetings.setVisibility(View.GONE);
            btnGoToChat.setVisibility(View.GONE);
            btnUpdateProfile.setVisibility(View.GONE);
            btnSignOut.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            if(resultCode==RESULT_OK){
                Toast.makeText(MainActivity.this,"Sign-In Successful",Toast.LENGTH_SHORT).show();
                new CheckUserExists(MainActivity.this, progressDialog);
            }
            else {
                Toast.makeText(MainActivity.this,"Sign-In Cancelled",Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode==RC_UPDATE_PROFILE){
            new UpdateCurrentUserData(this,progressDialog);
        }
    }

    private void CreateNotificationChannel(){
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.O){
            CharSequence name=getString(R.string.NotificationChannelName);
            String description=getString(R.string.NotificationChannelDescription);
            int importance= NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel=new NotificationChannel(NOTIFICATION_CHANNEL_ID,name,importance);
            channel.setDescription(description);
            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSignInLink=findViewById(R.id.tvMainSignInLink);
        tvGreetings=findViewById(R.id.tvMainGreetings);
        btnGoToChat=findViewById(R.id.btnMainGoToChat);
        btnUpdateProfile=findViewById(R.id.btnMainUpdateProfile);
        btnSignOut=findViewById(R.id.btnMainSignOut);
        progressDialog=new ProgressDialog(this);

        CreateNotificationChannel();


        if(FirebaseHandler.IsUserLoggedIn()){
            new UpdateCurrentUserData(this,progressDialog);
        }
        else
            UpdateMainActivity();

        tvSignInLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult
                        (AuthUI.getInstance()
                                        .createSignInIntentBuilder()
                                        .setAvailableProviders(AuthProviders)
                                        .setLogo(R.drawable.logo)
                                        .setIsSmartLockEnabled(false)
                                        .setTheme(R.style.LoginTheme)
                                .build()
                                , RC_SIGN_IN);
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseHandler.SignOutCurrentUser();
                UpdateMainActivity();
            }
        });

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this,UpdateProfile.class),RC_UPDATE_PROFILE);
            }
        });

        btnGoToChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ChatActivity.class));
            }
        });

    }

    @Override
    public void UserExists(boolean result) {
        if(!result){
            FirebaseUser cUser=FirebaseAuth.getInstance().getCurrentUser();
            FirebaseHandler.AddNewUser(progressDialog);
            CurrentUserData.uId=cUser.getUid();
            CurrentUserData.DisplayName=cUser.getDisplayName();
            CurrentUserData.EmailId=cUser.getEmail();
            CurrentUserData.NumFriends=0;
            CurrentUserData.photoExists=false;
            UpdateMainActivity();
            FriendRequestHandler.setUpRequestListener();
        }
        else
            new UpdateCurrentUserData(MainActivity.this,progressDialog);
    }

    @Override
    public void DataUpdated() {
        UpdateMainActivity();
        FriendRequestHandler.setUpRequestListener();
    }
}