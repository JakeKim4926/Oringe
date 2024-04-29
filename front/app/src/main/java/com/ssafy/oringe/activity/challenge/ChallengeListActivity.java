package com.ssafy.oringe.activity.challenge;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ssafy.oringe.R;
import com.ssafy.oringe.api.challenge.Challenge;
import com.ssafy.oringe.api.challenge.ChallengeService;
import com.ssafy.oringe.ui.component.challenge.ChallengeListView;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChallengeListActivity extends AppCompatActivity {
    private static final String API_URL = "http://10.0.2.2:8050/api/";
    private List<Challenge> challengeList;
    private ChallengeListView challengeListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_challenge_list);

        getChallengeList();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.challenge_list), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void getChallengeList() {
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        long id = Long.parseLong("11");
        Call<List<Challenge>> call = retrofit.create(ChallengeService.class).getData(Long.parseLong("11"));
        call.enqueue(new Callback<List<Challenge>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<List<Challenge>> call, Response<List<Challenge>> response) {
                if (response.isSuccessful()) {
                    challengeList = response.body();
                    runOnUiThread(() -> {
                        Toast.makeText(ChallengeListActivity.this, "챌린지 전체 조회", Toast.LENGTH_SHORT).show();
                        challengeList = response.body();
                        System.out.println(challengeList);
                        challengeListView = findViewById(R.id.challengeList); // XML에서 정의한 ChallengeListView의 id를 사용
                        challengeListView.setChallengeList(challengeList);
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(ChallengeListActivity.this, "챌린지 조회 실패", Toast.LENGTH_SHORT).show();
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
}