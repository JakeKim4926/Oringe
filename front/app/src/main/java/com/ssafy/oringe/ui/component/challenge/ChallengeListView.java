package com.ssafy.oringe.ui.component.challenge;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.ssafy.oringe.R;
import com.ssafy.oringe.api.challenge.Challenge;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ChallengeListView extends LinearLayout {
    private List<Challenge> challengeList;

    public ChallengeListView(Context context) {
        super(context);
        init(context);
    }

    public ChallengeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChallengeListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_list_view, this, false);
        addView(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setChallengeList(List<Challenge> challenges) {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        for(Challenge challenge : challenges) {
            System.out.println("challenge "+challenge);
            View challengesView = inflater.inflate(R.layout.sample_list_view,this, false);

            TextView titleView = challengesView.findViewById(R.id.challengeList_title);
            ImageView alarmView = challengesView.findViewById(R.id.challengeList_alarm);
            TextView dDayView = challengesView.findViewById(R.id.challengeList_dDay);
            TextView cycleView = challengesView.findViewById(R.id.challengeList_cycle);
            TextView dateRangeView = challengesView.findViewById(R.id.challengeList_dateRange);

            LocalDate startDate = LocalDate.parse(challenge.getChallengeStart());
            LocalDate today = LocalDate.now();

            // 시작 날짜와 오늘 날짜 사이의 차이를 계산합니다.
            long daysBetween = ChronoUnit.DAYS.between(startDate, today);

            titleView.setText(challenge.getChallengeTitle());
            alarmView.setVisibility(challenge.getChallengeAlarm() ? View.VISIBLE : View.GONE); // 알람이 true일 때만 보이도록
            dDayView.setText("D-"+ daysBetween);
            cycleView.setText("매일");
            dateRangeView.setText(challenge.getChallengeStart() + " ~ " + challenge.getChallengeEnd());

            this.addView(challengesView);
        }
    }


}