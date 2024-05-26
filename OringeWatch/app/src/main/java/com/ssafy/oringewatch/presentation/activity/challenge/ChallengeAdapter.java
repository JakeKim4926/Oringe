package com.ssafy.oringewatch.presentation.activity.challenge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ssafy.oringewatch.R;
import com.ssafy.oringewatch.presentation.api.challenge.Challenge;

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
        holder.dDay.setText("D-5");
        holder.days.setText("매일");
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
