package com.ssafy.oringewatch.presentation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.activity.ComponentActivity;
import androidx.core.splashscreen.SplashScreen;

import com.ssafy.oringewatch.R;
import com.ssafy.oringewatch.presentation.activity.common.LogoActivity;

public class MainActivity extends ComponentActivity {

    private GestureDetector gestureDetector;
    private boolean isSwipeHandled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_DeviceDefault);
        setContentView(R.layout.activity_main);

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
            if(isSwipeHandled)
                return false;

            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    // Right swipe
                    Intent intent = new Intent(MainActivity.this, LogoActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    return true;
                }
            }
            return false;
        }
    }
}
