package com.ssafy.oringe.ui.component.challenge;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.ssafy.oringe.R;

public class ChallengeListView extends FrameLayout {

    public ChallengeListView(Context context) {
        super(context);
        init(context);
    }

    public ChallengeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChallengeListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_list_view, this, false);
        addView(view);
    }


}