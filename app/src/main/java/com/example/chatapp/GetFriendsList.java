package com.example.chatapp;

import android.app.ProgressDialog;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class GetFriendsList {

    private ArrayList<FriendListData> friendList;
    private ArrayList<String> nameList;
    ProgressDialog progressDialog;
    FriendsListInterface callBackInterface;

    public interface FriendsListInterface{
        public void callBackResult(ArrayList<FriendListData> listData);
    }

    GetFriendsList(FriendsListInterface callBackInterface, ProgressDialog progressDialog){
        this.callBackInterface=callBackInterface;
        this.progressDialog=progressDialog;
        nameList=FriendRequestHandler.FriendsList;
        friendList=new ArrayList<FriendListData>();
        RetrieveData();
    }

    private void RetrieveData(){
        progressDialog.setTitle("Synchronizing with Database");
        progressDialog.setMessage("Retrieving Data");
        progressDialog.show();
        GetData();
    }

    private int i=0;

    private void GetData() {
        if(i<nameList.size()){
            DocumentReference userRef= FirebaseFirestore.getInstance().collection(FirebaseHandler.KEY_USER).document(nameList.get(i));
            userRef.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot UserSnapshot) {
                            FriendListData tempData=new FriendListData();
                            tempData.setUId(UserSnapshot.getId());
                            Map<String,Object> userData=UserSnapshot.getData();
                            tempData.setDisplayName(userData.get(FirebaseHandler.KEY_USER_NAME).toString());
                            tempData.setEmailId(userData.get(FirebaseHandler.KEY_USER_EMAIL_ID).toString());
                            tempData.setPhotoExists((Boolean)userData.get(FirebaseHandler.KEY_USER_PHOTO_EXISTS));
                            friendList.add(tempData);
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            i++;
                            GetData();
                        }
                    });
        }
        else{
            ReturnResult();
        }

    }

    private void ReturnResult(){
        progressDialog.dismiss();
        callBackInterface.callBackResult(friendList);
    }

}
