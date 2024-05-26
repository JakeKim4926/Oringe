package com.ssafy.oringewatch.presentation.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.core.splashscreen.SplashScreen;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ssafy.oringewatch.R;
import com.ssafy.oringewatch.presentation.activity.alarm.AlarmReceiver;
import com.ssafy.oringewatch.presentation.activity.common.AlarmActivity;
import com.ssafy.oringewatch.presentation.activity.challenge.ChallengeActivity;

import java.util.Calendar;

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

        TextView speechBubble = findViewById(R.id.speechBubble);
        String colorText = "2개";
        String text = "오늘의 챌린지가 " +  colorText + " 남아있어요!";
        SpannableString spannableString = new SpannableString(text);

        // "2개" 부분에 다른 색상을 적용
        int start = text.indexOf(colorText);
        int end = start + colorText.length();
        spannableString.setSpan(new ForegroundColorSpan(Color.rgb(255,107,0)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        speechBubble.setText(spannableString);


//        setAlarm();
        FirebaseApp.initializeApp(MainActivity.this);

        if (checkFirebaseInitialized()) {
            // Subscribe to topic (optional)
            FirebaseMessaging.getInstance().subscribeToTopic("all")
                    .addOnCompleteListener(task -> {
                        String msg = "Subscribed to topic";
                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                        // Log or toast the message
                        Toast.makeText(MainActivity.this, "알람!aaaaaaaaaaaaaa", Toast.LENGTH_SHORT).show();
                        // 진동 발생
                        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        if (vibrator != null) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                            } else {
                                //deprecated in API 26
                                vibrator.vibrate(500);
                            }
                        }


                    });
        } else {
            Log.e("Firebase Init", "Trying to re-initialize Firebase App.");
            FirebaseApp.initializeApp(this); // 다시 시도
        }

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
                    Intent intent = new Intent(MainActivity.this, ChallengeActivity.class);
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

    private void setAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // 알람 시간 설정: 01:05
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, 5);
        calendar.set(Calendar.SECOND, 0);

        // RTC_WAKEUP 유형을 사용하여 디바이스가 슬립 모드일 때도 알람이 작동하도록 설정
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    private boolean checkFirebaseInitialized() {
        try {
            FirebaseApp.getInstance();
            Log.e("Firebase Init", "Firebase is initialized successfully");
            return true;
        } catch (IllegalStateException e) {
            Log.e("Firebase Init", "Firebase is not initialized.", e);
            return false;
        }
    }
}
