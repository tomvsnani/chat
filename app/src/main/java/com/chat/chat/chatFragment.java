package com.chat.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;


public class chatFragment extends AppCompatActivity {
    DatabaseReference databaseReference;
    FloatingActionButton sendButton;
    EditText replyText;
    RecyclerView recyclerView;
    FirebaseStorage firebaseStorage;
    LinearLayoutManager linearLayoutManager;
    Calendar calendar;
    ChatAdapter chatAdapter;
    ImageButton deliverSymbol;
    GoogleSignInClient googleSignInClient;
    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;
    ImageView profileImage;
    DatabaseReference databaseReference1;
    StorageReference storageReference;
    StorageReference sr1;
    String username;
    TextView username_on_toolbar_textView;
    TextView lastSeen_textview;
    Entity entity;
    List<Entity> list = new ArrayList<>();
    LiveData<List<Entity>> local_db_list;
    ChildEventListener childEventListener;
    ImageView select_from_gallary;
    Toolbar toolbar;
    Database localdatabase;
    LiveData<Entity> lastSeenEntity;
    ConstraintLayout constraintLayout;
    ChildEventListener childEventListener1;
    ChildEventListener acknowledgelistener;
    DatabaseReference acknowledgeReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_chat);
        googleSignInClient = ((ApplicationClass) getApplication()).signInClient();
        entity = new Entity();
        sendButton = findViewById(R.id.send);
        replyText = findViewById(R.id.replytype);
        deliverSymbol = findViewById(R.id.deliveryReport);
        username_on_toolbar_textView = findViewById(R.id.username_on_toolbar);
        toolbar = findViewById(R.id.toolbar_chatFragment);
        lastSeen_textview = findViewById(R.id.lastseen);
        select_from_gallary = findViewById(R.id.pick_images_to_send);
        constraintLayout = findViewById(R.id.constraint);
        username = getIntent().getStringExtra("username");
        calendar = Calendar.getInstance();
        profileImage = findViewById(R.id.profile_image_in_chat_activity);
        localdatabase = Database.getInstance(this);
        firebaseStorage = FirebaseStorage.getInstance();
        Log.d("timezone", String.valueOf(calendar.getTimeZone()));
        Log.d("time", String.valueOf(calendar.getTime()));
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView = findViewById(R.id.chatrecycler);
        chatAdapter = new ChatAdapter(this, googleSignInClient, username, recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(chatAdapter);

        recyclerView.setLayoutManager(linearLayoutManager);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        submit();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }

    public void submit() {

        if (GoogleSignIn.getLastSignedInAccount(chatFragment.this) != null) {
            retrieve_name_pic();

            retrieve_LastSeen();

            retrieve_local_db();

            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(chatFragment.this, ProfileDetailActivity.class);
                    intent.putExtra("user", username);
                    startActivity(intent);
                }
            });




            FirebaseDatabase.getInstance().getReference("users")
                    .child(GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getDisplayName())
                    .child("messageId")
                    .child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Long l=0L;
                    if(dataSnapshot.getValue()==null) {
                        FirebaseDatabase.getInstance().getReference("users")
                                .child(GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getDisplayName())
                                .child("messageId")
                                .child(username).setValue(l);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

    }


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }



