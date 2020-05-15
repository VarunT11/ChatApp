package com.example.chatapp;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class FriendMessageHandler {

    static final String DB_KEY_MESSAGES="Messages";
    static final String DB_KEY_MESSAGE_SOURCE_UID="Source";
    static final String DB_KEY_MESSAGE_DESTINATION_UID="Destination";
    static final String DB_KEY_MESSAGE_TIME_OF_MESSAGE="MessageTime";
    static final String DB_KEY_MESSAGE_DATA="MessageData";
    static final String DB_KEY_MESSAGE_PHOTO_EXISTS="PhotoExists";
    static final String DB_KEY_MESSAGE_SEEN_STATUS="Seen";
    static boolean ChatActivityStart;

    static Map<String, ArrayList<MessageData>> MessagesList;
    private static ChildEventListener MessagesListener;
    private Context context;

    static MessageHandlerInterface messageHandlerInterface;

    public interface MessageHandlerInterface{
        public void OnEventTrigger();
    }

    FriendMessageHandler(Context context){
        this.context=context;
        CreateEventListener();
    }

    MessageData getMessageFromSnapshot(DataSnapshot messageSnapshot){
        MessageData newMessage=new MessageData();
        newMessage.setMessageId(messageSnapshot.getKey());
        newMessage.setSourceUId(messageSnapshot.child(DB_KEY_MESSAGE_SOURCE_UID).getValue().toString());
        newMessage.setDestinationUId(messageSnapshot.child(DB_KEY_MESSAGE_DESTINATION_UID).getValue().toString());
        newMessage.setMessage(messageSnapshot.child(DB_KEY_MESSAGE_DATA).getValue().toString());
        newMessage.setTimeOfMessage((Long)messageSnapshot.child(DB_KEY_MESSAGE_TIME_OF_MESSAGE).getValue());
        newMessage.setPhotoExists((Boolean)messageSnapshot.child(DB_KEY_MESSAGE_PHOTO_EXISTS).getValue());
        return newMessage;
    }

    boolean allMessageChildExists(DataSnapshot messageSnapshot){
        return (messageSnapshot.hasChild(DB_KEY_MESSAGE_SOURCE_UID) && messageSnapshot.hasChild(DB_KEY_MESSAGE_DESTINATION_UID) && messageSnapshot.hasChild(DB_KEY_MESSAGE_TIME_OF_MESSAGE)
        && messageSnapshot.hasChild(DB_KEY_MESSAGE_PHOTO_EXISTS) && messageSnapshot.hasChild(DB_KEY_MESSAGE_DATA) && messageSnapshot.hasChild(DB_KEY_MESSAGE_SEEN_STATUS));
    }

    boolean MessageNotExists(String UId, String MessageId){
        ArrayList<MessageData> messageData=MessagesList.get(UId);
        for(int i=0;i<messageData.size();i++){
            if(messageData.get(i).getMessageId().equals(MessageId))
                return false;
        }
        return true;
    }

    private void CreateEventListener(){
        MessagesListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MessagesList.put(dataSnapshot.getKey(),new ArrayList<MessageData>());
                for(DataSnapshot messageSnapshot:dataSnapshot.getChildren()){
                    if(allMessageChildExists(messageSnapshot)){
                        if(MessageNotExists(dataSnapshot.getKey(),messageSnapshot.getKey())) {
                            MessagesList.get(dataSnapshot.getKey()).add(getMessageFromSnapshot(messageSnapshot));
                            if(ChatActivityStart) {
                                if(ChatActivity.FriendData.getUId().equals(dataSnapshot.getKey()))
                                    messageHandlerInterface.OnEventTrigger();
                                else {
                                    if(messageSnapshot.child(DB_KEY_MESSAGE_SEEN_STATUS).getValue().equals(false))
                                        new NotificationHandler(context,dataSnapshot.getKey(),messageSnapshot.child(DB_KEY_MESSAGE_DATA).getValue().toString()
                                                ,messageSnapshot.getKey(),"FriendMessage");
                                }
                            }
                            else {
                                if(messageSnapshot.child(DB_KEY_MESSAGE_SEEN_STATUS).getValue().equals(false))
                                    new NotificationHandler(context,dataSnapshot.getKey(),messageSnapshot.child(DB_KEY_MESSAGE_DATA).getValue().toString()
                                            ,messageSnapshot.getKey(),"FriendMessage");
                            }
                            FirebaseDatabase.getInstance().getReference().child(DB_KEY_MESSAGES).child(CurrentUserData.uId)
                                    .child(dataSnapshot.getKey()).child(messageSnapshot.getKey()).child(DB_KEY_MESSAGE_SEEN_STATUS).setValue(true);
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for(DataSnapshot messageSnapshot:dataSnapshot.getChildren()){
                    if(allMessageChildExists(messageSnapshot)){
                        if(MessageNotExists(dataSnapshot.getKey(),messageSnapshot.getKey())) {
                            MessagesList.get(dataSnapshot.getKey()).add(getMessageFromSnapshot(messageSnapshot));
                            if(ChatActivityStart) {
                                if(ChatActivity.FriendData.getUId().equals(dataSnapshot.getKey()))
                                    messageHandlerInterface.OnEventTrigger();
                                else {
                                    if(messageSnapshot.child(DB_KEY_MESSAGE_SEEN_STATUS).getValue().equals(false))
                                        new NotificationHandler(context,dataSnapshot.getKey(),messageSnapshot.child(DB_KEY_MESSAGE_DATA).getValue().toString()
                                                ,messageSnapshot.getKey(),"FriendMessage");
                                }
                            }
                            else {
                                if(messageSnapshot.child(DB_KEY_MESSAGE_SEEN_STATUS).getValue().equals(false))
                                    new NotificationHandler(context,dataSnapshot.getKey(),messageSnapshot.child(DB_KEY_MESSAGE_DATA).getValue().toString()
                                            ,messageSnapshot.getKey(),"FriendMessage");
                            }
                            FirebaseDatabase.getInstance().getReference().child(DB_KEY_MESSAGES).child(CurrentUserData.uId)
                                    .child(dataSnapshot.getKey()).child(messageSnapshot.getKey()).child(DB_KEY_MESSAGE_SEEN_STATUS).setValue(true);
                        }
                    }
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
        };
    }


    public static void setUpRequestListener(){
        ChatActivityStart=false;
        MessagesList=new HashMap<>();
        DatabaseReference userRef=FirebaseDatabase.getInstance().getReference().child(DB_KEY_MESSAGES).child(CurrentUserData.uId);
        userRef.addChildEventListener(MessagesListener);
    }

    public static void onSignOut(){
        MessagesList=null;
        DatabaseReference userRef=FirebaseDatabase.getInstance().getReference().child(DB_KEY_MESSAGES).child(CurrentUserData.uId);
        userRef.removeEventListener(MessagesListener);
    }

}
