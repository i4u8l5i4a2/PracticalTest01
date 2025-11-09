package ro.pub.cs.systems.eim.practicaltest01;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PracticalTest01SecondaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test01_secondary);

        // Obținem Intent-ul care a pornit această activitate
        Intent intent = getIntent();

        // Extragem datele trimise din activitatea principală
        int countLeft = intent.getIntExtra("countLeft", 0);
        int countRight = intent.getIntExtra("countRight", 0);
        int total = countLeft + countRight;

        // Găsim TextView-ul și afișăm totalul
        TextView textViewTotal = findViewById(R.id.textViewTotal);
        textViewTotal.setText("Total apăsări: " + total);

        // Butonul OK
        Button buttonOk = findViewById(R.id.buttonOk);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Setăm rezultatul RESULT_OK și închidem activitatea
                setResult(RESULT_OK);
                finish();
            }
        });

        // Butonul Cancel
        Button buttonCancel = findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Setăm rezultatul RESULT_CANCELED și închidem activitatea
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}
