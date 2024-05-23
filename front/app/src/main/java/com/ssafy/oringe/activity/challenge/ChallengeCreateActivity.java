package com.ssafy.oringe.activity.challenge;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ssafy.oringe.R;
import com.ssafy.oringe.api.TrustOkHttpClientUtil;
import com.ssafy.oringe.api.challenge.Challenge;
import com.ssafy.oringe.api.challenge.ChallengeService;
import com.ssafy.oringe.api.member.Member;
import com.ssafy.oringe.api.member.MemberService;
import com.ssafy.oringe.ui.component.common.BackView;
import com.ssafy.oringe.ui.component.common.CalendarView;
import com.ssafy.oringe.ui.component.common.OnBackButtonClickListener;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChallengeCreateActivity extends AppCompatActivity implements OnBackButtonClickListener {
    private static final int REQUEST_CODE = 222;
    /* member */
    private String API_URL;
    private Long memberId;
    private Long challengeId;

    private ViewGroup templateListContainer; // 동적 뷰를 추가할 컨테이너
    private ViewGroup modifyContainer; // 동적 뷰를 추가할 컨테이너
    private BackView backView;

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
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        memberId = sharedPref.getLong("loginId", 0);

        // 뒤로 가기
//        backView = findViewById(R.id.challengeCreate_header);
//        backView.setOnBackButtonClickListener(this);

        // 알람 토글
        setAlarm();

        // 요일 설정
        chooseCycle();

        // 템플릿 설정
        templateListContainer = findViewById(R.id.challengeCreate_templateLayout);
        modifyContainer = findViewById(R.id.challengeCreate_modifyLayout);
        orderMap = new HashMap<>();
        normalTemplates = new ArrayList<>();
        setTemplates();

        // 챌린지 생성
        Button create = findViewById(R.id.challnegeCreate_create);
        cycle = new ArrayList<>();
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                orderMap = (HashMap<String, Integer>) data.getSerializableExtra("orderMap");
                normalTemplates = data.getStringArrayListExtra("normalTemplates");
            }
            setTemplates();
        }
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

        View.OnClickListener moveTemplate = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChallengeCreateActivity.this, TemplateActivity.class);
                intent.putExtra("normalTemplates", normalTemplates);
                startActivityForResult(intent, REQUEST_CODE);
            }
        };

        LayoutInflater inflater = getLayoutInflater();
        templateListContainer.removeAllViews();
        modifyContainer.removeAllViews();

        if (orderMap.isEmpty()) {
            View addInstance = inflater.inflate(R.layout.sample_add_template_view, templateListContainer, false);
            TextView plusView = addInstance.findViewById(R.id.plus_template);
            plusView.setText("템플릿 설정하러 가기");
            plusView.setOnClickListener(moveTemplate);
            templateListContainer.addView(addInstance);
        } else {
            for (String str : normalTemplates) {
                View seeInstance = inflater.inflate(R.layout.sample_see_template_view, templateListContainer, false);
                TextView registerView = seeInstance.findViewById(R.id.register_template);
                registerView.setText(str);
                templateListContainer.addView(seeInstance);
            }
            View modifyInstance = inflater.inflate(R.layout.sample_modify_view, modifyContainer, false);
            TextView modifyView = modifyInstance.findViewById(R.id.modify_template);
            modifyView.setOnClickListener(moveTemplate);
            modifyContainer.addView(modifyInstance);
        }
    }

    // 챌린지 생성
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createChallenge() {
        System.out.println("챌린지 생성");
        View view = findViewById(R.id.challnegeCreate);

        EditText titleEdit = view.findViewById(R.id.challnegeCreate_input_title);
        CalendarView startEdit = view.findViewById(R.id.challnegeCreate_input_start);
        CalendarView endEdit = view.findViewById(R.id.challnegeCreate_input_end);
        EditText memoEdit = view.findViewById(R.id.challnegeCreate_input_memo);

        title = titleEdit.getText().toString();
        start = startEdit.getEditText();
        end = endEdit.getEditText();
        memo = memoEdit.getText().toString();
        templates = new ArrayList<>();

        // 날짜 비교
        if (compareDates(start, end)) {
            Toast.makeText(this, "시작일과 종료일을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date startDate = format.parse(start);
            Date endDate = format.parse(end);
            if (startDate.getDate()-endDate.getDate()<=7) {

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String[] arr = {"challengeDetailTitle", "challengeDetailContent",
            "challengeDetailImage", "challengeDetailAudio",
            "challengeDetailVideo", "challengeDetailSTT", "challengeDetailTTS"};

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
            System.out.println("선택: "+str+","+k);
            if (isThere) templates.add(k);
            else templates.add(0);
        }

        if (cycle.isEmpty()) {
            for (int i = 1; i < 8; i++) {
                if (clicked[i]) cycle.add(i);
            }
        }

        int resultCode = checkInputs();
        if (resultCode == 1001 || resultCode == 1002 || resultCode == 1003 || resultCode == 1004 || resultCode == 1005) {
            return;
        } else {
            System.out.println("challengeDetail: " + templates);
            Challenge challenge = Challenge.builder()
                .challengeTitle(title)
                .challengeStart(start.substring(0,10))
                .challengeEnd(end.substring(0,10))
                .challengeCycle(cycle)
                .challengeAlarm(isAlarm)
                .challengeAlarmTime(isAlarm ? formattedTime : null)
                .challengeMemo(memo)
                .order(templates)
                .build();

            OkHttpClient client = TrustOkHttpClientUtil.getUnsafeOkHttpClient();
            Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
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

    private int checkInputs() {
        View view = findViewById(R.id.challnegeCreate);

        TextView titleView = view.findViewById(R.id.challengeCreate_title_setting);
        TextView duringView = view.findViewById(R.id.challengeCreate_during_setting);
        TextView dayView = view.findViewById(R.id.challengeCreate_day_setting);
        TextView templateView = view.findViewById(R.id.challengeCreate_template_setting);

        // 제목
        if (title.isEmpty()) {
            titleView.setVisibility(View.VISIBLE);
            return 1001;
        } else {
            titleView.setVisibility(View.GONE);
        }

        // 기간
        if (start.isEmpty() || end.isEmpty()) {
            duringView.setVisibility(View.VISIBLE);

            return 1002;
        } else {
            duringView.setVisibility(View.GONE);
        }

        // 요일
        if (cycle.isEmpty()) {
            dayView.setVisibility(View.VISIBLE);

            return 1004;
        } else {
            dayView.setVisibility(View.GONE);
        }

        boolean isTemplate = false;
        for (int nums : templates) {
            if (nums > 0) {
                isTemplate = true;
                break;
            }
        }

        // 템플릿
        if (!isTemplate) {
            templateView.setVisibility(View.VISIBLE);
            return 1005;
        } else {
            templateView.setVisibility(View.GONE);
        }
        return 2001;
    }

    private boolean compareDates(String startDate, String endDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date start = format.parse(startDate);
            Date end = format.parse(endDate);
            return start.after(end); // 시작 날짜가 종료 날짜보다 뒤면 true 반환
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onBackButtonClick() {
        Intent intent = new Intent(this, ChallengeListActivity.class);

        startActivity(intent);
    }
}
