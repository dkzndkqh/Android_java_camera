package com.example.camarasample;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.File;


public class VideoPlayer extends AppCompatActivity {
    VideoView videoView;
    ImageView imageView;
    String str;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videoplayer);
        videoView = findViewById(R.id.videoView);
        imageView = findViewById(R.id.imageView);

        Intent i = getIntent();
        str = i.getStringExtra("목록");
        if (str.endsWith(".mp4")) {

            videoView.setMediaController(new MediaController(this));

            videoView.setVideoPath(str);

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) { videoView.start();
                }
            });


        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(videoView!=null && videoView.isPlaying()) {
            videoView.pause();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(videoView!=null) videoView.stopPlayback();
    }

}
