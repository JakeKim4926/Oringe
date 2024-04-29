package com.ssafy.oringe.activity.challenge;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ssafy.oringe.R;
import com.ssafy.oringe.api.member.Member;
import com.ssafy.oringe.api.member.MemberService;
import com.ssafy.oringe.ui.component.common.TitleView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TemplateActivity extends AppCompatActivity {
    private String API_URL;
    private FirebaseAuth auth;
    private Long memberId;
    private String memberNickname;

    /*template*/
    private ViewGroup templateContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_template);
        API_URL = getString(R.string.APIURL);

        // 로그인 정보
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String email = user.getEmail();
        getMemberId(email);

        templateContainer = findViewById(R.id.category);
        setCategoryList();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.template_create), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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

    // 카테고리
    private void setCategoryList() {
        List<String> categoryList = new ArrayList<>();
        categoryList.add("전체");
        categoryList.add("텍스트");
        categoryList.add("이미지");
        categoryList.add("영상");
        categoryList.add("추천 챌린지");

        LayoutInflater inflater = LayoutInflater.from(this);
        templateContainer.removeAllViews();

        for(String cate : categoryList) {
            View templateView = inflater.inflate(R.layout.sample_category_view, templateContainer, false);
            TextView textView = templateView.findViewById(R.id.category_name);
            textView.setText(cate);

            templateContainer.addView(templateView);
        }

    }

}