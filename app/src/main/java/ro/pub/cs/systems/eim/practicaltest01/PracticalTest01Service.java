package ro.pub.cs.systems.eim.practicaltest01;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class PracticalTest01Service extends Service {

    // Cele 3 acțiuni diferite pentru broadcast
    public static final String ACTION_1 = "ro.pub.cs.systems.eim.practicaltest01.broadcast1";
    public static final String ACTION_2 = "ro.pub.cs.systems.eim.practicaltest01.broadcast2";
    public static final String ACTION_3 = "ro.pub.cs.systems.eim.practicaltest01.broadcast3";

    // Cheile pentru datele din Intent
    public static final String EXTRA_COUNT_LEFT = "countLeft";
    public static final String EXTRA_COUNT_RIGHT = "countRight";

    // Cheile pentru datele din broadcast
    public static final String BROADCAST_TIMESTAMP = "timestamp";
    public static final String BROADCAST_ARITHMETIC_MEAN = "arithmeticMean";
    public static final String BROADCAST_GEOMETRIC_MEAN = "geometricMean";

    private Handler handler;
    private Runnable broadcastRunnable;
    private int countLeft;
    private int countRight;
    private Random random;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper());
        random = new Random();
        Log.d("PracticalTest01Service", "Service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Obținem numerele din Intent
        if (intent != null) {
            countLeft = intent.getIntExtra(EXTRA_COUNT_LEFT, 0);
            countRight = intent.getIntExtra(EXTRA_COUNT_RIGHT, 0);

            Log.d("PracticalTest01Service", "Service started with countLeft=" +
                    countLeft + ", countRight=" + countRight);

            // Oprim task-ul vechi dacă există
            if (broadcastRunnable != null) {
                handler.removeCallbacks(broadcastRunnable);
            }

            // Creăm task-ul care trimite broadcast la fiecare 10 secunde
            broadcastRunnable = new Runnable() {
                @Override
                public void run() {
                    sendBroadcastMessage();
                    // Repetăm la fiecare 10 secunde (10000 ms)
                    handler.postDelayed(this, 10000);
                }
            };

            // Pornim task-ul
            handler.post(broadcastRunnable);
        }

        // Returnăm START_STICKY pentru ca serviciul să fie repornit dacă e omorât
        return START_STICKY;
    }

    private void sendBroadcastMessage() {
        // Calculăm media aritmetică
        double arithmeticMean = (countLeft + countRight) / 2.0;

        // Calculăm media geometrică: √(countLeft × countRight)
        double geometricMean = Math.sqrt((double) countLeft * countRight);

        // Obținem data și ora curentă
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String timestamp = sdf.format(new Date());

        // Alegem aleator una din cele 3 acțiuni
        String[] actions = {ACTION_1, ACTION_2, ACTION_3};
        String selectedAction = actions[random.nextInt(3)];

        // Creăm Intent-ul pentru broadcast
        Intent broadcastIntent = new Intent(selectedAction);
        broadcastIntent.putExtra(BROADCAST_TIMESTAMP, timestamp);
        broadcastIntent.putExtra(BROADCAST_ARITHMETIC_MEAN, arithmeticMean);
        broadcastIntent.putExtra(BROADCAST_GEOMETRIC_MEAN, geometricMean);

        // Trimitem broadcast-ul
        sendBroadcast(broadcastIntent);

        Log.d("PracticalTest01Service", "Broadcast sent - Action: " + selectedAction +
                ", Time: " + timestamp +
                ", Arithmetic Mean: " + arithmeticMean +
                ", Geometric Mean: " + geometricMean);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Oprim task-ul când serviciul e distrus
        if (handler != null && broadcastRunnable != null) {
            handler.removeCallbacks(broadcastRunnable);
        }
        Log.d("PracticalTest01Service", "Service destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Nu suportăm binding, returnăm null
        return null;
    }
}
