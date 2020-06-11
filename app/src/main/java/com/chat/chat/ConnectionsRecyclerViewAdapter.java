package com.chat.chat;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ConnectionsRecyclerViewAdapter extends RecyclerView.Adapter<ConnectionsRecyclerViewAdapter.Holder> {

    Context context;
    List<String> list = new ArrayList<>();

    public ConnectionsRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.connections_listview_rowlayout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        ((Holder) (holder)).usernameTextview.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return Math.max(list.size(), 0);
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView usernameTextview;
        TextView add_connection;
        LinearLayout linearLayout;

        public Holder(@NonNull View itemView) {

            super(itemView);
            usernameTextview = itemView.findViewById(R.id.label);
            add_connection = itemView.findViewById(R.id.add_connection);
            linearLayout = itemView.findViewById(R.id.connectionslinear);
            add_connection.setVisibility(View.GONE);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, chatFragment.class);
                    intent.putExtra("username", (String) (list.get(getAdapterPosition())));
                    context.startActivity(intent);

                  String key=  FirebaseDatabase.getInstance().getReference("users")
                            .child(GoogleSignIn.getLastSignedInAccount(context).getDisplayName())
                            .child("recents")
                            .push().getKey();

                    FirebaseDatabase.getInstance().getReference("users")
                            .child(GoogleSignIn.getLastSignedInAccount(context).getDisplayName())
                            .child("recents")
                            .child(key)
                            .setValue(list.get(getAdapterPosition()));
                }
            });
        }


    }
}
