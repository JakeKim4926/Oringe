package com.ssafy.oringe.ui.component.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.ssafy.oringe.R;

public class HeaderWhiteView extends FrameLayout {
//    private ImageView logoImageView;

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

//        logoImageView = findViewById(R.id.logoImageView);

//        logoImageView.setOnClickListener(v -> {
//            // 홈 버튼 기능 구현
//            Toast.makeText(context, "Home Pressed", Toast.LENGTH_SHORT).show();
//        });
    }

}