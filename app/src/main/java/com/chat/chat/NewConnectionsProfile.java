package com.chat.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewConnectionsProfile extends AppCompatActivity {

    TextView usernameTextview;
    TextView addConnectionRequestTextview;
    EditText bioTextview;
    List<String> list = new ArrayList<>();
    Toolbar toolbar;
    TextView unFriendTextview;
    TextView messageTextview;
    String googleusername;
    DataSnapshot getKeyOnUnfriend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_connections_profile);
        final ImageView imageView = findViewById(R.id.image);
        usernameTextview = findViewById(R.id.connectionusername);
        addConnectionRequestTextview = findViewById(R.id.addfriend);
        toolbar = findViewById(R.id.newconnection_toolbar);
        unFriendTextview = findViewById(R.id.unfriend);
        messageTextview = findViewById(R.id.message);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final String username = getIntent().getStringExtra("username");

        googleusername=  GoogleSignIn.getLastSignedInAccount(NewConnectionsProfile.this)
                .getDisplayName();
        FirebaseDatabase.getInstance().getReference("users")
                .child(username)
                .child("userdetails").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                HashMap<String,String> hashMap= (HashMap<String, String>) dataSnapshot.getValue();
                Glide.with(NewConnectionsProfile.this).
                        load(hashMap.get("propicurl")
                        )
                        .transform(new CircleCrop()).into(imageView);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        if (username != null) {
            FirebaseDatabase.getInstance().getReference("users")
                    .child(googleusername)
                    .child("connections").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    HashMap<String, String> hashMap = (HashMap<String, String>) dataSnapshot.getValue();
                    list.add(hashMap.get("username"));
                    if (list.contains(username)) {
                        addConnectionRequestTextview.setText("You both are connected");
                        addConnectionRequestTextview.setClickable(false);
                        messageTextview.setVisibility(View.VISIBLE);
                        unFriendTextview.setVisibility(View.VISIBLE);
                        getKeyOnUnfriend=dataSnapshot;
                        //  bioTextview.setClickable(false);
                    }

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            })


            ;


            usernameTextview.setText(username);
            addConnectionRequestTextview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addConnectionRequestTextview.setText("request sent");
                    AcceptRequestModelClass acceptRequestModelClass = new AcceptRequestModelClass();
                    acceptRequestModelClass.setFriendRequestToUsername(username);
                    acceptRequestModelClass.setRequestAccepted(false);
                    acceptRequestModelClass.setFriendRequestFromUsername
                            (googleusername);

                    String requestKey = FirebaseDatabase.getInstance().getReference("users")
                            .child(username).child("FirendRequests")
                            .push().getKey();
                    acceptRequestModelClass.setKey(requestKey);
                    FirebaseDatabase.getInstance().getReference("users")
                            .child(username).child("FirendRequests")
                            .child(requestKey).setValue(acceptRequestModelClass);


                }
            });

            messageTextview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(NewConnectionsProfile.this, chatFragment.class);
                    intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("username", (username));
                    startActivity(intent);
                }
            });

            unFriendTextview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseDatabase.getInstance().getReference("users").child(googleusername)
                            .child("connections")
                            .child(getKeyOnUnfriend.getKey()).removeValue();
                    FirebaseDatabase.getInstance().getReference("users").child(username)
                            .child("connections")
                            .child(getKeyOnUnfriend.getKey()).removeValue();
                }
            });
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }
}