package com.ssafy.oringe.ui.component.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ssafy.oringe.R;
import com.ssafy.oringe.activity.common.MainActivity;

public class HeaderWhiteView extends LinearLayout {
    private ImageView logoImageView;

    public HeaderWhiteView(Context context) {
        super(context);
        init(context);
    }

    public HeaderWhiteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HeaderWhiteView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_header_white_view, this, false);
        addView(view);

        // 홈으로 가기
        logoImageView = findViewById(R.id.logoImageView);
        logoImageView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        });
    }

}