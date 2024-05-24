package com.ssafy.oringe.activity.challenge;

import android.content.Context;
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
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ssafy.oringe.R;
import com.ssafy.oringe.api.TrustOkHttpClientUtil;
import com.ssafy.oringe.api.challenge.Challenge;
import com.ssafy.oringe.api.challenge.ChallengeService;
import com.ssafy.oringe.ui.component.common.FooterBarView;
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
    private Long memberId;
    private String memberNickname;

    private TextView titleView;
    private ImageView alarmView;
    private TextView dDayView;
    private TextView cycleView;
    private TextView dateRangeView;
    private List<Challenge> challengeList;

    private ViewGroup challengeListContainer;
    private TitleView didView;
    private TitleView doView;
    private TitleView willView;
    private int currentStatus;
    private static final String PREFS_NAME = "FooterBarPrefs";
    private static final String KEY_ACTIVE_ACTIVITY = "ActiveActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_challenge_list);
        API_URL = getString(R.string.APIURL);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        memberId = sharedPref.getLong("loginId", 0);
        memberNickname = sharedPref.getString("loginNickName", "(알 수 없음)");
        getMemberNickname();

        currentStatus = 2;

        willView = findViewById(R.id.challengeList_will);
        doView = findViewById(R.id.challengeList_ing);
        didView = findViewById(R.id.challengeList_did);

        challengeListContainer = findViewById(R.id.challengeList_listLayout); // XML에서 레이아웃을 찾음

        View.OnClickListener tabListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                didView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray_400));
                doView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray_400));
                willView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray_400));

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
        setActiveActivity(this, ChallengeListActivity.class.getSimpleName());
        FooterBarView footerBarView = findViewById(R.id.footerBar);
        footerBarView.updateIcons(this);
        if (memberId != null) {
            getChallengeList(currentStatus);
        }
    }

    private void setActiveActivity(Context context, String activityName) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_ACTIVE_ACTIVITY, activityName);
        editor.apply();
    }

    private void getMemberNickname() {
        TextView whoView = findViewById(R.id.challengeList_who);
        whoView.setText(memberNickname + "님의 챌린지");
        getChallengeList(2);
    }

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
                    runOnUiThread(() -> setData(challengeList));
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
        if (challengeList.isEmpty()) {
            View challengeView = inflater.inflate(R.layout.sample_nothing_view, challengeListContainer, false);
            TextView textView = challengeView.findViewById(R.id.nothing);
            challengeListContainer.addView(challengeView);
        } else {
            for (Challenge challenge : challengeList) {
                View challengeView = inflater.inflate(R.layout.sample_list_view, challengeListContainer, false);
                titleView = challengeView.findViewById(R.id.challengeList_title);
                alarmView = challengeView.findViewById(R.id.challengeList_alarm);
                dDayView = challengeView.findViewById(R.id.challengeList_dDay);
                cycleView = challengeView.findViewById(R.id.challengeList_cycle);
                dateRangeView = challengeView.findViewById(R.id.challengeList_dateRange);

                LocalDate startDate = LocalDate.parse(challenge.getChallengeStart());
                LocalDate endDate = LocalDate.parse(challenge.getChallengeEnd());
                LocalDate today = LocalDate.now();

                long daysBetween = ChronoUnit.DAYS.between(startDate, today);
                long daysBetween2 = ChronoUnit.DAYS.between(today, endDate);

                List<Integer> cycle = challenge.getChallengeCycle();
                StringBuilder str = new StringBuilder();
                for (int day : cycle) {
                    switch (day) {
                        case 1:
                            str.append("월 ");
                            break;
                        case 2:
                            str.append("화 ");
                            break;
                        case 3:
                            str.append("수 ");
                            break;
                        case 4:
                            str.append("목 ");
                            break;
                        case 5:
                            str.append("금 ");
                            break;
                        case 6:
                            str.append("토 ");
                            break;
                        case 7:
                            str.append("일 ");
                            break;
                    }
                }

                titleView.setText(challenge.getChallengeTitle());
                alarmView.setVisibility(challenge.getChallengeAlarm() ? View.VISIBLE : View.GONE);
                if (daysBetween < 0 && daysBetween2 > 0) {
                    dDayView.setText("D" + daysBetween);
                } else if (daysBetween > 0 && daysBetween2 < 0) {
                    dDayView.setText("완료");
                }
                cycleView.setText(str.toString());
                dateRangeView.setText(challenge.getChallengeStart() + " ~ " + challenge.getChallengeEnd());

                challengeView.setOnClickListener(v -> {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    ChallengeDetailFragment challengeDetailFragment = ChallengeDetailFragment.newInstance(
                            challenge.getChallengeId(),
                            challenge.getChallengeTitle(),
                            challenge.getChallengeMemo(),
                            challenge.getChallengeStart(),
                            challenge.getChallengeEnd(),
                            currentStatus
                    );
                    challengeDetailFragment.show(fragmentManager, "challengeDetail");
                });

                challengeListContainer.addView(challengeView);
            }
        }
    }
}