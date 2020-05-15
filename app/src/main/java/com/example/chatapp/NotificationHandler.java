package com.example.chatapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.Map;

public class NotificationHandler {

    Context context;
    String UId;
    String Status;
    String DisplayName;
    String MessageData;
    String MessageID;

    NotificationHandler(Context context,String UId, String Status){
        this.UId=UId;
        this.Status=Status;
        this.context=context;
        GetUserDetails();
    }

    NotificationHandler(Context context,String UId, String MessageData,String MessageID, String Status){
        this.UId=UId;
        this.context=context;
        this.Status=Status;
        this.MessageData=MessageData;
        this.MessageID=MessageID;
        GetUserDetails();
    }

    private void GetUserDetails(){
        DocumentReference userRef= FirebaseFirestore.getInstance().collection(FirebaseHandler.KEY_USER).document(UId);
        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String,Object> userData=documentSnapshot.getData();
                        DisplayName=userData.get(FirebaseHandler.KEY_USER_NAME).toString();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DisplayNotification();
                    }
                });
    }

    private void DisplayNotification(){

        NotificationCompat.Builder notifyBuilder=new NotificationCompat.Builder(context,MainActivity.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("ChatApp Notification");

        if(Status.equals(FirebaseHandler.DB_VALUE_REQUEST_RECEIVED)){
            Intent intent=new Intent(context,ViewReceivedRequests.class);
            PendingIntent pendingIntent=PendingIntent.getActivity(context,0,intent,0);
            notifyBuilder.setContentText(DisplayName+" wants to be Friends with You")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(UId.hashCode(),notifyBuilder.build());
            FirebaseDatabase.getInstance().getReference().child(FirebaseHandler.DB_KEY_FRIEND_REQUESTS).child(CurrentUserData.uId).child(UId).child(FirebaseHandler.DB_KEY_REQUEST_SEEN).setValue(true);
        }

        if(Status.equals(FirebaseHandler.DB_VALUE_REQUEST_ACCEPTED)){
            Intent intent=new Intent(context,UpdateProfile.class);
            PendingIntent pendingIntent=PendingIntent.getActivity(context,0,intent,0);
            notifyBuilder.setContentText(DisplayName+" accepted your Friend Request")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(UId.hashCode(),notifyBuilder.build());
            FirebaseDatabase.getInstance().getReference().child(FirebaseHandler.DB_KEY_FRIEND_REQUESTS).child(CurrentUserData.uId).child(UId).child(FirebaseHandler.DB_KEY_REQUEST_SEEN).setValue(true);
        }

        if(Status.equals("FriendMessage")){
            Intent intent=new Intent(context,UpdateProfile.class);
            PendingIntent pendingIntent=PendingIntent.getActivity(context,0,intent,0);
            notifyBuilder.setContentText("You have a new Message from "+DisplayName+"\n MESSAGE: "+MessageData)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(MessageID.hashCode(),notifyBuilder.build());
        }
    }

}
