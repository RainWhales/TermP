package com.example.new_termp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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


public class Recording extends AppCompatActivity {


    private DatabaseReference FB_database;
    private boolean Sending = false;

    private TextView tv_Result;
    private HashMap<Integer, String> consonants; /*자음*/
    private HashMap<Integer, String> vowels; /*모음*/

    private static final String TAG = "Recording";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        initHashMaps(); /*해쉬맵 초기화*/

        FB_database = FirebaseDatabase.getInstance().getReference();

        tv_Result = findViewById(R.id.text_result);
        Button btn_Sending = (Button) findViewById(R.id.btn_Recording);
        btn_Sending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Sending) {
                    sendStartSignal();
                    fetchData(); // 데이터 불러오기
                } else {
                    sendStopSignal();
                }
                Sending = !Sending;
            }
        });
    }

    private void sendStartSignal() {
        FB_database.child("devices").child("signal").setValue("start_process");
    }

    private void sendStopSignal() {
        FB_database.child("devices").child("signal").setValue("stop_process");
    }

    private void fetchData() { // 데이터를 실시간 갱신
        FB_database.child("camResult").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot viosnapshot) {
                FB_database.child("vioResult").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot camsnapshot) {
                        StringBuilder resultBuilder = new StringBuilder();
                        processSnapshot(camsnapshot, resultBuilder);
                        processSnapshot(viosnapshot, resultBuilder);
                        tv_Result.setText(resultBuilder.toString().trim());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, " vioResult 불러오기 실패", error.toException());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, " camResult 불러오기 실패", error.toException());

            }
        });
    }

    private void processSnapshot(DataSnapshot snapshot, StringBuilder resultBuilder) {
        for (DataSnapshot data : snapshot.getChildren()) {
            Integer number = data.getValue(Integer.class);
            if (number != null) {
                String consonant = consonants.get(number);
               // String vowel = vowels.get(number); 모음추가부분

                if (consonant != null /* & vowel != null) { 모음추가*/) {
                    // String character = consonant + vowel; 자음 + 모음
                    resultBuilder.append(/*character*/consonant).append(" ");
                }
            }
            String resultCharacter = resultBuilder.toString().trim();
            tv_Result.setText(resultCharacter);
        }
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
}



