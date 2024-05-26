package com.ssafy.oringewatch.presentation.activity.challenge;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.activity.ComponentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ssafy.oringewatch.R;
import com.ssafy.oringewatch.presentation.activity.MainActivity;
import com.ssafy.oringewatch.presentation.api.challenge.Challenge;

import java.util.ArrayList;
import java.util.List;

public class ChallengeActivity extends ComponentActivity {

    private GestureDetector gestureDetector;
    private RecyclerView recyclerView;
    private ChallengeAdapter adapter;
    private List<Challenge> challenges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize your data here
        challenges = new ArrayList<>();
        challenges.add(Challenge.builder()
                        .challengeTitle("자존감 챌린지")
                        .challengeStart("2024-04-25")
                        .challengeEnd("2024-06-30")
                        .build());
        challenges.add(Challenge.builder()
                        .challengeTitle("플로깅 챌린지")
                        .challengeStart("2024-05-23")
                        .challengeEnd("2024-08-30")
                        .build());
        adapter = new ChallengeAdapter(challenges);
        recyclerView.setAdapter(adapter);

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
                if (diffX < 0) {
                    // Left swipe
                    Intent intent = new Intent(ChallengeActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    return true;
                } else
                    return false;
            }
            return false;
        }
    }
}
