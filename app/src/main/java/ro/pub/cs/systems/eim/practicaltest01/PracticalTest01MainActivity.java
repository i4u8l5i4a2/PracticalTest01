package ro.pub.cs.systems.eim.practicaltest01;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;



public class PracticalTest01MainActivity extends AppCompatActivity {
    private EditText editTextTopLeft;
    private EditText editTextTopRight;
    private Button buttonPressMe;
    private Button buttonPressMeToo;
    private int countLeft = 0;
    private int countRight = 0;

    // Chei pentru salvarea stării
    private static final String COUNT_LEFT_KEY = "countLeft";
    private static final String COUNT_RIGHT_KEY = "countRight";

    // declararea butonului de navigare și a lansatorului de activitate secundară
    private Button buttonNavigate;
    private ActivityResultLauncher<Intent> secondaryActivityLauncher;

    //pragul pentru serviciu
    private static final int THRESHOLD = 5;

    // flag pentru a verifica dacă serviciul a fost pornit
    private boolean isServiceRunning = false;

    // BroadcastReceiver și IntentFilter
    private PracticalTest01BroadcastReceiver broadcastReceiver;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inițializăm launcher-ul pentru activitatea secundară
        secondaryActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Toast.makeText(PracticalTest01MainActivity.this,
                                    "Ai apăsat OK", Toast.LENGTH_SHORT).show();
                        } else if (result.getResultCode() == RESULT_CANCELED) {
                            Toast.makeText(PracticalTest01MainActivity.this,
                                    "Ai apăsat Cancel", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );


        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_practical_test01_main);

        // Înregistrează BroadcastReceiver-ul
        broadcastReceiver = new PracticalTest01BroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PracticalTest01Service.ACTION_1);
        intentFilter.addAction(PracticalTest01Service.ACTION_2);
        intentFilter.addAction(PracticalTest01Service.ACTION_3);
        registerReceiver(broadcastReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);


        // Inițializăm referințele către componente
        editTextTopLeft = findViewById(R.id.editTextTopLeft);
        editTextTopRight = findViewById(R.id.editTextTopRight);
        buttonPressMe = findViewById(R.id.buttonPressMe);
        buttonPressMeToo = findViewById(R.id.buttonPressMeToo);

        // Ascultător pentru primul buton
        buttonPressMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countLeft++;
                editTextTopLeft.setText(String.valueOf(countLeft));
            }
        });

        // Ascultător pentru al doilea buton
        buttonPressMeToo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countRight++;
                editTextTopRight.setText(String.valueOf(countRight));
            }
        });

        buttonNavigate = findViewById(R.id.buttonNavigate);
        buttonNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creăm Intent-ul pentru activitatea secundară
                Intent intent = new Intent(PracticalTest01MainActivity.this,
                        PracticalTest01SecondaryActivity.class);

                // Adăugăm datele în Intent
                intent.putExtra("countLeft", countLeft);
                intent.putExtra("countRight", countRight);

                // Pornim activitatea secundară
                secondaryActivityLauncher.launch(intent);

                // Verificăm dacă trebuie să pornim serviciul
                // Calculăm suma valorilor
                int sum = countLeft + countRight;

                // Verificăm dacă suma depășește pragul
                if (sum > THRESHOLD) {
                    // Dacă serviciul nu rulează deja, îl pornim
                    if (!isServiceRunning) {
                        Intent serviceIntent = new Intent(PracticalTest01MainActivity.this,
                                PracticalTest01Service.class);
                        serviceIntent.putExtra(PracticalTest01Service.EXTRA_COUNT_LEFT, countLeft);
                        serviceIntent.putExtra(PracticalTest01Service.EXTRA_COUNT_RIGHT, countRight);
                        startService(serviceIntent);
                        isServiceRunning = true;

                        Toast.makeText(PracticalTest01MainActivity.this,
                                "Serviciul a fost pornit (suma = " + sum + ")",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PracticalTest01MainActivity.this,
                            "Serviciul nu pornește (suma = " + sum + ", prag = " + THRESHOLD + ")",
                            Toast.LENGTH_SHORT).show();
                }
            }

//                // ADAUGĂ AICI: Pornește serviciul
//                Intent serviceIntent = new Intent(PracticalTest01MainActivity.this,
//                        PracticalTest01Service.class);
//                serviceIntent.putExtra(PracticalTest01Service.EXTRA_COUNT_LEFT, countLeft);
//                serviceIntent.putExtra(PracticalTest01Service.EXTRA_COUNT_RIGHT, countRight);
//                startService(serviceIntent);
//            }
        });




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (savedInstanceState != null) {
            // Restaurăm starea contorilor dacă există
            countLeft = savedInstanceState.getInt(COUNT_LEFT_KEY, 0);
            countRight = savedInstanceState.getInt(COUNT_RIGHT_KEY, 0);
            editTextTopLeft.setText(String.valueOf(countLeft));
            editTextTopRight.setText(String.valueOf(countRight));
    }
}

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Salvăm starea contorilor
        outState.putInt(COUNT_LEFT_KEY, countLeft);
        outState.putInt(COUNT_RIGHT_KEY, countRight);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Oprim serviciul când aplicația este distrusă
        if (isServiceRunning) {
            Intent serviceIntent = new Intent(this, PracticalTest01Service.class);
            stopService(serviceIntent);
            isServiceRunning = false;
        }

        // Dezînregistrează BroadcastReceiver-ul
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }


}


