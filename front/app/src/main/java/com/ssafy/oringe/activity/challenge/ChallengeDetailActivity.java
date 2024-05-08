package com.ssafy.oringe.activity.challenge;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
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

import com.ssafy.oringe.ui.component.common.TitleView;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class ChallengeDetailActivity extends AppCompatActivity {
    private String memberNickname;
    private Long memberId;
    private String API_URL;

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
                    container.imageView.setVisibility(View.VISIBLE);
                    container.imageView.setImageResource(R.drawable.oranges_simple_blue1);
                } else {
                    container.imageView.setVisibility(View.GONE);
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
    }
    // 로그인 정보
    private void setDefaultInfo() {
        TitleView whoView = findViewById(R.id.challengeList_who);
        whoView.setText(memberNickname + "님의 챌린지");
        Intent intent = getIntent();
        long challengeId = intent.getLongExtra("challengeId", -1);
        String challengeTitle = intent.getStringExtra("challengeTitle");
        String challengeMemo = intent.getStringExtra("challengeMemo");

        TitleView titleView = findViewById(R.id.challengeDetail_titleView);
        TitleView memoView = findViewById(R.id.challengeDetail_memoView);

        titleView.setText(challengeTitle);
        memoView.setText(challengeMemo);
    }
    private boolean dayHasEvent(LocalDate date) {
        return Math.random() < 0.3;  // Example: Random event presence
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
