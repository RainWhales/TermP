package com.example.new_termp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class Free_Recording extends AppCompatActivity {

    private ActivityResultLauncher<Intent> sttLauncher;

    private DatabaseReference Firebase_DB_I;
    private StorageReference storageRef;

    private TextView Txt_Result_A;
    private ImageButton SignalBtn_A;
    private Button resetApp_A;
    private Button feedback_A;
    private EditText A_Outputbtn;

    private HashMap<Integer, String> columns; // 자음해시맵
    private String outputChar;

    private String imageUrl;
    private int jaumLabelData;
    private int mappedNumber;

    private boolean isRecording_A = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_recording);
        FirebaseApp.initializeApp(this);
        Firebase_DB_I = FirebaseDatabase.getInstance().getReference();
        storageRef = FirebaseStorage.getInstance().getReference();

        Txt_Result_A = findViewById(R.id.APR_output_text);
        SignalBtn_A = findViewById(R.id.imageButton);
        A_Outputbtn = findViewById(R.id.APR_input_text);
        resetApp_A = findViewById(R.id.reset_button_A);
        feedback_A = findViewById(R.id.btn_Feedback_A);
        resetApp_A.setVisibility(View.GONE);
        feedback_A.setVisibility(View.GONE);
        feedback_A.setEnabled(false);

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

        A_Outputbtn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mappedNumber = getMappedNumber(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        feedback_A.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUrl != null) {
                    openFeedbackActivity_A(imageUrl, mappedNumber, jaumLabelData); // URL이 있을 때만 액티비티 전환
                } else {
                    Log.e("Free_Recording", "이미지 URL이 없습니다. 이미지를 먼저 다운로드하세요.");
                }
            }
        });



        sttLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        ArrayList<String> results = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        if (results != null && !results.isEmpty()) {
                            Txt_Result_A.setText(results.get(0)); // STT 결과 표시
                            displayWithDiff();
                            fetchData_I(); // STT가 끝난 후 Firebase 데이터 가져오기
                        }
                    }

                    SendStopSignal();

                    isRecording_A = false;
                    SignalBtn_A.setImageResource(R.drawable.start_icon); // 시작 아이콘으로 변경
                }
        );
    }

    private int getMappedNumber (String input){
        if (input.equals("바") || input.equals("빠") || input.equals("파") || input.equals("마"))  {
            return 0;
        } else if (input.equals("다") || input.equals("따") || input.equals("타") || input.equals("자") || input.equals("짜") || input.equals("차") || input.equals("사") || input.equals("싸") || input.equals("나") || input.equals("라")) {
            return 1;
        } else if (input.equals("가") || input.equals("까") || input.equals("카") || input.equals("하"))  {
            return 2;
        }
        return -1; // 매칭되지 않는 경우
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
        columns.put(1, "가");

    }

    private void SendStartSignal() {
        Firebase_DB_I.child("voiceData").child("signal").setValue(true);
    }

    private void SendStopSignal() {
        Firebase_DB_I.child("voiceData").child("signal").setValue(false);
        fetchImageFromStorage();
    }

    private void fetchData_I() {
        // Firebase Database에서 데이터를 가져와 해시맵에 대응
        Firebase_DB_I.child("voiceData").child("captured_jaum").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override // 경로 따로 지정해줄것
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer capturedKey = snapshot.getValue(Integer.class);
                if (capturedKey != null && columns.containsKey(capturedKey)) {
                    outputChar = columns.get(capturedKey);
                    resetApp_A.setVisibility(View.VISIBLE);
                    feedback_A.setVisibility(View.VISIBLE);

                    // Firebase로부터 추가 데이터 가져오기 (예: jaumLabelData)
                    Firebase_DB_I.child("voiceData").child("jaumLabelData").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                jaumLabelData = snapshot.getValue(Integer.class);
                                Log.d("Free_Recording", "자음 데이터 저장됨: " + jaumLabelData);
                            } else {
                                Log.e("Free_Recording", "자음 데이터가 없습니다.");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Free_Recording", "데이터 가져오기 실패", error.toException());
                        }
                    });

                } else {
                    outputChar = "데이터 없음";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Free_Recording", "captured 데이터 가져오기 실패", error.toException());
            }
        });
    }


    private void fetchImageFromStorage() {
        // Firebase Storage에서 이미지 파일 경로 설정
        StorageReference imageRef = storageRef.child("image/g.png"); // 이미지 경로에 맞게 수정
        Log.d("Free_Recording", "이미지 경로: " + imageRef.getPath());

        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            imageUrl = uri.toString(); // 이미지 URL을 가져옴

            feedback_A.setEnabled(true);

        }).addOnFailureListener(exception -> {
            Log.e("Free_Recording", "이미지 다운로드 실패", exception);
        });
    }



    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "발음해주세요");

        try {
            sttLauncher.launch(intent);
        } catch (Exception e) {
            Txt_Result_A.setText("STT를 사용할 수 없습니다.");
        }
    }


    private void displayWithDiff() {
            // STT 결과와 사용자 입력 텍스트 가져오기
            String sttResult = Txt_Result_A.getText().toString().trim();
            String userInput = A_Outputbtn.getText().toString().trim();

            SpannableStringBuilder spannable = new SpannableStringBuilder(sttResult);

            // STT 결과와 사용자 입력 비교
            for (int i = 0; i < Math.min(userInput.length(), sttResult.length()); i++) {
                if (userInput.charAt(i) == sttResult.charAt(i)) {
                    // 문자가 일치하면 파란색으로 표시
                    spannable.setSpan(new ForegroundColorSpan(Color.BLUE), i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    // 문자가 일치하지 않으면 빨간색으로 표시
                    spannable.setSpan(new ForegroundColorSpan(Color.RED), i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }

            // `Txt_Result_A`에 색상 적용한 텍스트 설정
            Txt_Result_A.setText(spannable);
    }



    private void openFeedbackActivity_A(String imageUrl, int mappedNumber, int jaumLabelData) {
        Intent intent = new Intent(Free_Recording.this, Free_Feedback.class);
        intent.putExtra("imageUrl", imageUrl);
        intent.putExtra("outputChar", outputChar);
        intent.putExtra("mappedNumber", mappedNumber);
        intent.putExtra("jaumLabelData", jaumLabelData);
        String inputText = A_Outputbtn.getText().toString().trim();
        intent.putExtra("inputText", inputText);
        String sttResultText = Txt_Result_A.getText().toString().trim();
        intent.putExtra("sttResultText", sttResultText);
        startActivity(intent);
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