package com.tamc.oxfordgps_v1.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.tamc.oxfordgps_v1.broadcasts.AlarmReceiver;

import java.util.Calendar;

/**
 * Created by Usuario on 25/05/2015.
 */
public class Alarm {

    private static String TAG = "Alarm", _TAG = "";
    private Context ctx = null;
    private int minutos = 0;

    private static AlarmManager alarmManager = null;
    private Intent intent= null;
    private static PendingIntent pendingIntent= null;
    private DateAndTime dateAndTime= null;
    private Calendar calendar= null;
    private SharedPreferences sp;
    private long calculo = 0;
    private int hour_init = 0;
    private int min = 0;
    private int min_init = 0;

    public Alarm(Context context, String tag) {
        ctx = context;
        _TAG = tag;
        dateAndTime = new DateAndTime();
        calendar = Calendar.getInstance();
        sp = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public void openAlarm() {

        Log.d(TAG, _TAG + " openAlarm()");
        minutos = Integer.parseInt(sp.getString("INTERVAL", "1"));
        calculo = 1000 * 60 * minutos;
        Log.d(TAG, "MINUTOS: " + minutos + " MILISEGUNDO: " + calculo);

        hour_init = dateAndTime.getHour();
        min = dateAndTime.getMinute();
        min_init = min + minutos;

        Log.d(TAG, "Hora iniciar : "+hour_init + ", min:" +  min + ", min_init:"+ min_init);

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour_init);
        calendar.set(Calendar.MINUTE, min_init);

        try {
            Log.d(TAG, "> TRY ");
            alarmManager = (AlarmManager) ctx.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            intent = new Intent(ctx.getApplicationContext(), AlarmReceiver.class);
            intent.putExtra(AlarmReceiver.ACTION_ALARM, AlarmReceiver.ACTION_ALARM);
            pendingIntent = PendingIntent.getBroadcast(ctx.getApplicationContext(), 1, intent, 0);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), calculo, pendingIntent);
            Log.d(TAG, "> CATCH ");
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    public void closeAlarm() {
        Log.d(TAG, _TAG + " closeAlarm()");
       if(alarmManager != null && pendingIntent != null){
           Log.d(TAG, "closeAlarm() > alarmManager != null ");
           alarmManager.cancel(pendingIntent);
           pendingIntent.cancel();
           Log.d(TAG, "closeAlarm() > alarmManager cancelada ");
       }else{
           Log.d(TAG, "closeAlarm() > alarmManager == null ");
           Log.d(TAG, "No sera eliminado la alarma porque no existe");
       }
    }

    public void resetAlarm(){
        Log.d(TAG, "resetAlarm()");
        if(alarmManager != null){
            Log.d(TAG, "resetAlarm() > alarmManager != null ");
            closeAlarm();
            openAlarm();
        }else{
            Log.d(TAG, "resetAlarm() > alarmManager == null ");
            openAlarm();
        }
    }

}
