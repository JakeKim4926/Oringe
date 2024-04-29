package com.ssafy.oringe.activity.challenge;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ssafy.oringe.R;
import com.ssafy.oringe.api.challenge.Challenge;
import com.ssafy.oringe.api.challenge.ChallengeService;
import com.ssafy.oringe.ui.component.challenge.InputView;
import com.ssafy.oringe.ui.component.common.CalendarView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChallengeCreateFormActivity extends AppCompatActivity {
    private static final String API_URL = "http://10.0.2.2:8050/api/";

    private Button cancel;
    private Button create;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_challenge_create_form);

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
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               createChallenge();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.challenge_create_form), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createChallenge() {
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
            .challengeCycle(list)
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
        long id = Long.parseLong("11");
        Call<ResponseBody> call = retrofit.create(ChallengeService.class).sendData(challenge);
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
