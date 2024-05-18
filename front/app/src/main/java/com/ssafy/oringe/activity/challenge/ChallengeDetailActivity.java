package com.ssafy.oringe.activity.challenge;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.CalendarMonth;
import com.kizitonwose.calendar.core.DayPosition;
import com.kizitonwose.calendar.view.CalendarView;
import com.kizitonwose.calendar.view.MonthDayBinder;
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder;
import com.kizitonwose.calendar.view.ViewContainer;
import com.ssafy.oringe.R;
import com.ssafy.oringe.activity.record.RecordCreateActivity;
import com.ssafy.oringe.activity.record.RecordDetailDialogFragment;
import com.ssafy.oringe.api.RetrofitClient;
import com.ssafy.oringe.api.TrustOkHttpClientUtil;
import com.ssafy.oringe.api.challenge.ChallengeService;
import com.ssafy.oringe.api.record.Record;
import com.ssafy.oringe.api.record.RecordService;
import com.ssafy.oringe.ui.component.common.TitleView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChallengeDetailActivity extends AppCompatActivity {
    private String memberNickname;
    private Long memberId;
    private String API_URL;
    private Button btn_record;
    private long challengeId;
    private String challengeTitle;
    private String challengeMemo;
    private RecordService recordService;
    private List<Record> monthlyRecords = new ArrayList<>(); // Initialize to an empty list
    private CalendarView calendarView;
    private List<Integer> cycleDays = new ArrayList<>();
    private LocalDate challengeStartDate;
    private LocalDate challengeEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_detail);
        API_URL = getString(R.string.APIURL);

        // Retrieve challenge details from the intent
        Intent intent = getIntent();
        challengeId = intent.getLongExtra("challengeId", -1);
        challengeTitle = intent.getStringExtra("challengeTitle");
        challengeMemo = intent.getStringExtra("challengeMemo");
        challengeStartDate = LocalDate.parse(intent.getStringExtra("challengeStart"));
        challengeEndDate = LocalDate.parse(intent.getStringExtra("challengeEnd"));

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        memberId = sharedPref.getLong("loginId", 0);
        memberNickname = sharedPref.getString("loginNickName", "(알 수 없음)");
        setDefaultInfo();

        RelativeLayout animatedLayout = findViewById(R.id.animated_layout);
        LayoutTransition layoutTransition = new LayoutTransition();
        animatedLayout.setLayoutTransition(layoutTransition);

        calendarView = findViewById(R.id.calendarView);
        setupCalendarView();

        recordService = RetrofitClient.getApiRecordService();
        loadMonthlyRecordsSync(YearMonth.from(challengeStartDate));
        loadCycleDays();

        calendarView.setMonthScrollListener(calendarMonth -> {
            loadMonthlyRecordsSync(calendarMonth.getYearMonth());
            return null;
        });

        Button deleteBtn = findViewById(R.id.calendar_btn_delete);
        deleteBtn.setOnClickListener(v -> checkDelete());

        btn_record = findViewById(R.id.calendar_btn_record);
        int challengeStatus = intent.getIntExtra("challengeStatus", -1);
        System.out.println("challengeStatus: " + challengeStatus);
        if (challengeStatus != 2) { // 2 == "In Progress" status
            btn_record.setVisibility(View.GONE);
        }

        // 비활성화된 레코드버튼 예외처리.
        btn_record.setOnClickListener(v -> {
            if (!btn_record.isClickable()) {
                Toast.makeText(ChallengeDetailActivity.this, "오늘 인증을 완료했습니다", Toast.LENGTH_SHORT).show();
            } else {
                Intent recordIntent = new Intent(ChallengeDetailActivity.this, RecordCreateActivity.class);
                recordIntent.putExtra("challengeTitle", challengeTitle);
                startActivity(recordIntent);
            }
        });

    }

    private void setDefaultInfo() {
        TitleView whoView = findViewById(R.id.challengeList_who);
        whoView.setText(memberNickname + "님의 챌린지");

        TitleView titleView = findViewById(R.id.challengeDetail_titleView);
        TitleView memoView = findViewById(R.id.challengeDetail_memoView);

        titleView.setText(challengeTitle);
        memoView.setText(challengeMemo);
    }

    private boolean dayHasEvent(LocalDate date) {
        if (monthlyRecords == null) return false;
        return monthlyRecords.stream().anyMatch(record -> record.getRecordDate().equals(date) && record.getRecordSuccess());
    }

    private void setupCalendarView() {
        calendarView.setDayBinder(new MonthDayBinder<DayViewContainer>() {
            @NonNull
            @Override
            public DayViewContainer create(@NonNull View view) {
                return new DayViewContainer(view);
            }

            @Override
            public void bind(@NonNull DayViewContainer container, CalendarDay day) {
                LocalDate date = day.getDate();
                container.textView.setText(String.valueOf(date.getDayOfMonth()));
                container.recordId = null;  // Reset recordId

                // 오늘 날짜의 TextView 배경 설정
                if (date.equals(LocalDate.now())) { // 오늘은 배경있음
                    container.textView.setBackgroundResource(R.drawable.today_circle);
                } else {
                    container.textView.setBackgroundResource(0); // 다른 날짜는 배경 없음
                }

                if (dayHasEvent(date)) {
                    TypedArray oranges = getResources().obtainTypedArray(R.array.orange_images_orange);
                    int imageId = oranges.getResourceId(new Random().nextInt(oranges.length()), -1);
                    container.imageView.setImageResource(imageId);
                    animateImageView(container.imageView);
                    container.imageView.setVisibility(View.VISIBLE);
                    oranges.recycle();

                    // Set recordId if the day has an event
                    Record record = getRecordForDate(date);
                    if (record != null) {
                        container.recordId = (long) record.getRecordId();
                    }
                } else if (shouldHighlightDay(date)) {
                    TypedArray blues = getResources().obtainTypedArray(R.array.orange_images_blue);
                    int imageId = blues.getResourceId(new Random().nextInt(blues.length()), -1);
                    container.imageView.setImageResource(imageId);
                    animateImageView(container.imageView);
                    container.imageView.setVisibility(View.VISIBLE);
                    blues.recycle();
                } else {
                    container.imageView.setVisibility(View.GONE);
                }

                float alpha = (day.getPosition() == DayPosition.MonthDate) ? 1.0f : 0.3f;
                container.textView.setAlpha(alpha);
                container.imageView.setAlpha(alpha);

                if (date.getDayOfWeek() == DayOfWeek.SATURDAY) {
                    container.textView.setTextColor(Color.parseColor("#2196F3"));
                } else if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    container.textView.setTextColor(Color.parseColor("#D32F2F"));
                }
            }
        });

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

        // Set the calendar view range to the challenge start and end dates
        calendarView.setup(YearMonth.from(challengeStartDate), YearMonth.from(challengeEndDate), WeekFields.of(Locale.getDefault()).getFirstDayOfWeek());
        calendarView.scrollToMonth(YearMonth.from(challengeStartDate));
    }

    private void animateImageView(ImageView imageView) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(
            0f, 1f,  // Start and end values for the X axis scaling
            0f, 1f,  // Start and end values for the Y axis scaling
            Animation.RELATIVE_TO_SELF, 0.5f,  // Pivot point of X scaling
            Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling

        scaleAnimation.setDuration(300);
        scaleAnimation.setFillAfter(true);  // Needed to keep the result of the animation
        imageView.startAnimation(scaleAnimation);
    }

    private boolean shouldHighlightDay(LocalDate date) {
        if (date.isBefore(challengeStartDate) || date.isAfter(LocalDate.now()))
            return false; // Only highlight days between the challenge start date and today
        if (date.equals(LocalDate.now())) return false; // Exclude today
        if (!cycleDays.contains(date.getDayOfWeek().getValue())) return false;
        if (monthlyRecords == null) return false;
        return monthlyRecords.stream().noneMatch(record -> record.getRecordDate().equals(date));
    }


    private Record getRecordForDate(LocalDate date) {
        for (Record record : monthlyRecords) {
            if (record.getRecordDate().equals(date)) {
                return record;
            }
        }
        return null;
    }

    private void loadMonthlyRecordsSync(YearMonth month) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                Response<List<Record>> response = recordService.fetchMonthlyRecords(memberId, challengeId, month.getMonthValue()).execute();
                handler.post(() -> {
                    if (response.isSuccessful()) {
                        monthlyRecords = response.body();
                        calendarView.notifyCalendarChanged();
                        // Check if today has an event and disable the button if so
                        if (dayHasEvent(LocalDate.now())) {
                            btn_record.setClickable(false);
                            btn_record.setAlpha(0.5f);
                        }
                    } else {
                        Toast.makeText(ChallengeDetailActivity.this, "Failed to load records", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                handler.post(() -> Toast.makeText(ChallengeDetailActivity.this, "Error loading records: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void loadCycleDays() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                Response<List<Integer>> response = recordService.fetchCycleDays(challengeId).execute();
                handler.post(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        cycleDays = response.body();
                        calendarView.notifyCalendarChanged(); // Refresh the calendar view
                    } else {
                        Toast.makeText(ChallengeDetailActivity.this, "Failed to load cycle days", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                handler.post(() -> Toast.makeText(ChallengeDetailActivity.this, "Error loading cycle days: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    class DayViewContainer extends ViewContainer {
        TextView textView;
        ImageView imageView;
        Long recordId;  // Added recordId

        DayViewContainer(View view) {
            super(view);
            textView = view.findViewById(R.id.dayText);
            imageView = view.findViewById(R.id.dayIcon);

            view.setOnClickListener(v -> {
                if (recordId != null) {
                    RecordDetailDialogFragment.newInstance(recordId)
                        .show(getSupportFragmentManager(), "recordDetails");
                }
            });
        }
    }

    // 챌린지 삭제
    private void checkDelete() {
        new AlertDialog.Builder(ChallengeDetailActivity.this)
            .setTitle("정말 삭제하시겠습니까?")
            .setPositiveButton("삭제하기", (dialog, which) -> {
                deleteChallenge();
            })
            .setNegativeButton("취소", null)
            .show();
    }

    private void deleteChallenge() {
        OkHttpClient client = TrustOkHttpClientUtil.getUnsafeOkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build();

        Call<Void> call = retrofit.create(ChallengeService.class).deleteChallenge(challengeId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(ChallengeDetailActivity.this, "삭제되었습니다!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ChallengeDetailActivity.this, ChallengeListActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ChallengeDetailActivity.this, "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    class MonthViewContainer extends ViewContainer {
        TextView monthText;

        MonthViewContainer(View view) {
            super(view);
            monthText = view.findViewById(R.id.monthText);
        }
    }
}
