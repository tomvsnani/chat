package com.chat.chat;

import android.app.Application;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.database.FirebaseDatabase;

public class ApplicationClass extends Application {
   private  GoogleSignInClient googleSignInClient;
   private  GoogleSignInOptions googleSignInOptions;
   public GoogleSignInClient signInClient(){
       googleSignInOptions=new GoogleSignInOptions.Builder().requestIdToken(getString(R.string.web)).requestProfile().build();
       googleSignInClient= GoogleSignIn.getClient(this,googleSignInOptions);

return googleSignInClient;
   }

    @Override
    public void onCreate() {
      //  FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        super.onCreate();
    }

}
