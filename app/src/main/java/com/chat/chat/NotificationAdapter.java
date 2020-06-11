package com.chat.chat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends ListAdapter {

    protected NotificationAdapter() {
        super(AcceptRequestModelClass.diff);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.connections_listview_rowlayout, parent, false);
        return new NotificationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d("positionnn", String.valueOf(position));

        AcceptRequestModelClass acceptRequestModelClass = (AcceptRequestModelClass) getCurrentList().get(position);
        ((NotificationViewHolder) (holder)).userNameTextview.setText(acceptRequestModelClass.getFriendRequestFromUsername());
        if (acceptRequestModelClass.isRequestAccepted) {
            ((NotificationViewHolder) (holder)).addFriendTextview.setText("Request Accepted");
            FirebaseDatabase.getInstance().getReference("users")
                    .child(acceptRequestModelClass.getFriendRequestToUsername())
                    .child("FirendRequests")
                    .child(acceptRequestModelClass.getKey())
                    .removeValue();
            List<AcceptRequestModelClass> acceptRequestModelClassList=new ArrayList<>(getCurrentList());
            acceptRequestModelClassList.remove(position);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            submitList(acceptRequestModelClassList);


        }
        else
            ((NotificationViewHolder) (holder)).addFriendTextview.setText("Accept Friend request");


    }

    @Override
    public void submitList(@Nullable List list) {
        super.submitList(list==null?null:new ArrayList(list));
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextview;
        TextView addFriendTextview;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextview = itemView.findViewById(R.id.label);
            addFriendTextview = itemView.findViewById(R.id.add_connection);
            addFriendTextview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AcceptRequestModelClass acceptRequestModelClass = (AcceptRequestModelClass) getCurrentList().get(getAdapterPosition());
                    acceptRequestModelClass.setRequestAccepted(true);
                    FirebaseDatabase.getInstance().getReference("users").child(acceptRequestModelClass.getFriendRequestFromUsername())
                            .child("FirendRequests")
                            .child(acceptRequestModelClass.getKey())
                            .setValue(acceptRequestModelClass);

                    FirebaseDatabase.getInstance().getReference("users").child(acceptRequestModelClass.getFriendRequestFromUsername())
                            .child("connections")
                            .child(acceptRequestModelClass.getKey())
                            .setValue(acceptRequestModelClass.getFriendRequestToUsername());
                    Log.d("positionnnadapter", String.valueOf(getAdapterPosition()));
                    FirebaseDatabase.getInstance().getReference("users").child(acceptRequestModelClass.getFriendRequestToUsername())
                            .child("connections")
                            .child(acceptRequestModelClass.getKey())
                            .setValue(acceptRequestModelClass.getFriendRequestFromUsername());
                    List<AcceptRequestModelClass> acceptRequestModelClassList=new ArrayList<>(getCurrentList());

                    acceptRequestModelClassList.set(getAdapterPosition(),acceptRequestModelClass);
                    submitList(acceptRequestModelClassList);
                    notifyDataSetChanged();
                }
            });
        }
    }
}
