package com.ssafy.oringe.activity.challenge;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;



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
    }
    // 로그인 정보
    private void getMemberNickname() {
        TitleView whoView = findViewById(R.id.challengeList_who);
        whoView.setText(memberNickname + "님의 챌린지");
    }


}
