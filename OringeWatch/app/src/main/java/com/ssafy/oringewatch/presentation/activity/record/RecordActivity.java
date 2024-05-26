package com.ssafy.oringewatch.presentation.activity.record;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ssafy.oringewatch.R;

public class RecordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        long challengeId = getIntent().getLongExtra("challengeId", -1);
        if (challengeId != -1) {
            fetchRecordsForMonth(challengeId);
        }
    }

    private void fetchRecordsForMonth(long challengeId) {
        // Retrofit 호출로 challengeId에 해당하는 이번 달의 Record들을 조회
    }
}