package com.chat.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Application;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chat.chat.Database.Database;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final Integer PERMISSION_CONSTANT = 1;
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;
    GoogleSignInAccount googleSignInAccount;
    FirebaseAuth firebaseAuth;
    SignInButton signInButton;
    int RC_SIGN_IN = 1;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    Adapter adapter;
    Button pickImage;
    ImageView imageselected;
    int count=0;
    List<Integer> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        googleSignInClient = ((ApplicationClass) getApplication()).signInClient();
        firebaseAuth = FirebaseAuth.getInstance();
        signInButton = findViewById(R.id.gsignin);
        recyclerView = findViewById(R.id.recycler);
        pickImage = findViewById(R.id.pickimage);
        imageselected = findViewById(R.id.imageSelected);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new Adapter(this, googleSignInClient);
        recyclerView.setAdapter(adapter);
        adapter.submit();
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

    @Override
    protected void onStart() {
        super.onStart();
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);

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
}
