package com.example.chatapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.ViewHolder> {

    private ArrayList<FriendListData> friendList;

    FriendsListAdapter(Context context, ArrayList<FriendListData> friendList){
        this.friendList=friendList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgPhoto;
        TextView tvName,tvEmail;
        Button btnGoToChat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPhoto=itemView.findViewById(R.id.imgFriendPhoto);
            tvName=itemView.findViewById(R.id.tvFriendName);
            tvEmail=itemView.findViewById(R.id.tvFriendEmail);
            btnGoToChat=itemView.findViewById(R.id.btnGoToChat);
        }
    }

    @NonNull
    @Override
    public FriendsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_list_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsListAdapter.ViewHolder holder, int position) {
        FriendListData tempData=friendList.get(position);
        holder.tvName.setText(tempData.getDisplayName());
        holder.tvEmail.setText(tempData.getEmailId());
        if(tempData.getPhotoUrl()!=null)
            Picasso.get().load(tempData.getPhotoUrl()).into(holder.imgPhoto);
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }
}
