package com.ssafy.oringe.activity.challenge;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ssafy.oringe.R;
import com.ssafy.oringe.api.TrustOkHttpClientUtil;
import com.ssafy.oringe.api.member.Member;
import com.ssafy.oringe.api.member.MemberService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TemplateActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 222;
    private String API_URL;
    private FirebaseAuth auth;
    private Long memberId;

    /*template*/
    private ViewGroup categoryContainer;
    private ViewGroup templateContainer;
    private List<String> chooseTemplates;
    private ArrayList<String> normalTemplates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_template);
        API_URL = getString(R.string.APIURL);

        normalTemplates = new ArrayList<>();


        // 로그인 정보
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String email = user.getEmail();
        getMemberId(email);

        // 카테고리 목록
        categoryContainer = findViewById(R.id.template_category);
        getCategoryList();

        // 템플릿 목록
        templateContainer = findViewById(R.id.template_templateDetail);
        getTemplateList("전체");

        // 선택완료
        TextView create = findViewById(R.id.template_create);
        create.setOnClickListener(v -> {
            Intent intent = new Intent();
            HashMap<String, Integer> orderMap = setOrder();
            intent.putExtra("orderMap", orderMap);
            intent.putExtra("normalTemplates", normalTemplates);
            setResult(Activity.RESULT_OK, intent);
            finish();

        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.template), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    // 데이터 받아오기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("데이터 받아오기");
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                normalTemplates = data.getStringArrayListExtra("normalTemplates");
            }
            setOrder();
        }
    }


    // 로그인 정보
    private void getMemberId(String memberEmail) {
        OkHttpClient client = TrustOkHttpClientUtil.getUnsafeOkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(API_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        Call<Member> call = retrofit.create(MemberService.class).getMemberByEmail(memberEmail);
        call.enqueue(new Callback<Member>() {
            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {
                if (response.isSuccessful()) {
                    Member memberResponse = response.body();
                    memberId = memberResponse.getMemberId();
                } else {
                    Log.e("API_CALL", "Response Error : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Member> call, Throwable t) {
                Log.e("API_CALL", "Failed to get member details", t);
            }
        });
    }

    // 카테고리 목록
    private void getCategoryList() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View categoryView = inflater.inflate(R.layout.sample_category_view, categoryContainer, false);

        TextView totalView = categoryView.findViewById(R.id.template_category_total);
        TextView txtView = categoryView.findViewById(R.id.template_category_text);
        TextView imgView = categoryView.findViewById(R.id.template_category_img);
        TextView videoView = categoryView.findViewById(R.id.template_category_video);
        TextView audioView = categoryView.findViewById(R.id.template_category_audio);

        View.OnClickListener tabListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 모든 탭의 색상을 초기화
                totalView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                totalView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.category_border_pink));
                txtView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                txtView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.category_border_pink));
                imgView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                imgView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.category_border_pink));
                videoView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                videoView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.category_border_pink));
                audioView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                audioView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.category_border_pink));

                // 클릭된 뷰의 색상 변경
                ((TextView) v).setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.category_color_pink));

                getTemplateList(((TextView) v).getText().toString());
            }
        };

        totalView.setOnClickListener(tabListener);
        txtView.setOnClickListener(tabListener);
        imgView.setOnClickListener(tabListener);
        videoView.setOnClickListener(tabListener);
        audioView.setOnClickListener(tabListener);

        categoryContainer.addView(categoryView);
    }

    // 템플릿 목록
    private void getTemplateList(String category) {
        // 템플릿 리스트 데이터
        List<Map<String, String>> templates = new ArrayList<>();
        Map<String, String> tem_text = new TreeMap<>();
        tem_text.put("제목", "텍스트");
        tem_text.put("본문", "텍스트");
        Map<String, String> tem_img = new TreeMap<>();
        tem_img.put("사진", "이미지");
        Map<String, String> tem_audio = new TreeMap<>();
        tem_audio.put("음성", "음성");
        tem_audio.put("STT", "음성");
        tem_audio.put("TTS", "음성");
        Map<String, String> tem_video = new TreeMap<>();
        tem_video.put("영상", "영상");

        // 텍스트-이미지-음성-영상-추천챌린지 순서
        templates.add(tem_text);
        templates.add(tem_img);
        templates.add(tem_audio);
        templates.add(tem_video);

        int index = -1;
        if (category.equals("텍스트")) index = 0;
        else if (category.equals("이미지")) index = 1;
        else if (category.equals("음성")) index = 2;
        else if (category.equals("영상")) index = 3;

        LayoutInflater inflater = LayoutInflater.from(this);
        templateContainer.removeAllViews();

        if (index == -1) { // 전체
            for (Map<String, String> map : templates) {
                for (String tem : map.keySet()) {
                    View templateView = inflater.inflate(R.layout.sample_template_view, templateContainer, false);
                    TextView textView = templateView.findViewById(R.id.template_templateDetail_name);
                    textView.setText(tem);
                    templateContainer.addView(templateView);
                    chooseTemplate(textView);
                }
            }
        } else { // 특정
            Map<String, String> temMap = templates.get(index);
            for (String tem : temMap.keySet()) {
                View templateView = inflater.inflate(R.layout.sample_template_view, templateContainer, false);
                TextView textView = templateView.findViewById(R.id.template_templateDetail_name);
                textView.setText(tem);
                templateContainer.addView(templateView);
                chooseTemplate(textView);
            }
        }
    }

    // 템플릿 선택
    private void chooseTemplate(TextView chooseView) {
        chooseTemplates = new ArrayList<>(); // 고른 템플릿

        chooseView.setTag(false);
        chooseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("선택한 템플릿: "+chooseView.getText().toString());
                boolean clicked = (boolean) v.getTag();
                clicked = !clicked;

                v.setTag(clicked); // 새로운 선택 상태를 저장
                updateUIForSelection(v, clicked);

            }
        });
    }

    // UI 업데이트 메소드
    private void updateUIForSelection(View v, boolean isSelected) {
        int textColor = ContextCompat.getColor(getApplicationContext(), R.color.gray_600);
        int bgDrawable = isSelected ? R.drawable.category_color_gray : R.drawable.category_color_mid;

        if (isSelected) {
            if (chooseTemplates.size() >= 5) {
                Toast.makeText(TemplateActivity.this, "템플릿은 최대 5개까지만 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            chooseTemplates.add(((TextView) v).getText().toString());
        } else {
            chooseTemplates.remove(((TextView) v).getText().toString());
        }

        ((TextView) v).setTextColor(textColor);
        ((TextView) v).setBackground(ContextCompat.getDrawable(getApplicationContext(), bgDrawable));

    }

    // 템플릿 순서 매칭
    public HashMap<String, Integer> setOrder() {
        Map<String, String> matchChallengeDetail = new HashMap<>();
        matchChallengeDetail.put("challengeDetailTitle", "제목");
        matchChallengeDetail.put("challengeDetailContent", "본문");
        matchChallengeDetail.put("challengeDetailImage", "사진");
        matchChallengeDetail.put("challengeDetailAudio", "음성");
        matchChallengeDetail.put("challengeDetailSTT", "STT");
        matchChallengeDetail.put("challengeDetailTTS", "TTS");
        matchChallengeDetail.put("challengeDetailVideo", "영상");

        HashMap<String, Integer> ordermap = new HashMap<>();
        int k = 1;
        for (String name : chooseTemplates) {
            normalTemplates.add(name);
            for (Map.Entry<String, String> entry : matchChallengeDetail.entrySet()) {
                if (entry.getValue().equals(name)) {
                    ordermap.put(entry.getKey(), k++);
                }
            }
        }
        return ordermap;
    }

}
