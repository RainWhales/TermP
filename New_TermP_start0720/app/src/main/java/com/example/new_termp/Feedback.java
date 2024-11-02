package com.example.new_termp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Feedback extends AppCompatActivity {

    private TextView feedback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        String mostFrequentData = getIntent().getStringExtra("mostFrequentData");

        TextView similar = findViewById(R.id.simiar_txt);
        feedback = findViewById(R.id.Feedback_txt);

        similar.setText(String.format("일치율 : %s%%", mostFrequentData));


    }


}