package com.chat.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.chat.chat.Database.Database;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;


public class chatFragment extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Button sendButton;
    EditText replyText;
    RecyclerView recyclerView;
    FirebaseStorage firebaseStorage;
    LinearLayoutManager linearLayoutManager;
    Calendar calendar;
    ChatAdapter chatAdapter;
    GoogleSignInClient googleSignInClient;
    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference1;
    StorageReference storageReference;
    StorageReference sr1;
    String username;
    Entity entity;
  List<Entity> list=new ArrayList<>();
    ImageView select_from_gallary;
    Database localdatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_chat);
        googleSignInClient = ((ApplicationClass) getApplication()).signInClient();
        entity = new Entity();
        sendButton = findViewById(R.id.send);
        replyText = findViewById(R.id.replytype);
        select_from_gallary = findViewById(R.id.pick_images_to_send);
        username = getIntent().getStringExtra("username");
        calendar = Calendar.getInstance();
        localdatabase = Database.getInstance(this);
        firebaseStorage = FirebaseStorage.getInstance();
        Log.d("timezone", String.valueOf(calendar.getTimeZone()));
        Log.d("time", String.valueOf(calendar.getTime()));
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        chatAdapter = new ChatAdapter(this, googleSignInClient,username);
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView = findViewById(R.id.chatrecycler);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        submit();

    }

    public void submit() {

               if (GoogleSignIn.getLastSignedInAccount(chatFragment.this) != null) {
//                   list = localdatabase.dao().getTotaldata();
//                   list.observe(chatFragment.this, new Observer<List<Entity>>() {
//                       @Override
//                       public void onChanged(final List<Entity> entities) {
//
//
//                       }
//                   });
                   list.clear();
                   databaseReference = database.getReference("users")
                           .child(GoogleSignIn.getLastSignedInAccount(chatFragment.this).getDisplayName()).child("messages").child(username);


                   databaseReference.addChildEventListener(new ChildEventListener() {
                       @Override
                       public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
                           Entity entity = dataSnapshot.child("entity").getValue(Entity.class);
//                           if(!GoogleSignIn)
//                               insertEntityinLocalDb(entity);
                           list.add(entity);
                           chatAdapter.submitList(list);
                           recyclerView.postDelayed(new Runnable() {
                               @Override
                               public void run() {
                                   recyclerView.scrollToPosition(recyclerView.getChildCount()-1);
                               }
                           }, 200);

                       }

                       @Override
                       public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                           Log.d("datasnaplisttchat", dataSnapshot.toString());
                       }

                       @Override
                       public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//                           entities.remove(dataSnapshot.getValue(Entity.class));
//                           chatAdapter.submit(entities);
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
                        entity.setIsImage(false);
                        entity.setTimeofMessage(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
                        entity.setNew_message(true);


                        if (GoogleSignIn.getLastSignedInAccount(getApplicationContext()) != null) {
                            databaseReference1 = database.getReference("users");
                            String st = databaseReference1.child(GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getDisplayName()).child("messages")
                                    .child(username).push().getKey();
                            databaseReference1.child(GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getDisplayName()).child("messages")
                                    .child(username).child(st).child("entity").setValue(entity);

                            databaseReference1.child(username).child("messages")
                                    .child(GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getDisplayName())
                                    .child(st).child("entity").setValue(entity);


                        }


                        insertEntityinLocalDb(entity);
                    }
                }
            }
        });

        select_from_gallary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("working", "yes");
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
    }

    private void insertEntityinLocalDb(final Entity entity) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("primeid", String.valueOf(localdatabase.dao().insert(entity)));
            }

        });
        thread.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            final String decodedimagepathname = data.getData().getLastPathSegment().split(":")[1];
            getContentResolver().takePersistableUriPermission(data.getData(), Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            storageReference = firebaseStorage.getReference("images");
            final StorageReference child = storageReference.child(decodedimagepathname);
            Log.d("checking", data.getData().getLastPathSegment());

            UploadTask i = null;
            try {
                i = child.putStream(getContentResolver().openInputStream(data.getData()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            i.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        Toast.makeText(chatFragment.this, "could not finish the upload . Please retry again ", Toast.LENGTH_SHORT).show();
                        throw task.getException();
                    }
                    return child.getDownloadUrl();
                }
            }).addOnCompleteListener(this, new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        entity.setFrom(GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getDisplayName());
                        entity.setTo(username);
                        entity.setIsImage(true);
                        entity.setNew_message(true);
                        entity.setUri(decodedimagepathname);
                        entity.setTimeofMessage(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
                        databaseReference1 = database.getReference("users");
                        String st = databaseReference1.child(GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getDisplayName()).child("messages")
                                .child(username).push().getKey();
                        databaseReference1.child(GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getDisplayName()).child("messages")
                                .child(username).child(st).child("entity").setValue(entity);

                        databaseReference1.child(username).child("messages")
                                .child(GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getDisplayName())
                                .child(st).child("entity").setValue(entity);
                        insertEntityinLocalDb(entity);
                    }

                }
            });
//
//

            //imageselected.setImageURI(uri);
//          Intent intent=new Intent(Intent.ACTION_VIEW);
//          intent.setDataAndType(uri,"audio/*");
//           // intent.putExtra(Intent.EXTRA_STREAM, uri);
//          intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//          startActivity(intent);
        }
    }
}
