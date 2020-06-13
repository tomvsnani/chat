package com.chat.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddConnectionsActivity extends AppCompatActivity {
    RecyclerView userRecyclerView;
    AutoCompleteTextView searchUserEditText;
    ImageButton searchUserImageButton;
    ConnectionsRecyclerViewAdapter connectionsRecyclerViewAdapter;
    LinearLayoutManager linearLayoutManager;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference connectionsReference;
    String googleUsername;
    List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    List<String> autoCompleteList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_connections);
        userRecyclerView = findViewById(R.id.add_connections_recycler);
        searchUserEditText = findViewById(R.id.search_connections_edittext);
        searchUserImageButton = findViewById(R.id.search_connections);
        connectionsRecyclerViewAdapter = new ConnectionsRecyclerViewAdapter(this);
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        userRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        arrayAdapter = new ArrayAdapter<>(AddConnectionsActivity.this, android.R.layout.simple_list_item_1, autoCompleteList);

        if (GoogleSignIn.getLastSignedInAccount(this) != null) {


            googleUsername = GoogleSignIn.getLastSignedInAccount(this).getDisplayName();
            firebaseDatabase = FirebaseDatabase.getInstance();
            connectionsReference = firebaseDatabase.getReference("users")
                    .child(googleUsername)
                    .child("connections");
            connectionsReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    if (dataSnapshot.getValue() != null) {
                        HashMap<String ,String> hashMap= (HashMap<String, String>) dataSnapshot.getValue();
                        list.add(hashMap);
                        connectionsRecyclerViewAdapter.list = list;
                        connectionsRecyclerViewAdapter.notifyDataSetChanged();
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
            });
        }
        userRecyclerView.setAdapter(connectionsRecyclerViewAdapter);
        userRecyclerView.setLayoutManager(linearLayoutManager);


        searchUserEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                firebaseDatabase.getReference().child("users").orderByKey()
                        .startAt(s.toString()).endAt(s.toString() + "\uf8ff")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

//
                                    if (arrayAdapter.getPosition(postSnapshot.getKey()) < 0)
                                        arrayAdapter.add(postSnapshot.getKey());
                                    arrayAdapter.notifyDataSetChanged();


                                    Log.d("searchquery", postSnapshot.toString() + "  " + autoCompleteList.size());
                                }
                                if (searchUserEditText.getAdapter() == null) {
                                    Log.d("searchquery", "inside");
                                    searchUserEditText.setAdapter(arrayAdapter);
                                    searchUserEditText.setThreshold(1);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        });
        searchUserEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AddConnectionsActivity.this, NewConnectionsProfile.class);


                String s = (String) parent.getItemAtPosition(position);

                intent.putExtra("username", s);
                startActivity(intent);
            }
        });
    }


}