package com.example.chatapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class NotificationHandler {

    Context context;
    String UId;
    String Status;
    String DisplayName;

    NotificationHandler(Context context,String UId, String Status){
        this.UId=UId;
        this.Status=Status;
        this.context=context;
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
            notificationManagerCompat.notify(1,notifyBuilder.build());
        }

        if(Status.equals(FirebaseHandler.DB_VALUE_REQUEST_ACCEPTED)){
            Intent intent=new Intent(context,UpdateProfile.class);
            PendingIntent pendingIntent=PendingIntent.getActivity(context,0,intent,0);
            notifyBuilder.setContentText(DisplayName+" accepted your Friend Request")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(2,notifyBuilder.build());
        }
    }

}
