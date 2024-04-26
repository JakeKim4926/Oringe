package com.ssafy.oringe.ui.component.challenge;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.ssafy.oringe.R;

public class ChallengeStatusView extends LinearLayout {

    public ChallengeStatusView(Context context) {
        super(context);
        init(context);
    }

    public ChallengeStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChallengeStatusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_challenge_status_view, this, false);
        addView(view);
    }

}