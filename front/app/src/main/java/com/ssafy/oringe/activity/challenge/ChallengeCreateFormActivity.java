package com.ssafy.oringe.activity.challenge;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ssafy.oringe.R;
import com.ssafy.oringe.api.challenge.Challenge;
import com.ssafy.oringe.api.challenge.ChallengeService;
import com.ssafy.oringe.api.member.Member;
import com.ssafy.oringe.api.member.MemberService;
import com.ssafy.oringe.ui.component.challenge.InputView;
import com.ssafy.oringe.ui.component.common.CalendarView;
import com.ssafy.oringe.ui.component.common.TitleView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChallengeCreateFormActivity extends AppCompatActivity {
    /* member */
    private static final String LOGIN_API_URL = "http://10.0.2.2:8050/api/";
    private FirebaseAuth auth;
    private Long memberId;

    /* challenge */
    private static final String API_URL = "http://10.0.2.2:8050/api/";

    private Button cancel;
    private Button create;

    private boolean[] clicked;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_challenge_create_form);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String email = user.getEmail();
        getMemberId(email);

        chooseCycle();

        // 취소
        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "취소되었습니다!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ChallengeCreateFormActivity.this, ChallengeListActivity.class);
                startActivity(intent);
            }
        });

        // 생성
        create = findViewById(R.id.create);
        List<Integer> cycle = new ArrayList<>();
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 1; i < 8; i++) {
                    if (clicked[i]) cycle.add(i);
                }
                createChallenge(cycle);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.challenge_create_form), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void getMemberId(String memberEmail) {
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(LOGIN_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        Call<Member> call = retrofit.create(MemberService.class).getMemberByEmail(memberEmail);
        call.enqueue(new Callback<Member>() {
            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {
                if (response.isSuccessful()) {
                    Member memberResponse = response.body();
                    memberId = memberResponse.getMemberId();
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

    public void chooseCycle() {
        View view = findViewById(R.id.dayLayout);
        TextView monView = view.findViewById(R.id.monday);
        TextView tueView = view.findViewById(R.id.tuesday);
        TextView wedView = view.findViewById(R.id.wednesday);
        TextView thuView = view.findViewById(R.id.thursday);
        TextView friView = view.findViewById(R.id.friday);
        TextView satView = view.findViewById(R.id.saturday);
        TextView sunView = view.findViewById(R.id.sunday);

        clicked = new boolean[8];

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView dayView = (TextView) v;
                String day = dayView.getText().toString();
                int i = 0;
                if (day.equals("월")) i = 1;
                else if (day.equals("화")) i = 2;
                else if (day.equals("수")) i = 3;
                else if (day.equals("목")) i = 4;
                else if (day.equals("금")) i = 5;
                else if (day.equals("토")) i = 6;
                else if (day.equals("일")) i = 7;

                clicked[i] = !clicked[i];
                if (clicked[i]) dayView.setBackgroundColor(ContextCompat.getColor(ChallengeCreateFormActivity.this, R.color.oringe_sub));
                else dayView.setBackgroundColor(ContextCompat.getColor(ChallengeCreateFormActivity.this, R.color.transparent));
            }
        };
        monView.setOnClickListener(clickListener);
        tueView.setOnClickListener(clickListener);
        wedView.setOnClickListener(clickListener);
        thuView.setOnClickListener(clickListener);
        friView.setOnClickListener(clickListener);
        satView.setOnClickListener(clickListener);
        sunView.setOnClickListener(clickListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createChallenge(List<Integer> cycle) {
        View view = findViewById(R.id.challenge_create_form);

        InputView titleEdit = view.findViewById(R.id.input_title);
        CalendarView startEdit = view.findViewById(R.id.input_start_date);
        CalendarView endEdit = view.findViewById(R.id.input_end_date);

        String title = titleEdit.getEditText();
        String start = startEdit.getEditText();
        String end = endEdit.getEditText();
        System.out.println(title);
        System.out.println(start);
        System.out.println(end);

        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(0);
        list.add(0);
        list.add(0);
        list.add(0);
        list.add(0);
        list.add(0);
        list.add(0);
        list.add(0);
        list.add(2);
        list.add(0);

        Challenge challenge = Challenge.builder()
            .challengeTitle(title)
            .challengeStart(startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            .challengeEnd(endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            .challengeCycle(cycle)
            .challengeAlarm(false)
            .challengeAlarmTime(LocalTime.MIDNIGHT.format(DateTimeFormatter.ofPattern("HH:mm")))
            .challengeMemo("")
            .challengeAppName("")
            .challengeAppTime(LocalTime.MIDNIGHT.format(DateTimeFormatter.ofPattern("HH:mm")))
            .challengeCallName("")
            .challengeCallNumber("")
            .challengeWakeupTime(LocalTime.MIDNIGHT.format(DateTimeFormatter.ofPattern("HH:mm")))
            .challengeWalk(0)
            .order(list)
            .build();

        System.out.println(challenge);
        Call<ResponseBody> call = retrofit.create(ChallengeService.class).sendData(challenge, memberId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                System.out.println(response.code());
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(ChallengeCreateFormActivity.this, "챌린지 생성!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ChallengeCreateFormActivity.this, ChallengeListActivity.class));
                        Log.d("HTTP Status Code", String.valueOf(response.code()));
                        finish();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(ChallengeCreateFormActivity.this, "챌린지 생성 실패", Toast.LENGTH_SHORT).show();
                        Log.d("HTTP Status Code", String.valueOf(response.code()));
                    });
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

}
