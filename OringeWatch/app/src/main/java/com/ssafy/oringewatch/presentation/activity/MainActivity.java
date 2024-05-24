package com.ssafy.oringewatch.presentation.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.activity.ComponentActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.splashscreen.SplashScreen;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ssafy.oringewatch.R;
import com.ssafy.oringewatch.presentation.activity.common.AlarmActivity;
import com.ssafy.oringewatch.presentation.activity.common.LogoActivity;

public class MainActivity extends ComponentActivity {

    private GestureDetector gestureDetector;
    private boolean isSwipeHandled = false;
    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_DeviceDefault);
        setContentView(R.layout.activity_main);

//        // Initialize Firebase
//        FirebaseApp.initializeApp(this);
//
//        // Subscribe to topic (optional)
//        FirebaseMessaging.getInstance().subscribeToTopic("all")
//                .addOnCompleteListener(task -> {
//                    String msg = "Subscribed to topic";
//                    if (!task.isSuccessful()) {
//                        msg = "Subscription failed";
//                    }
//                    // Log or toast the message
//                });

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
                } else if(diffX < 0){
                    // Right swipe
                    Intent intent = new Intent(MainActivity.this, AlarmActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    return true;
                }
            }
            return false;
        }
    }
}
