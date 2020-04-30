package com.example.chatapp;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ApplicationClass extends Application
{
    public static ArrayList<User> userList;

    @Override
    public void onCreate() {
        super.onCreate();
        userList=new ArrayList<User>();
    }
}
