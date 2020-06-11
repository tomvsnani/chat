package com.chat.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class NewConnectionsProfile extends AppCompatActivity {

    TextView usernameTextview;
    TextView addConnectionRequestTextview;
    EditText bioTextview;
    List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_connections_profile);
        ImageView imageView = findViewById(R.id.image);
        usernameTextview = findViewById(R.id.connectionusername);
        addConnectionRequestTextview = findViewById(R.id.addfriend);
        Glide.with(this)
                .load(GoogleSignIn.getLastSignedInAccount(this).getPhotoUrl())
                .into(imageView);
        final String username = getIntent().getStringExtra("username");


        if (username != null) {


            FirebaseDatabase.getInstance().getReference("users")
                    .child(GoogleSignIn.getLastSignedInAccount(this).getDisplayName())
                    .child("connections").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    list.add((String) dataSnapshot.getValue());
                    if (list.contains(username)) {
                        addConnectionRequestTextview.setText("You both are connected");
                        addConnectionRequestTextview.setClickable(false);
                        bioTextview.setClickable(false);
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
                    AcceptRequestModelClass acceptRequestModelClass = new AcceptRequestModelClass();
                    acceptRequestModelClass.setFriendRequestToUsername(username);
                    acceptRequestModelClass.setRequestAccepted(false);
                    acceptRequestModelClass.setFriendRequestFromUsername
                            (GoogleSignIn.getLastSignedInAccount(NewConnectionsProfile.this)
                                    .getDisplayName());

                    String requestKey = FirebaseDatabase.getInstance().getReference("users")
                            .child(username).child("FirendRequests")
                            .push().getKey();
                    acceptRequestModelClass.setKey(requestKey);
                    FirebaseDatabase.getInstance().getReference("users")
                            .child(username).child("FirendRequests")
                            .child(requestKey).setValue(acceptRequestModelClass);


                }
            });
        }

    }
}