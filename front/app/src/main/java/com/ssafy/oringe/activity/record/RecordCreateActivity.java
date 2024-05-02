package com.ssafy.oringe.activity.record;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ssafy.oringe.R;
import com.ssafy.oringe.activity.common.MainActivity;
import com.ssafy.oringe.api.challenge.Challenge;
import com.ssafy.oringe.api.challenge.ChallengeService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecordCreateActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String API_URL = "http://10.0.2.2:8050/api/";
    private List<Challenge> challengeList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_record_create);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Long savedLoginId = sharedPref.getLong("loginId", 0);

        getChallengeList(savedLoginId, new ChallengeListCallback() {
            @Override
            public void onChallengeListReceived(String[] titles) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(RecordCreateActivity.this, android.R.layout.simple_spinner_item, titles);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                Spinner spinner = findViewById(R.id.spinner);
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(RecordCreateActivity.this);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedOption = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // 사용자가 아무것도 선택하지 않은 경우
    }

    public void getChallengeList(Long memberId, ChallengeListCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        System.out.println("memberID: " + memberId);
        Call<List<Challenge>> call = retrofit.create(ChallengeService.class).getData(memberId, 2);
        call.enqueue(new Callback<List<Challenge>>() {
            @Override
            public void onResponse(Call<List<Challenge>> call, Response<List<Challenge>> response) {
                if (response.isSuccessful()) {
                    List<Challenge> challengeList = response.body();
                    String[] titles = new String[challengeList.size()];
                    for (int i = 0; i < challengeList.size(); i++) {
                        titles[i] = challengeList.get(i).getChallengeTitle();
                        System.out.println(titles[i]);
                    }
                    callback.onChallengeListReceived(titles);
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(RecordCreateActivity.this, "챌린지 조회 실패", Toast.LENGTH_SHORT).show();
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

    public interface ChallengeListCallback {
        void onChallengeListReceived(String[] titles);
    }
}