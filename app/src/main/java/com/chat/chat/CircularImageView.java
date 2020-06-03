package com.chat.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignIn;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CircularImageView extends androidx.appcompat.widget.AppCompatImageView {
    Bitmap roundBitmap;

    public CircularImageView(Context context) {
        super(context);

    }

    public CircularImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public CircularImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }
    public float convertDpToPx(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public  Bitmap getRoundedCroppedBitmap(Bitmap bitmap, int radius) {
        Bitmap finalBitmap;
        if (bitmap.getWidth() != radius || bitmap.getHeight() != radius)
            finalBitmap = Bitmap.createScaledBitmap(bitmap, radius, radius,
                    false);
        else
            finalBitmap = bitmap;

        Bitmap output = Bitmap.createBitmap(finalBitmap.getWidth(),
                finalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0,0, finalBitmap.getWidth(),
                finalBitmap.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        canvas.drawCircle(finalBitmap.getWidth() / 2 ,
                finalBitmap.getHeight() / 2 ,
                finalBitmap.getWidth() / 2 , paint);
       paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(finalBitmap, null, rect, paint);

        return output;
    }


    @Override
    public void setImageBitmap(Bitmap bm) {
        roundBitmap = bm;
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                roundBitmap = getRoundedCroppedBitmap(roundBitmap, (int)convertDpToPx(getContext(),48));
                postInvalidate();
            }
        });
        super.setImageBitmap(bm);
    }

    @Override
    protected void onDraw(Canvas canvas) {





        Paint paint = new Paint();
        paint.setColor(Color.RED);
        if (roundBitmap != null)

            canvas.drawBitmap(roundBitmap, convertDpToPx(getContext(),48), (float)roundBitmap.getHeight()/2, null);

        // canvas.drawColor(Color.RED);
    }



//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        setMeasuredDimension((int) convertDpToPx(getContext(), 80), (int) convertDpToPx(getContext(), 80));
//    }
}
