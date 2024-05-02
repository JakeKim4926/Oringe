package com.ssafy.oringe.ui.component.common;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.ssafy.oringe.R;
import com.ssafy.oringe.activity.challenge.ChallengeCreateActivity;
import com.ssafy.oringe.activity.challenge.ChallengeListActivity;

public class MenuView extends LinearLayout {
    private int src;
    private TypedArray typedArray;

    public MenuView(Context context) {
        super(context);
        init(context);
    }

    public MenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        try {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.MenuView);
            src = typedArray.getResourceId(R.styleable.MenuView_src, 0);
            init(context);
        } finally {
            typedArray.recycle(); // 메모리 누수 방지 보장
        }
    }

    public MenuView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_menu_view, this, false);
        ImageButton imageButton = view.findViewById(R.id.menu_btn);

        if (src != 0) {
            imageButton.setImageResource(src);
            if (src == R.drawable.plus_btn) {
                imageButton.setOnClickListener(v -> {
                    Intent intent = new Intent(context, ChallengeCreateActivity.class);
                    context.startActivity(intent);
                });
            } else if (src == R.drawable.main_menu) {
                imageButton.setOnClickListener(v -> {
                    Intent intent = new Intent(context, ChallengeListActivity.class);
                    context.startActivity(intent);
                });
            }
        }

        addView(view);
    }


}