package com.tamc.oxfordgps_v1.broadcasts;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.tamc.oxfordgps_v1.services.GpsService;
import com.tamc.oxfordgps_v1.utils.Alarm;
import com.tamc.oxfordgps_v1.utils.RunServicesByBattery;

public class PowerConnectionReceiver extends BroadcastReceiver {

    private static final String TAG = "PowerConnectionReceiver";
    private SharedPreferences sp = null;
    private Alarm alarm = null;
    private int minutos = 0;
    String action = null;
    private Context ctx = null;
    public PowerConnectionReceiver() {
        Log.d(TAG, "Construct() ");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        minutos = Integer.parseInt(sp.getString("INTERVAL", "1"));
        alarm = new Alarm(context, TAG);

        ctx = context;

        String action = intent.getAction();
        if (action.equals(Intent.ACTION_POWER_CONNECTED)) {
//            Toast.makeText(context, "Cable USB conectado.", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Cable USB conectado");
            initService();
        } else if (action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
//            Toast.makeText(context, "Cable USB desconectado.", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Cable USB desconectado");
            initService();
        }
    }

    private void initService() {
        Log.d(TAG, "initService()");
        if (sp.getBoolean("POWER_GPS", false)) {
            Log.d(TAG, "POWER_GPS TRUE)");
            alarm.resetAlarm();
            ctx.startService(new Intent(ctx, GpsService.class));
        } else {
            Log.d(TAG, "POWER_GPS FALSE)");
        }
    }

}
