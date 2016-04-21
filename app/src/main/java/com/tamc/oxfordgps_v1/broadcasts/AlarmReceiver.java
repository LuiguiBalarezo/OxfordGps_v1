package com.tamc.oxfordgps_v1.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.tamc.oxfordgps_v1.intentservices.UDPClientService;
import com.tamc.oxfordgps_v1.services.GpsService;
import com.tamc.oxfordgps_v1.utils.RunServicesByBattery;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";
    public static String ACTION_ALARM = "com.alarammanager.alaram";
    private RunServicesByBattery runServicesByBattery = null;

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

//        runServicesByBattery = new RunServicesByBattery(context.getApplicationContext(), GpsService.class, TAG);
//        runServicesByBattery.activateService();

        Log.d(TAG, "> onReceive()  ----------- ");
        Bundle bundle = intent.getExtras();
        String action = bundle.getString(ACTION_ALARM);

        if (action.equals(ACTION_ALARM)) {
            Log.d(TAG, "> onReceive() > if action.equals(ACTION_ALARM)");
            Intent inService = new Intent(context.getApplicationContext(), UDPClientService.class);
            context.startService(inService);
        } else {
            Log.d(TAG, "> onReceive() > else action.equals(ACTION_ALARM)");
        }

        Log.d(TAG, "> onReceive()  -----------/ ");

    }


}
