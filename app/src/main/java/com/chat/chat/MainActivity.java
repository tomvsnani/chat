package com.chat.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final Integer PERMISSION_CONSTANT = 1;
    private static List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    FloatingActionButton addConnectionsFloatingButton;
    GoogleSignInClient googleSignInClient;
    GoogleSignInAccount googleSignInAccount;
    FirebaseAuth firebaseAuth;
    SignInButton signInButton;
    Toolbar toolbar;
    int RC_SIGN_IN = 1;
    RecyclerView recyclerView;
    DatabaseReference check_if_online;
    Adapter adapter;
    Button pickImage;
    ImageView imageselected;
    DatabaseReference userslist_databaseReference;
    FirebaseDatabase database;
    ChildEventListener childEventListener;
    TextView notificationTextview;
    TextView dataLoadingTextview;
    ProgressBar dataLoadingProgressBar;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        googleSignInClient = ((ApplicationClass) getApplication()).signInClient();
        firebaseAuth = FirebaseAuth.getInstance();
        signInButton = findViewById(R.id.gsignin);
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycler);
        dataLoadingProgressBar = findViewById(R.id.progressbar);
        pickImage = findViewById(R.id.pickimage);
        dataLoadingTextview = findViewById(R.id.loadingtextview);
        imageselected = findViewById(R.id.imageSelected);
        notificationTextview = findViewById(R.id.notification);
        addConnectionsFloatingButton = findViewById(R.id.add_connections_fab);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new Adapter(this, googleSignInClient);
        recyclerView.setAdapter(adapter);

        database = FirebaseDatabase.getInstance();

        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        }
                        , PERMISSION_CONSTANT);
            }
        }

    }

    public void notificationClick(View v) {
        Intent intent = new Intent(this, NotificationActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (GoogleSignIn.getLastSignedInAccount(this) != null)
            FirebaseDatabase.getInstance().getReference("users")
                    .child(GoogleSignIn.getLastSignedInAccount(this).getDisplayName())
                    .child("status")
                    .setValue(ServerValue.TIMESTAMP);

    }

    @Override
    protected void onStop() {
        list.clear();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (GoogleSignIn.getLastSignedInAccount(this) != null)
            FirebaseDatabase.getInstance().getReference("users")
                    .child(GoogleSignIn.getLastSignedInAccount(this).getDisplayName())
                    .child("status")
                    .setValue("online");


    }


    @Override
    protected void onStart() {
        super.onStart();

        setUpReferences();

        setUpClickListeners();

    }

    private void setUpReferences() {
        if (GoogleSignIn.getLastSignedInAccount(this) != null) {

            userslist_databaseReference = database.getReference("users");
            userslist_databaseReference.child(GoogleSignIn.getLastSignedInAccount(MainActivity.this).getDisplayName())
                    .child("status")
                    .setValue("online");

            final HashMap<String,String> hashMap=new HashMap<>();
            hashMap.put("username",GoogleSignIn.getLastSignedInAccount(this).getDisplayName());
            hashMap.put("propicurl",GoogleSignIn.getLastSignedInAccount(MainActivity.this).getPhotoUrl().toString());
            hashMap.put("email",GoogleSignIn.getLastSignedInAccount(MainActivity.this).getEmail());


//            userslist_databaseReference
//                    .child(GoogleSignIn.getLastSignedInAccount(this).getDisplayName())
//                    .child("propic")
//                    .setValue((GoogleSignIn.getLastSignedInAccount(MainActivity.this).getPhotoUrl()));

            userslist_databaseReference
                    .child(GoogleSignIn.getLastSignedInAccount(this).getDisplayName())
                    .child("userdetails")
                    .child("details")
                    .setValue(hashMap);

            childEventListener = userslist_databaseReference.
                    child(GoogleSignIn.getLastSignedInAccount(MainActivity.this).getDisplayName())
                    .child("recents")
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            dataLoadingProgressBar.setVisibility(View.GONE);
                            HashMap<String,String> hashMap1= (HashMap<String, String>) dataSnapshot.getValue();
                            list.add(hashMap1);
                            adapter.submitList(list);

                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                            list.remove(dataSnapshot.getKey());
                            adapter.submitList(list);
                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

        }


        check_if_online = database.getReference(".info/connected");
        check_if_online.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean bool = dataSnapshot.getValue(Boolean.class);

                if (bool) {
                    if (GoogleSignIn.getLastSignedInAccount(MainActivity.this) != null) {
                        userslist_databaseReference.child(GoogleSignIn.getLastSignedInAccount(MainActivity.this).getDisplayName())
                                .child("status").onDisconnect().setValue(ServerValue.TIMESTAMP);
                        userslist_databaseReference.child(GoogleSignIn.getLastSignedInAccount(MainActivity.this).getDisplayName())
                                .child("status").setValue("online");

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUpClickListeners() {
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);

            }
        });


        addConnectionsFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this,
                        AddConnectionsActivity.class));
            }
        });


        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                Thread thread = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Cursor cursor = getContentResolver().query(
//                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//
//                                new String[]{MediaStore.Images.ImageColumns._ID},
//                                null,
//                                null,
//                                null
//                        );
//
//                        while (cursor.moveToNext()) {
//
//
//                            int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID));
//
//count++;
//                            list.add(id);
////                            final Uri uri= ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,id);
//                            Log.d("runninghey", String.valueOf(list.size()));
//                        }
//
//
//
//
//                    }
//                });
//                thread.start();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> googleSignInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                googleSignInAccount = googleSignInAccountTask.getResult(ApiException.class);

            } catch (ApiException e) {
                e.printStackTrace();
                Log.d("ddddd", e.toString());
            }
        }
        if (requestCode == 1) {
            Uri uri = data.getData();
            imageselected.setImageURI(uri);
//          Intent intent=new Intent(Intent.ACTION_VIEW);
//          intent.setDataAndType(uri,"audio/*");
//           // intent.putExtra(Intent.EXTRA_STREAM, uri);
//          intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//          startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CONSTANT) {

            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                AlertDialog dialog = new AlertDialog.Builder(this).create();
                dialog.setMessage("You have to grant read and write permissions to share media with other connections." +
                        "Do you want to grant permissions ?");
                dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CONSTANT);
                        }

                    }
                });
                dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        }

    }
}
