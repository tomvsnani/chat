package com.chat.chat;

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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;


public class chatFragment extends AppCompatActivity implements Callback {
    DatabaseReference messageReceiveReference;
    FloatingActionButton sendButton;
    EditText replyText;
    RecyclerView recyclerView;
    FirebaseStorage firebaseStorage;
    LinearLayoutManager linearLayoutManager;
    ChatViewModel viewModel;
    Calendar calendar;
    ChatAdapter chatAdapter;
    ImageButton deliverSymbol;
    GoogleSignInClient googleSignInClient;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    ImageView profileImage;
    DatabaseReference messageSendReference;
    StorageReference storageReference;
    StorageReference sr1;
    String username;
    TextView username_on_toolbar_textView;
    TextView lastSeen_textview;

    List<Entity> list = new ArrayList<>();
    LiveData<List<Entity>> local_db_list;
    ChildEventListener messageEventListener;
    ImageView select_from_gallary;
    Toolbar toolbar;
    Database localdatabase;
    LiveData<Entity> lastSeenEntity;
    ConstraintLayout constraintLayout;

    ChildEventListener acknowledgelistener;
    DatabaseReference acknowledgeReceiveReference;
    DatabaseReference acknowledgeSendReference;
    DatabaseReference statusReference;
    GoogleSignInAccount googleSignInAccount;
    String googleUserName;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_chat);
        googleSignInClient = ((ApplicationClass) getApplication()).signInClient();
        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        googleUserName = googleSignInAccount.getDisplayName();
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
        localdatabase = Database.getInstance(chatFragment.this);
        firebaseStorage = FirebaseStorage.getInstance();
        Log.d("timezone", String.valueOf(calendar.getTimeZone()));
        Log.d("time", String.valueOf(calendar.getTime()));
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView = findViewById(R.id.chatrecycler);
        chatAdapter = new ChatAdapter(this, googleSignInClient, username, recyclerView);
        recyclerView.setHasFixedSize(true);
        chatAdapter.setHasStableIds(true);
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


        }

    }


    @Override
    protected void onPause() {

        if (googleSignInAccount != null)
            statusReference
                    .setValue(ServerValue.TIMESTAMP);

        if (messageEventListener != null && acknowledgelistener != null) {

            messageReceiveReference.removeEventListener(messageEventListener);


            acknowledgeReceiveReference.removeEventListener(acknowledgelistener);
        }

        super.onPause();
    }


    private void retrieve_local_db() {
        viewModel = new ViewModelProvider(this, new MainViewModelFactory(getApplication(), googleUserName, username)).get(ChatViewModel.class);
        viewModel.getTotalChatDataBetweenUsers().observe(chatFragment.this, new Observer<List<Entity>>() {
            @Override
            public void onChanged(final List<Entity> entities) {
                for (Entity e : entities)
                    Log.d("entityyyinInsert", entities.size() + "  " + e.getMessage());
                chatAdapter.submit(entities, recyclerView);

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (googleSignInAccount != null)

            setUpFirebaseReferences();


        setUpFirebaseEventListeners();


        setUpClickListeners();
    }

    private void setUpClickListeners() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.send) {
                    if (!replyText.getText().toString().equals("")) {
                        Entity entity = new Entity();
                        String s = replyText.getText().toString();
                        entity.setFrom(GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getDisplayName());
                        entity.setTo(username);
                        entity.setMessage(s);
                        entity.setIsImage(false);
                        entity.setTimeofMessage(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
                        entity.setNew_message(true);
                        insertEntityinLocalDb(entity, "send");
                    }
                }
            }
        });


        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(chatFragment.this, ProfileDetailActivity.class);
                intent.putExtra("user", username);
                startActivity(intent);
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


    private void setUpFirebaseEventListeners() {

        messageEventListener = messageReceiveReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
                Entity entity = dataSnapshot.child("entity").getValue(Entity.class);
                Log.d("datasnaplisttchat", dataSnapshot.toString());


                insertEntityinLocalDb(entity, "ack");


                messageReceiveReference.child(dataSnapshot.getKey()).removeValue();

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


        acknowledgelistener = acknowledgeReceiveReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {

                Long lo = (Long) dataSnapshot.getValue();
                Log.d("messageidack", String.valueOf(lo));
                final LiveData<Entity> entityLiveData = viewModel.getEntitybyMessageid(lo);
                entityLiveData.observe(chatFragment.this, new Observer<Entity>() {
                    @Override
                    public void onChanged(Entity entity1) {
                        entityLiveData.removeObserver(this);
                        if (entityLiveData.hasObservers())
                            return;
                        if (entity1 != null) {

                            entity1.setSent_status("seen");
                            viewModel.update(entity1);
                        }
                        acknowledgeReceiveReference.child(dataSnapshot.getKey()).removeValue();
                    }
                });


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

    private void setUpFirebaseReferences() {


        statusReference = firebaseDatabase.getReference("users")
                .child(googleUserName)
                .child("status");

        messageReceiveReference = firebaseDatabase.getReference("users")
                .child(googleUserName)
                .child("messages")
                .child(username);

        acknowledgeReceiveReference = firebaseDatabase.getReference("users")
                .child(googleUserName)
                .child("acknowledgement")
                .child(username);

        messageSendReference = firebaseDatabase.getReference("users").
                child(username)
                .child("messages")
                .child(googleUserName);

        acknowledgeSendReference = firebaseDatabase.getReference("users")
                .child(username)
                .child("acknowledgement")
                .child(googleUserName);

        statusReference.setValue("online");
    }


    private void insertEntityinLocalDb(final Entity entity, final String ack) {


        viewModel.insert(entity, this, ack);


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
                        Entity entity = new Entity();
                        entity.setFrom(GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getDisplayName());
                        entity.setTo(username);
                        entity.setIsImage(true);
                        entity.setNew_message(true);
                        entity.setUri(decodedimagepathname);
                        entity.setTimeofMessage(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
                        messageSendReference = firebaseDatabase.getReference("users");
                        String st = messageSendReference.child(username).child("messages")
                                .child(GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getDisplayName()).push().getKey();
//                        databaseReference1.child(GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getDisplayName()).child("messages")
//                                .child(username).child(st).child("entity").setValue(entity);

                        messageSendReference.child(username).child("messages")
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
        firebaseDatabase.getReference("users").child(username).child("status").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            Log.d("lastseen", dataSnapshot.toString());
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MMM:dd hh:mm a");
                            SimpleDateFormat getDateFromFormat = new SimpleDateFormat("dd");
                            simpleDateFormat.setTimeZone(TimeZone.getDefault());
                            if (!dataSnapshot.getValue().equals("online")) {
                                String[] serverTime = simpleDateFormat.format(dataSnapshot.getValue()).split(" ");
                                int serverDate = Integer.parseInt(getDateFromFormat.format(dataSnapshot.getValue()));
                                int localDate = Integer.parseInt(getDateFromFormat.format(Calendar.getInstance().getTimeInMillis()));

                                int diff = Math.abs(localDate - serverDate);
                                if(diff==0)
                                lastSeen_textview.setText("last seen : today at " + serverTime[1] + " " + serverTime[2]);
                                if(diff==1)
                                    lastSeen_textview.setText("last seen : yesterday at " + serverTime[1] + " " + serverTime[2]);
                                if(diff>1)
                                    lastSeen_textview.setText("last seen : " +serverTime[0]+" "+ serverTime[1] + " " + serverTime[2]);
                            } else
                                lastSeen_textview.setText("online");


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    @Override
    public void callbackForInsertId(final long i, String ack, final Entity entity) {


        if (ack.equals("send")) {

            if (i > 1) {
                Log.d("entityyy", "incallbackk" + "id  " + i);
                final LiveData<Entity> entityLiveData = viewModel.getEntitybyid((i - 1));
                entityLiveData.observe(this, new Observer<Entity>() {
                    @Override
                    public void onChanged(Entity entity1) {
                        Log.d("entityyy1", entityLiveData.hasObservers()+"   "+entityLiveData.hasActiveObservers());
                        entityLiveData.removeObserver(this);
                        Log.d("entityyy2", entityLiveData.hasObservers()+"   "+entityLiveData.hasActiveObservers());
                        if (entityLiveData.hasObservers()) {
                            Log.d("entityyy3", entityLiveData.hasObservers() + "   " + entityLiveData.hasActiveObservers());
                            return;
                        }
                        Log.d("entityyy4", entityLiveData.hasObservers()+"   "+entityLiveData.hasActiveObservers());
                        Log.d("entityyy", "incallback");
                        entity.setMessageId(entity1.getMessageId() + 1);


                        messageSendReference.child(messageSendReference.push().getKey()).child("entity").setValue(entity);
                        entity.setId(i);
                        viewModel.update(entity);
                    }
                });

            } else {
                entity.setMessageId(entity.getMessageId() + 1);
                Entity entity1;
                entity1 = entity;
                messageSendReference.child(messageSendReference.push().getKey()).child("entity").setValue(entity);
                entity1.setId(i);
                viewModel.update(entity1);
            }


        }


        if (ack.equals("ack")) {

            acknowledgeSendReference
                    .child(acknowledgeSendReference.push().getKey())
                    .setValue(entity.getMessageId());
        }

    }
}
