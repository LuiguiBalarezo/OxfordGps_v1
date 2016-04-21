package com.tamc.oxfordgps_v1.broadcasts;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.tamc.oxfordgps_v1.services.GpsService;
import com.tamc.oxfordgps_v1.utils.Alarm;

/**
 * Created by Usuario on 07/05/2015.
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    private static final String TAG = "BootCompleteReceiver";
    private SharedPreferences sp;
    private Alarm alarm;
    private int minutos;
    private Context ctx = null;
    @Override
    public void onReceive(Context context, Intent intent) {
        ctx = context;
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        minutos = Integer.parseInt(sp.getString("INTERVAL", "1"));
        alarm = new Alarm(context, TAG);

        if (sp.getBoolean("POWER_GPS", false)) {
            Toast.makeText(context, "BootCompleteReceiver aplicacion :  " + sp.getBoolean("POWER_GPS", false), Toast.LENGTH_SHORT).show();
            alarm.openAlarm();
            ctx.startService(new Intent(ctx, GpsService.class));
        }
    }

}
