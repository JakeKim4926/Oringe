package com.ssafy.oringe.activity.challenge;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ssafy.oringe.R;
import com.ssafy.oringe.api.TrustOkHttpClientUtil;
import com.ssafy.oringe.api.challenge.Challenge;
import com.ssafy.oringe.api.challenge.ChallengeService;
import com.ssafy.oringe.api.member.Member;
import com.ssafy.oringe.api.member.MemberService;
import com.ssafy.oringe.ui.component.common.TitleView;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChallengeListActivity extends AppCompatActivity {
    private String API_URL;
    /* member */
    private Long memberId;
    private String memberNickname;

    /* challenge */
    private TextView titleView;
    private ImageView alarmView;
    private TextView dDayView;
    private TextView cycleView;
    private TextView dateRangeView;
    private List<Challenge> challengeList;

    private ViewGroup challengeListContainer; // 동적 뷰를 추가할 컨테이너
    private TitleView didView;
    private TitleView doView;
    private TitleView willView;
    private int currentStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_challenge_list);
        API_URL = getString(R.string.APIURL);

        // 로그인 정보
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        memberId = sharedPref.getLong("loginId", 0);
        memberNickname = sharedPref.getString("loginNickName", "(알 수 없음)");
        getMemberNickname();

        // 맨 처음에는 진행중 리스트 조회
        currentStatus = 2;

        // 진행예정,진행중,진행완료
        willView = findViewById(R.id.challengeList_will);
        doView = findViewById(R.id.challengeList_ing);
        didView = findViewById(R.id.challengeList_did);

        challengeListContainer = findViewById(R.id.challengeList_list); // XML에서 레이아웃을 찾음

        View.OnClickListener tabListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 모든 탭의 색상을 초기화
                didView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray_400));
                doView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray_400));
                willView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray_400));

                // 클릭된 뷰의 색상 변경
                ((TitleView) v).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.oringe_sub));

                if (v == willView) currentStatus = 1;
                else if (v == doView) currentStatus = 2;
                else if (v == didView) currentStatus = 3;
                getChallengeList(currentStatus);
            }
        };

        didView.setOnClickListener(tabListener);
        doView.setOnClickListener(tabListener);
        willView.setOnClickListener(tabListener);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.challenge_list), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (memberId != null) {
            getChallengeList(currentStatus);
        }
    }

    // 로그인 정보
    private void getMemberNickname() {
        TitleView whoView = findViewById(R.id.challengeList_who);
        whoView.setText(memberNickname + "님의 챌린지");
        getChallengeList(2);

    }

    // status에 맞는 챌린지 리스트 조회
    public void getChallengeList(int status) {
        OkHttpClient client = TrustOkHttpClientUtil.getUnsafeOkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        Call<List<Challenge>> call = retrofit.create(ChallengeService.class).getData(memberId, status);
        call.enqueue(new Callback<List<Challenge>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<List<Challenge>> call, Response<List<Challenge>> response) {
                if (response.isSuccessful()) {
                    challengeList = response.body();
                    runOnUiThread((new Runnable() {
                        @Override
                        public void run() {
                            setData(challengeList);
                        }
                    }));
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(ChallengeListActivity.this, "챌린지 조회 실패", Toast.LENGTH_SHORT).show();
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

    // 챌린지 리스트 뷰에 추가
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setData(List<Challenge> challengeList) {
        LayoutInflater inflater = LayoutInflater.from(this);
        challengeListContainer.removeAllViews();
        if (challengeList.isEmpty()) {
            View challengeView = inflater.inflate(R.layout.sample_nothing_view, challengeListContainer, false);
            TextView textView = challengeView.findViewById(R.id.nothing);
            challengeListContainer.addView(challengeView);
        } else {
            for (Challenge challenge : challengeList) {
                // 각 challenge 객체에 대한 뷰를 동적으로 만들자!
                View challengeView = inflater.inflate(R.layout.sample_list_view, challengeListContainer, false);
                titleView = challengeView.findViewById(R.id.challengeList_title);
                alarmView = challengeView.findViewById(R.id.challengeList_alarm);
                dDayView = challengeView.findViewById(R.id.challengeList_dDay);
                cycleView = challengeView.findViewById(R.id.challengeList_cycle);
                dateRangeView = challengeView.findViewById(R.id.challengeList_dateRange);

                LocalDate startDate = LocalDate.parse(challenge.getChallengeStart());
                LocalDate endDate = LocalDate.parse(challenge.getChallengeEnd());
                LocalDate today = LocalDate.now();

                // 시작 날짜와 오늘 날짜 사이의 차이를 계산합니다.
                long daysBetween = ChronoUnit.DAYS.between(startDate, today);
                long daysBetween2 = ChronoUnit.DAYS.between(today, endDate);

                List<Integer> cycle = challenge.getChallengeCycle();
                List<String> days = new ArrayList<>();
                String str = "";
                for (int day : cycle) {
                    if (day == 1) str += "월 ";
                    else if (day == 2) str += "화 ";
                    else if (day == 3) str += "수 ";
                    else if (day == 4) str += "목 ";
                    else if (day == 5) str += "금 ";
                    else if (day == 6) str += "토 ";
                    else if (day == 7) str += "일 ";
                }

                titleView.setText(challenge.getChallengeTitle());
                alarmView.setVisibility(challenge.getChallengeAlarm() ? View.VISIBLE : View.GONE); // 알람이 true일 때만 보이도록
                if (daysBetween < 0 && daysBetween2 > 0) {
                    dDayView.setText("D" + daysBetween);
                } else if (daysBetween > 0 && daysBetween2 < 0) {
                    dDayView.setText("완료");
                }
                cycleView.setText(str);
                dateRangeView.setText(challenge.getChallengeStart() + " ~ " + challenge.getChallengeEnd());

                challengeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v ) {
                        Intent intent = new Intent(ChallengeListActivity.this, ChallengeDetailActivity.class);
                        intent.putExtra("challengeId", challenge.getChallengeId());
                        intent.putExtra("challengeTitle", challenge.getChallengeTitle());
                        intent.putExtra("challengeMemo", challenge.getChallengeMemo());
                        intent.putExtra("challengeStart", challenge.getChallengeStart());
                        intent.putExtra("challengeEnd", challenge.getChallengeEnd());
                        intent.putExtra("challengeStatus", currentStatus);
                        startActivity(intent);
                    }
                });
                challengeListContainer.addView(challengeView);

            }
        }
    }
}