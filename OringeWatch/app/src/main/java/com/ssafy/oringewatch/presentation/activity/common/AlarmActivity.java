package com.ssafy.oringewatch.presentation.activity.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.util.Log;
import androidx.activity.ComponentActivity;
import com.ssafy.oringewatch.R;
import com.ssafy.oringewatch.presentation.activity.MainActivity;
import com.ssafy.oringewatch.presentation.activity.alarm.CustomToggleButton;

public class AlarmActivity extends ComponentActivity {

    private GestureDetector gestureDetector;
    private static final String TAG = "AlarmActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        CustomToggleButton customToggleButton = findViewById(R.id.custom_toggle_button);
//        customToggleButton.setChecked(false); // 초기 상태 설정
        if (savedInstanceState == null) {
            customToggleButton.setChecked(false); // 초기 상태 설정
        }
        Log.d(TAG, "CustomToggleButton 초기 상태: " + customToggleButton.isChecked());

        gestureDetector = new GestureDetector(this, new SwipeGestureDetector());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    private class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    // Left swipe
                    Intent intent = new Intent(AlarmActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    return true;
                }
            }
            return false;
        }
    }
}
