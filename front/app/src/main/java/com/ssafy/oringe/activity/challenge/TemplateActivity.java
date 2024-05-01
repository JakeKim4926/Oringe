package com.ssafy.oringe.activity.challenge;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ssafy.oringe.R;
import com.ssafy.oringe.api.member.Member;
import com.ssafy.oringe.api.member.MemberService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TemplateActivity extends AppCompatActivity {
    private String API_URL;
    private FirebaseAuth auth;
    private Long memberId;

    /*template*/
    private ViewGroup categoryContainer;
    private ViewGroup templateContainer;
    private List<String> chooseTemplates;
    private List<Map<String, String>> chooseResult;
    private boolean walkChallenge;
    private boolean wakeupChallenge;
    private boolean callChallenge;
    private boolean digitalChallenge;
    private String challenge;
    private HashMap<String, String> inputData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_template);
        API_URL = getString(R.string.APIURL);

        inputData = new HashMap<>();

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
            System.out.println("digitalChallenge,callChallenge,wakeupChallenge,walkChallenge: " + digitalChallenge + "," + callChallenge + "," + wakeupChallenge + "," + walkChallenge);
            if (digitalChallenge || callChallenge || wakeupChallenge || walkChallenge) {
                String challengeTitle = getChallengeTitle();
                showChallengeDialog(challengeTitle);
            } else {
                returnOrderMapOnly();
            }

        });

    }

    // 로그인 정보
    private void getMemberId(String memberEmail) {
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(API_URL)
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
        TextView gptView = categoryView.findViewById(R.id.template_category_gpt);
        TextView recommendView = categoryView.findViewById(R.id.template_category_recommend);
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
                gptView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                gptView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.category_border_pink));
                recommendView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                recommendView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.category_border_pink));

                // 클릭된 뷰의 색상 변경
                ((TextView) v).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray_600));
                ((TextView) v).setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.category_color_pink));

                getTemplateList(((TextView) v).getText().toString());
            }
        };

        totalView.setOnClickListener(tabListener);
        txtView.setOnClickListener(tabListener);
        imgView.setOnClickListener(tabListener);
        videoView.setOnClickListener(tabListener);
        gptView.setOnClickListener(tabListener);
        recommendView.setOnClickListener(tabListener);

        categoryContainer.addView(categoryView);
    }

    // 템플릿 목록
    private void getTemplateList(String category) {
        // 템플릿 리스트 데이터
        List<Map<String, String>> templates = new ArrayList<>();
        Map<String, String> tem_text = new HashMap<>();
        tem_text.put("제목", "텍스트");
        tem_text.put("본문", "텍스트");
        Map<String, String> tem_img = new HashMap<>();
        tem_img.put("사진", "이미지");
        tem_img.put("움짤", "이미지");
        Map<String, String> tem_video = new HashMap<>();
        tem_video.put("영상", "영상");
        Map<String, String> tem_recommend = new HashMap<>();
        tem_recommend.put("디지털 디톡스", "추천 챌린지");
        tem_recommend.put("기상", "추천 챌린지");
        tem_recommend.put("소중한 사람과의 통화", "추천 챌린지");
        tem_recommend.put("걷기", "추천 챌린지");

        // 텍스트-이미지-영상-추천챌린지 순서
        templates.add(tem_text);
        templates.add(tem_img);
        templates.add(tem_video);
        templates.add(tem_recommend);

        int index = -1;
        if (category.equals("텍스트")) index = 0;
        else if (category.equals("이미지")) index = 1;
        else if (category.equals("영상")) index = 2;
        else if (category.equals("추천 챌린지")) index = 3;

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
                boolean clicked = (boolean) v.getTag();
                clicked = !clicked;
                v.setTag(clicked); // 새로운 선택 상태를 저장

                if (clicked) {
                    ((TextView) v).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray_600));
                    ((TextView) v).setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.category_color_gray));
                    chooseTemplates.add(((TextView) v).getText().toString());
                    if (((TextView) v).getText().toString().equals("소중한 사람과의 통화")) callChallenge = true;
                    if (((TextView) v).getText().toString().equals("기상")) wakeupChallenge = true;
                    if (((TextView) v).getText().toString().equals("걷기")) walkChallenge = true;
                    if (((TextView) v).getText().toString().equals("디지털 디톡스")) digitalChallenge = true;
                } else {
                    ((TextView) v).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray_600));
                    ((TextView) v).setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.category_color_mid));
                    chooseTemplates.remove(((TextView) v).getText().toString());
                    if (((TextView) v).getText().toString().equals("소중한 사람과의 통화")) callChallenge = false;
                    if (((TextView) v).getText().toString().equals("기상")) wakeupChallenge = false;
                    if (((TextView) v).getText().toString().equals("걷기")) walkChallenge = false;
                    if (((TextView) v).getText().toString().equals("디지털 디톡스")) digitalChallenge = false;
                }
            }
        });
    }

    // 템플릿 순서 매칭
    public HashMap<String, Integer> setOrder() {
        Map<String, String> matchChallengeDetail = new HashMap<>();
        matchChallengeDetail.put("challengeDetailTitle", "제목");
        matchChallengeDetail.put("challengeDetailContent", "본문");
        matchChallengeDetail.put("challengeDetailImage", "사진");
        matchChallengeDetail.put("challengeDetailImageContent", "움짤");
        matchChallengeDetail.put("challengeDetailVideo", "영상");
        matchChallengeDetail.put("Digital", "디지털 디톡스");
        matchChallengeDetail.put("Call", "소중한 사람과의 통화");
        matchChallengeDetail.put("WakeUp", "기상");
        matchChallengeDetail.put("Walk", "걷기");

        HashMap<String, Integer> ordermap = new HashMap<>();
        int k = 1;
        System.out.println("chooseTemplates: " + chooseTemplates);
        for (String name : chooseTemplates) {
            for (Map.Entry<String, String> entry : matchChallengeDetail.entrySet()) {
                if (entry.getValue().equals(name)) {
                    ordermap.put(entry.getKey(), k++);
                }
            }
        }
        return ordermap;
    }

    private String getChallengeTitle() {
        if (digitalChallenge) return "디지털 디톡스";
        if (callChallenge) return "소중한 사람과 통화하기";
        if (wakeupChallenge) return "기상";
        if (walkChallenge) return "걷기";
        return null;
    }

    private void showChallengeDialog(String title) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.sample_modal_view, null);
        LinearLayout container = dialogView.findViewById(R.id.template_modal);

        AlertDialog.Builder dlg = new AlertDialog.Builder(TemplateActivity.this);
        dlg.setTitle(title);
        dlg.setView(dialogView);

        if (digitalChallenge) {
            addEditText(container, "제한할 앱 이름을 입력하세요.");
            addEditText(container, "제한 시간을 입력하세요.");
        } else if (callChallenge) {
            addEditText(container, "통화 대상 이름을 입력하세요.");
            addEditText(container, "통화 대상 전화번호를 입력하세요.");
        } else if (wakeupChallenge) {
            addEditText(container, "기상 시간을 입력하세요.");
        } else if (walkChallenge) {
            addEditText(container, "목표 걸음 수를 입력하세요.");
        }

        dlg.setPositiveButton("완료", (dialog, which) -> {
            for (int i = 0; i < container.getChildCount(); i++) {
                View view = container.getChildAt(i);
                if (view instanceof EditText) {
                    EditText editText = (EditText) view;
                    inputData.put(editText.getHint().toString(), editText.getText().toString());
                }
            }
            Intent intent = new Intent();
            intent.putExtra("challenge", title);
            intent.putExtra("inputData", inputData);
            intent.putExtra("orderMap", setOrder());
            System.out.println("title: " + challenge);
            System.out.println("inputData: " + inputData);
            System.out.println("orderMap: " + setOrder());
            setResult(Activity.RESULT_OK, intent);
            finish();
        });
        dlg.setNegativeButton("취소", null);
        dlg.show();
    }

    private void addEditText(LinearLayout container, String hint) {
        EditText editText = new EditText(TemplateActivity.this);
        editText.setHint(hint);
        container.addView(editText);
    }

    private void returnOrderMapOnly() {
        Intent intent = new Intent();
        intent.putExtra("orderMap", setOrder());
        System.out.println("only orderMap: " + setOrder());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }


}
