package com.chat.chat;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ShowFullImageOnClickInChat extends AppCompatActivity {
    ImageView imageView;
    Uri uri;
    Executor executor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_imageview_on_click);
        imageView = findViewById(R.id.displayfullimageonclick);
        executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if(getIntent().getData()!=null)
                uri= getIntent().getData();
            }
        });
        executor.execute(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (uri != null)
                            imageView.setImageURI(uri);
                    }
                });

            }
        });


    }
}
