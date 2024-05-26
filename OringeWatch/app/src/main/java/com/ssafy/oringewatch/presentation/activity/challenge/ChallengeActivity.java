package com.ssafy.oringewatch.presentation.activity.challenge;

import static com.ssafy.oringewatch.presentation.common.Util.API_URL;
import static com.ssafy.oringewatch.presentation.common.Util.memberId;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ssafy.oringewatch.R;
import com.ssafy.oringewatch.presentation.activity.MainActivity;
import com.ssafy.oringewatch.presentation.activity.record.RecordActivity;
import com.ssafy.oringewatch.presentation.api.TrustOkHttpClientUtil;
import com.ssafy.oringewatch.presentation.api.challenge.Challenge;
import com.ssafy.oringewatch.presentation.api.challenge.ChallengeService;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChallengeActivity extends ComponentActivity {

    private GestureDetector gestureDetector;
    private RecyclerView recyclerView;
    private ChallengeAdapter adapter;
    private List<Challenge> challenges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Add item decoration
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        recyclerView.addItemDecoration(new SpacingItemDecoration(spacingInPixels));

        gestureDetector = new GestureDetector(this, new SwipeGestureDetector());

        getChallengeList(memberId);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (gestureDetector != null && gestureDetector.onTouchEvent(event)) {
            return true;
        }
        return super.dispatchTouchEvent(event);
    }

    private class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX < 0) {
                    // Left swipe
                    Intent intent = new Intent(ChallengeActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    return true;
                }
            }
            return false;
        }
    }

    public void getChallengeList(Long memberId) {
        OkHttpClient client = TrustOkHttpClientUtil.getUnsafeOkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        Call<List<Challenge>> call = retrofit.create(ChallengeService.class).getData(memberId, 2);
        call.enqueue(new Callback<List<Challenge>>() {
            @Override
            public void onResponse(Call<List<Challenge>> call, Response<List<Challenge>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    challenges = new ArrayList<>();
                    challenges.addAll(response.body());

                    adapter = new ChallengeAdapter(challenges, ChallengeActivity.this::onChallengeClicked);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(ChallengeActivity.this, "Failed to fetch challenges", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Challenge>> call, Throwable t) {
                Toast.makeText(ChallengeActivity.this, "An error occurred during network communication", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onChallengeClicked(Challenge challenge) {
        Intent intent = new Intent(ChallengeActivity.this, RecordActivity.class);
        intent.putExtra("challengeId", challenge.getChallengeId());
        startActivity(intent);
    }
}
