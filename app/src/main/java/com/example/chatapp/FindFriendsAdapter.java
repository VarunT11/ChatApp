package com.example.chatapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FindFriendsAdapter extends RecyclerView.Adapter<FindFriendsAdapter.ViewHolder> implements SendFriendRequest.SendRequestInterface,
        AcceptFriendRequest.AcceptRequestInterface
{

    public static final int LIST_TYPE_FIND_NEW_FRIENDS=1;
    public static final int LIST_TYPE_RECEIVED_REQUESTS=2;
    public static final int LIST_TYPE_SENT_REQUESTS=3;

    Context context;
    ArrayList<FriendListData> list;
    int ListType;
    ProgressDialog progressDialog;
    FriendRequestSentInterface requestSentInterface;
    int DataPosition;

    @Override
    public void RequestAccepted() {
        progressDialog.dismiss();
        Toast.makeText(context,"Friend Request Accepted",Toast.LENGTH_SHORT).show();
        requestSentInterface.PostRequestSent(DataPosition);
    }

    public interface FriendRequestSentInterface{
        public void PostRequestSent(int index);
    }

    FindFriendsAdapter(FriendRequestSentInterface requestSentInterface,Context context, ArrayList<FriendListData> friendsArrayList, int ListType){
        this.requestSentInterface=requestSentInterface;
        this.context=context;
        progressDialog=new ProgressDialog(context);
        list=friendsArrayList;
        this.ListType=ListType;
    }

    @Override
    public void OnRequestSent() {
        Toast.makeText(context, "Friend Request Sent", Toast.LENGTH_SHORT).show();
        requestSentInterface.PostRequestSent(DataPosition);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imgUserPhoto;
        TextView tvUserName,tvUserEmail;
        Button btnAction,btnCancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUserPhoto=itemView.findViewById(R.id.imgFindFriendsUserDp);
            tvUserName=itemView.findViewById(R.id.tvFindFriendsUserDisplayName);
            tvUserEmail=itemView.findViewById(R.id.tvFindFriendsUserEmailId);
            btnAction=itemView.findViewById(R.id.btnSendOrAcceptRequest);
            btnCancel=itemView.findViewById(R.id.btnCancelRequest);
        }
    }


    @NonNull
    @Override
    public FindFriendsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.find_friends_user_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FindFriendsAdapter.ViewHolder holder, final int position) {
        FriendListData tempData=list.get(position);
        holder.tvUserName.setText(tempData.getDisplayName());
        holder.tvUserEmail.setText(tempData.getEmailId());
        final String uID=tempData.getUId();
        if(tempData.getPhotoUrl()!=null)
            Picasso.get().load(tempData.getPhotoUrl()).into(holder.imgUserPhoto);

        if(ListType==LIST_TYPE_FIND_NEW_FRIENDS) {
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnAction.setText("Send Request");
            holder.btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SendFriendRequest(FindFriendsAdapter.this,uID,progressDialog);
                    DataPosition=position;
                }
            });
        }
        if(ListType==LIST_TYPE_RECEIVED_REQUESTS){
            holder.btnAction.setText("Accept Request");
            holder.btnCancel.setText("Cancel");
            holder.btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.setTitle("Synchronizing with Database");
                    progressDialog.setMessage("Cancelling Request");
                    progressDialog.show();
                    final DatabaseReference RequestRef= FirebaseDatabase.getInstance().getReference().child(FirebaseHandler.DB_KEY_FRIEND_REQUESTS);
                    RequestRef.child(CurrentUserData.uId).child(uID).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    RequestRef.child(uID).child(CurrentUserData.uId).removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(context,"Received Request Cancelled",Toast.LENGTH_SHORT).show();
                                                    requestSentInterface.PostRequestSent(position);
                                                }
                                            });
                                }
                            });
                }
            });
            holder.btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.setTitle("Synchronizing with Database");
                    progressDialog.setMessage("Accepting Friend Request");
                    progressDialog.show();
                    DataPosition=position;
                    new AcceptFriendRequest(FindFriendsAdapter.this,uID);
                }
            });
        }
        if(ListType==LIST_TYPE_SENT_REQUESTS){
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnAction.setText("Cancel Request");
            holder.btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.setTitle("Synchronizing with Database");
                    progressDialog.setMessage("Cancelling Request");
                    progressDialog.show();
                    final DatabaseReference RequestRef= FirebaseDatabase.getInstance().getReference().child(FirebaseHandler.DB_KEY_FRIEND_REQUESTS);
                    RequestRef.child(CurrentUserData.uId).child(uID).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    RequestRef.child(uID).child(CurrentUserData.uId).removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(context,"Sent Request Cancelled",Toast.LENGTH_SHORT).show();
                                                    requestSentInterface.PostRequestSent(position);
                                                }
                                            });
                                }
                            });
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
