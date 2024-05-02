package com.ssafy.oringe.activity.record;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ssafy.oringe.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RecordCallActivity extends AppCompatActivity {

    private static final int REQUEST_READ_CALL_LOG = 101;
    private ViewGroup callListContainer;
    private TextView nameView;
    private TextView timeView;
    private TextView dateView;
    private TextView doneView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_record_create);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        callListContainer = findViewById(R.id.record_create);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG}, REQUEST_READ_CALL_LOG);
            } else {
                getCallHistory();
            }
        } else {
            getCallHistory();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_CALL_LOG) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCallHistory();
            }
        }
    }


    public Boolean getCallHistory() {
        String[] callSet = new String[]{CallLog.Calls.DATE, CallLog.Calls.TYPE, CallLog.Calls.NUMBER, CallLog.Calls.DURATION};

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long todayStart = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        long todayEnd = calendar.getTimeInMillis();

        String selection = CallLog.Calls.DATE + " BETWEEN ? AND ? AND " + CallLog.Calls.NUMBER + "=?";
        String[] selectionArgs = new String[]{String.valueOf(todayStart), String.valueOf(todayEnd), "01012345679ys"};

        Cursor c = getContentResolver().query(CallLog.Calls.CONTENT_URI, callSet, selection, selectionArgs, null);

        LayoutInflater inflater = LayoutInflater.from(this);
        View callView = inflater.inflate(R.layout.sample_record_call_view, callListContainer, false);
        nameView = callView.findViewById(R.id.call_name);
        timeView = callView.findViewById(R.id.call_time);
        dateView = callView.findViewById(R.id.call_date);
        doneView = callView.findViewById(R.id.call_done);
        if (c == null || c.getCount() == 0) {
            doneView.setText("통화 내역이 없습니다.");
            callListContainer.addView(callView);
            return false;
        }

        if (c.moveToFirst()) {
            do {
                long callDate = c.getLong(0);
                SimpleDateFormat datePattern = new SimpleDateFormat("yyyy-MM-dd");
                String date_str = datePattern.format(new Date(callDate));

                nameView.setText("📞 " + c.getString(2));
                int callSeconds = Integer.parseInt(c.getString(3));
                if (callSeconds >= 60) {
                    timeView.setText(callSeconds / 60 + "분 " + callSeconds % 60 + "초");
                } else {
                    timeView.setText(callSeconds + "초");
                }
                dateView.setText(date_str);
                doneView.setText(" 통화했습니다.");
                callListContainer.addView(callView);
            } while (c.moveToNext());
        }
        c.close();
        return true;
    }
}