package com.chat.chat;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class chatFragment extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Button sendButton;
    EditText replyText;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    Calendar calendar;
    ChatAdapter chatAdapter;
    GoogleSignInClient googleSignInClient;
    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference1;
    String username;
    Entity entity;
    List<Entity> list = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_chat);
        googleSignInClient = ((ApplicationClass) getApplication()).signInClient();
        entity = new Entity();
        sendButton = findViewById(R.id.send);
        replyText = findViewById(R.id.replytype);
        username = getIntent().getStringExtra("username");
        calendar=Calendar.getInstance();
        Log.d("timezone", String.valueOf(calendar.getTimeZone()));
        Log.d("time", String.valueOf( calendar.getTime()));


        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        chatAdapter = new ChatAdapter(this, googleSignInClient);
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView = findViewById(R.id.chatrecycler);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        submit();

    }

    public void submit() {
        if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            list.clear();

            Log.d("changedkk", list.toString());
            databaseReference = database.getReference("users")
                    .child(GoogleSignIn.getLastSignedInAccount(this).getDisplayName()).child("messages").child(username);
            databaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.d("datasnapchat", String.valueOf(dataSnapshot.getValue(Entity.class).getMessage()));
                    list.add(dataSnapshot.getValue(Entity.class));
                    chatAdapter.submit(list);


                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.d("datasnaplisttchat", dataSnapshot.toString());
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    list.remove(dataSnapshot.getValue(Entity.class));
                    chatAdapter.submit(list);
                    // chatAdapter.submit(dataSnapshot.getValue(Entity.class));
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


//

    @Override
    protected void onStart() {
        super.onStart();
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.send) {
                    if (!replyText.getText().toString().equals("")) {

                        String s = replyText.getText().toString();

                        entity.setFrom(GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getDisplayName());
                        entity.setTo(username);
                        entity.setMessage(s);
                        entity.setTimeofMessage(calendar.get(Calendar.ZONE_OFFSET));
                        if (GoogleSignIn.getLastSignedInAccount(getApplicationContext()) != null) {
                            databaseReference1 = database.getReference("users");
                            databaseReference1.child(GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getDisplayName()).child("messages")
                                    .child(username).push().setValue(entity);
                            databaseReference1.child(username).child("messages")
                                    .child(GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getDisplayName())
                                    .push().setValue(entity);
                        }
                    }
                }
            }
        });
    }
}
