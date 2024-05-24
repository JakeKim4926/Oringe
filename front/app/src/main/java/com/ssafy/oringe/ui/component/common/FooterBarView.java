package com.ssafy.oringe.ui.component.common;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ssafy.oringe.R;
import com.ssafy.oringe.activity.challenge.ChallengeListActivity;
import com.ssafy.oringe.activity.common.MainActivity;
import com.ssafy.oringe.activity.record.RecordCreateActivity;

public class FooterBarView extends LinearLayout {

    private static final String PREFS_NAME = "FooterBarPrefs";
    private static final String KEY_ACTIVE_ACTIVITY = "ActiveActivity";

    private ImageView icon1;
    private ImageView icon2;
    private ImageView icon3;

    public FooterBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public FooterBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(final Context context) {
        LayoutInflater.from(context).inflate(R.layout.footer_bar_view, this, true);

        icon1 = findViewById(R.id.icon1);
        icon2 = findViewById(R.id.icon2);
        icon3 = findViewById(R.id.icon3);

        icon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveActivity(context, ChallengeListActivity.class.getSimpleName());
                Intent intent = new Intent(context, ChallengeListActivity.class);
                context.startActivity(intent);
            }
        });

        icon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveActivity(context, MainActivity.class.getSimpleName());
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            }
        });

        icon3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveActivity(context, RecordCreateActivity.class.getSimpleName());
                Intent intent = new Intent(context, RecordCreateActivity.class);
                context.startActivity(intent);
            }
        });

        updateIcons(context);
    }

    private void setActiveActivity(Context context, String activityName) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_ACTIVE_ACTIVITY, activityName);
        editor.apply();
    }

    public void updateIcons(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String activeActivity = prefs.getString(KEY_ACTIVE_ACTIVITY, "");

        if (ChallengeListActivity.class.getSimpleName().equals(activeActivity)) {
            icon1.setImageResource(R.drawable.list_orange);
            icon2.setImageResource(R.drawable.home);
            icon3.setImageResource(R.drawable.stamp);
        } else if (MainActivity.class.getSimpleName().equals(activeActivity)) {
            icon1.setImageResource(R.drawable.list);
            icon2.setImageResource(R.drawable.home_orange);
            icon3.setImageResource(R.drawable.stamp);
        } else if (RecordCreateActivity.class.getSimpleName().equals(activeActivity)) {
            icon1.setImageResource(R.drawable.list);
            icon2.setImageResource(R.drawable.home);
            icon3.setImageResource(R.drawable.stamp_orange);
        } else {
            icon1.setImageResource(R.drawable.list);
            icon2.setImageResource(R.drawable.home);
            icon3.setImageResource(R.drawable.stamp);
        }
    }
}
