package com.ssafy.oringe.ui.component.common;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ssafy.oringe.R;

import java.util.Calendar;


public class TimeView extends LinearLayout {

    private TypedArray typedArray;
    private Calendar myCalendar = Calendar.getInstance();

    TimePickerDialog.OnTimeSetListener myTimepicker = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            myCalendar.set(Calendar.MINUTE, minute);
        }
    };

    public TimeView(Context context) {
        super(context);
        init(context);
    }

    public TimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TimeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_time_view, this, false);
        TextView textView = view.findViewById(R.id.time_alarm);

        View.OnClickListener timeClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(context, myTimepicker, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true);
                timePickerDialog.show();

            }
        };
        textView.setOnClickListener(timeClickListener);

        addView(view);
    }

    public String getEditText() {
        TextView textView = findViewById(R.id.time_alarm);
        return textView.getText().toString();
    }
}