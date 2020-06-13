package com.chat.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
RecyclerView recyclerView;
LinearLayoutManager linearLayoutManager;
NotificationAdapter notificationAdapter;
Toolbar toolbar;
List<AcceptRequestModelClass> acceptRequestModelClassList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        recyclerView=findViewById(R.id.notificationrecycler);
        linearLayoutManager=new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        notificationAdapter=new NotificationAdapter();
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(notificationAdapter);
        toolbar=findViewById(R.id.toolbar_notification);
        toolbar.setTitle("Friend Requests");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FirebaseDatabase.getInstance().getReference("users")
                .child(GoogleSignIn.getLastSignedInAccount(this).getDisplayName())
                .child("FirendRequests")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                       AcceptRequestModelClass acceptRequestModelClass= dataSnapshot.getValue(AcceptRequestModelClass.class);
                   acceptRequestModelClassList.add(acceptRequestModelClass);
                   notificationAdapter.submitList(acceptRequestModelClassList);
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
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();
        return true;
    }
}