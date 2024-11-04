package com.example.new_termp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

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


        String imageUrl = getIntent().getStringExtra("imageUrl");
        if (imageUrl != null) {
            // Glide로 이미지 URL에서 이미지를 로드하여 표시
            Glide.with(this).load(imageUrl).into(inputImageView);
        }
    }
}