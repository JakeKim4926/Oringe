package com.ssafy.oringewatch.presentation.activity.alarm;

import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.ssafy.oringewatch.R;

public class CustomToggleButton extends ConstraintLayout {

    private boolean isChecked = false;
    private ImageView toggleImage;
    private TransitionDrawable transitionDrawable;

    public CustomToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomToggleButton(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.alarm_toggle_button, this, true);
        toggleImage = view.findViewById(R.id.toggle_image);
        toggleImage.setImageResource(R.drawable.toggle_transition);
        transitionDrawable = (TransitionDrawable) toggleImage.getDrawable();

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle(context);
            }
        });
    }

    private void toggle(Context context) {
        isChecked = !isChecked;
        updateView();
        showToastMessage(context);
    }

    private void updateView() {
        if (isChecked) {
            transitionDrawable.startTransition(300); // 300ms 애니메이션
        } else {
            transitionDrawable.reverseTransition(300); // 300ms 애니메이션
        }
    }

    private void showToastMessage(Context context) {
        if (isChecked) {
            Toast.makeText(context, "챌린지 알람을 수신합니다.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "챌린지 알람을 수신하지 않습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
        if (isChecked) {
            transitionDrawable.startTransition(0); // 즉시 ON 상태로 전환
        } else {
            transitionDrawable.reverseTransition(0); // 즉시 OFF 상태로 전환
        }
        // 상태 변경 시 토스트 메시지 표시
        showToastMessage(getContext());
    }
}
