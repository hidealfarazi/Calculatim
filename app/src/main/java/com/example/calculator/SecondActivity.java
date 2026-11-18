package com.example.calculator;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_second);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.second_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView tvResultValue = findViewById(R.id.tvResultValue);
        TextView tvDetailInfo = findViewById(R.id.tvDetailInfo);
        Button btnBack = findViewById(R.id.btnBack);

        String result = getIntent().getStringExtra("EXTRA_RESULT");
        String detail = getIntent().getStringExtra("EXTRA_DETAIL");

        if (result != null && !result.isEmpty()) {
            tvResultValue.setText(result);
        }

        if (detail != null && !detail.isEmpty()) {
            tvDetailInfo.setText(detail);
        }

        btnBack.setOnClickListener(v -> finish());
    }
}
