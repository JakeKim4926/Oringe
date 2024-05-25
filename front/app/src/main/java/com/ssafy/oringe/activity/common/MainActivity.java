package com.ssafy.oringe.activity.common;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.ssafy.oringe.R;
import com.ssafy.oringe.activity.challenge.ChallengeDetailActivity;
import com.ssafy.oringe.activity.challenge.ChallengeDetailFragment;
import com.ssafy.oringe.activity.challenge.ChallengeListActivity;
import com.ssafy.oringe.activity.record.RecordCreateActivity;
import com.ssafy.oringe.api.TrustOkHttpClientUtil;
import com.ssafy.oringe.api.challenge.Challenge;
import com.ssafy.oringe.api.challenge.ChallengeService;
import com.ssafy.oringe.api.challengeDetail.ChallengeDetailService;
import com.ssafy.oringe.api.challengeDetail.dto.ChallengeDetailIdResponse;
import com.ssafy.oringe.api.member.Member;
import com.ssafy.oringe.api.member.MemberService;
import com.ssafy.oringe.api.record.RecordService;
import com.ssafy.oringe.ui.component.common.FooterBarView;
import com.ssafy.oringe.ui.component.common.MenuView;
import com.ssafy.oringe.ui.component.common.TitleView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private String API_URL;
    private FirebaseAuth auth;
    private Button btn_record;

    private TextView titleView;
    private TextView progressView;
    private ProgressBar progressBarView;
    private TextView successView;

    private ImageView orgView;

    private List<Challenge> challengeList;

    private ViewGroup challengeListContainer; // 동적 뷰를 추가할 컨테이너

    private RecordService recordService;

    private Long memberId;
    private static final String PREFS_NAME = "FooterBarPrefs";
    private static final String KEY_ACTIVE_ACTIVITY = "ActiveActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        API_URL = getString(R.string.APIURL);

        OkHttpClient client = TrustOkHttpClientUtil.getUnsafeOkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        recordService = retrofit.create(RecordService.class);


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

        Button btn_record = findViewById(R.id.btn_record);
        btn_record.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RecordCreateActivity.class)));

//        MenuView btn_list = findViewById(R.id.btn_list);
//        btn_list.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ChallengeListActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setActiveActivity(this, MainActivity.class.getSimpleName());
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Long memberId = sharedPref.getLong("loginId", 0);
        getTodayChallengeList(memberId);
        FooterBarView footerBarView = findViewById(R.id.footerBar);
        footerBarView.updateIcons(this);    }
    private void setActiveActivity(Context context, String activityName) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_ACTIVE_ACTIVITY, activityName);
        editor.apply();
    }
    private void getMemberId(String memberEmail) {
        OkHttpClient client = TrustOkHttpClientUtil.getUnsafeOkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build();
        Call<Member> call = retrofit.create(MemberService.class).getMemberByEmail(memberEmail);
        call.enqueue(new Callback<Member>() {
            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {
                if (response.isSuccessful()) {
                    Member memberResponse = response.body();
                    Long loginId = memberResponse.getMemberId();
                    memberId = loginId;
                    String loginNickName = memberResponse.getMemberNickName();

                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putLong("loginId", loginId);
                    editor.putString("loginNickName", loginNickName);
                    editor.apply();

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
        OkHttpClient client = TrustOkHttpClientUtil.getUnsafeOkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
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
                        dateTextView.setText(currentDate + " \n" + challengeCount + "개의 오린지가 있어요");
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
        if (challengeList.isEmpty()) {
            View challengeView = inflater.inflate(R.layout.sample_main_list_view, challengeListContainer, false);

            titleView = challengeView.findViewById(R.id.main_list_title);
            titleView.setText("오늘의 챌린지가 없어요 \n 챌린지를 생성해 보세요!");
            challengeListContainer.addView(challengeView);
            return;
        }
        for (Challenge challenge : challengeList) {
            View challengeView = inflater.inflate(R.layout.sample_main_list_view, challengeListContainer, false);

            titleView = challengeView.findViewById(R.id.main_list_title);
            progressView = challengeView.findViewById(R.id.main_list_progress);
            progressBarView = challengeView.findViewById(R.id.main_list_progressbar);
            successView = challengeView.findViewById(R.id.main_list_success);
            orgView = challengeView.findViewById(R.id.main_list_org);

            LocalDate startDate = LocalDate.parse(challenge.getChallengeStart());
            LocalDate endDate = LocalDate.parse(challenge.getChallengeEnd());
            LocalDate today = LocalDate.now();

            long totaldate = ChronoUnit.DAYS.between(startDate, endDate);
            long nowdate = ChronoUnit.DAYS.between(startDate, today);

            titleView.setText(challenge.getChallengeTitle());
            if (nowdate == 0) {
                progressView.setText("0" + "% 진행중");
                progressBarView.setProgress(1);
            } else {
                if ((int) ((double) nowdate / totaldate * 100) >= 100) {
                    progressView.setText("100" + "% 진행중");
                    progressBarView.setProgress(100);
                } else {
                    progressView.setText((int) ((double) nowdate / totaldate * 100) + "% 진행중");
                    progressBarView.setProgress((int) ((double) nowdate / totaldate * 100));
                }
            }
            getSuccessToday(memberId, challenge.getChallengeId(), orgView, successView);

            challengeView.setOnClickListener(v -> {
                FragmentManager fragmentManager = getSupportFragmentManager();
                ChallengeDetailFragment challengeDetailFragment = ChallengeDetailFragment.newInstance(
                        challenge.getChallengeId(),
                        challenge.getChallengeTitle(),
                        challenge.getChallengeMemo(),
                        challenge.getChallengeStart(),
                        challenge.getChallengeEnd(),
                        2
                );
                challengeDetailFragment.show(fragmentManager, "challengeDetail");
            });

            challengeListContainer.addView(challengeView);

        }
    }

    private void getSuccessToday(Long memberId, Long challengeId, ImageView imageView, TextView textView) {
        int TRUE = 1;
        Call<Integer> call = recordService.getTodaySuccess(memberId, challengeId);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()) {
                    Integer result = response.body();
                    if (result != null && result == TRUE) {
                        runOnUiThread(() -> {
                            imageView.setImageResource(R.drawable.main_org);
                            textView.setText("오늘의 챌린지 성공 !  ");
                        });
                    } else {
                        runOnUiThread(() -> {
                            imageView.setImageResource(R.drawable.sad_org);
                            textView.setText("오늘의 챌린지를 인증해주세요  ");
                        });
                    }
                } else {
                    try {
                        System.out.println("Response was not successful: " + response.code());
                        System.out.println("Response error body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                System.out.println("Request failed: " + t.getMessage());
                t.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "An error occurred while success of today", Toast.LENGTH_SHORT).show());
            }
        });
    }

}