package com.example.chatapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserListFragment extends Fragment {

    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference dbRef=database.getReference();
    RecyclerView rcvUser;
    RecyclerView.Adapter userAdapter;
    RecyclerView.LayoutManager layoutManager;
    View view;

    public UserListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.fragment_user_list, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rcvUser=view.findViewById(R.id.rcvUser);
        rcvUser.setHasFixedSize(true);

        layoutManager=new LinearLayoutManager(this.getActivity());
        rcvUser.setLayoutManager(layoutManager);

        userAdapter=new UserAdapter(this.getActivity(),ApplicationClass.userList);
        rcvUser.setAdapter(userAdapter);

    }

}
