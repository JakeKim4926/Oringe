package com.ssafy.oringe.ui.component.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.ssafy.oringe.R;

public class HeaderOrangeView extends FrameLayout {

    private ImageButton backButton;
    private ImageView logoImageView;

    public HeaderOrangeView(Context context) {
        super(context);
        init(context);
    }

    public HeaderOrangeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HeaderOrangeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_header_view, this, false);
        addView(view);

        backButton = findViewById(R.id.backButton);
        logoImageView = findViewById(R.id.logoImageView);

        backButton.setOnClickListener(v -> {
            // 뒤로 가기 기능 구현
            Toast.makeText(context, "Back Pressed", Toast.LENGTH_SHORT).show();
        });

        logoImageView.setOnClickListener(v -> {
            // 홈 버튼 기능 구현
            Toast.makeText(context, "Home Pressed", Toast.LENGTH_SHORT).show();
        });
    }

}