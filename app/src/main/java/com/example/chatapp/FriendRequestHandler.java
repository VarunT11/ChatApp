package com.example.chatapp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FriendRequestHandler{

    public static ArrayList<String> AlreadyFriendList;
    public static ArrayList<String> SentRequestsList;
    public static ArrayList<String> ReceivedRequestsList;
    public static ArrayList<String> FriendsList;
    private static ChildEventListener UserRequestListener;

    private Context context;

    FriendRequestHandler(Context context){
        CreateEventListener();
        this.context=context;
    }

    public void CreateEventListener(){
        UserRequestListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                AlreadyFriendList.add(dataSnapshot.getKey());
                if(dataSnapshot.getValue().equals(FirebaseHandler.DB_VALUE_REQUEST_SENT))
                    SentRequestsList.add(dataSnapshot.getKey());
                if(dataSnapshot.getValue().equals(FirebaseHandler.DB_VALUE_REQUEST_RECEIVED)) {
                    ReceivedRequestsList.add(dataSnapshot.getKey());
                    new NotificationHandler(context,dataSnapshot.getKey(),dataSnapshot.getValue().toString());
                }
                if(dataSnapshot.getValue().equals(FirebaseHandler.DB_VALUE_REQUEST_ACCEPTED))
                    FriendsList.add(dataSnapshot.getKey());
                Log.d("FriendRequest",dataSnapshot.getValue()+" "+dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FriendsList.add(dataSnapshot.getKey());
                if(SentRequestsList.contains(dataSnapshot.getKey())){
                    SentRequestsList.remove(dataSnapshot.getKey());
                    Log.d("MESSAGE","Sent Request to "+dataSnapshot.getKey()+" was Accepted");
                    CurrentUserData.NumFriends++;
                    new NotificationHandler(context,dataSnapshot.getKey(),dataSnapshot.getValue().toString());
                }
                if(ReceivedRequestsList.contains(dataSnapshot.getKey())) {
                    ReceivedRequestsList.remove(dataSnapshot.getKey());
                    Log.d("MESSAGE", "Received Request from " + dataSnapshot.getKey() + " was Accepted");
                    CurrentUserData.NumFriends++;
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue().equals(FirebaseHandler.DB_VALUE_REQUEST_SENT))
                    SentRequestsList.remove(dataSnapshot.getKey());
                if(dataSnapshot.getValue().equals(FirebaseHandler.DB_VALUE_REQUEST_RECEIVED))
                    ReceivedRequestsList.remove(dataSnapshot.getKey());
                AlreadyFriendList.remove(dataSnapshot.getKey());
                Log.d("Cancelled Request",dataSnapshot.getValue()+" "+dataSnapshot.getKey());
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
        AlreadyFriendList=new ArrayList<String>();
        SentRequestsList=new ArrayList<String>();
        ReceivedRequestsList=new ArrayList<String>();
        FriendsList=new ArrayList<String>();
        DatabaseReference userFriendRef= FirebaseDatabase.getInstance().getReference().child(FirebaseHandler.DB_KEY_FRIEND_REQUESTS).child(CurrentUserData.uId);
        userFriendRef.addChildEventListener(UserRequestListener);
    };

    public static void onSignOut(){
        AlreadyFriendList=null;
        DatabaseReference userFriendRef= FirebaseDatabase.getInstance().getReference().child(FirebaseHandler.DB_KEY_FRIEND_REQUESTS).child(CurrentUserData.uId);
        userFriendRef.removeEventListener(UserRequestListener);
    }


}
