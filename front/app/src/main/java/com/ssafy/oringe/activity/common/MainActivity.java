package com.ssafy.oringe.activity.common;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.ssafy.oringe.R;
import com.ssafy.oringe.activity.challenge.ChallengeListActivity;
import com.ssafy.oringe.activity.record.RecordCreateActivity;
import com.ssafy.oringe.api.TrustOkHttpClientUtil;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        API_URL = getString(R.string.APIURL);

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

        MenuView btn_list = findViewById(R.id.btn_list);
        btn_list.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ChallengeListActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Long memberId = sharedPref.getLong("loginId", 0);
        getTodayChallengeList(memberId);
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
                progressBarView.setProgress(0);
            } else {
                if ((int) ((double) nowdate / totaldate * 100) >= 100) {
                    progressView.setText("100" + "% 진행중");
                    progressBarView.setProgress(100);
                } else {
                    progressView.setText((int) ((double) nowdate / totaldate * 100) + "% 진행중");
                    progressBarView.setProgress((int) ((double) nowdate / totaldate * 100));
                }
            }
            orgView.setImageResource(R.drawable.sad_org);
            successView.setText("오늘은 달성하지 못했어요");
//            alarmView.setVisibility(challenge.getChallengeAlarm() ? View.VISIBLE : View.GONE);
//            successView.setText(인증있으면?"오늘 달성 완료!":"오늘은 달성하지 못했어요");
            challengeListContainer.addView(challengeView);

        }
    }
}