package com.ssafy.oringewatch.presentation.activity.record;

import static com.ssafy.oringewatch.presentation.common.Util.API_URL;
import static com.ssafy.oringewatch.presentation.common.Util.memberId;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ssafy.oringewatch.R;
import com.ssafy.oringewatch.presentation.api.TrustOkHttpClientUtil;
import com.ssafy.oringewatch.presentation.api.record.Record;
import com.ssafy.oringewatch.presentation.api.record.RecordService;

import java.util.Calendar;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecordActivity extends ComponentActivity {

    private RecyclerView recyclerView;
    private RecordAdapter adapter;
    private List<Record> records;
    private Long challengeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        challengeId = getIntent().getLongExtra("challengeId", -1);

        if (memberId != -1 && challengeId != -1) {
            getMonthlyRecords(memberId, challengeId);
        } else {
            Toast.makeText(this, "Invalid memberId or challengeId", Toast.LENGTH_SHORT).show();
        }
    }

    public void getMonthlyRecords(Long memberId, Long challengeId) {
        OkHttpClient client = TrustOkHttpClientUtil.getUnsafeOkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        RecordService recordService = retrofit.create(RecordService.class);

        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;

        Call<List<Record>> call = recordService.fetchMonthlyRecords(memberId, challengeId, currentMonth);
        call.enqueue(new Callback<List<Record>>() {
            @Override
            public void onResponse(Call<List<Record>> call, Response<List<Record>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    records = response.body();
                    adapter = new RecordAdapter(records, RecordActivity.this);
                    recyclerView.setAdapter(adapter);  // 어댑터를 설정합니다.
                } else {
                    Toast.makeText(RecordActivity.this, "Failed to fetch records", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Record>> call, Throwable t) {
                Toast.makeText(RecordActivity.this, "An error occurred during network communication", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
