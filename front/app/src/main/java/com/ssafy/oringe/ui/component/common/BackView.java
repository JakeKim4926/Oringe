package com.ssafy.oringe.ui.component.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ssafy.oringe.R;
import com.ssafy.oringe.activity.common.MainActivity;

public class BackView extends LinearLayout {
    private CharSequence headerText;
    private TypedArray typedArray;
    private ImageButton backButton;
    private OnBackButtonClickListener onBackButtonClickListener;

    public BackView(Context context) {
        super(context);
        init(context);
    }

    public BackView(Context context, AttributeSet attrs) {
        super(context, attrs);
        try {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.BackView);
            headerText = typedArray.getText(R.styleable.BackView_headerText);
            init(context);
        } finally {
            typedArray.recycle(); // 메모리 누수 방지 보장
        }
    }

    public BackView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_back_view, this, false);
        TextView textView = view.findViewById(R.id.backHeader_title);
        textView.setText(headerText);
        addView(view);

        // 뒤로 가기
        backButton = findViewById(R.id.backHeader_backButton);
//        backButton.setOnClickListener(v -> {
//            if (onBackButtonClickListener != null) {
//                onBackButtonClickListener.onBackButtonClick();
//            }
//        });
        backButton.setOnClickListener(v -> {
            if (context instanceof Activity) {
                ((Activity) context).finish();
            }
        });
    }

    public void setOnBackButtonClickListener(OnBackButtonClickListener listener) {
        this.onBackButtonClickListener = listener;
    }

}