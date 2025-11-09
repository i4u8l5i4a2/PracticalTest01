package ro.pub.cs.systems.eim.practicaltest01;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PracticalTest01BroadcastReceiver extends BroadcastReceiver {

    // Eticheta pentru jurnalizare
    private static final String TAG = "PracticalTest01BRec";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Obținem acțiunea broadcast-ului
        String action = intent.getAction();

        // Extragem datele din Intent
        String timestamp = intent.getStringExtra(PracticalTest01Service.BROADCAST_TIMESTAMP);
        double arithmeticMean = intent.getDoubleExtra(PracticalTest01Service.BROADCAST_ARITHMETIC_MEAN, 0.0);
        double geometricMean = intent.getDoubleExtra(PracticalTest01Service.BROADCAST_GEOMETRIC_MEAN, 0.0);

        // Jurnalizăm în consolă
        Log.d(TAG, "==========================================");
        Log.d(TAG, "Broadcast received!");
        Log.d(TAG, "Action: " + action);
        Log.d(TAG, "Timestamp: " + timestamp);
        Log.d(TAG, "Arithmetic Mean: " + arithmeticMean);
        Log.d(TAG, "Geometric Mean: " + geometricMean);
        Log.d(TAG, "==========================================");
    }
}
