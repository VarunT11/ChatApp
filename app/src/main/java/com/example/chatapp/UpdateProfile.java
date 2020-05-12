package com.example.chatapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class UpdateProfile extends AppCompatActivity implements GetPhotoUrlFromUId.PhotoUrlCallBackInterface {

    public final int RC_UPDATE_DP=1;
    public final int RC_UPDATE_NAME=2;
    public final int RC_ADD_FRIEND=3;

    TextView tvGreetings,tvNumFriends;
    ImageView imgProfileDp;
    Button btnUpdateDp,btnUpdateName,btnFindFriends,btnViewSentRequests,btnViewReceivedRequests,btnViewFriendsList;
    ProgressDialog progressDialog;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_UPDATE_DP){
            UpdateActivity();
        }
        if(requestCode==RC_UPDATE_NAME){
            tvGreetings.setText(CurrentUserData.DisplayName);
        }
        if(requestCode==RC_ADD_FRIEND){
            tvNumFriends.setText(Long.toString(CurrentUserData.NumFriends));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        tvNumFriends=findViewById(R.id.tvUpdateProfileFriendsFriendListNumFriends);
        tvGreetings=findViewById(R.id.tvProfileUpdateUserName);
        imgProfileDp=findViewById(R.id.imgProfilePic);
        progressDialog=new ProgressDialog(this);
        btnUpdateDp=findViewById(R.id.btnUpdatePhoto);
        btnUpdateName=findViewById(R.id.btnUpdateDisplayName);
        btnFindFriends=findViewById(R.id.btnUpdateProfileFriendsFindNewFriends);
        btnViewSentRequests=findViewById(R.id.btnUpdateProfileFriendsViewSentRequests);
        btnViewReceivedRequests=findViewById(R.id.btnUpdateProfileFriendsViewReceivedRequests);
        btnViewFriendsList=findViewById(R.id.btnUpdateProfileFriendsFriendListViewFriends);

        UpdateActivity();

        btnUpdateDp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(UpdateProfile.this,UpdateDp.class),RC_UPDATE_DP);
            }
        });

        btnUpdateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(UpdateProfile.this,UpdateProfileName.class),RC_UPDATE_NAME);
            }
        });

        btnFindFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UpdateProfile.this,FindFriends.class));
            }
        });

        btnViewSentRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UpdateProfile.this,ViewSentRequests.class));
            }
        });

        btnViewReceivedRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(UpdateProfile.this,ViewReceivedRequests.class),RC_ADD_FRIEND);
            }
        });

        btnViewFriendsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UpdateProfile.this,ViewFriends.class));
            }
        });
    }

    public void UpdateActivity(){
        tvGreetings.setText(CurrentUserData.DisplayName);
        tvNumFriends.setText(Long.toString(CurrentUserData.NumFriends));
        if(CurrentUserData.photoExists)
            new GetPhotoUrlFromUId(this,CurrentUserData.uId,progressDialog);
        else
            imgProfileDp.setImageResource(R.drawable.fui_ic_anonymous_white_24dp);
    }

    @Override
    public void PhotoResult(Uri PhotoUrl) {
        CurrentUserData.photoURL=PhotoUrl;
        if(PhotoUrl!=null){
            Picasso.get().load(PhotoUrl).into(imgProfileDp);
        }
    }
}