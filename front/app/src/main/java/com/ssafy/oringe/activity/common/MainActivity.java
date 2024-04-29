package com.ssafy.oringe.activity.common;

import android.content.Intent;
import android.graphics.Typeface;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
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
import com.ssafy.oringe.ui.component.common.TitleView;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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

    private TextView titleView;
    private TextView progressView;
    private ProgressBar progressBarView;
    private TextView successView;
    private List<Challenge> challengeList;

    private ViewGroup challengeListContainer; // 동적 뷰를 추가할 컨테이너


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String email = user.getEmail();
        getMemberId(email);
        EdgeToEdge.enable(this);

        challengeListContainer = findViewById(R.id.main_list);

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


                    runOnUiThread(() -> {
                        TextView mainHi = findViewById(R.id.text_nickname_hi);
                        mainHi.setText(loginNickName + "님 \n 오늘도 오린지 하세요!");
                        // 챌린지 목록 가져오기
                        getTodayChallengeList(loginId);
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

    public void getTodayChallengeList(Long memberId) {
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        Call<List<Challenge>> call = retrofit.create(ChallengeService.class).getTodayChallengeList(memberId);
        call.enqueue(new Callback<List<Challenge>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<List<Challenge>> call, Response<List<Challenge>> response) {
                if (response.isSuccessful()) {
                    challengeList = response.body();
                    int challengeCount = challengeList != null ? challengeList.size() : 0;

                    runOnUiThread(() -> {
                        TextView dateTextView = findViewById(R.id.text_today_oringe);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());
                        String currentDate = dateFormat.format(new Date());
                        dateTextView.setText(currentDate + " \n" + challengeCount + "개의 오린지가 남았어요");
                        dateTextView.setTextSize(15);
                        dateTextView.setTypeface(null, Typeface.BOLD);
                        setData(challengeList);
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "챌린지 조회 실패", Toast.LENGTH_SHORT).show();
                        Log.d("HTTP Status Code", String.valueOf(response.code()));
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Challenge>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setData(List<Challenge> challengeList) {
        LayoutInflater inflater = LayoutInflater.from(this);
        challengeListContainer.removeAllViews();
        for (Challenge challenge : challengeList) {
            // 각 challenge 객체에 대한 뷰를 동적으로 만들자!
            View challengeView = inflater.inflate(R.layout.sample_main_list_view, challengeListContainer, false);

            titleView = challengeView.findViewById(R.id.main_list_title);
            progressView = challengeView.findViewById(R.id.main_list_progress);
            progressBarView = challengeView.findViewById(R.id.main_list_progressbar);
            successView = challengeView.findViewById(R.id.main_list_success);

            LocalDate startDate = LocalDate.parse(challenge.getChallengeStart());
            LocalDate endDate = LocalDate.parse(challenge.getChallengeEnd());
            LocalDate today = LocalDate.now();

            // 시작 날짜와 오늘 날짜 사이의 차이를 계산합니다.
            long totaldate = ChronoUnit.DAYS.between(startDate, endDate);
            long nowdate = ChronoUnit.DAYS.between(startDate, today);

            titleView.setText(challenge.getChallengeTitle());
            if(nowdate == 0){
                progressView.setText("0"+ "% 진행중");
                progressBarView.setProgress(0);
            }else{
                progressView.setText((int) ((double) nowdate / totaldate * 100) + "% 진행중");
                progressBarView.setProgress((int) ((double) nowdate / totaldate * 100));
            }

            challengeListContainer.addView(challengeView);

        }
    }
}