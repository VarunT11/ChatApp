package com.example.chatapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    ArrayList<MessageData> messageList;
    Context context;

    MessageAdapter(Context context, ArrayList<MessageData> list){
        this.context=context;
        messageList=list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvDate,tvTime,tvSender,tvMessage;
        CardView cvMessageBox;
        ImageView imgMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate=itemView.findViewById(R.id.tvMessageDate);
            tvTime=itemView.findViewById(R.id.tvMessageTime);
            tvSender=itemView.findViewById(R.id.tvMessageSender);
            tvMessage=itemView.findViewById(R.id.tvMessageData);
            cvMessageBox=itemView.findViewById(R.id.cvMessageBox);
            imgMessage=itemView.findViewById(R.id.imgMessage);
        }
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_item,parent,false);
        return new ViewHolder(v);
    }

    ImageView photoView;
    Uri photoURL;

    private void getMessagePhoto(MessageData newMessage){
        StorageReference mssgRef= FirebaseStorage.getInstance().getReference().child(FirebaseHandler.KEY_USER).child(CurrentUserData.uId)
                .child("PhotoMessages").child(newMessage.getMessageId());
        mssgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                photoURL=uri;
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Picasso.get().load(photoURL).into(photoView);
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessageData newMessage=messageList.get(position);
        holder.itemView.setTag(messageList.get(position));
        holder.tvMessage.setText(newMessage.getMessage());
        String senderName="";

        if(newMessage.isPhotoExists()) {
            holder.imgMessage.setVisibility(View.VISIBLE);
            holder.tvMessage.setVisibility(View.GONE);
            photoView=holder.imgMessage;
            getMessagePhoto(newMessage);
        }
        else {
            holder.tvMessage.setVisibility(View.VISIBLE);
            holder.imgMessage.setVisibility(View.GONE);
        }

        if(ChatActivity.FriendData.getUId().equals(newMessage.getSourceUId())) {
            senderName = ChatActivity.FriendData.getDisplayName();
            holder.tvSender.setGravity(Gravity.START);
            holder.tvMessage.setGravity(Gravity.START);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.cvMessageBox.setCardBackgroundColor(context.getColor(R.color.colorTextIcons));
                holder.tvMessage.setTextColor(context.getColor(R.color.colorPrimaryText));
            }
        }

        if(CurrentUserData.uId.equals(newMessage.getSourceUId())) {
            senderName = "You";
            holder.tvSender.setGravity(Gravity.END);
            holder.tvMessage.setGravity(Gravity.END);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.cvMessageBox.setCardBackgroundColor(context.getColor(R.color.colorPrimary));
                holder.tvMessage.setTextColor(context.getColor(R.color.colorTextIcons));
            }
        }

        holder.tvSender.setText(senderName);
        Date date=new Date(newMessage.getTimeOfMessage());
        String dateOfMessage=(new SimpleDateFormat("dd MMMM, yyyy")).format(date);
        String timeOfMessage=(new SimpleDateFormat("hh:mm a")).format(date);
        holder.tvDate.setText(dateOfMessage);
        holder.tvTime.setText(timeOfMessage);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
