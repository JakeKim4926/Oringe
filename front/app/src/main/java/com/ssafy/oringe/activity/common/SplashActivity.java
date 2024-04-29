package com.ssafy.oringe.activity.common;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ssafy.oringe.R;
import com.ssafy.oringe.activity.login.SigninActivity;
import com.ssafy.oringe.activity.login.SignupActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SplashActivity extends AppCompatActivity {

    // Firebase 인증 객체
    private FirebaseAuth auth;
    private static final int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        auth = FirebaseAuth.getInstance();

//        auth.signOut(); // 테스트용 로그아웃

        ImageView imageView = findViewById(R.id.imageViewSplash);
        Glide.with(this)
            .asGif()
            .load(R.drawable.splash)
            .into(imageView);

        new Handler().postDelayed(() -> {
            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser != null) {
                // 이미 로그인된 사용자
                checkUserRegistration(currentUser);
            } else {
                // 로그인 되어있지 않은 사용자
                promptLogin();
            }

        }, SPLASH_TIME_OUT);
    }

    private void checkUserRegistration(FirebaseUser user) {
        String userEmail = user.getEmail();
        sendEmailToServer(userEmail);
    }

    private void sendEmailToServer(String email) {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://10.0.2.2:8050/api/valid").newBuilder();
        urlBuilder.addQueryParameter("memberEmail", email);
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.e("SplashActivity", "Email 검증 실패", e);
                promptLogin();
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> promptLogin());
                    return;
                }
                try {
                    String jsonResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    boolean isRegistered = jsonObject.getBoolean("data");

                    runOnUiThread(() -> {
                        if (isRegistered) {
                            proceedToMain();
                        } else {
                            promptSignup();
                        }
                    });
                } catch (JSONException e) {
                    Log.e("SplashActivity", "JSON 파싱 오류", e);
                    runOnUiThread(() -> promptLogin());
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

    private void promptSignup() {
        startActivity(new Intent(SplashActivity.this, SignupActivity.class));
        finish();
    }
}
