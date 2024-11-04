package com.example.new_termp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

public class Free_Feedback extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_feedback);

        ImageView inputImageView = findViewById(R.id.InputImage);
        ImageView StandardImageview = findViewById(R.id.StandardImage);
        TextView Standardtext = findViewById(R.id.std_textView2);
        TextView input_Txt = findViewById(R.id.input_txt);


        String imageUrl = getIntent().getStringExtra("imageUrl");
        if (imageUrl != null) {
            // Glide로 이미지 URL에서 이미지를 로드하여 표시
            Glide.with(this).load(imageUrl).into(inputImageView);
        }
        int mappingNumber = getIntent().getIntExtra("mappedNumber", -1); // 기본값 -1로 설정
        int jaumLabelData = getIntent().getIntExtra("jaumLabelData", -1); // 기본값 -1로 설정
        String inputText = getIntent().getStringExtra("inputText");
        String sttResultText = getIntent().getStringExtra("sttResultText");


        switch (mappingNumber) {
            case 0:
                // 양순음 설정
                Standardtext.setText(String.format("입력하신 문자는 '%s' 입니다. 문자가 속한 양순음의 파형입니다", inputText));
                StandardImageview.setImageResource(R.drawable.yang); // yang.jpg는 res/drawable/yang.jpg에 위치해야 함
                break;
            case 1:
                // 설음 설정
                Standardtext.setText(String.format("입력하신 문자는 '%s' 입니다. 문자가 속한 전설음의 파형입니다", inputText));
                StandardImageview.setImageResource(R.drawable.seol); // seol.jpg는 res/drawable/seol.jpg에 위치해야 함
                break;
            case 2:
                // 후음 설정
                Standardtext.setText(String.format("입력하신 문자는 '%s' 입니다. 문자가 속한 후설음의 파형입니다", inputText));
                StandardImageview.setImageResource(R.drawable.hoo); // hoo.jpg는 res/drawable/hoo.jpg에 위치해야 함
                break;
            default:
                // 매칭되지 않는 경우 기본 설정
                Standardtext.setText("알 수 없는 소리입니다");// 기본 이미지가 있을 경우
                break;
        }

        switch (jaumLabelData) {
            case 0:
                // 양순음 설정
                input_Txt.setText(String.format("발음하신 문자는 '%s' 입니다. 이는 양순음이며 실제로 발음하신 파형입니다.", sttResultText));
                break;
            case 1:
                // 설음 설정
                input_Txt.setText(String.format("발음하신 문자는 '%s' 입니다. 이는 전설음이며 실제로 발음하신 파형입니다.", sttResultText));
                break;
            case 2:
                // 후음 설정
                input_Txt.setText(String.format("발음하신 문자는 '%s' 입니다. 이는 후설음이며 실제로 발음하신 파형입니다.", sttResultText));
                break;
            default:
                // 매칭되지 않는 경우 기본 설정
                input_Txt.setText("알 수 없는 소리입니다");
                break;
        }


    }
}