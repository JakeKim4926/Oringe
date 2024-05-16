package com.ssafy.oringe.activity.common;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ssafy.oringe.R;

public class FullscreenImageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        ImageView imageView = findViewById(R.id.fullscreen_image_view);
        String imageUrl = getIntent().getStringExtra("image_url");

        Glide.with(this)
                .load(imageUrl)
                .into(imageView);
    }
}
