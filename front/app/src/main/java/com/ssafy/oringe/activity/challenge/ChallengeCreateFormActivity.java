package com.ssafy.oringe.activity.challenge;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ssafy.oringe.R;
import com.ssafy.oringe.activity.common.MainActivity;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChallengeCreateFormActivity extends AppCompatActivity {
    private static final String API_URL = "https://k10b201.p.ssafy.io/oringe/api/challenge";

    private Button cancel;
    private Button create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_challenge_create_form);

        cancel = findViewById(R.id.cancel);
        create = findViewById(R.id.create);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // AlertDialog를 생성하고 설정합니다.
                new AlertDialog.Builder(ChallengeCreateFormActivity.this)
                    .setTitle("취소 확인") // 제목 설정
                    .setMessage("취소하시겠습니까?") // 메시지 설정
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(ChallengeCreateFormActivity.this, ChallengeListActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("취소", null)
                    .show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.challenge_create_form), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

//    public void createChallenge() {
//        create.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void onClick(View v) {
//                // 입력된 데이터 가져오기
//                EditText titleEdit = findViewById(R.id.title);
//                EditText startEdit = findViewById(R.id.start_date);
//                EditText endEdit = findViewById(R.id.end_date);
//
//                String title = titleEdit.getText().toString();
//                String start = startEdit.getText().toString();
//                String end = endEdit.getText().toString();
//                LocalDate startDate = LocalDate.parse(start);
//                LocalDate endDate = LocalDate.parse(end);
//
//
//                // 여기서 startDate와 endDate를 어떻게 가져올지 구현해야 합니다.
//                // 예시: String startDate = "2022-01-01"; String endDate = "2022-12-31";
//
//                // Retrofit 인스턴스 생성
//                Retrofit retrofit = new Retrofit.Builder()
//                    .baseUrl(API_URL) // 실제 서버 URL로 변경해야 합니다.
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//
//                // 서버로 보낼 객체 생성
//                Challenge challenge = new Challenge(title, startDate, endDate);
//
//                // 서버 요청
//                service.createChallenge(challenge).enqueue(new Callback<Void>() {
//                    @Override
//                    public void onResponse(Call<Void> call, Response<Void> response) {
//                        if (response.isSuccessful()) {
//                            // 요청 성공 처리
//                            Toast.makeText(ChallengeCreateFormActivity.this, "도전과제 생성 성공", Toast.LENGTH_SHORT).show();
//                        } else {
//                            // 서버 에러 처리
//                            Toast.makeText(ChallengeCreateFormActivity.this, "서버 에러", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<Void> call, Throwable t) {
//                        // 네트워크 에러 처리
//                        Toast.makeText(ChallengeCreateFormActivity.this, "네트워크 에러", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });
//    }


}
