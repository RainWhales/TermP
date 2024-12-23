package com.example.new_termp;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Appoint_Recording extends AppCompatActivity {

    private DatabaseReference Firebase_DB;
    private TextView Txt_Result;
    private EditText Out_Txt;
    private ImageButton SignalBtn;
    private Button ResetBtn;

    private boolean isRecording = false;
    private HashMap<Integer, String> consonants; // 자음 해시맵
    private HashMap<Integer, String> vowels; // 모음 해시맵

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appoint_recording);

        Txt_Result = findViewById(R.id.APR_output_text);
        Firebase_DB = FirebaseDatabase.getInstance().getReference();
        Out_Txt = findViewById(R.id.APR_input_text);
        SignalBtn = findViewById(R.id.imageButton);
        ResetBtn = findViewById(R.id.reset_button);
        ResetBtn.setVisibility(View.GONE);

        initHashMaps();

        SignalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleRecording();
            }
        });

        ResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetApp();
            }
        });
    }

    private void initHashMaps() {
        consonants = new HashMap<>();
        consonants.put(1, "ㄱ");
        consonants.put(2, "ㄴ");
        consonants.put(3, "ㄷ");
        consonants.put(4, "ㄹ");
        consonants.put(5, "ㅁ");
        consonants.put(6, "ㅂ");
        consonants.put(7, "ㅅ");
        consonants.put(8, "ㅇ");
        consonants.put(9, "ㅈ");

        vowels = new HashMap<>();
        vowels.put(1, "ㅏ");
        vowels.put(2, "ㅓ");
        vowels.put(3, "ㅗ");
        vowels.put(4, "ㅜ");
        vowels.put(5, "ㅡ");
        vowels.put(6, "ㅛ");
        vowels.put(7, "ㅠ");
        vowels.put(8, "ㅔ");
        vowels.put(9, "ㅗ");
    }


    private void toggleRecording() {
        if (isRecording) {
            SendStopSignal();
            SignalBtn.setImageResource(R.drawable.start_icon);  // 버튼 아이콘을 시작으로 변경
        } else {
            SendStartSignal();
            SignalBtn.setImageResource(R.drawable.stop_icon);  // 버튼 아이콘을 중지로 변경
        }
        isRecording = !isRecording;
    }


    private void SendStartSignal() {
        Firebase_DB.child("voiceData").child("signal").setValue(true);
    }

    private void SendStopSignal() {
        Firebase_DB.child("voiceData").child("signal").setValue(false);
        fetchData();
    }

    private void fetchData() {
        Firebase_DB.child("voiceData").child("capturedText").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                String receivedText = snapshot.getValue(String.class);
                if (receivedText != null) {
                    displayWithDiff(receivedText);
                    ResetBtn.setVisibility(View.VISIBLE); //
                }
            } else {
                Log.e("Appoint_Recording", "데이터 가져오기 실패", task.getException());
            }
        });
    }

    private void displayWithDiff(String receivedText) {
        String inputTxt = Out_Txt.getText().toString().trim();

        SpannableStringBuilder spannable = new SpannableStringBuilder(receivedText);

        for (int i = 0; i < Math.min(inputTxt.length(), receivedText.length()); i++) {
            if (inputTxt.charAt(i) != receivedText.charAt(i)) {
                spannable.setSpan(new ForegroundColorSpan(Color.RED), i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        Txt_Result.setText(spannable);

    }

    private void resetApp() {
        Txt_Result.setText(""); // 텍스트 출력 초기화
        Out_Txt.setText(""); // 입력 필드 초기화
        isRecording = false; // recording 상태 초기화
        SignalBtn.setImageResource(R.drawable.start_icon); // 시작 아이콘으로 변경
        Firebase_DB.child("voiceData").child("signal").setValue(false); // Firebase 신호 초기화
        ResetBtn.setVisibility(View.GONE); // reset 버튼 숨기기

    }

}