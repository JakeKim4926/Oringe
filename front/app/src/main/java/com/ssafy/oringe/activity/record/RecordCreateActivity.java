package com.ssafy.oringe.activity.record;

import static com.ssafy.oringe.common.ChallengeDetailOrders.CHALLENGE_DETAIL_AUDIO;
import static com.ssafy.oringe.common.ChallengeDetailOrders.CHALLENGE_DETAIL_CONTENT;
import static com.ssafy.oringe.common.ChallengeDetailOrders.CHALLENGE_DETAIL_IMAGE;
import static com.ssafy.oringe.common.ChallengeDetailOrders.CHALLENGE_DETAIL_STT;
import static com.ssafy.oringe.common.ChallengeDetailOrders.CHALLENGE_DETAIL_TITLE;
import static com.ssafy.oringe.common.ChallengeDetailOrders.CHALLENGE_DETAIL_TTS;
import static com.ssafy.oringe.common.ChallengeDetailOrders.CHALLENGE_DETAIL_VIDEO;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ssafy.oringe.R;
import com.ssafy.oringe.api.TrustOkHttpClientUtil;
import com.ssafy.oringe.api.challenge.Challenge;
import com.ssafy.oringe.api.challenge.ChallengeService;
import com.ssafy.oringe.api.challengeDetail.dto.ChallengeDetailIdResponse;
import com.ssafy.oringe.api.challengeDetail.ChallengeDetailService;
import com.ssafy.oringe.common.ChallengeDetailOrders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecordCreateActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private String API_URL;
    private List<Challenge> challengeList;
    private List<Integer> challengeDetailOrder = new ArrayList<>();
    private Spinner spinner;

    private ChallengeDetailService challengeDetailService;

    Button buttonTitle;
    Button buttonContent;
    Button buttonImage;
    Button buttonAudio;
    Button buttonVideo;
    Button buttonSTT;
    Button buttonTTS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_record_create);
        API_URL = getString(R.string.APIURL);

        setupRetrofitClient();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            v.setPadding(insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom);
            return insets;
        });

        buttonTitle = findViewById(R.id.button_title);
        buttonContent = findViewById(R.id.button_content);
        buttonImage = findViewById(R.id.button_image);
        buttonAudio = findViewById(R.id.button_audio);
        buttonVideo = findViewById(R.id.button_video);
        buttonSTT = findViewById(R.id.button_stt);
        buttonTTS = findViewById(R.id.button_tts);

        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Long savedLoginId = sharedPref.getLong("loginId", 0);
        getChallengeList(savedLoginId);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Challenge selectedChallenge = (Challenge) parent.getItemAtPosition(position);
        Long challengeId = selectedChallenge.getChallengeId();
        Toast.makeText(this, "현재 챌린지 : " + selectedChallenge.getChallengeTitle(), Toast.LENGTH_LONG).show();

        getChallengeDetailIdAndOrderList(challengeId);
    }

    public void showButton() {
        buttonTitle.setVisibility(View.GONE);
        buttonContent.setVisibility(View.GONE);
        buttonImage.setVisibility(View.GONE);
        buttonAudio.setVisibility(View.GONE);
        buttonVideo.setVisibility(View.GONE);
        buttonSTT.setVisibility(View.GONE);
        buttonTTS.setVisibility(View.GONE);

        buttonTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditTitleDialog(CHALLENGE_DETAIL_TITLE);
            }
        });


        for(int value : challengeDetailOrder) {
            System.out.println(value);
            if(value == CHALLENGE_DETAIL_TITLE.getOrderCode())      buttonTitle.setVisibility(View.VISIBLE);
            if(value == CHALLENGE_DETAIL_CONTENT.getOrderCode())    buttonContent.setVisibility(View.VISIBLE);
            if(value == CHALLENGE_DETAIL_IMAGE.getOrderCode())      buttonImage.setVisibility(View.VISIBLE);
            if(value == CHALLENGE_DETAIL_AUDIO.getOrderCode())      buttonAudio.setVisibility(View.VISIBLE);
            if(value == CHALLENGE_DETAIL_VIDEO.getOrderCode())      buttonVideo.setVisibility(View.VISIBLE);
            if(value == CHALLENGE_DETAIL_STT.getOrderCode())        buttonSTT.setVisibility(View.VISIBLE);
            if(value == CHALLENGE_DETAIL_TTS.getOrderCode())        buttonTTS.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
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
                if (response.isSuccessful()) {
                    challengeList = response.body();
                    ArrayAdapter<Challenge> adapter = new ArrayAdapter<>(RecordCreateActivity.this,
                            android.R.layout.simple_spinner_item,
                            challengeList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                } else {
                    Toast.makeText(RecordCreateActivity.this, "Failed to fetch challenges", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Challenge>> call, Throwable t) {
                Toast.makeText(RecordCreateActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setupRetrofitClient() {
        OkHttpClient client = TrustOkHttpClientUtil.getUnsafeOkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        challengeDetailService = retrofit.create(ChallengeDetailService.class);
    }

    private void getChallengeDetailIdAndOrderList(Long challengeId) {
        challengeDetailService.getTemplatesId(challengeId).enqueue(new Callback<ChallengeDetailIdResponse>() {
            @Override
            public void onResponse(Call<ChallengeDetailIdResponse> call, Response<ChallengeDetailIdResponse> response) {
                if (response.isSuccessful()) {
                    ChallengeDetailIdResponse challengeDetailIdResponse = response.body();
                    if (challengeDetailIdResponse != null) {
                        getTemplatesOrder(challengeDetailIdResponse.getBody());
                    } else {
                        Toast.makeText(RecordCreateActivity.this, "Challenge Detail ID not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        Log.e("API Error Body", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(RecordCreateActivity.this, "Failed to fetch Challenge Detail ID", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChallengeDetailIdResponse> call, Throwable t) {
                System.out.println("여기냐 ");
                System.out.println(t.getMessage());
                Toast.makeText(RecordCreateActivity.this, "An error occurred while fetching Challenge Detail ID", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTemplatesOrder(Long challengeDetailId) {
        challengeDetailService.getTemplatesOrder(challengeDetailId).enqueue(new Callback<List<Integer>>() {
            @Override
            public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response) {
                if (response.isSuccessful()) {
                    challengeDetailOrder = response.body();
                    System.out.println(challengeDetailOrder);
                    showButton();
                    // TODO: Handle the order list, e.g., show in UI or process further
                } else {
                    Toast.makeText(RecordCreateActivity.this, "Failed to fetch order list", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Integer>> call, Throwable t) {
                Toast.makeText(RecordCreateActivity.this, "An error occurred while fetching order list", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditTitleDialog(ChallengeDetailOrders challengeDetailOrders) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String inputText = input.getText().toString();

                if(challengeDetailOrders == CHALLENGE_DETAIL_TITLE) {
                    // 여기에 제목은 100자 이하만 가능
                } else if(challengeDetailOrders == CHALLENGE_DETAIL_CONTENT) {
                    // 여기에 본문은 1000자 이하만 가능
                }

                updateButtonTitle(inputText);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void updateButtonTitle(String text) {
        Button buttonTitle = findViewById(R.id.button_title);
        buttonTitle.setText(text);

        // Adjust button width based on the text length
        ViewGroup.LayoutParams params = buttonTitle.getLayoutParams();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT; // Allow the button to size itself
        buttonTitle.setLayoutParams(params);
    }

}
