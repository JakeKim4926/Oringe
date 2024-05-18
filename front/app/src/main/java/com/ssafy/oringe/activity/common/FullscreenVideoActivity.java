package com.ssafy.oringe.activity.common;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ssafy.oringe.R;

public class FullscreenVideoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_video);

        VideoView videoView = findViewById(R.id.fullscreen_video_view);
        String videoUrl = getIntent().getStringExtra("video_url");

        videoView.setVideoURI(Uri.parse(videoUrl));
        videoView.setMediaController(new MediaController(this));
        videoView.requestFocus();
        videoView.start();
    }
}
