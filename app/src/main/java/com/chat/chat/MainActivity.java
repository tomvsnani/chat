package com.chat.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
GoogleSignInOptions googleSignInOptions;
GoogleSignInClient googleSignInClient;
GoogleSignInAccount googleSignInAccount;
FirebaseAuth firebaseAuth;
SignInButton signInButton;
int RC_SIGN_IN=1;
RecyclerView recyclerView;
LinearLayoutManager linearLayoutManager;
Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        googleSignInClient=((ApplicationClass)getApplication()).signInClient();
        firebaseAuth=FirebaseAuth.getInstance();
         signInButton=findViewById(R.id.gsignin);
        recyclerView=findViewById(R.id.recycler);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
       adapter= new Adapter(this,googleSignInClient);
        recyclerView.setAdapter(adapter);
        adapter.submit();


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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN) {
            Task<GoogleSignInAccount> googleSignInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
              googleSignInAccount=  googleSignInAccountTask.getResult(ApiException.class);

            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }
}
