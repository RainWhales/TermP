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

public class Free_Feedback extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_feedback);

        ImageView inputImageView = findViewById(R.id.InputImage);


        byte[] byteArray = getIntent().getByteArrayExtra("imageBitmap");
        if (byteArray != null) {
            // 바이트 배열을 다시 Bitmap으로 변환
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            // Bitmap을 InputImage에 설정
            inputImageView.setImageBitmap(imageBitmap);
        }
    }
}