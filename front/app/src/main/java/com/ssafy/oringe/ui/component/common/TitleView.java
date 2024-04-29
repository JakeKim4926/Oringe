package com.ssafy.oringe.ui.component.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ssafy.oringe.R;

public class TitleView extends LinearLayout {
    private CharSequence text;
    private int color;
    private float size;
    private TypedArray typedArray;

    public TitleView(Context context) {
        super(context);
        init(context);
    }

    public TitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        try {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.TitleView);
            text = typedArray.getText(R.styleable.TitleView_text);
            color = typedArray.getColor(R.styleable.TitleView_textColor, Color.BLACK);
            size = typedArray.getDimension(R.styleable.TitleView_textSize, 12);
            init(context);
        } finally {
            typedArray.recycle(); // 메모리 누수 방지 보장
        }
    }

    public TitleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_title_view, this, false);
        TextView textView = view.findViewById(R.id.title);

        textView.setText(text);
        textView.setTextColor(color);
        textView.setTextSize(size);

        addView(view);
    }

    public void setText(String text) {
        TextView textView = findViewById(R.id.title);
        textView.setText(text);
    }

    public void setTextColor(int color) {
        TextView textView = findViewById(R.id.title);
        textView.setTextColor(color);
    }

}