//    @Override
//    protected void onPause() {
//
//        if (GoogleSignIn.getLastSignedInAccount(this) != null)
//            FirebaseDatabase.getInstance().getReference("users").child(GoogleSignIn.getLastSignedInAccount(this).getDisplayName()).child("status").setValue(ServerValue.TIMESTAMP);
//
//        databaseReference.removeEventListener(childEventListener);
//        if (databaseReference1 != null && childEventListener1 != null)
//            databaseReference1.removeEventListener(childEventListener1);
//
//        if (acknowledgelistener != null)
//            acknowledgeReference.removeEventListener(acknowledgelistener);
//        super.onPause();
//    }




    private void retrieve_local_db() {
        local_db_list = localdatabase.dao().getdatabychat(GoogleSignIn.getLastSignedInAccount(this).getDisplayName(), username);
        local_db_list.observe(chatFragment.this, new Observer<List<Entity>>() {
            @Override
            public void onChanged(final List<Entity> entities) {
                Log.d("calledonupdate", "yes");
                chatAdapter.submit(entities, recyclerView);

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (GoogleSignIn.getLastSignedInAccount(this) != null)
            FirebaseDatabase.getInstance().getReference("users")
                    .child(GoogleSignIn.getLastSignedInAccount(this).getDisplayName())
                    .child("status").setValue("online");

        databaseReference = database.getReference("users")
                .child(GoogleSignIn.getLastSignedInAccount(chatFragment.this).getDisplayName());

        childEventListener = databaseReference.child("messages").child(username).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
                Entity entity = dataSnapshot.child("entity").getValue(Entity.class);
                Log.d("datasnaplisttchat", dataSnapshot.toString());


                insertEntityinLocalDb(entity, "ack");

                String key = dataSnapshot.getKey();
                databaseReference.child("messages").child(username).child(key).removeValue();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("datasnaplisttchat", dataSnapshot.toString());
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


        acknowledgeReference = databaseReference.child("acknowledgement").child(username);
        acknowledgelistener = databaseReference.child("acknowledgement").child(username).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Long lo = (Long) dataSnapshot.getValue();
                        Entity entity1 = localdatabase.dao().getdatabyMessageid(lo.intValue());
                        if(entity1!=null)
                        Log.d("entityyyy",entity1.getMessage());
                        if (entity1 != null) {
                            entity1.setSent_status("seen");
                            localdatabase.dao().update(entity1);
                            //databaseReference.child("acknowledgement").child(username).child(dataSnapshot.getKey()).removeValue();

                        }
                        ;
                    }
                });
                thread.start();


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
                            databaseReference1 = database.getReference("users").
                                    child(username)
                                    .child("messages")
                                    .child(GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getDisplayName());
                            final String st = databaseReference1.push().getKey();
//                            databaseReference1.child(GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getDisplayName()).child("messages")
//                                    .child(username).child(st).child("entity").setValue(entity);



                            FirebaseDatabase.getInstance().getReference("users")
                                    .child(GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getDisplayName())
                                    .child("messageId")
                                    .child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                 Long  l= (Long) dataSnapshot.getValue()+1;


                                    FirebaseDatabase.getInstance().getReference("users")
                                            .child(GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getDisplayName())
                                            .child("messageId")
                                            .child(username).setValue(l);
                                    entity.setMessageId(l);
                                    databaseReference1.child(st).setValue(entity);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        }


                        insertEntityinLocalDb(entity, "no");
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

    void updateEntity(final Entity entity) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                localdatabase.dao().update(entity);

            }

        });
        thread.start();
    }

    private void insertEntityinLocalDb(final Entity entity, final String ack) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                long i = localdatabase.dao().insert(entity);
                if (ack.equals("ack")) {

                    String acknowledge_key = FirebaseDatabase.getInstance()
                            .getReference("users")
                            .child(username)
                            .child("acknowledgement")
                            .push()
                            .getKey();

                    FirebaseDatabase.getInstance()
                            .getReference("users")
                            .child(username).child("acknowledgement")
                            .child(GoogleSignIn.getLastSignedInAccount(chatFragment.this).getDisplayName())
                            .child(acknowledge_key)
                            .setValue(entity.getMessageId());
                }
                Entity entity1 = localdatabase.dao().getdatabyid((int) i);
                Log.d("entityyy","ls  "+entity1.getMessage());
            }

        });
        thread.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && data.getData() != null) {
            final String decodedimagepathname = data.getData().getLastPathSegment().split(":")[1];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getContentResolver().takePersistableUriPermission(data.getData(), Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            storageReference = firebaseStorage.getReference("images");
            final StorageReference child = storageReference.child(decodedimagepathname);
            Log.d("checking", data.getData().getLastPathSegment());

            UploadTask i = null;
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
                File file = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DCIM)
                        + File.separator + "sent");
                file.mkdirs();
                File f1 = new File(file, decodedimagepathname);
                f1.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(f1);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, fileOutputStream);

                i = child.putStream(getContentResolver().openInputStream(Uri.fromFile(f1)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
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
                        String st = databaseReference1.child(username).child("messages")
                                .child(GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getDisplayName()).push().getKey();
//                        databaseReference1.child(GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getDisplayName()).child("messages")
//                                .child(username).child(st).child("entity").setValue(entity);

                        databaseReference1.child(username).child("messages")
                                .child(GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getDisplayName())
                                .child(st).child("entity").setValue(entity);
                        insertEntityinLocalDb(entity, "no");
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

    private void retrieve_name_pic() {
        username_on_toolbar_textView.setText(username);
        Glide.with(chatFragment.this).load(GoogleSignIn.getLastSignedInAccount(chatFragment.this).getPhotoUrl())
                .transform(new CircleCrop()).into(profileImage);
    }


    private void retrieve_LastSeen() {
        database.getReference("users").child(username).child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Log.d("lastseen", dataSnapshot.toString());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MMM:dd hh:mm a");
                    simpleDateFormat.setTimeZone(TimeZone.getDefault());
                    if (!dataSnapshot.getValue().equals("online")) {
                        String[] s = simpleDateFormat.format(dataSnapshot.getValue()).split(" ");
                        lastSeen_textview.setText("Last Seen : " + s[1] + " " + s[2]);
                    } else
                        lastSeen_textview.setText("online");


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
