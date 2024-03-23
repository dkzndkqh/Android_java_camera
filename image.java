package com.example.camarasample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.File;


public class image extends AppCompatActivity {
    String str;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imageview);


        ImageView imageView = (ImageView)findViewById(R.id.imageview11);


        Intent t = getIntent();

        str = t.getStringExtra("목록");

        Bitmap bm = BitmapFactory.decodeFile(str);
        imageView.setImageBitmap(bm);
    }
}
