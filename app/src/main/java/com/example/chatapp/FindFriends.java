package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;

import java.util.ArrayList;

public class FindFriends extends AppCompatActivity implements GetSuggestedFriends.GetCallBackInterface, GetPhotoUrlFromUId.PhotoUrlCallBackInterface,
        FindFriendsAdapter.FriendRequestSentInterface
{

    ArrayList<FriendListData> friendListData=new ArrayList<FriendListData>();

    RecyclerView rcvFindFriends;
    RecyclerView.Adapter AdapterFindFriend;
    RecyclerView.LayoutManager FindFriendsLayoutManager;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        progressDialog=new ProgressDialog(this);
        rcvFindFriends=findViewById(R.id.rcvFindFriends);
        rcvFindFriends.setHasFixedSize(true);

        FindFriendsLayoutManager=new LinearLayoutManager(this);
        rcvFindFriends.setLayoutManager(FindFriendsLayoutManager);

        new GetSuggestedFriends(this,progressDialog);

    }

    int ListPosition=0;

    @Override
    public void getFriends(ArrayList<FriendListData> listData) {
        friendListData=listData;
        UpdateDPs();
    }

    public void UpdateDPs(){
        if(ListPosition==friendListData.size())
            UpdateActivity();
        else
            new GetPhotoUrlFromUId(this,friendListData.get(ListPosition).getUId(),progressDialog);
    }

    @Override
    public void PhotoResult(Uri PhotoUrl) {
        friendListData.get(ListPosition).setPhotoUrl(PhotoUrl);
        ListPosition++;
        UpdateDPs();
    }

    public void UpdateActivity(){
        AdapterFindFriend=new FindFriendsAdapter(this,this,friendListData,FindFriendsAdapter.LIST_TYPE_FIND_NEW_FRIENDS);
        rcvFindFriends.setAdapter(AdapterFindFriend);
    }

    @Override
    public void PostRequestSent(int index) {
        friendListData.remove(index);
        AdapterFindFriend.notifyDataSetChanged();
    }
}
