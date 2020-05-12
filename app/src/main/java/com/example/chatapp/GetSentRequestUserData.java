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

public class GetSentRequestUserData {

    SentRequestUserDataCallback callbackInterface;
    ProgressDialog progressDialog;
    ArrayList<FriendListData> dataList;
    ArrayList<String> sentRequestsList;

    public interface SentRequestUserDataCallback{
        public void UserDataReceived(ArrayList<FriendListData> returnList);
    }

    GetSentRequestUserData(SentRequestUserDataCallback callbackInterface, ProgressDialog progressDialog){
        this.callbackInterface=callbackInterface;
        this.progressDialog=progressDialog;
        dataList=new ArrayList<FriendListData>();
        RetrieveData();
    }

    private int i=0;

    private void RetrieveData(){
        progressDialog.setTitle("Synchronizing with Database");
        progressDialog.setMessage("Retrieving Data");
        progressDialog.show();
        sentRequestsList=FriendRequestHandler.SentRequestsList;
        GetData();
    }

    private void GetData(){
        if(i<sentRequestsList.size()){
            DocumentReference userRef= FirebaseFirestore.getInstance().collection(FirebaseHandler.KEY_USER).document(sentRequestsList.get(i));
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
                            dataList.add(tempData);
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
        else {
            ReturnResult();
        }
    }

    private void ReturnResult(){
        progressDialog.dismiss();
        callbackInterface.UserDataReceived(dataList);
    }

}
