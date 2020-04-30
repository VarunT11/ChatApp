package com.example.chatapp;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private ArrayList<UserMessage> messageList;

    MessageAdapter(Context context, ArrayList<UserMessage> list){
        messageList=list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CardView cv;
        TextView tvDate,tvTime,tvSourceName,tvMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cv=itemView.findViewById(R.id.cvMessage);
            tvDate=itemView.findViewById(R.id.tvMessageItemDate);
            tvTime=itemView.findViewById(R.id.tvMessageItemTime);
            tvSourceName=itemView.findViewById(R.id.tvMessageItemSourceName);
            tvMessage=itemView.findViewById(R.id.tvMessageItemData);
        }
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        UserMessage tempMessage=messageList.get(position);
        holder.itemView.setTag(tempMessage);
        holder.tvMessage.setText(tempMessage.getData());

        Date date=tempMessage.getTimeOfMessage();

        SimpleDateFormat tempDate=new SimpleDateFormat("EE, dd MMMM, y ");
        SimpleDateFormat tempTime=new SimpleDateFormat("h:mm a");

        holder.tvDate.setText(tempDate.format(date));
        holder.tvTime.setText(tempTime.format(date));

        if(tempMessage.getSourceUsername().equals(CurrentUserInfo.Username)){
            holder.tvSourceName.setText("YOU");
            holder.cv.setBackgroundResource(R.color.colorPrimaryDark);
            holder.tvSourceName.setGravity(Gravity.END);
            holder.tvMessage.setGravity(Gravity.END);
        }
        else{
            holder.cv.setBackgroundResource(R.color.colorPrimary);
            holder.tvSourceName.setText(tempMessage.getSourceName());
            holder.tvSourceName.setGravity(Gravity.START);
            holder.tvMessage.setGravity(Gravity.START);
        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
