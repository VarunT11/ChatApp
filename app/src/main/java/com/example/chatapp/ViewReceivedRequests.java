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

public class ViewReceivedRequests extends AppCompatActivity implements GetReceivedRequestUserData.ReceivedRequestInterface,
        GetPhotoUrlFromUId.PhotoUrlCallBackInterface,
        FindFriendsAdapter.FriendRequestSentInterface
{


    ArrayList<FriendListData> receivedRequestData;
    TextView tvNoRequest;
    RecyclerView rcvReceivedRequests;
    RecyclerView.Adapter AdapterReceivedRequests;
    RecyclerView.LayoutManager ReceivedRequestsLayoutManager;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_received_requests);

        tvNoRequest=findViewById(R.id.tvReceivedRequestsNoRequest);

        if(FriendRequestHandler.ReceivedRequestsList.size()==0){
            tvNoRequest.setVisibility(View.VISIBLE);
            tvNoRequest.setText("You have 0 Received Requests");
        }
        else {
            tvNoRequest.setVisibility(View.GONE);
            rcvReceivedRequests = findViewById(R.id.rcvReceivedRequests);
            rcvReceivedRequests.setHasFixedSize(true);

            ReceivedRequestsLayoutManager = new LinearLayoutManager(this);
            rcvReceivedRequests.setLayoutManager(ReceivedRequestsLayoutManager);

            progressDialog = new ProgressDialog(this);

            new GetReceivedRequestUserData(this, progressDialog);
        }
    }

    int ListPosition=0;

    @Override
    public void UserDataReceived(ArrayList<FriendListData> returnList) {
        receivedRequestData=returnList;
        UpdateDPs();
    }

    public void UpdateDPs(){
        if(ListPosition==receivedRequestData.size())
            UpdateActivity();
        else
            new GetPhotoUrlFromUId(this,receivedRequestData.get(ListPosition).getUId(),progressDialog);
    }


    @Override
    public void PhotoResult(Uri PhotoUrl) {
        receivedRequestData.get(ListPosition).setPhotoUrl(PhotoUrl);
        ListPosition++;
        UpdateDPs();
    }

    public void UpdateActivity(){
        AdapterReceivedRequests=new FindFriendsAdapter(this,this,receivedRequestData,FindFriendsAdapter.LIST_TYPE_RECEIVED_REQUESTS);
        rcvReceivedRequests.setAdapter(AdapterReceivedRequests);
    }

    @Override
    public void PostRequestSent(int index) {
        receivedRequestData.remove(index);
        AdapterReceivedRequests.notifyDataSetChanged();
    }
}
