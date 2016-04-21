package com.tamc.oxfordgps_v1.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import com.tamc.oxfordgps_v1.services.GpsService;

/**
 * Created by Usuario on 15/05/2015.
 */
public class RunServicesByBattery {

    private static String _TAG = "", TAG="RunServicesByBattery";
    private Context ctx;
    private IntentFilter batteryStatusIntentFilter;
    private Intent bateerystatus;
    private int rawlevel = 0, scale = 0, level = 0;
    private Class<?> servicesClass = null;

    public RunServicesByBattery(Context context, Class<?> serviceclass, String tag) {
        ctx = context;
        _TAG = tag;
        servicesClass = serviceclass;
    }

    public void activateService(){
        Log.d(TAG, _TAG + " activate"+servicesClass.getSimpleName()+"()");
        try {

            if (!isMyServiceRunnig(servicesClass)) {
                Log.d(TAG, servicesClass.getSimpleName() + " no esta activo.");
                if (statusBattery(ctx) > 15) {
                    ctx.getApplicationContext().startService(new Intent(ctx.getApplicationContext(), servicesClass));
                    Log.d(TAG, " Bateria mayor de 15%, Se activara "+ servicesClass.getSimpleName());
                } else {
                    Log.d(TAG, " Bateria menor de 15%, No se puede activar " +  servicesClass.getSimpleName());
                }
            }else{
                Log.d(TAG,  servicesClass.getSimpleName() +" ya se encuentra activo.");
            }

        }catch (Exception e){
            Log.d(TAG,e.getMessage());
        }

    }

    private boolean isMyServiceRunnig(Class<?> servicesClass) {
        ActivityManager manager = (ActivityManager)ctx.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (servicesClass.getName().equals(serviceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    private int statusBattery(Context context) {
        batteryStatusIntentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        bateerystatus = context.getApplicationContext().registerReceiver(null, batteryStatusIntentFilter);
        if (bateerystatus != null) {
            Log.d(TAG, "bateerystatus != null");
            rawlevel = bateerystatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            scale = bateerystatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            level = -1;
            if (rawlevel >= 0 && scale > 0) {
                level = (rawlevel * 100) / scale;
            }
            Log.d(TAG, "SCALE BATTERY: " + level + "%");
        } else {
            Log.d(TAG, "bateerystatus == null");
        }

        return level;
    }

}
