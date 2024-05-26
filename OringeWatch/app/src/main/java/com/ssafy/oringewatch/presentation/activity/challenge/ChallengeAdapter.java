package com.ssafy.oringewatch.presentation.activity.challenge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ssafy.oringewatch.R;
import com.ssafy.oringewatch.presentation.api.challenge.Challenge;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ViewHolder> {
    private List<Challenge> challenges;

    public ChallengeAdapter(List<Challenge> challenges) {
        this.challenges = challenges;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_challenge_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Challenge challenge = challenges.get(position);
        holder.title.setText(challenge.getChallengeTitle());

        // 날짜 계산을 위한 formatter 설정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 종료 날짜 파싱
        LocalDate endDate = LocalDate.parse(challenge.getChallengeEnd(), formatter);
        // 현재 날짜
        LocalDate today = LocalDate.now();
        // D-day 계산
        long daysBetween = ChronoUnit.DAYS.between(today, endDate);
        holder.dDay.setText("D-" + daysBetween);

        // challengeCycle에서 요일 계산
        String[] dayNames = {"월", "화", "수", "목", "금", "토", "일"};
        StringBuilder daysText = new StringBuilder();
        for (Integer day : challenge.getChallengeCycle()) {
            if (day >= 1 && day <= 7) {
                if (daysText.length() > 0) daysText.append(", ");
                daysText.append(dayNames[day - 1]);
            }
        }
        holder.days.setText(daysText.toString());
        holder.date.setText(challenge.getChallengeStart() + "~" + challenge.getChallengeEnd());
    }

    @Override
    public int getItemCount() {
        return challenges.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, dDay, days, date;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.challengeTitle);
            dDay = itemView.findViewById(R.id.challengeDDay);
            days = itemView.findViewById(R.id.challengeDay);
            date = itemView.findViewById(R.id.challengeDate);
        }
    }
}
