package com.ssafy.oringe.activity.challenge;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ssafy.oringe.R;
import com.ssafy.oringe.api.challenge.Challenge;
import com.ssafy.oringe.api.challenge.ChallengeService;
import com.ssafy.oringe.api.member.Member;
import com.ssafy.oringe.api.member.MemberService;
import com.ssafy.oringe.ui.component.challenge.InputView;
import com.ssafy.oringe.ui.component.common.CalendarView;
import com.ssafy.oringe.ui.component.common.TitleView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChallengeCreateActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 222;
    /* member */
    private String API_URL;
    private FirebaseAuth auth;
    private Long memberId;

    /* challenge */
    private boolean[] clicked;
    private boolean isAlarm;
    private String formattedTime;
    private ArrayList<Integer> cycle;
    private ArrayList<Integer> templates;
    private String title;
    private String start;
    private String end;
    private String memo;
    private String whatChallenge;
    private HashMap<String, String> inputData;
    private HashMap<String, Integer> orderMap;
    private ArrayList<String> normalTemplates;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_challenge_create_form);
        API_URL = getString(R.string.APIURL);

        // 로그인 정보
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String email = user.getEmail();
        getMemberId(email);

        // 알람 토글
        setAlarm();

        // 요일 설정
        chooseCycle();

        // 템플릿 설정
        inputData = new HashMap<>();
        orderMap = new HashMap<>();
        normalTemplates = new ArrayList<>();
        setTemplates();

        // 챌린지 생성
        Button create = findViewById(R.id.challnegeCreate_create);
        cycle = new ArrayList<>();
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 1; i < 8; i++) {
                    if (clicked[i]) cycle.add(i);
                }
                createChallenge();
            }
        });

        // 취소
        Button cancel = findViewById(R.id.challnegeCreate_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "취소되었습니다!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ChallengeCreateActivity.this, ChallengeListActivity.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.challnegeCreate), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    // 아이템의 상태를 저장
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("Title", title);
        outState.putString("Start", start);
        outState.putString("End", end);
        outState.putString("Memo", memo);
        outState.putIntegerArrayList("Cycle", cycle);
        outState.putBooleanArray("Clicked", clicked);
        outState.putBoolean("IsAlarm", isAlarm);
        outState.putString("FormattedTime", formattedTime);
    }

    // 아이템의 상태를 복원
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        title = savedInstanceState.getString("Title");
        start = savedInstanceState.getString("Start");
        end = savedInstanceState.getString("End");
        memo = savedInstanceState.getString("Memo");
        cycle = savedInstanceState.getIntegerArrayList("Cycle");
        clicked = savedInstanceState.getBooleanArray("Clicked");
        isAlarm = savedInstanceState.getBoolean("IsAlarm");
        formattedTime = savedInstanceState.getString("FormattedTime");
    }

    // 데이터 받아오기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("데이터 받아오기");
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                whatChallenge = data.getStringExtra("challenge");
                inputData = (HashMap<String, String>) data.getSerializableExtra("inputData");
                orderMap = (HashMap<String, Integer>) data.getSerializableExtra("orderMap");
                normalTemplates = data.getStringArrayListExtra("normalTemplates");
            }
        }
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

    // 알람 설정
    private void setAlarm() {
        Switch toggle = findViewById(R.id.challengeCreate_alarm);
        TextView textView = findViewById(R.id.challnegeCreate_input_alarmTime);
        Calendar myCalendar = Calendar.getInstance();

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    textView.setVisibility(View.VISIBLE);
                    View.OnClickListener timeClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TimePickerDialog timePickerDialog = new TimePickerDialog(ChallengeCreateActivity.this,
                                android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                                    textView.setText(formattedTime);
                                }
                            }, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true);

                            timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            timePickerDialog.show();
                        }
                    };
                    isAlarm = isChecked;
                    textView.setOnClickListener(timeClickListener);
                } else {
                    textView.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "알람이 꺼졌습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 요일 설정
    public void chooseCycle() {
        View view = findViewById(R.id.challnegeCreate_dayLayout);
        TextView monView = view.findViewById(R.id.monday);
        TextView tueView = view.findViewById(R.id.tuesday);
        TextView wedView = view.findViewById(R.id.wednesday);
        TextView thuView = view.findViewById(R.id.thursday);
        TextView friView = view.findViewById(R.id.friday);
        TextView satView = view.findViewById(R.id.saturday);
        TextView sunView = view.findViewById(R.id.sunday);

        clicked = new boolean[8];

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView dayView = (TextView) v;
                String day = dayView.getText().toString();
                int i = 0;
                if (day.equals("월")) i = 1;
                else if (day.equals("화")) i = 2;
                else if (day.equals("수")) i = 3;
                else if (day.equals("목")) i = 4;
                else if (day.equals("금")) i = 5;
                else if (day.equals("토")) i = 6;
                else if (day.equals("일")) i = 7;

                clicked[i] = !clicked[i];
                if (clicked[i])
                    dayView.setBackgroundColor(ContextCompat.getColor(ChallengeCreateActivity.this, R.color.oringe_sub));
                else
                    dayView.setBackgroundColor(ContextCompat.getColor(ChallengeCreateActivity.this, R.color.transparent));
            }
        };
        monView.setOnClickListener(clickListener);
        tueView.setOnClickListener(clickListener);
        wedView.setOnClickListener(clickListener);
        thuView.setOnClickListener(clickListener);
        friView.setOnClickListener(clickListener);
        satView.setOnClickListener(clickListener);
        sunView.setOnClickListener(clickListener);
    }

    // 템플릿 생성 및 수정
    private void setTemplates() {
        System.out.println("템플릿 생성 및 수정");

        LinearLayout setLayout = findViewById(R.id.challengeCreate_setTemplateLayout);

        LayoutInflater inflater = getLayoutInflater();
        View updateView = inflater.inflate(R.layout.sample_modify_view, null);
        TextView modifyView = updateView.findViewById(R.id.modify_template);
        TextView plusView = updateView.findViewById(R.id.plus_template);
        TextView registerView = updateView.findViewById(R.id.register_template);

        LinearLayout plusLayout = findViewById(R.id.challengeCreate_templateLayout);

        View.OnClickListener moveTemplate = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChallengeCreateActivity.this, TemplateActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        };
        plusView.setOnClickListener(moveTemplate);
        modifyView.setOnClickListener(moveTemplate);

        if (orderMap.isEmpty()) {
            plusView.setText("템플릿 설정하러 가기");
            setLayout.addView(plusView);
        } else {
            for(String str : normalTemplates) {
                registerView.setText(str);
                plusLayout.addView(registerView);
            }
        }
    }

    // 챌린지 생성
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createChallenge() {
        System.out.println("챌린지 생성");
        System.out.println("challenge: "+whatChallenge);
        System.out.println("inputData: "+inputData);
        System.out.println("orderMap: "+orderMap);
        View view = findViewById(R.id.challnegeCreate);

        InputView titleEdit = view.findViewById(R.id.challnegeCreate_input_title);
        CalendarView startEdit = view.findViewById(R.id.challnegeCreate_input_start);
        CalendarView endEdit = view.findViewById(R.id.challnegeCreate_input_end);
        InputView memoEdit = view.findViewById(R.id.challnegeCreate_input_memo);

        title = titleEdit.getEditText();
        start = startEdit.getEditText();
        end = endEdit.getEditText();
        memo = memoEdit.getEditText();
        templates = new ArrayList<>();

        String[] arr = {"challengeDetailTitle", "challengeDetailContent",
            "challengeDetailImage", "challengeDetailImageContent",
            "challengeDetailVideo", "Digital", "Call", "WakeUp", "Walk"};

        boolean isThere = false;
        for (String str : arr) {
            isThere = false;
            int k = -1;
            loop:
            for (String key : orderMap.keySet()) {
                if (key.equals(str)) {
                    isThere = true;
                    k = orderMap.get(key);
                    break loop;
                }
            }
            if (isThere) templates.add(k);
            else templates.add(0);
        }

        System.out.println("challengeDetail: "+templates);
        Challenge.ChallengeBuilder builder = Challenge.builder()
            .challengeTitle(title)
            .challengeStart(start)
            .challengeEnd(end)
            .challengeCycle(cycle)
            .challengeAlarm(isAlarm)
            .challengeAlarmTime(isAlarm ? formattedTime : null)
            .challengeMemo(memo)
            .order(templates);

        if (whatChallenge != null) {
            switch (whatChallenge) {
                case "디지털 디톡스":
                    builder.challengeAppName(inputData.get("제한할 앱 이름을 입력하세요."));
                    builder.challengeAppTime(inputData.get("제한 시간을 입력하세요."));
                    break;
                case "소중한 사람과 통화하기":
                    builder.challengeCallName(inputData.get("통화 대상 이름을 입력하세요."));
                    builder.challengeCallNumber(inputData.get("통화 대상 전화번호를 입력하세요."));
                    break;
                case "기상":
                    builder.challengeWakeupTime(inputData.get("기상 시간을 입력하세요."));
                    break;
                case "걷기":
                    builder.challengeWalk(inputData.get("목표 걸음 수를 입력하세요."));
                    break;
            }
        }

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        Challenge challenge = builder.build();
        System.out.println("challenge: " + challenge);

        Call<ResponseBody> call = retrofit.create(ChallengeService.class).sendData(challenge, memberId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                System.out.println(response.code());
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(ChallengeCreateActivity.this, "챌린지 생성!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ChallengeCreateActivity.this, ChallengeListActivity.class));
                        finish();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(ChallengeCreateActivity.this, "챌린지 생성 실패", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }
}
