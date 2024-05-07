package com.ssafy.oringe.activity.challenge;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import com.ssafy.oringe.R;

import com.ssafy.oringe.ui.component.common.TitleView;

// ChallengeListActivity로 부터 Challenge의

public class ChallengeDetailActivity extends AppCompatActivity {
    private String memberNickname;
    private Long memberId;
    private String API_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_detail);
        API_URL = getString(R.string.APIURL);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        memberId = sharedPref.getLong("loginId", 0);
        memberNickname = sharedPref.getString("loginNickName", "(알 수 없음)");
        getMemberNickname();
        Intent intent = getIntent();
        long challengeId = intent.getLongExtra("challengeId", -1);
        String challengeTitle = intent.getStringExtra("challengeTitle");
        String challengeMemo = intent.getStringExtra("challengeMemo");

        // 수신된 데이터를 UI 컴포넌트에 설정
//        TextView titleTextView = findViewById(R.id.challengeDetail_title);
//        TextView memoTextView = findViewById(R.id.challengeDetail_memo);

//        titleTextView.setText(challengeTitle);
//        titleTextView.setText(challengeMemo);

        TitleView titleView = findViewById(R.id.challengeDetail_titleView);
        TitleView memoView = findViewById(R.id.challengeDetail_memoView);

        titleView.setText(challengeTitle);
        memoView.setText(challengeMemo);
    }
    // 로그인 정보
    private void getMemberNickname() {
        TitleView whoView = findViewById(R.id.challengeList_who);
        whoView.setText(memberNickname + "님의 챌린지");
    }


}
