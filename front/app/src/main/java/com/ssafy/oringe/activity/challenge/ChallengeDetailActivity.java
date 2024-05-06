package com.ssafy.oringe.activity.challenge;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.ssafy.oringe.R;

public class ChallengeDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_detail);
    }

}
// ChallengeListActivity에 추가할것.
//    challengeView.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            Intent intent = new Intent(ChallengeListActivity.this, ChallengeDetailActivity.class);
//            startActivity(intent);
//        }
//    });