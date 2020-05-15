package com.example.chatapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity implements FriendMessageHandler.MessageHandlerInterface {

    public static FriendListData FriendData;
    private final int RC_GET_IMAGE=1;

    TextView tvName,tvEmail;
    ImageView imgFriendPhoto,imgMessagePhoto;
    EditText etMessage;
    Button btnSubmit;
    Uri MessagePhotoUri=null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_GET_IMAGE){
            if(resultCode==RESULT_OK && data!=null && data.getData()!=null){
                MessagePhotoUri=data.getData();
                FirebaseHandler.AddNewPhotoMessage(FriendData.getUId(),MessagePhotoUri);
            }
        }
    }

    ArrayList<MessageData> listMessages;

    RecyclerView rcvMessages;
    RecyclerView.Adapter ChatAdapter;
    RecyclerView.LayoutManager ChatLayoutManager;

    @Override
    protected void onStart() {
        super.onStart();
        FriendMessageHandler.ChatActivityStart=true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        FriendMessageHandler.ChatActivityStart=false;
    }

    private void sortListByTime(){
        for(int i=0;i<listMessages.size()-1;i++){
            long min=listMessages.get(i).getTimeOfMessage();
            int minInd=i;
            for(int j=i+1;j<listMessages.size();j++){
                if(listMessages.get(j).getTimeOfMessage()<min){
                    minInd=j;
                    min=listMessages.get(j).getTimeOfMessage();
                }
            }
            MessageData tempMssg=listMessages.get(i);
            listMessages.set(i,listMessages.get(minInd));
            listMessages.set(minInd,tempMssg);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        tvName=findViewById(R.id.tvChatActivityFriendName);
        tvEmail=findViewById(R.id.tvChatActivityFriendEmail);
        imgFriendPhoto=findViewById(R.id.imgChatActivityFriendPhoto);
        etMessage=findViewById(R.id.etInputMessage);
        btnSubmit=findViewById(R.id.btnChatActivitySendMessage);
        imgMessagePhoto=findViewById(R.id.imgChatActivitySelectPhoto);

        rcvMessages=findViewById(R.id.rcvMessages);
        rcvMessages.setHasFixedSize(true);

        FriendMessageHandler.messageHandlerInterface=this;

        ChatLayoutManager=new LinearLayoutManager(this);
        rcvMessages.setLayoutManager(ChatLayoutManager);

        if(FriendMessageHandler.MessagesList.containsKey(FriendData.getUId())){
            listMessages=FriendMessageHandler.MessagesList.get(FriendData.getUId());
            sortListByTime();
            ChatAdapter=new MessageAdapter(this,FriendMessageHandler.MessagesList.get(FriendData.getUId()));
            rcvMessages.setAdapter(ChatAdapter);
            rcvMessages.scrollToPosition(ChatAdapter.getItemCount()-1);
        }


        tvName.setText(FriendData.getDisplayName());
        tvEmail.setText(FriendData.getEmailId());
        if(FriendData.getPhotoUrl()!=null)
            Picasso.get().load(FriendData.getPhotoUrl()).into(imgFriendPhoto);

        imgMessagePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"Select Image"),RC_GET_IMAGE);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message=etMessage.getText().toString();
                if(message.isEmpty()){
                    Toast.makeText(ChatActivity.this,"Message can't be Empty!",Toast.LENGTH_SHORT).show();
                }
                else{
                    MessageData newMessage=new MessageData();
                    newMessage.setMessage(message);
                    newMessage.setPhotoExists(false);
                    newMessage.setTimeOfMessage((new Date()).getTime());
                    newMessage.setSourceUId(CurrentUserData.uId);
                    newMessage.setDestinationUId(FriendData.getUId());
                    FirebaseHandler.AddNewMessage(newMessage);
                    etMessage.setText("");
                }
            }
        });

    }

    @Override
    public void OnEventTrigger() {
        if(ChatAdapter==null){
            if(FriendMessageHandler.MessagesList.containsKey(FriendData.getUId())){
                listMessages=FriendMessageHandler.MessagesList.get(FriendData.getUId());
                sortListByTime();
                ChatAdapter=new MessageAdapter(this,FriendMessageHandler.MessagesList.get(FriendData.getUId()));
                rcvMessages.setAdapter(ChatAdapter);
                rcvMessages.scrollToPosition(ChatAdapter.getItemCount()-1);
            }
        }
        else{
            sortListByTime();
            ChatAdapter.notifyDataSetChanged();
            rcvMessages.scrollToPosition(ChatAdapter.getItemCount()-1);
        }
    }
}
