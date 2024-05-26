package com.ssafy.oringewatch.presentation.activity.alarm;

import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.ssafy.oringewatch.R;

import android.os.Parcelable;
import android.os.Parcel;
import android.view.View.BaseSavedState;

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

        // 초기 상태 반영
        updateView();
    }

    private void toggle(Context context) {
        isChecked = !isChecked;
        updateView();
        showCustomToast(context);
    }

    private void updateView() {
        if (isChecked) {
            transitionDrawable.startTransition(0); // 즉시 ON 상태로 전환
        } else {
            transitionDrawable.reverseTransition(0); // 즉시 OFF 상태로 전환
        }
    }

    private void showCustomToast(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.activity_toast, (ViewGroup) findViewById(R.id.toast_container));

        TextView text = layout.findViewById(R.id.toast_text);
        ImageView icon = layout.findViewById(R.id.toast_icon);

        if (isChecked) {
            text.setText("챌린지 알람을 수신합니다.");
            icon.setImageResource(R.drawable.oringe_charecter);
        } else {
            text.setText("챌린지 알람을 수신하지 않습니다.");
            icon.setImageResource(R.drawable.oringe_charecter);
        }

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);

        // 위치와 오프셋 설정
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
        updateView();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.isChecked = this.isChecked;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.isChecked = savedState.isChecked;
        updateView();
    }

    static class SavedState extends BaseSavedState {
        boolean isChecked;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            isChecked = (in.readInt() == 1);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(isChecked ? 1 : 0);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
