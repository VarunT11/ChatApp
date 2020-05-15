package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class ViewSentRequests extends AppCompatActivity implements GetSentRequestUserData.SentRequestUserDataCallback,
        GetPhotoUrlFromUId.PhotoUrlCallBackInterface,
        FindFriendsAdapter.FriendRequestSentInterface
{

    ArrayList<FriendListData> sentRequestsListData;

    TextView tvNoRequest;
    RecyclerView rcvSentRequests;
    RecyclerView.Adapter AdapterSentRequests;
    RecyclerView.LayoutManager LayoutManagerAdapterSentRequests;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_sent_requests);

        tvNoRequest=findViewById(R.id.tvSentRequestsNoRequest);

        if(FriendRequestHandler.SentRequestsList.size()==0){
            tvNoRequest.setVisibility(View.VISIBLE);
            tvNoRequest.setText("You have 0 Pending Sent Requests");
        }

        else {
            tvNoRequest.setVisibility(View.GONE);
            progressDialog = new ProgressDialog(this);

            rcvSentRequests = findViewById(R.id.rcvSentRequests);
            rcvSentRequests.setHasFixedSize(true);

            LayoutManagerAdapterSentRequests = new LinearLayoutManager(this);
            rcvSentRequests.setLayoutManager(LayoutManagerAdapterSentRequests);

            new GetSentRequestUserData(this, progressDialog);
        }

    }

    int ListPosition=0;

    @Override
    public void UserDataReceived(ArrayList<FriendListData> returnList) {
        sentRequestsListData=returnList;
        UpdateDPs();
    }

    public void UpdateDPs(){
        if(ListPosition==sentRequestsListData.size())
            UpdateActivity();
        else
            new GetPhotoUrlFromUId(this,sentRequestsListData.get(ListPosition).getUId(),progressDialog);
    }

    @Override
    public void PhotoResult(Uri PhotoUrl) {
        sentRequestsListData.get(ListPosition).setPhotoUrl(PhotoUrl);
        ListPosition++;
        UpdateDPs();
    }

    public void UpdateActivity(){
        AdapterSentRequests=new FindFriendsAdapter(this,this,sentRequestsListData,FindFriendsAdapter.LIST_TYPE_SENT_REQUESTS);
        rcvSentRequests.setAdapter(AdapterSentRequests);
    }

    @Override
    public void PostRequestSent(int index) {
        sentRequestsListData.remove(index);
        AdapterSentRequests.notifyDataSetChanged();
    }
}