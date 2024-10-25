package com.example.new_termp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    Button btn_ITR;
    Button btn_APR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ITRButton();
        APRButton();
    }

    public void ITRButton(){ // 메인 화면->자유 녹음 화면 전환

        btn_ITR = (Button) findViewById(R.id.btn_Intent_to_Rec);
        btn_ITR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent MTR = new Intent(MainActivity.this, Recording.class);
                startActivity(MTR);
            }
        });


    }

    public void APRButton(){ // 메인 화면-> 지정 녹음 화면 전환

        btn_APR = (Button) findViewById(R.id.btn_Intent_to_APR);
        btn_APR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent MTAR = new Intent(MainActivity.this, Appoint_Recording.class);
                startActivity(MTAR);
            }
        });


    }

}