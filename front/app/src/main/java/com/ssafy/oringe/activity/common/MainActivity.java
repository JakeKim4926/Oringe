package com.ssafy.oringe.activity.common;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.ssafy.oringe.R;
import com.ssafy.oringe.activity.challenge.ChallengeListActivity;
import com.ssafy.oringe.activity.login.SignupActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    Button btn_record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String email = user.getEmail();
        getMemberId(email);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //메인 인사
//        TextView mainHi = findViewById(R.id.text_nickname_hi);

        //오늘의 남은 오린지
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        TextView dateTextView = findViewById(R.id.text_today_oringe);

        dateTextView.setText(currentDate + " \n 1개의 오린지가 남았어요");
        dateTextView.setTypeface(null, Typeface.BOLD);

        //챌린지리스트로 가기
        FloatingActionButton btn_list;
        btn_list = findViewById(R.id.btn_list);
        btn_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ChallengeListActivity.class));

            }
        });
    }

    private void getMemberId(String memberEmail){
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://10.0.2.2:8050/api/signin").newBuilder();
        urlBuilder.addQueryParameter("memberEmail", memberEmail);
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    Gson gson = new Gson();
                    MemberResponse memberResponse = gson.fromJson(responseBody, MemberResponse.class);


                    Long loginId = memberResponse.getMemberId();
                    String loginNickName = memberResponse.getMemberNickName();

//                    Intent intent = new Intent(MainActivity.this, ChallengeListActivity.class);
//                    intent.putExtra("memberId", loginId);
//                    intent.putExtra("memberNickname", loginNickName);
//                    startActivity(intent);

                    //이거 사용할 때
//                    Intent intent = getIntent();
//                    Long memberId = intent.getLongExtra("memberId", -1); // 기본값으로 -1을 설정하여 에러를 방지
//                    String memberNickname = intent.getStringExtra("memberNickname");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 메인 인사 텍스트뷰 업데이트
                            TextView mainHi = findViewById(R.id.text_nickname_hi);
                            mainHi.setText(loginNickName  + "님 \n 오늘도 오린지 하세요!");
                        }
                    });
                }
            }
        });
    }

}
class MemberResponse {
    private Long memberId;
    private String memberEmail;
    private String memberNickName;

    // Getter 메서드들
    public Long getMemberId() {
        return memberId;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public String getMemberNickName() {
        return memberNickName;
    }
}