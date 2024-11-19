package com.example.new_termp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Feedback extends AppCompatActivity {

    private TextView feedback;
    private TextView similar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        String mostFrequentData = getIntent().getStringExtra("mostFrequentData");
        String inputChar = getIntent().getStringExtra("inputChar");
        String outputChar = getIntent().getStringExtra("outputChar");

        similar = findViewById(R.id.simiar_txt);
        feedback = findViewById(R.id.Feedback_txt);

        if (inputChar == null) inputChar = "";
        if (outputChar == null) outputChar = "";

        determineResultText(inputChar, outputChar, mostFrequentData);


    }

    private void determineResultText(String inputChar, String outputChar, String mostFrequentData) {
        if (inputChar.equals(outputChar)) {
            similar.setText(String.format("일치율 : %s%%", mostFrequentData));
            feedback.setText("올바른 발음입니다!");
            return;
        }

        switch (inputChar) {
            case "ㅣ":
                if (outputChar.equals("ㅡ")) feedback.setText("발음하신 문자는 'ㅣ'보다는 'ㅡ'에 가깝습니다. 혀를 앞니 사이로 넣고 입을 살짝 벌려주세요. ");
                else feedback.setText(" 발음이 정확하지 않습니다. 다시 발음해주세요! ");
                break;

            case "ㅡ":
                if (outputChar.equals("ㅣ")) feedback.setText("발음하신 문자는 'ㅡ'보다는 'ㅣ'에 가깝습니다. 혀가 안보이게 발음하세요.");
                else feedback.setText(" 발음이 정확하지 않습니다. 다시 발음해주세요! ");
                break;

            case "ㅐ":
                if (outputChar.equals("ㅔ")) feedback.setText("발음하신 문자는 'ㅐ'보다는 'ㅔ'에 가깝습니다. 턱을 더 밑으로 해서 입을 크게 벌리세요.");
                else feedback.setText(" 발음이 정확하지 않습니다. 다시 발음해주세요! ");
                break;

            case "ㅔ":
                if (outputChar.equals("ㅐ")) feedback.setText("발음하신 문자는 'ㅔ'보다는 'ㅐ'에 가깝습니다. 입을 조금 작게 벌리세요. ");
                else feedback.setText(" 발음이 정확하지 않습니다. 다시 발음해주세요! ");
                break;

            case "ㅓ":
                if (outputChar.equals("ㅏ")) feedback.setText("발음하신 문자는 'ㅓ'보다는 'ㅏ'에 가깝습니다. 턱을 더 밑으로 해서 입을 벌리세요. ");
                else feedback.setText(" 발음이 정확하지 않습니다. 다시 발음해주세요! ");
                break;

            case "ㅏ":
                if (outputChar.equals("ㅓ")) feedback.setText("발음하신 문자는 'ㅏ'보다는 'ㅓ'에 가깝습니다. 턱을 조금 위로 하면서 조금 입술을 모아주세요. ");
                else feedback.setText(" 발음이 정확하지 않습니다. 다시 발음해주세요! ");
                break;

            case "ㅜ":
                if (outputChar.equals("ㅗ")) feedback.setText("발음하신 문자는 'ㅜ'보다는 'ㅗ'에 가깝습니다. 입술을 조금 더 위로 모으고 발음하세요. ");
                else feedback.setText(" 발음이 정확하지 않습니다. 다시 발음해주세요! ");
                break;

            case "ㅗ":
                if (outputChar.equals("ㅜ")) feedback.setText("발음하신 문자는 'ㅗ'보다는 'ㅜ'에 가깝습니다. 입술을 조금 더 아래로 모으고 발음하세요. ");
                else feedback.setText(" 발음이 정확하지 않습니다. 다시 발음해주세요! ");
                break;

            default:
                feedback.setText("오류발생");
                break;
        }
    }
}

