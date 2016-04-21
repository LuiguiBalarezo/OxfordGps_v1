package com.tamc.oxfordgps_v1.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.text.DecimalFormat;

/**
 * Created by Usuario on 22/04/2015.
 */
public class GpsService extends Service {

    private String TAG = "GpsService";
    private Context ctx;

    /*SharedPreferences*/
    private SharedPreferences sp;
    private SharedPreferences.Editor edit;
    /*---*/

    /*seccion prueba*/
    final Handler handler = new Handler();
    /*--------------*/

    private Criteria criteria;
    private String mejorProveedor;
    private LocationListener locationListenerGPSProvider = null;
    private LocationManager locationManager;
    private static Location locationGPSProvider = null;

    private double gpsLatitud = 0.0;
    private double gpsLongitud = 0.0;
    private double gpsPrecision = 0.0;
    private double velocidad = 0.0;
    private double altitud = 0.0;
    private double cachedGPSLatitude = 0.0;
    private double cachedGPSLongitude = 0.0;
    private double cachedGPSAccuracy = 0.0;
    private boolean isGpsEnabled = false;
    private int countDestroyServiceGps = 0;
    private int countLocationChangeGps = 0;
    private int countStartServiceGps = 0;

    private static final DecimalFormat DECIMAL_FORMAT_4 = new DecimalFormat("#,###.0000");

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 2;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    @Override
    public void onCreate() {
        super.onCreate();
        ctx = this;
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Log.d(TAG, "onCreate() ");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d(TAG, "onStart() ");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void startLocation() {
        if (locationManager == null) {
            try {

                locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
                locationManager.getBestProvider(getBestCriteria(), false);
                isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                locationGPSProvider = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (isGpsEnabled) {
                    edit = sp.edit();
                    edit.putBoolean("GPSENABLED", true);
                    edit.commit();
                    if (locationGPSProvider != null) {

                        cachedGPSLatitude = locationGPSProvider.getLatitude();
                        cachedGPSLongitude = locationGPSProvider.getLongitude();
                        cachedGPSAccuracy = locationGPSProvider.getAccuracy();

                        if (cachedGPSLatitude != 0.0 || cachedGPSLongitude != 0.0 || cachedGPSAccuracy != 0.0) {

                            Log.d(TAG, "Ultima posicion conocida en cache inicio ---- ");
                            gpsLatitud = locationGPSProvider.getLatitude();
                            gpsLongitud = locationGPSProvider.getLongitude();
                            gpsPrecision = locationGPSProvider.getAccuracy();
                            edit = sp.edit();
                            edit.putString("CACHEDLATITUD", String.valueOf(gpsLatitud));
                            edit.putString("CACHEDLONGITUD", String.valueOf(gpsLongitud));
                            edit.putString("CACHEDPRECISION", String.valueOf(gpsPrecision));
                            edit.commit();

                            Log.d(TAG, "GPS LAT/LON: " + gpsLatitud + " - " + gpsLongitud + ", Precision:" + DECIMAL_FORMAT_4.format(gpsPrecision) + " metros.");
                            Log.d(TAG, "Ultima posicion conocida en cache fin ---- ");

                        } else {
                            Log.d(TAG, "Latitud != 0.0 o Longitud != 0.0 o Precision != 0.0");
                        }
                    } else {
                        Log.d(TAG, "No hay Posicion en cache.");
                    }
                } else {
                    edit = sp.edit();
                    edit.putBoolean("GPSENABLED", false);
                    edit.commit();
                    Log.d(TAG, "PROVEEDOR GPS NO ACTIVO");
                }

            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }

            setLocationListenerGpsProvider();
        } else {
            Log.d(TAG, "locationManager != NULL ");
        }

    }

    private void stopLocation() {
        if (locationManager != null) {
            if (locationListenerGPSProvider != null) {
                locationManager.removeUpdates(locationListenerGPSProvider);
            }
            locationManager = null;
        }
        if (locationListenerGPSProvider != null) {
            locationListenerGPSProvider = null;
        }
    }

    private void setLocationListenerGpsProvider() {
        locationListenerGPSProvider = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                edit = sp.edit();
                edit.putString("OLDLATITUD", sp.getString("LATITUD", "0.0"));
                edit.putString("OLDLONGITUD", sp.getString("LONGITUD", "0.0"));
                edit.putString("OLDPRECISION", sp.getString("PRECISION", "0.0"));
                gpsLatitud = location.getLatitude();
                gpsLongitud = location.getLongitude();
                gpsPrecision = location.getAccuracy();
                edit.putString("LATITUD", String.valueOf(gpsLatitud));
                edit.putString("LONGITUD", String.valueOf(gpsLongitud));
                edit.putString("PRECISION", String.valueOf(gpsPrecision));
                edit.commit();

                countLocationChangeGps++;
                Toast.makeText(ctx, "Cabio ubicacion Gps", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onLocationChanged() Antigua LAT/LON/PRE: " + sp.getString("OLDLATITUD", "0.0") + ", " + sp.getString("OLDLONGITUD", "0.0") + ", " + sp.getString("OLDPRECISION", "0.0") + " metros. **/** Nueva LAT/LON/PRE: " + gpsLatitud + " - " + gpsLongitud + ", " + DECIMAL_FORMAT_4.format(gpsPrecision) + " metros.");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                edit = sp.edit();
                edit.putBoolean("GPSENABLED", true);
                edit.commit();
                Log.d(TAG, "onProviderEnabled() ");
            }

            @Override
            public void onProviderDisabled(String provider) {
                edit = sp.edit();
                edit.putBoolean("GPSENABLED", false);
                edit.commit();
                Log.d(TAG, "onProviderDisabled() ");
            }
        };

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerGPSProvider);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    private Criteria getBestCriteria() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(1);
        return criteria;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d(this.TAG, "onLowMemory() **** ");
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        return super.getApplicationInfo();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(this.TAG, "onStartCommand() INICIO GPSSERVICE ");
        if (intent != null && intent.hasExtra("callType") && intent.getStringExtra("callType").equals("Alarm")) {
            Log.d(TAG, "GpsService Iniciado");
            startToastMessages();
        } else {
            Log.d(TAG, "GpsService Re-Iniciado");
            startToastMessages();
        }

        startLocation();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocation();
        Log.d(TAG, "onDestroy() DESTRUYO GPSSERVICE " + this.countDestroyServiceGps);
        Toast.makeText(ctx, "GpsService Destruido", Toast.LENGTH_SHORT).show();
    }


    /*seccion de pruebas*/

    public void startToastMessages() {
        Log.d(TAG, "startToastMessages()");
        scheduleToastEvent();
    }

    public void stopToastMessages() {
        Log.d(TAG, "stopToastMessages()");
        clearToastEvent();
    }

    private void scheduleToastEvent() {
        Log.d(TAG, "scheduleToastEvent()");
        clearToastEvent();
        int delay = 4000;
        handler.postDelayed(toastRunnable, delay);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1 || Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Log.d(TAG, "scheduleToastEvent() > API Android de 4.4.1 / 4.4.2");
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getApplicationContext(), GpsService.class);
            intent.putExtra("callType", "Alarm");
            PendingIntent scheduledIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + delay, scheduledIntent);
            } else {
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + delay, scheduledIntent);
            }

        } else {
            Log.d(TAG, "Version de android distinta de 4.4.1 / 4.4.2");
        }
    }

    private void clearToastEvent() {
        Log.d(TAG, "clearToastEvent()");
        // Remove Handler-based events
        handler.removeCallbacks(toastRunnable);
        // Remove Alarm-based events
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), GpsService.class);
        PendingIntent scheduledIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(scheduledIntent);
    }

    private Runnable toastRunnable = new Runnable() {
        @Override
        public void run() {
//            Log.d(TAG, "toastRunnable run()");
            doToastEvent();
        }
    };

    private void doToastEvent() {
        Log.d(TAG, "doToastEvent() ding");
        scheduleToastEvent();
    }

    //----------------------------------

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()");
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public GpsService getService() {
            Log.d(TAG, "getService()");
            return GpsService.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();
    /*------------------*/

    /*Nota: supuesta solucion para Android 4.4 (API 19) - START_STICKY roto
    * Http://stackoverflow.com/questions/20636330/start-sticky-does-not-work-on-android-kitkat
    */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, "onTaskRemoved()");
        if (Build.VERSION.SDK_INT >= 19) {
            Log.d(TAG, "Build.VERSION.SDK_INT >= 19");
            Intent restartService = new Intent(getApplicationContext(), this.getClass());
            restartService.setPackage(getPackageName());
            PendingIntent restartServicePI = PendingIntent.getService(getApplicationContext(), 1, restartService, PendingIntent.FLAG_ONE_SHOT);
            AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePI);
        }
    }
}
