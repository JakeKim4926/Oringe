package com.ssafy.oringewatch.presentation.activity.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // 알람이 울릴 때 실행할 코드
        Toast.makeText(context, "알람!aaaaaaaaaaaaaa", Toast.LENGTH_SHORT).show();
    }
}
