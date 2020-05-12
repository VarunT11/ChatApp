package com.example.chatapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class GetSuggestedFriends {

    ArrayList<FriendListData> friendListData;
    GetCallBackInterface callBackInterface;
    ProgressDialog progressDialog;

    public interface GetCallBackInterface{
        public void getFriends(ArrayList<FriendListData> listData);
    }

    public GetSuggestedFriends(GetCallBackInterface FriendCallBackInterface, ProgressDialog progressDialogBox){
        callBackInterface=FriendCallBackInterface;
        progressDialog=progressDialogBox;
        friendListData=new ArrayList<FriendListData>();
        progressDialog.setTitle("Retrieving Data");
        progressDialog.setMessage("Getting Users List");
        progressDialog.show();
        CollectionReference UserListRef= FirebaseFirestore.getInstance().collection(FirebaseHandler.KEY_USER);
        UserListRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot UserSnapshot:queryDocumentSnapshots.getDocuments()){
                            if(UserSnapshot.getId().equals(CurrentUserData.uId))
                                continue;
                            FriendListData tempData=new FriendListData();
                            tempData.setUId(UserSnapshot.getId());
                            Map<String,Object> userData=UserSnapshot.getData();
                            tempData.setDisplayName(userData.get(FirebaseHandler.KEY_USER_NAME).toString());
                            tempData.setEmailId(userData.get(FirebaseHandler.KEY_USER_EMAIL_ID).toString());
                            tempData.setPhotoExists((Boolean)userData.get(FirebaseHandler.KEY_USER_PHOTO_EXISTS));
                            friendListData.add(tempData);
                        }
                        Log.d("Getting All Users List","Task Successful");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Getting All Users List","Task Failed",e);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        RemoveAlreadyFriends();
                        Log.d("Getting All Users List","Task Completed");
                    }
                });
    }


    private void RemoveAlreadyFriends(){
        int i=0;
        while(i<friendListData.size()){
            if(FriendRequestHandler.AlreadyFriendList.contains(friendListData.get(i).getUId()))
                friendListData.remove(i);
            else
                i++;
        }
        ReturnResult();
    }


    private void ReturnResult(){
        progressDialog.dismiss();
        callBackInterface.getFriends(friendListData);
    }

}
