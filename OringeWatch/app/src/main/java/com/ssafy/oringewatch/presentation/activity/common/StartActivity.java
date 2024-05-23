package com.ssafy.oringewatch.presentation.activity.common;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import androidx.activity.ComponentActivity;
import androidx.core.splashscreen.SplashScreen;

import com.bumptech.glide.Glide;
import com.ssafy.oringewatch.R;
import com.ssafy.oringewatch.presentation.activity.MainActivity;

public class StartActivity extends ComponentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ImageView imageViewSplash = findViewById(R.id.imageViewSplash);
        Glide.with(this).asGif().load(R.drawable.splash).into(imageViewSplash);

        // 딜레이 후 MainActivity로 전환
        int splashScreenDuration = 3000; // GIF의 재생 시간 (밀리초)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, splashScreenDuration);
    }
}