package com.ssafy.oringe.activity.common;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ssafy.oringe.R;
import com.ssafy.oringe.activity.login.SigninActivity;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SplashActivity extends AppCompatActivity {

    // Firebase 인증 객체
    private FirebaseAuth auth;

    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        auth = FirebaseAuth.getInstance();

        new Handler().postDelayed(() -> {

            FirebaseUser currentUser = auth.getCurrentUser();

            if (currentUser != null) {
                currentUser.getIdToken(true).addOnCompleteListener(task -> {
                    // 기존 회원 -> 메인 페이지
                    if (task.isSuccessful()) {
                        String idToken = task.getResult().getToken();
                        sendTokenToServer(idToken);
//                        proceedToMain();
                        // 신규 회원 -> 회원가입 페이지
                    } else {
                        Log.e("SplashActivity", "ID Token 가져올 수 없음", task.getException());
                        promptLogin();
                    }
                });
            } else {
                promptLogin();
            }
        }, SPLASH_TIME_OUT);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void sendTokenToServer(String idToken) {
        OkHttpClient client = new OkHttpClient();
        String url = "http://10.0.2.2:8050/api/signup";

        MediaType mediaType = MediaType.parse("application/json");
        String requestBody = "{\"idToken\":\"" + idToken + "\"}";
        RequestBody body = RequestBody.create(mediaType, requestBody);

        Request request = new Request.Builder()
            .url(url)
            .post(body)
            .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.e("SplashActivity", "서버 연결 실패", e);
                promptLogin();
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    proceedToMain();
                } else {
                    Log.e("SplashActivity", "토큰 검증 실패");
                    promptLogin();
                }
            }
        });
    }

    private void proceedToMain() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    private void promptLogin() {
        startActivity(new Intent(SplashActivity.this, SigninActivity.class));
        finish();
    }
}