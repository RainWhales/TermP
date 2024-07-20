package com.example.new_termp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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

public class Recording extends AppCompatActivity {


    private DatabaseReference FB_database;
    private boolean Sending = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        FB_database = FirebaseDatabase.getInstance().getReference();

        Button btn_Sending = (Button) findViewById(R.id.btn_Recording);
        btn_Sending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Sending) {
                    sendStartSignal();
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
}



