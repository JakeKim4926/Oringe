package com.ssafy.oringewatch.presentation.activity.common;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.ComponentActivity;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.ssafy.oringewatch.R;
import com.ssafy.oringewatch.presentation.activity.MainActivity;

public class StartActivity extends ComponentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ImageView imageViewSplash = findViewById(R.id.imageViewSplash);

        Glide.with(this)
                .load(R.drawable.splash)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // 실패 시 동작 정의
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (resource instanceof GifDrawable) {
                            GifDrawable gifDrawable = (GifDrawable) resource;
                            gifDrawable.setLoopCount(1); // 한 사이클만 재생
                            gifDrawable.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
                                @Override
                                public void onAnimationEnd(Drawable drawable) {
                                    // GIF 애니메이션이 끝났을 때 MainActivity로 전환
                                    Intent intent = new Intent(StartActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                        return false;
                    }
                })
                .into(imageViewSplash);
    }
}