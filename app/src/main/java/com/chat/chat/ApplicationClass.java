package com.chat.chat;

import android.app.Application;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class ApplicationClass extends Application {
   private  GoogleSignInClient googleSignInClient;
   private  GoogleSignInOptions googleSignInOptions;
   public GoogleSignInClient signInClient(){
       googleSignInOptions=new GoogleSignInOptions.Builder().requestIdToken(getString(R.string.web)).requestProfile().build();
       googleSignInClient= GoogleSignIn.getClient(this,googleSignInOptions);
return googleSignInClient;
   }
}
