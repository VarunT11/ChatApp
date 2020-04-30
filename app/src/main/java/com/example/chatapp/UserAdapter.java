package com.example.chatapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>
{

    private ArrayList<User> people;

    ItemClicked activity;

    public interface ItemClicked{
        void onItemClicked(int index);
    }

    UserAdapter(Context context, ArrayList<User> list){
        people=list;
        activity=(ItemClicked) context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName, tvUsername, tvID;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvID=itemView.findViewById(R.id.tvUserItemNumber);
            tvName=itemView.findViewById(R.id.tvUserItemName);
            tvUsername=itemView.findViewById(R.id.tvUserItemUsername);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onItemClicked(people.indexOf((User) v.getTag()));
                }
            });

        }
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {

        holder.itemView.setTag(people.get(position));
        holder.tvID.setText(Integer.toString(position+1));
        holder.tvName.setText(people.get(position).getName());
        holder.tvUsername.setText(people.get(position).getUsername());

    }

    @Override
    public int getItemCount() {
        return people.size();
    }
}
