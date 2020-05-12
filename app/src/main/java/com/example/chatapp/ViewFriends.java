package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;

import java.util.ArrayList;

public class ViewFriends extends AppCompatActivity implements GetFriendsList.FriendsListInterface, GetPhotoUrlFromUId.PhotoUrlCallBackInterface {

    ArrayList<FriendListData> friendList;
    RecyclerView rcvFriendList;
    RecyclerView.Adapter AdapterFriendList;
    RecyclerView.LayoutManager FriendListLayoutManager;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friends);

        progressDialog=new ProgressDialog(this);
        rcvFriendList=findViewById(R.id.rcvViewFriends);
        rcvFriendList.setHasFixedSize(true);

        FriendListLayoutManager=new LinearLayoutManager(this);
        rcvFriendList.setLayoutManager(FriendListLayoutManager);

        new GetFriendsList(this,progressDialog);
    }

    @Override
    public void callBackResult(ArrayList<FriendListData> listData) {
        friendList=listData;
        UpdateDPs();
    }

    int ListPosition=0;

    private void UpdateDPs(){
        if(ListPosition==friendList.size())
            UpdateActivity();
        else
            new GetPhotoUrlFromUId(this,friendList.get(ListPosition).getUId(),progressDialog);
    }

    @Override
    public void PhotoResult(Uri PhotoUrl) {
        friendList.get(ListPosition).setPhotoUrl(PhotoUrl);
        ListPosition++;
        UpdateDPs();
    }

    private void UpdateActivity(){
        AdapterFriendList=new FriendsListAdapter(this,friendList);
        rcvFriendList.setAdapter(AdapterFriendList);
    }
}
