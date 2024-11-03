package com.example.new_termp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class Free_Recording extends AppCompatActivity {

    private DatabaseReference Firebase_DB_I;
    private TextView Txt_Result_A;
    private ImageButton SignalBtn_A;
    private Button resetApp_A;
    private Button feedback_A;
    private EditText A_Outputbtn;

    private HashMap<Integer, String> columns; // 자음해시맵
    private String inputChar;
    private String outputChar;

    private Bitmap imageBitmap;

    private boolean isRecording_A = false;
    private static final int REQUEST_CODE_STT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_recording);

        Firebase_DB_I = FirebaseDatabase.getInstance().getReference();
        Txt_Result_A = findViewById(R.id.APR_output_text);
        SignalBtn_A = findViewById(R.id.imageButton);
        A_Outputbtn = findViewById(R.id.APR_input_text);
        resetApp_A = findViewById(R.id.reset_button_A);
        feedback_A = findViewById(R.id.btn_Feedback_A);
        resetApp_A.setVisibility(View.GONE);
        feedback_A.setVisibility(View.GONE);

        initHashMaps();


        SignalBtn_A.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toggleRecording();
            }
        });

        resetApp_A.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resetApp();
            }
        });

        feedback_A.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //피드백 액티비티 새로 생성;
            }
        });

    }
    private void toggleRecording() {
        if (isRecording_A) {
            SendStopSignal();
            SignalBtn_A.setImageResource(R.drawable.start_icon);  // 버튼 아이콘을 시작으로 변경
        } else {
            SendStartSignal();
            SignalBtn_A.setImageResource(R.drawable.stop_icon);  // 버튼 아이콘을 중지로 변경
            startSpeechToText();
        }
        isRecording_A = !isRecording_A;
    }

    private void initHashMaps() {
        columns = new HashMap<>();
        columns.put(0, "ㅣ"); // 자음 해시맵 받아서 새로 작성할것,
    }

    private void SendStartSignal() {
        Firebase_DB_I.child("voiceData").child("signal").setValue(true);
    }

    private void SendStopSignal() {
        Firebase_DB_I.child("voiceData").child("signal").setValue(false);
        fetchData_I();
    }

    private void fetchData_I() {
        // Firebase Database에서 데이터를 가져와 해시맵에 대응
        Firebase_DB_I.child("voiceData").child("captured").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override // 경로 따로 지정해줄것
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer capturedKey = snapshot.getValue(Integer.class);
                if (capturedKey != null && columns.containsKey(capturedKey)) {
                    outputChar = columns.get(capturedKey);
                } else {
                    outputChar = "데이터 없음";
                }
                // fetchImageData(); // 이미지 가져오기 시작 , 스토리지 구축 후 적용
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Free_Recording", "captured 데이터 가져오기 실패", error.toException());
            }
        });
    }


            private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "발음해주세요");

        try {
            startActivityForResult(intent, REQUEST_CODE_STT);
        } catch (Exception e) {
            Txt_Result_A.setText("STT를 사용할 수 없습니다.");
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_STT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                Txt_Result_A.setText(result.get(0)); // 인식된 텍스트를 출력
            }
        }
        // STT가 종료되었으므로 녹음 상태를 false로 설정
        isRecording_A = false;
        SignalBtn_A.setImageResource(R.drawable.start_icon); // 아이콘을 시작 아이콘으로 변경
    }

    private void resetApp() {
        Txt_Result_A.setText(""); // 텍스트 출력 초기화
        A_Outputbtn.setText(""); // 입력 필드 초기화
        isRecording_A = false;
        SignalBtn_A.setImageResource(R.drawable.start_icon); // 시작 아이콘으로 변경
        Firebase_DB_I.child("voiceData").child("signal").setValue(false); // Firebase 신호 초기화
        resetApp_A.setVisibility(View.GONE);
        feedback_A.setVisibility(View.GONE);
    }

}