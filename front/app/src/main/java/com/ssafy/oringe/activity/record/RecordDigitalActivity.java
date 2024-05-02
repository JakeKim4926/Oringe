package com.ssafy.oringe.activity.record;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ssafy.oringe.R;

public class RecordDigitalActivity extends AppCompatActivity {

    private ViewGroup callListContainer;
    private TextView nameView;
    private TextView timeView;
    private TextView dateView;
    private TextView doneView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        callListContainer = findViewById(R.id.record_create);
    }
}