package com.ssafy.oringe.ui.component.common;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.ssafy.oringe.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CalendarView extends LinearLayout {
    private EditText current;
    private TypedArray typedArray;

    private Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener myDatepicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            // 출력 형식 지정
            String myFormat = "YYYY-MM-dd(E)";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
            current.setText(sdf.format(myCalendar.getTime()));
        }
    };

    public CalendarView(Context context) {
        super(context);
        init(context);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_calendar_view, this, false);
        EditText editText = view.findViewById(R.id.calendar_date);

        View.OnClickListener dateClickListner = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current = (EditText) v;
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, myDatepicker,
                    myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();

                // 최소 오늘~1년 후까지만 선택 가능
                Calendar minDate = Calendar.getInstance();
                Calendar maxDate = Calendar.getInstance();
                maxDate.add(Calendar.YEAR, 1);

                datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
                datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

            }
        };
        editText.setOnClickListener(dateClickListner);

        addView(view);
    }

    public String getEditText() {
        EditText editText = findViewById(R.id.calendar_date);
        return editText.getText().toString();
    }

    public void setHint(String s) {
        EditText editText = findViewById(R.id.calendar_date);
        editText.setHint(s);
    }

    public void setHintColor(int color) {
        EditText editText = findViewById(R.id.calendar_date);
        editText.setHintTextColor(color);
    }
}