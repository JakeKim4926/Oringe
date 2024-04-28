package com.ssafy.oringe.ui.component.challenge;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ssafy.oringe.R;

public class InputView extends LinearLayout {
    private CharSequence text;
    private boolean singleLine;
    private TypedArray typedArray;

    public InputView(Context context) {
        super(context);
        init(context);
    }

    public InputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        try {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.InputView);
            singleLine = typedArray.getBoolean(R.styleable.InputView_singleLine, true);
            init(context);
        } finally {
            typedArray.recycle();
        }
    }

    public InputView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_input_view, this, false);
        EditText textView = view.findViewById(R.id.input);
        textView.setSingleLine(singleLine);

        addView(view);
    }


}