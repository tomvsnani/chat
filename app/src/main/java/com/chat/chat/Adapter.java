package com.chat.chat;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chat.chat.Database.Database;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Adapter extends ListAdapter {

    Bitmap finalBitmap;
    DatabaseReference userslist_databaseReference;
    DatabaseReference check_if_online;
    private FirebaseDatabase database;
    private Context context;
    private MainActivity mainActivity;
    private GoogleSignInClient googleSignInClient;
    private TextView textView;
    private Button button;
    private ImageView imageView;
    private Executor executor = Executors.newSingleThreadExecutor();


    Adapter(Context context, GoogleSignInClient googleSignInClient) {
        super(Entity.diffcallAdapter);
        database = FirebaseDatabase.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        this.googleSignInClient = googleSignInClient;


        this.context = context;
        Entity entity = new Entity();
        mainActivity = (MainActivity) context;

    }


    @Override
    public void submitList(@Nullable List list) {
        super.submitList(list != null ? new ArrayList<String>(list) : null);
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        return new Holder(v);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final String s = (String) getCurrentList().get(position);

        textView.setText(s);
//
//        URL url= null;
//        try {
//            url = new URL(GoogleSignIn.getLastSignedInAccount(context).getPhotoUrl().toString());
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        try {
//            imageView.setImageBitmap(BitmapFactory.decodeStream(url.openConnection().getInputStream()));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        ViewCompat.setTransitionName(imageView,s);

        if (!s.equals(GoogleSignIn.getLastSignedInAccount(context).getDisplayName())) {
            button.setVisibility(View.GONE);

        }
        Glide.with(context).asBitmap().load(GoogleSignIn.getLastSignedInAccount(context).getPhotoUrl())
                .transform(new CircleCrop())
                .into(imageView);
       // ViewCompat.setTransitionName(imageView, s);
//                .listener(new RequestListener<Bitmap>() {
//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
//                        RoundedBitmapDrawable drawable=RoundedBitmapDrawableFactory.create(context.getResources(),resource);
//                        drawable.setCircular(true);
//                        imageView.setImageDrawable(drawable);
//                        ViewCompat.setTransitionName(imageView,s);
//
//                        return false;
//                    }
//                }).submit()
//              ;


    }

    public float convertDpToPx(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }


    @Override
    public int getItemCount() {
        if (!getCurrentList().isEmpty()) {
            return getCurrentList().size();
        } else
            return 0;
    }


    class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ConstraintLayout constraintLayout;

        Holder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.usernames);

            button = itemView.findViewById(R.id.logout);

            imageView = itemView.findViewById(R.id.profileimage);
            constraintLayout = itemView.findViewById(R.id.constraint);

            imageView.setOnClickListener(this);

            textView.setOnClickListener(this);

            button.setOnClickListener(this);
            constraintLayout.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.logout:
                    googleSignInClient.signOut();
                    //databaseReference1.removeValue();
                    break;
                case R.id.constraint:
                    Intent intent = new Intent(context, chatFragment.class);
                    intent.putExtra("username", (String) (getCurrentList().get(getAdapterPosition())));
                    context.startActivity(intent);
                    break;
                case R.id.profileimage:
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

                        Intent intent1 = new Intent(mainActivity, ZoomInProfileImage.class);
                       // intent1.putExtra("transitionName", ViewCompat.getTransitionName(v));
                       // Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(mainActivity, imageView, ViewCompat.getTransitionName(v)).toBundle();
                        context.startActivity(intent1);
                    }

                    break;


            }
        }
    }

}

