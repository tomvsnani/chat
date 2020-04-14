package com.chat.chat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Database;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class Adapter extends ListAdapter {
    public static List<String> list = new ArrayList<>();
    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    Context context;
    GoogleSignInClient googleSignInClient;
    GoogleSignInOptions googleSignInOptions;
    DatabaseReference databaseReference1;
    TextView textView;
    Button button;
    String s;
    MainActivity mainActivity;


    private Entity entity;


    protected Adapter(Context context, GoogleSignInClient googleSignInClient) {
        super(Entity.diffcallAdapter);
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        this.googleSignInClient = googleSignInClient;

        this.context = context;
        entity = new Entity();
        this.mainActivity= (MainActivity) context;

    }


    @Override
    public void submitList(@Nullable List list) {
        super.submitList(list != null ? new ArrayList<String>(list) : null);
    }

    public void submit() {
        if (GoogleSignIn.getLastSignedInAccount(context) != null) {

            Log.d("changedkk", "ll");
            databaseReference1 = database.getReference("users");
           databaseReference1.child(GoogleSignIn.getLastSignedInAccount(context).getDisplayName()).child("status").setValue("online");

            databaseReference1.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.d("datasnaplist", dataSnapshot.toString());
                    list.add(dataSnapshot.getKey());
                    submitList(list);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.d("datasnaplistt", dataSnapshot.toString());
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    Log.d("datasnaplistremoved", dataSnapshot.toString());
                    list.remove(dataSnapshot.getKey());
                    submitList(list);
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        return new Holder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String s = list.get(position);
        textView.setText(s);
        if(!s.equals(GoogleSignIn.getLastSignedInAccount(context).getDisplayName())){
           button.setVisibility(View.GONE);

        }
        else
            textView.setVisibility(View.GONE);
    }



    @Override
    public int getItemCount() {
        if (!getCurrentList().isEmpty()) {
            return getCurrentList().size();
        } else
            return 0;
    }


    class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Holder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.usernames);

            button = itemView.findViewById(R.id.logout);

            textView.setOnClickListener(this);

            button.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.logout:
                    googleSignInClient.signOut();
                    //databaseReference1.removeValue();
                    break;
                case R.id.usernames:
                    Intent intent=new Intent(context,chatFragment.class);
                    intent.putExtra("username",((TextView)v).getText());
                    context.startActivity(intent);


            }
        }
    }

}