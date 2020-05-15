package com.example.chatapp;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class AcceptFriendRequest {

    private AcceptRequestInterface callBackInterface;
    private String FriendUId;

    public interface AcceptRequestInterface{
        public void RequestAccepted();
    }

    AcceptFriendRequest(AcceptRequestInterface callBackInterface, String FriendUId){
        this.callBackInterface=callBackInterface;
        this.FriendUId=FriendUId;
        UpdateDatabase();
    }

    private void UpdateDatabase(){
        DatabaseReference UserRef= FirebaseDatabase.getInstance().getReference().child(FirebaseHandler.DB_KEY_FRIEND_REQUESTS);
        UserRef.child(CurrentUserData.uId).child(FriendUId).child(FirebaseHandler.DB_KEY_FRIEND_REQUESTS_STATUS).setValue(FirebaseHandler.DB_VALUE_REQUEST_ACCEPTED);
        UserRef.child(CurrentUserData.uId).child(FriendUId).child(FirebaseHandler.DB_KEY_REQUEST_SEEN).setValue(true);
        UserRef.child(FriendUId).child(CurrentUserData.uId).child(FirebaseHandler.DB_KEY_FRIEND_REQUESTS_STATUS).setValue(FirebaseHandler.DB_VALUE_REQUEST_ACCEPTED);
        UserRef.child(FriendUId).child(CurrentUserData.uId).child(FirebaseHandler.DB_KEY_REQUEST_SEEN).setValue(false);
        UpdateFirestore();
    }

    private void UpdateFirestore(){
        final Map<String,Object> newData=new HashMap<>();
        newData.put("RequestAcceptTime",(new Date()).getTime());
        DocumentReference UserRef=FirebaseFirestore.getInstance().collection(FirebaseHandler.KEY_USER)
                .document(CurrentUserData.uId).collection(FirebaseHandler.KEY_USER_FRIEND).document(FriendUId);
        UserRef.set(newData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        DocumentReference FriendRef=FirebaseFirestore.getInstance().collection(FirebaseHandler.KEY_USER)
                                .document(FriendUId).collection(FirebaseHandler.KEY_USER_FRIEND).document(CurrentUserData.uId);
                        FriendRef.set(newData)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        UpdateUserData();
                                    }
                                });
                    }
                });
    }

    private long NumFriends;

    private void UpdateUserData(){
        final DocumentReference userRef=FirebaseFirestore.getInstance().collection(FirebaseHandler.KEY_USER).document(CurrentUserData.uId);
        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String,Object> userData=documentSnapshot.getData();
                        NumFriends=(Long)userData.get(FirebaseHandler.KEY_USER_NUM_FRIENDS);
                        NumFriends++;
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Map<String,Object> userData=new HashMap<>();
                        userData.put(FirebaseHandler.KEY_USER_NUM_FRIENDS,NumFriends);
                        userRef.update(userData)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        UpdateFriendData();
                                    }
                                });
                    }
                });
    }

    private void UpdateFriendData(){
        final DocumentReference userRef=FirebaseFirestore.getInstance().collection(FirebaseHandler.KEY_USER).document(FriendUId);
        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String,Object> userData=documentSnapshot.getData();
                        NumFriends=(Long)userData.get(FirebaseHandler.KEY_USER_NUM_FRIENDS);
                        NumFriends++;
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Map<String,Object> userData=new HashMap<>();
                        userData.put(FirebaseHandler.KEY_USER_NUM_FRIENDS,NumFriends);
                        userRef.update(userData)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        ReturnResult();
                                    }
                                });
                    }
                });
    }

    private void ReturnResult(){
        callBackInterface.RequestAccepted();
    }
}
