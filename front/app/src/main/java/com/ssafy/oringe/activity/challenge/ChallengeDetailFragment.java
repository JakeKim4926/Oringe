package com.ssafy.oringe.activity.challenge;

import android.animation.LayoutTransition;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChallengeDetailFragment extends BottomSheetDialogFragment {
    private String memberNickname;
    private Long memberId;
    private String API_URL;
    private Button btn_record;
    private long challengeId;
    private String challengeTitle;
    private String challengeMemo;
    private RecordService recordService;
    private List<Record> monthlyRecords = new ArrayList<>();
    private CalendarView calendarView;
    private List<Integer> cycleDays = new ArrayList<>();
    private LocalDate challengeStartDate;
    private LocalDate challengeEndDate;

    public static ChallengeDetailFragment newInstance(long challengeId, String challengeTitle, String challengeMemo, String challengeStart, String challengeEnd, int challengeStatus) {
        ChallengeDetailFragment fragment = new ChallengeDetailFragment();
        Bundle args = new Bundle();
        args.putLong("challengeId", challengeId);
        args.putString("challengeTitle", challengeTitle);
        args.putString("challengeMemo", challengeMemo);
        args.putString("challengeStart", challengeStart);
        args.putString("challengeEnd", challengeEnd);
        args.putInt("challengeStatus", challengeStatus);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_challenge_detail, container, false);
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog d = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);
            }
        });
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        return dialog;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        API_URL = getString(R.string.APIURL);

        // Retrieve challenge details from the arguments
        Bundle args = getArguments();
        if (args != null) {
            challengeId = args.getLong("challengeId");
            challengeTitle = args.getString("challengeTitle");
            challengeMemo = args.getString("challengeMemo");
            challengeStartDate = LocalDate.parse(args.getString("challengeStart"));
            challengeEndDate = LocalDate.parse(args.getString("challengeEnd"));
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        memberId = sharedPref.getLong("loginId", 0);
        memberNickname = sharedPref.getString("loginNickName", "(알 수 없음)");
        setDefaultInfo(view);

        RelativeLayout animatedLayout = view.findViewById(R.id.animated_layout);
        if (animatedLayout != null && animatedLayout.getLayoutTransition() != null) {
            animatedLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        } else {
            Log.e("ChallengeDetailFragment", "animatedLayout or its LayoutTransition is null");
        }


        calendarView = view.findViewById(R.id.calendarView);
        setupCalendarView(view);

        recordService = RetrofitClient.getApiRecordService();
        loadMonthlyRecordsSync(YearMonth.from(challengeStartDate));
        loadCycleDays();

        calendarView.setMonthScrollListener(calendarMonth -> {
            loadMonthlyRecordsSync(calendarMonth.getYearMonth());
            return null;
        });

        Button deleteBtn = view.findViewById(R.id.calendar_btn_delete);
        deleteBtn.setOnClickListener(v -> checkDelete());

        btn_record = view.findViewById(R.id.calendar_btn_record);
        int challengeStatus = args.getInt("challengeStatus", -1);
        if (challengeStatus != 2) {
            btn_record.setVisibility(View.GONE);
        }

        btn_record.setOnClickListener(v -> {
            if (!btn_record.isClickable()) {
                Toast.makeText(getContext(), "오늘 인증을 완료했습니다", Toast.LENGTH_SHORT).show();
            } else {
                Intent recordIntent = new Intent(getContext(), RecordCreateActivity.class);
                recordIntent.putExtra("challengeTitle", challengeTitle);
                startActivity(recordIntent);
            }
        });

    }
    private void setDefaultInfo(View view) {

        TitleView titleView = view.findViewById(R.id.challengeDetail_titleView);
        TitleView memoView = view.findViewById(R.id.challengeDetail_memoView);

        titleView.setText(challengeTitle);
        memoView.setText(challengeMemo);
    }

    private boolean dayHasEvent(LocalDate date) {
        if (monthlyRecords == null) return false;
        return monthlyRecords.stream().anyMatch(record -> record.getRecordDate().equals(date) && record.getRecordSuccess());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupCalendarView(View view) {
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
                container.recordId = null;

                if (date.equals(LocalDate.now())) {
                    container.textView.setBackgroundResource(R.drawable.today_circle);
                } else {
                    container.textView.setBackgroundResource(0);
                }

                if (dayHasEvent(date)) {
                    TypedArray oranges = getResources().obtainTypedArray(R.array.orange_images_orange);
                    int imageId = oranges.getResourceId(new Random().nextInt(oranges.length()), -1);
                    container.imageView.setImageResource(imageId);
                    animateImageView(container.imageView);
                    container.imageView.setVisibility(View.VISIBLE);
                    oranges.recycle();

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
            @NonNull
            @Override
            public MonthViewContainer create(@NonNull View view) {
                return new MonthViewContainer(view);
            }

            @Override
            public void bind(@NonNull MonthViewContainer container, CalendarMonth month) {
                container.monthText.setText(String.format(Locale.ENGLISH, "<   %s   >", month.getYearMonth().getMonth()));
            }
        });

        calendarView.setup(YearMonth.from(challengeStartDate), YearMonth.from(challengeEndDate), WeekFields.of(Locale.getDefault()).getFirstDayOfWeek());
        calendarView.scrollToMonth(YearMonth.from(challengeStartDate));
    }

    private void animateImageView(ImageView imageView) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                0f, 1f,
                0f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        scaleAnimation.setDuration(300);
        scaleAnimation.setFillAfter(true);
        imageView.startAnimation(scaleAnimation);
    }

    private boolean shouldHighlightDay(LocalDate date) {
        if (date.isBefore(challengeStartDate) || date.isAfter(LocalDate.now())) return false;
        if (date.equals(LocalDate.now())) return false;
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
                        if (dayHasEvent(LocalDate.now())) {
                            btn_record.setClickable(false);
                            btn_record.setAlpha(0.5f);
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to load records", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                handler.post(() -> Toast.makeText(getContext(), "Error loading records: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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
                        calendarView.notifyCalendarChanged();
                    } else {
                        Toast.makeText(getContext(), "Failed to load cycle days", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                handler.post(() -> Toast.makeText(getContext(), "Error loading cycle days: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    class DayViewContainer extends ViewContainer {
        TextView textView;
        ImageView imageView;
        Long recordId;

        DayViewContainer(View view) {
            super(view);
            textView = view.findViewById(R.id.dayText);
            imageView = view.findViewById(R.id.dayIcon);

            view.setOnClickListener(v -> {
                if (recordId != null) {
                    RecordDetailDialogFragment.newInstance(recordId).show(getChildFragmentManager(), "recordDetails");
                }
            });
        }
    }

    private void checkDelete() {
        new AlertDialog.Builder(getContext())
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
                Toast.makeText(getContext(), "삭제되었습니다!", Toast.LENGTH_SHORT).show();
                dismiss();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
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
