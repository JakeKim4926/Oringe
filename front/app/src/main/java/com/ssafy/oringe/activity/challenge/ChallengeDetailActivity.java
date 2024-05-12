package com.ssafy.oringe.activity.challenge;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.CalendarMonth;
import com.kizitonwose.calendar.view.CalendarView;
import com.kizitonwose.calendar.view.MonthDayBinder;
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder;
import com.kizitonwose.calendar.view.ViewContainer;
import com.ssafy.oringe.R;

import com.ssafy.oringe.activity.common.MainActivity;
import com.ssafy.oringe.activity.record.RecordCreateActivity;
import com.ssafy.oringe.ui.component.common.TitleView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Random;

public class ChallengeDetailActivity extends AppCompatActivity {
    private String memberNickname;
    private Long memberId;
    private String API_URL;
    private Button btn_record;
    private long challengeId;
    private String challengeTitle;
    private String challengeMemo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_detail);
        API_URL = getString(R.string.APIURL);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        memberId = sharedPref.getLong("loginId", 0);
        memberNickname = sharedPref.getString("loginNickName", "(알 수 없음)");
        setDefaultInfo();

        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setDayBinder(new MonthDayBinder<DayViewContainer>() {
            @NonNull
            @Override
            public DayViewContainer create(@NonNull View view) {
                return new DayViewContainer(view);
            }
            @Override
            public void bind(@NonNull DayViewContainer container, CalendarDay day) {
                container.textView.setText(String.valueOf(day.getDate().getDayOfMonth()));


                if (dayHasEvent(day.getDate())) {
                    TypedArray oranges = getResources().obtainTypedArray(R.array.orange_images_orange);
                    int imageId = oranges.getResourceId(new Random().nextInt(oranges.length()), -1);
                    container.imageView.setImageResource(imageId);
                    container.imageView.setVisibility(View.VISIBLE);
                    oranges.recycle();
                } else {
                    container.imageView.setVisibility(View.GONE);
                }

                // 요일 별 day color 렌더링
                if (day.getDate().getDayOfWeek() == DayOfWeek.SATURDAY) {
                    container.textView.setTextColor(Color.parseColor("#2196F3"));
                } else if (day.getDate().getDayOfWeek() == DayOfWeek.SUNDAY) {
                    container.textView.setTextColor(Color.parseColor("#D32F2F"));
                }
            }

        });
        YearMonth currentMonth = YearMonth.now();
        calendarView.setup(currentMonth.minusMonths(1), currentMonth.plusMonths(1), WeekFields.of(Locale.getDefault()).getFirstDayOfWeek());
        calendarView.scrollToMonth(currentMonth);
        calendarView.setMonthHeaderBinder(new MonthHeaderFooterBinder<MonthViewContainer>() {

            @Override
            public MonthViewContainer create(View view) {
                return new MonthViewContainer(view);
            }

            @Override
            public void bind(MonthViewContainer container, CalendarMonth month) {
                container.monthText.setText(String.format(Locale.ENGLISH, "<   %s   >", month.getYearMonth().getMonth()));
            }
        });
        // 오린지 인증하기 버튼 동작
        Button btn_record = findViewById(R.id.btn_record);
        // challengeStatus를 받아와서 인증하기버튼 조건부 렌더링.
        int challengeStatus = getIntent().getIntExtra("challengeStatus", -1);
        if (challengeStatus == 2) { // 2 == "진행중" 상태를 나타냄
            btn_record.setVisibility(View.VISIBLE);
        } else {
            btn_record.setVisibility(View.GONE);
        }
        btn_record.setOnClickListener(v -> {
            Intent intent = new Intent(ChallengeDetailActivity.this, RecordCreateActivity.class);
            intent.putExtra("challengeTitle", challengeTitle);
            startActivity(intent);
        });

    }
    // 로그인 정보
    private void setDefaultInfo() {
        TitleView whoView = findViewById(R.id.challengeList_who);
        whoView.setText(memberNickname + "님의 챌린지");
        Intent intent = getIntent();
        challengeId = intent.getLongExtra("challengeId", -1);
        challengeTitle = intent.getStringExtra("challengeTitle");
        challengeMemo = intent.getStringExtra("challengeMemo");

        TitleView titleView = findViewById(R.id.challengeDetail_titleView);
        TitleView memoView = findViewById(R.id.challengeDetail_memoView);

        titleView.setText(challengeTitle);
        memoView.setText(challengeMemo);
    }
    private boolean dayHasEvent(LocalDate date) {
        // 레코드리스트로부터 recordsuccess 값이 없는지 트루인지 펄스인지를 이용해서
        return Math.random() < 0.3;
    }

    class DayViewContainer extends ViewContainer {
        TextView textView;
        ImageView imageView;

        DayViewContainer(View view) {
            super(view);
            textView = view.findViewById(R.id.dayText);
            imageView = view.findViewById(R.id.dayIcon);
        }
    }
    class MonthViewContainer extends ViewContainer {
        TextView monthText;

        MonthViewContainer(View view) {
            super(view);
            monthText = view.findViewById(R.id.monthText);
        }
    }


}
