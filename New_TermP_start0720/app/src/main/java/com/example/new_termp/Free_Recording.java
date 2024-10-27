package com.example.new_termp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Free_Recording extends AppCompatActivity {

    private DatabaseReference Firebase_DB_I;
    private TextView Txt_Result_I;
    private ImageButton SignalBtn_I;

    private boolean isRecording_I = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_recording);

        Firebase_DB_I = FirebaseDatabase.getInstance().getReference();
        Txt_Result_I = findViewById(R.id.IPR_output_text);
        SignalBtn_I = findViewById(R.id.imageButton);

        SignalBtn_I.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleRecording();
            }
        });
    }
    private void toggleRecording() {
        if (isRecording_I) {
            SendStopSignal();
            SignalBtn_I.setImageResource(R.drawable.start_icon);  // 버튼 아이콘을 시작으로 변경
        } else {
            SendStartSignal();
            SignalBtn_I.setImageResource(R.drawable.stop_icon);  // 버튼 아이콘을 중지로 변경
        }
        isRecording_I = !isRecording_I;
    }

    private void SendStartSignal() {
        Firebase_DB_I.child("voiceData").child("signal").setValue(true);
    }

    private void SendStopSignal() {
        Firebase_DB_I.child("voiceData").child("signal").setValue(false);
        fetchData_I();
    }

    private void fetchData_I(){
        Firebase_DB_I.child("voiceData").child("capturedText").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                String receivedText = snapshot.getValue(String.class);
                if (receivedText != null) {
                    Txt_Result_I.setText(receivedText); // 가져온 텍스트를 그대로 출력
                }
            } else {
                Log.e("Appoint_Recording", "데이터 가져오기 실패", task.getException());
            }
        });
    }

}