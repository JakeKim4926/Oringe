package com.ssafy.oringe.activity.common;

import android.content.Intent;
import android.graphics.Typeface;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.ssafy.oringe.R;
import com.ssafy.oringe.activity.challenge.ChallengeListActivity;
import com.ssafy.oringe.api.challenge.Challenge;
import com.ssafy.oringe.api.challenge.ChallengeService;
import com.ssafy.oringe.api.member.Member;
import com.ssafy.oringe.api.member.MemberService;
import com.ssafy.oringe.ui.component.common.MenuView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String API_URL = "http://10.0.2.2:8050/api/";
    private FirebaseAuth auth;
    private Button btn_record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String email = user.getEmail();
        getMemberId(email);
        EdgeToEdge.enable(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        //챌린지리스트로 가기
        MenuView btn_list = findViewById(R.id.btn_list);
        btn_list.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ChallengeListActivity.class)));
    }

    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build();
    private void getMemberId(String memberEmail) {
        Call<Member> call = retrofit.create(MemberService.class).getMemberByEmail(memberEmail);
        call.enqueue(new Callback<Member>() {
            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {
                if (response.isSuccessful()) {
                    Member memberResponse = response.body();
                    Long loginId = memberResponse.getMemberId();
                    String loginNickName = memberResponse.getMemberNickName();

                    // 챌린지 목록 가져오기
                    getTodayChallengeList(loginId);

                    runOnUiThread(() -> {
                        TextView mainHi = findViewById(R.id.text_nickname_hi);
                        mainHi.setText(loginNickName + "님 \n 오늘도 오린지 하세요!");
                    });
                } else {
                    Log.e("API_CALL", "Response Error : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Member> call, Throwable t) {
                Log.e("API_CALL", "Failed to get member details", t);
            }
        });
    }

    private void getTodayChallengeList(Long memberId) {
        Call<List<Challenge>> call = retrofit.create(ChallengeService.class).getTodayChallengeList(memberId);
        call.enqueue(new Callback<List<Challenge>>() {
            @Override
            public void onResponse(Call<List<Challenge>> call, Response<List<Challenge>> response) {
                if (response.isSuccessful()) {
                    List<Challenge> challenges = response.body();
                    int challengeCount = challenges != null ? challenges.size() : 0;

                    LinearLayout mainLayout = findViewById(R.id.main_list); // 메인 레이아웃 참조

                    for (Challenge challenge : challenges) {
                        // 챌린지용 LinearLayout 생성
                        LinearLayout challengeLayout = new LinearLayout(MainActivity.this);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        challengeLayout.setLayoutParams(layoutParams);
                        challengeLayout.setOrientation(LinearLayout.VERTICAL);
                        challengeLayout.setGravity(Gravity.CENTER);
                        challengeLayout.setPadding(20, 20, 20, 20);
                        challengeLayout.setBackgroundResource(R.drawable.main_oringe_list);

                        // 챌린지 이름 및 달성률 TextView 추가
                        TextView challengeName = new TextView(MainActivity.this);
                        challengeName.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        challengeName.setGravity(Gravity.CENTER);
                        challengeName.setText(challenge.getChallengeTitle());
                        challengeName.setTextColor(getResources().getColor(R.color.black));
                        challengeName.setTextSize(15);

                        TextView challengePercent = new TextView(MainActivity.this);
                        challengePercent.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        challengePercent.setGravity(Gravity.CENTER);
                        challengePercent.setText("% 달성");
                        challengePercent.setTextColor(getResources().getColor(R.color.black));
                        challengePercent.setTextSize(15);

                        // 프로그래스바 추가
                        ProgressBar progressBar = new ProgressBar(MainActivity.this, null, android.R.attr.progressBarStyleHorizontal);
                        LinearLayout.LayoutParams progressBarParams = new LinearLayout.LayoutParams(
                            1000, LinearLayout.LayoutParams.WRAP_CONTENT);
                        progressBarParams.setMargins(0, 20, 0, 0);
                        progressBar.setLayoutParams(progressBarParams);
                        progressBar.setProgress(50);
                        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.main_progressbar));

                        // LinearLayout에 챌린지 이름, 달성률, 프로그래스바 추가
                        challengeLayout.addView(challengeName);
                        challengeLayout.addView(challengePercent);
                        challengeLayout.addView(progressBar);

                        // 메인 레이아웃에 챌린지 레이아웃 추가
                        mainLayout.addView(challengeLayout);
                    }

                    runOnUiThread(() -> {
                        TextView dateTextView = findViewById(R.id.text_today_oringe);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());
                        String currentDate = dateFormat.format(new Date());
                        dateTextView.setText(currentDate + " \n" + challengeCount + "개의 오린지가 남았어요");
                        dateTextView.setTextSize(15);
                        dateTextView.setTypeface(null, Typeface.BOLD);
                    });
                } else {
                    Log.e("API_CALL", "Response Error : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Challenge>> call, Throwable t) {
                Log.e("API_CALL", "Failed to get challenges", t);
            }
        });
    }

}