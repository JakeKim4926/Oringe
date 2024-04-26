package com.ssafy.oringe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SigninActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    // 구글 로그인 버튼
    private SignInButton btn_google;

    // Firebase 인증 객체
    private FirebaseAuth auth;

    // 구글 API 클라이언트 객체
    private GoogleApiClient googleApiClient;

    //구글 로그인 결과 코드
    private static final int REQ_SIGN_GOOLE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signin);

        auth = FirebaseAuth.getInstance();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.firebase.ui.auth.R.string.default_web_client_id))
            .requestEmail()
            .build();

        Log.i("SigninActivity", "token = + " + String.valueOf(getString(com.firebase.ui.auth.R.string.default_web_client_id)));

        googleApiClient = new GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
            .build();

        btn_google = findViewById(R.id.btn_google);

        // 구글 로그인 버튼을 클릭했을 때 이 곳을 수행
        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, REQ_SIGN_GOOLE);
            }
        });
    }

    // 구글 로그인 인증을 요청 했을 때 결과 값을 되돌려 받는 곳
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_SIGN_GOOLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                resultLogin(account);
            } else {
                Log.e("SigninActivity", "Google 로그인 실패: " + result.getStatus());
            }
        }

    }

    // 로그인 결과 값 출력
    private void resultLogin(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    // 로그인 성공
                    if (task.isSuccessful()) {

                        // 서버로 토큰 전달
                        sendTokenToServer(account.getIdToken());

                        Toast.makeText(SigninActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                        startActivity(intent);

                    } else { // 로그인 실패 - 로그인 화면으로 돌아가기
                        Log.e("LoginActivity", "로그인 실패", task.getException());
                        Toast.makeText(SigninActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }


    private void sendTokenToServer(String idToken) {
        OkHttpClient client = new OkHttpClient();
        String url = "http://10.0.2.2:8050/api/signup";

        MediaType mediaType = MediaType.parse("application/json");
        String requestBody = "{\"idToken\":\"" + idToken + "\"}";
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
                if (response.isSuccessful()) {
                    Log.i("Server Response", response.body().string());
                } else {
                    Log.e("Server Error", "Response not successful");
                }
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("GoogleSignIn", "연결 실패: " + connectionResult.getErrorMessage());
    }
}