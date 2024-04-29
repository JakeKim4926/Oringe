package com.ssafy.oringe.activity.challenge;

import android.os.Build;
import android.os.Bundle;
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

import com.ssafy.oringe.R;
import com.ssafy.oringe.api.challenge.Challenge;
import com.ssafy.oringe.api.challenge.ChallengeService;
import com.ssafy.oringe.ui.component.common.TitleView;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChallengeListActivity extends AppCompatActivity {
    private static final String API_URL = "http://10.0.2.2:8050/api/";
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_challenge_list);

        willView = findViewById(R.id.challengeList_will);
        doView = findViewById(R.id.challengeList_ing);
        didView = findViewById(R.id.challengeList_did);

        challengeListContainer = findViewById(R.id.challengeList); // XML에서 레이아웃을 찾음
        getChallengeList(2);
        View.OnClickListener tabListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 모든 탭의 색상을 초기화
                didView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray_400));
                doView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray_400));
                willView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray_400));

                // 클릭된 뷰의 색상 변경
                ((TitleView) v).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.oringe_sub));

                int status = -1;
                if (v == willView) status = 1;
                else if (v == doView) status = 2;
                else if (v == didView) status = 3;
                getChallengeList(status);
            }
        };

        // 각 탭에 리스너 설정
        didView.setOnClickListener(tabListener);
        doView.setOnClickListener(tabListener);
        willView.setOnClickListener(tabListener);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.challenge_list), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void getChallengeList(int status) {
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        long id = Long.parseLong("11");
        Call<List<Challenge>> call = retrofit.create(ChallengeService.class).getData(Long.parseLong("11"), status);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setData(List<Challenge> challengeList) {
        LayoutInflater inflater = LayoutInflater.from(this);
        challengeListContainer.removeAllViews();
        for (Challenge challenge : challengeList) {
            // 각 challenge 객체에 대한 뷰를 동적으로 만들자!
            View challengeView = inflater.inflate(R.layout.sample_list_view, challengeListContainer, false);

            titleView = challengeView.findViewById(R.id.challengeList_title);
            alarmView = challengeView.findViewById(R.id.challengeList_alarm);
            dDayView = challengeView.findViewById(R.id.challengeList_dDay);
            cycleView = challengeView.findViewById(R.id.challengeList_cycle);
            dateRangeView = challengeView.findViewById(R.id.challengeList_dateRange);

            System.out.println("challenge " + challenge);
            System.out.println(challenge.getChallengeTitle());
            LocalDate startDate = LocalDate.parse(challenge.getChallengeStart());
            LocalDate today = LocalDate.now();

            // 시작 날짜와 오늘 날짜 사이의 차이를 계산합니다.
            long daysBetween = ChronoUnit.DAYS.between(startDate, today);

            titleView.setText(challenge.getChallengeTitle());
            alarmView.setVisibility(challenge.getChallengeAlarm() ? View.VISIBLE : View.GONE); // 알람이 true일 때만 보이도록
            dDayView.setText("D-" + daysBetween);
            cycleView.setText("매일");
            dateRangeView.setText(challenge.getChallengeStart() + " ~ " + challenge.getChallengeEnd());

            challengeListContainer.addView(challengeView);

        }
    }
}