package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Time;
import java.text.DateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity implements UserAdapter.ItemClicked {

    TextView tvHelloMssg;
    Button btnLogout;

    TextView tvMessagesHeading;
    ImageView imgReturn;
    EditText etMssgInput;
    Button btnMssgSubmit;

    ArrayList<UserMessage> messages;

    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference dbRef=database.getReference();

    User messageUser;

    RecyclerView rcvMessages;
    RecyclerView.Adapter messageAdapter;
    RecyclerView.LayoutManager messageLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        tvHelloMssg = findViewById(R.id.tvChatActivityHelloMssg);
        btnLogout = findViewById(R.id.btnChatActivityLogout);
        tvHelloMssg.setText("Hello " + CurrentUserInfo.Name);

        messages=new ArrayList<UserMessage>();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrentUserInfo.LoggedIn = false;
                CurrentUserInfo.Name = null;
                CurrentUserInfo.Username = null;
                ChatActivity.this.finish();
            }
        });

        FragmentManager manager=this.getSupportFragmentManager();
        manager.beginTransaction()
                .show(manager.findFragmentById(R.id.fragment))
                .hide(manager.findFragmentById(R.id.fragment2))
                .commit();

        messageUser=null;
        tvMessagesHeading=findViewById(R.id.tvMessagesHeading);
        imgReturn=findViewById(R.id.ivReturn);
        etMssgInput=findViewById(R.id.etFragmentMessageInput);
        btnMssgSubmit=findViewById(R.id.btnFragmentMessageSubmit);

        rcvMessages=findViewById(R.id.rcvMessages);
        rcvMessages.setHasFixedSize(true);

        messageLayoutManager=new LinearLayoutManager(this);
        rcvMessages.setLayoutManager(messageLayoutManager);

        messageAdapter=new MessageAdapter(this,messages);
        rcvMessages.setAdapter(messageAdapter);

    }

    @Override
    public void onItemClicked(int index) {
        messageUser=ApplicationClass.userList.get(index);

        DatabaseReference myRef=dbRef.child(DbVariables.KEY_USER).child(CurrentUserInfo.Username).child(DbVariables.KEY_USER_MESSAGES).child(messageUser.getUsername());
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.hasChild(DbVariables.MESSAGES_SOURCE_USER) && dataSnapshot.hasChild(DbVariables.MESSAGES_DESTINATION_USER) &&
                dataSnapshot.hasChild(DbVariables.MESSAGES_TIME) && dataSnapshot.hasChild(DbVariables.MESSAGES_DATA)){

                    UserMessage tempMessage=new UserMessage();
                    tempMessage.setSourceUsername(dataSnapshot.child(DbVariables.MESSAGES_SOURCE_USER).getValue().toString());
                    tempMessage.setDestinationUsername(dataSnapshot.child(DbVariables.MESSAGES_DESTINATION_USER).getValue().toString());
                    tempMessage.setData(dataSnapshot.child(DbVariables.MESSAGES_DATA).getValue().toString());
                    tempMessage.setTimeOfMessage(new Date(Long.parseLong(dataSnapshot.child(DbVariables.MESSAGES_TIME).getValue().toString())));
                    tempMessage.setId(dataSnapshot.getKey());

                    if(tempMessage.getSourceUsername().equals(CurrentUserInfo.Username)){
                        tempMessage.setSourceName(CurrentUserInfo.Name);
                        tempMessage.setDestinationName(messageUser.getName());
                    }
                    else{
                        tempMessage.setDestinationName(CurrentUserInfo.Name);
                        tempMessage.setSourceName(messageUser.getName());
                    }

                    messages.add(tempMessage);
                    messageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.hasChild(DbVariables.MESSAGES_SOURCE_USER) && dataSnapshot.hasChild(DbVariables.MESSAGES_DESTINATION_USER) &&
                        dataSnapshot.hasChild(DbVariables.MESSAGES_TIME) && dataSnapshot.hasChild(DbVariables.MESSAGES_DATA) && !(messages.get(messages.size()-1).getId().equals(dataSnapshot.getKey()))){

                    UserMessage tempMessage=new UserMessage();
                    tempMessage.setSourceUsername(dataSnapshot.child(DbVariables.MESSAGES_SOURCE_USER).getValue().toString());
                    tempMessage.setDestinationUsername(dataSnapshot.child(DbVariables.MESSAGES_DESTINATION_USER).getValue().toString());
                    tempMessage.setData(dataSnapshot.child(DbVariables.MESSAGES_DATA).getValue().toString());
                    tempMessage.setTimeOfMessage(new Date(Long.parseLong(dataSnapshot.child(DbVariables.MESSAGES_TIME).getValue().toString())));
                    tempMessage.setId(dataSnapshot.getKey());

                    if(tempMessage.getSourceUsername().equals(CurrentUserInfo.Username)){
                        tempMessage.setSourceName(CurrentUserInfo.Name);
                        tempMessage.setDestinationName(messageUser.getName());
                    }
                    else{
                        tempMessage.setDestinationName(CurrentUserInfo.Name);
                        tempMessage.setSourceName(messageUser.getName());
                    }

                    messages.add(tempMessage);
                    messageAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        imgReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageUser=null;
                FragmentManager manager=ChatActivity.this.getSupportFragmentManager();
                manager.beginTransaction()
                        .show(manager.findFragmentById(R.id.fragment))
                        .hide(manager.findFragmentById(R.id.fragment2))
                        .commit();
                messages.clear();
                messageAdapter.notifyDataSetChanged();
            }
        });

        FragmentManager manager=this.getSupportFragmentManager();
        manager.beginTransaction()
                .hide(manager.findFragmentById(R.id.fragment))
                .show(manager.findFragmentById(R.id.fragment2))
                .commit();

        if(messageUser.getUsername().equals(CurrentUserInfo.Username))
            tvMessagesHeading.setText("Your Saved Notes");
        else
            tvMessagesHeading.setText("Your Messages with "+messageUser.getName());


        btnMssgSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mssg=etMssgInput.getText().toString();
                if(mssg.isEmpty()){
                    Toast.makeText(ChatActivity.this,"Message can't be Empty",Toast.LENGTH_SHORT).show();
                }
                else{
                    etMssgInput.setText("");

                    UserMessage message=new UserMessage(CurrentUserInfo.Name,messageUser.getName(),CurrentUserInfo.Username,messageUser.getUsername(),mssg,new Date());

                    DatabaseReference ref1=dbRef.child(DbVariables.KEY_USER).child(CurrentUserInfo.Username).child(DbVariables.KEY_USER_MESSAGES).child(messageUser.getUsername()).push();
                    ref1.child(DbVariables.MESSAGES_SOURCE_USER).setValue(message.getSourceUsername());
                    ref1.child(DbVariables.MESSAGES_DESTINATION_USER).setValue(message.getDestinationUsername());
                    ref1.child(DbVariables.MESSAGES_DATA).setValue(message.getData());
                    ref1.child(DbVariables.MESSAGES_TIME).setValue(message.getTimeOfMessage().getTime());

                    if(!CurrentUserInfo.Username.equals(messageUser.getUsername())) {
                        DatabaseReference ref2 = dbRef.child(DbVariables.KEY_USER).child(messageUser.getUsername()).child(DbVariables.KEY_USER_MESSAGES).child(CurrentUserInfo.Username).push();
                        ref2.child(DbVariables.MESSAGES_SOURCE_USER).setValue(message.getSourceUsername());
                        ref2.child(DbVariables.MESSAGES_DESTINATION_USER).setValue(message.getDestinationUsername());
                        ref2.child(DbVariables.MESSAGES_DATA).setValue(message.getData());
                        ref2.child(DbVariables.MESSAGES_TIME).setValue(message.getTimeOfMessage().getTime());
                    }

                }
            }
        });

    }
}
