package com.chat.chat;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;

public class ZoomInProfileImage extends AppCompatActivity {
    ImageView imageView;
    Bitmap bitmapp;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().getAttributes().height = (int) convertDpToPx(this, 300);
        getWindow().getAttributes().width = (int) convertDpToPx(this, 300);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_in_profile_image);
        setTitle("");
        imageView = findViewById(R.id.image);
        String transitionName = getIntent().getStringExtra("transitionName");
       // imageView.setTransitionName(transitionName);


        bitmapp = getRoundedCroppedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.s),
                (int) convertDpToPx(this, 300));
        imageView.setImageBitmap(bitmapp);


    }


    public float convertDpToPx(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }


    public Bitmap getRoundedCroppedBitmap(Bitmap bitmap, int newheight) {
        Bitmap bitmap1 = Bitmap.createBitmap(newheight, newheight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap1);
        canvas.drawBitmap(bitmap, null, new RectF(0, 0, newheight, newheight), null);
        return bitmap1;
    }


    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }
}
