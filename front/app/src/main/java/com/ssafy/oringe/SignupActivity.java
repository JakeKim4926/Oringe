package com.ssafy.oringe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignupActivity extends AppCompatActivity {

    private EditText et_nickname;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Button btn_start = findViewById(R.id.btn_start);
        et_nickname = findViewById(R.id.et_nickname);
        auth = FirebaseAuth.getInstance();

        Log.d("SignupActivity", "signupActivity 진입");

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = auth.getCurrentUser();
                Log.d("SignupActivity", "버튼 클릭됨");

                if (user != null) {
                    Log.d("SignupActivity", "email = " + user.getEmail());
                    String email = user.getEmail();
                    String nickname = et_nickname.getText().toString();
                    Log.d("SignupActivity", "nickname = " + nickname);

                    if (!nickname.isEmpty()) {
                        sendUserInfoToServer(email, nickname);
                    } else {
                        Toast.makeText(SignupActivity.this, "체리톡에서 사용할 영어이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("SignupActivity", "사용자 정보 없음");
                    Toast.makeText(SignupActivity.this, "로그인 되어있지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void sendUserInfoToServer(String email, String nickname) {

        OkHttpClient client = new OkHttpClient();
        String url = "http://10.0.2.2:8050/api/signup";

        MediaType mediaType = MediaType.parse("application/json");
        String requestBody = "{\"memberEmail\":\"" + email + "\", \"memberNickname\":\"" + nickname + "\"}";
        RequestBody body = RequestBody.create(requestBody, mediaType);

        Request request = new Request.Builder()
            .url(url)
            .post(body)
            .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                Log.d("HTTP Status Code", String.valueOf(response.code()));
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(SignupActivity.this, "회원가입 성공!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignupActivity.this, MainActivity.class));
                        Log.d("HTTP Status Code", String.valueOf(response.code()));
                        finish();
                    });
                } else {
                    runOnUiThread(() -> {
                        Log.d("HTTP Status Code", String.valueOf(response.code()));
                        Toast.makeText(SignupActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                        Log.d("HTTP Status Code", String.valueOf(response.code()));
                    });
                }
            }
        });
    }
}