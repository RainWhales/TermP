package com.example.new_termp;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.EditText;
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

public class Appoint_Recording extends AppCompatActivity {

    private DatabaseReference Firebase_DB;
    private TextView Txt_Result;
    private EditText Out_Txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appoint_recording);

        Txt_Result = findViewById(R.id.APR_output_text);
        Firebase_DB = FirebaseDatabase.getInstance().getReference();
        Out_Txt = findViewById(R.id.APR_input_text);

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
                }
            } else {
                Log.e("Appoint_Recording", "데이터 가져오기 실패", task.getException());
            }
        });
    }

    private void displayWithDiff(String receivedText) {
        String inputTxt = Out_Txt.getText().toString().trim();

        SpannableStringBuilder spannable = new SpannableStringBuilder(receivedText);

        for (int i = 0; i < Math.min(inputTxt.length(), receivedText.length()); i++ ) {
            if (inputTxt.charAt(i) != receivedText.charAt(i)) {
                spannable.setSpan(new ForegroundColorSpan(Color.RED), i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        Txt_Result.setText(spannable);

    }


}