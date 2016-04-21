package com.tamc.oxfordgps_v1;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.tamc.oxfordgps_v1.broadcasts.AlarmReceiver;
import com.tamc.oxfordgps_v1.fragments.Configuracion;
import com.tamc.oxfordgps_v1.fragments.Gps;
import com.tamc.oxfordgps_v1.fragments.Login;
import com.tamc.oxfordgps_v1.models.AlarmModel;
import com.tamc.oxfordgps_v1.services.GpsService;
import com.tamc.oxfordgps_v1.utils.Alarm;

import java.util.Calendar;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class MainActivity extends AppCompatActivity implements Login.ClicksLogin, Configuracion.ClicksConfiguracion, Gps.ClicksGps {


    private static final String TAG = "MainActivity";
    public static Context mContext;
    public static Activity mActivity;
    public static Toolbar mToolbar;
    private boolean isPowerGps;
//    private int minutos;
    private Alarm alarm;

    /*Spinner*/
    private String[] emails;
    private String[] dnis;
    private String[] celulares;
    private String[] nombres;
    private String[] apellidos;
    Bundle bundleConfiguracion;
    /*----*/

    /*SharedPreferences*/
    private SharedPreferences sp;
    private SharedPreferences.Editor edit;
    /*---*/

    FragmentManager fm;
    FragmentTransaction ft;

    Fragment login, configuracion, gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate() ESTAS EN ACTIVITY");

        fm = getSupportFragmentManager();
        mContext = getApplicationContext();
        mActivity = MainActivity.this;
        sp = PreferenceManager.getDefaultSharedPreferences(mContext);

        alarm = new Alarm(this, TAG);

        /*initialize Fragmengs*/
        login = new Login();
        configuracion = new Configuracion();
        gps = new Gps();

        /*Initialize Controls*/
        initToolbar();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, login)
                    .commit();
        }

        /*seccion de pruebas*/
        isPowerGps = sp.getBoolean("POWER_GPS", false);
        if(isPowerGps){
            if(savedInstanceState == null || !savedInstanceState.containsKey("serviceHasStarted")){
//                Toast.makeText(getApplicationContext(), "TAG > savedInstanceState == null || !savedInstanceState.containsKey(serviceHasStarted)", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "savedInstanceState == null || !savedInstanceState.containsKey(serviceHasStarted)");
                alarm.resetAlarm();
                startService(new Intent(getApplicationContext(), GpsService.class));
                doBindService();
            }
        }
        /*-----------------*/
    }



    @Override
    public void onClickButtonlogin(String user, String clave) {
//        String crashString = null;
//        crashString.length();
        if (user.equals("adminapp@oxfordperu.com") && clave.equals("00000000")) {
            if (TextUtils.isEmpty(sp.getString("DNI", ""))) {
                Configuration();
            } else {
                Gps();
            }
        } else {
            showCroutonCustomError();
        }
    }

    private void Configuration() {
        initArgumentsConfiguracion();
        transactionFragments(configuracion, false);
    }

    private void Gps() {
        transactionFragments(gps, false);
    }

    private void initArgumentsConfiguracion() {
        emails = new String[]{"Seleccione correo.", "elevano@oxfordperu.com", "fmedina@oxfordperu.com", "mmartel@oxfordperu.com", "mchirinos@oxfordperu.com", "dacha@oxfordperu.com", "pclavijo@oxfordperu.com", "ssantos@oxfordperu.com"};
        dnis = new String[]{"", "40302362", "46138848", "09615499", "09679032", "41229456", "41921661", "42714239"};
        celulares = new String[]{"", "997962652", "953652496", "967789674", "962200535", "944568665", "944575693", "944575694"};
        nombres = new String[]{"", "Edgar", "Fabian", "Mario", "martin", "Danilo David", "Paola Elizabeth", "Sadith"};
        apellidos = new String[]{"", "Levano Tenemas", "Medina Lira", "Martel Espinoza", "chirinos", "Acha Ysique", "Clavijo Garibay", "Santos Torres"};

        bundleConfiguracion = new Bundle();
        bundleConfiguracion.putStringArray("itemsEmails", emails);
        bundleConfiguracion.putStringArray("itemsDnis", dnis);
        bundleConfiguracion.putStringArray("itemsCelulares", celulares);
        bundleConfiguracion.putStringArray("itemsNombres", nombres);
        bundleConfiguracion.putStringArray("itemsApellidos", apellidos);

        configuracion.setArguments(bundleConfiguracion);
    }

    private void transactionFragments(Fragment f, boolean isaddToBack) {
        ft = fm.beginTransaction();
        ft.replace(R.id.container, f);
        if (isaddToBack) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    private void savePreferences(String correo, String dni, String celular, String nombres, String apellidos, String ip, String port, String interval) {
        edit = sp.edit();
        edit.putString("CORREO", correo);
        edit.putString("DNI", dni);
        edit.putString("CELULAR", celular);
        edit.putString("NOMBRES", nombres);
        edit.putString("APELLIDOS", apellidos);
        edit.putString("IP", ip);
        edit.putString("PORT", port);
        edit.putString("INTERVAL", interval);
        edit.putBoolean("POWER_GPS", false);
        edit.commit();
    }

    public static void showCroutonOk(String msj) {
        Crouton.makeText(mActivity, msj, Style.CONFIRM).show();
    }

    public static void showCroutonCustomError() {
        View view = mActivity.getLayoutInflater().inflate(R.layout.crouton_custom, null);
        final Crouton crouton = Crouton.make(mActivity, view);
        crouton.show();
    }

    public static void showCroutonCancel() {
        Crouton.cancelAllCroutons();
    }

    public static void setShowToolBar(boolean b, CharSequence s) {
        mActivity.setTitle(s);
        mToolbar.setTitleTextColor(mActivity.getResources().getColor(android.R.color.white));
        if (b) {
            mToolbar.setVisibility(View.VISIBLE);
        } else {
            mToolbar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClickGuardar(String correo, String dni, String celular, String nombres, String apellidos, String ip, String port, String interval) {
        savePreferences(correo, dni, celular, nombres, apellidos, ip, port, interval);
        transactionFragments(gps, false);
    }

    @Override
    public void onClickModificar(String intervalo) {
        edit = sp.edit();
        edit.putString("INTERVAL", intervalo);
        edit.commit();
        showCroutonOk("Se guardo configuracion.");
    }

    @Override
    public void onClickButtonPower() {
        isPowerGps = sp.getBoolean("POWER_GPS", false);
        edit = sp.edit();
        if (isPowerGps) {
            stopGpsService();
            stopAlarm();
            edit.putBoolean("POWER_GPS", false);
            edit.commit();
        } else {
            startGpsService();
            startAlarm();
            edit.putBoolean("POWER_GPS", true);
            edit.commit();
            this.finish();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /*seccion de prueba*/
        outState.putBoolean("serviceHasStarted", true);
        /*-----------------*/
    }

    private void startGpsService() {
        startService(new Intent(getApplicationContext(), GpsService.class));
    }

    private void stopGpsService() {
        killService();
    }

    private void startAlarm() {
        Log.d(TAG, "startAlarm() Se inicio alarma");
        alarm.openAlarm();
    }

    private void stopAlarm() {
        Log.d(TAG, "stopAlarm() Se detuvo alarma");
        alarm.closeAlarm();
    }

    @Override
    public void onClickButtonOpenLog() {

    }

    @Override
    public void onClickButtonOpenConfig() {
        isPowerGps = sp.getBoolean("POWER_GPS", false);
        if (isPowerGps) {
            Toast.makeText(getApplicationContext(), "Debe detener el rastreo par apoder entrar a configuracion.", Toast.LENGTH_LONG).show();
        } else {
            transactionFragments(configuracion, true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        isPowerGps = sp.getBoolean("POWER_GPS", false);
        if (isPowerGps) {
            if (mBoundService != null) {
                unbindService(serviceConnection);
                mBoundService = null;
            }
        }

        Log.d(TAG, "onDestroy() >> Se destruyo Mainactivity");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() >> Se Pauso Mainactivity");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() >> Se Resumio Mainactivity");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart() >> Se Reseteo Mainactivity");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() >> Se Top Mainactivity");
    }

    /*Seccion de pruebas*/

    protected void killService() {
        Log.d(TAG, "killService()");
        if (isServiceBound()) {
            Log.d(TAG, "killService() > isServiceBound()");
            mBoundService.stopToastMessages();
        }
        doUnbindService();
        stopService(new Intent(getApplicationContext(), GpsService.class));
    }

    // Service Binding code
    private GpsService mBoundService;

    private void doBindService() {
        Log.d(TAG, "doBindService()");
        bindService(new Intent(getApplicationContext(), GpsService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected()");
            mBoundService = ((GpsService.LocalBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected()");
            mBoundService = null;
        }
    };

    private void doUnbindService() {
        Log.d(TAG, "doUnbindService()");
        if (mBoundService != null) {
            Log.d(TAG, "doUnbindService() > mBoundService != null");
            unbindService(serviceConnection);
            mBoundService = null;
        }
    }

    private boolean isServiceBound() {
        Log.d(TAG, "isServiceBound()");
        return mBoundService != null;
    }
    /*------------------*/


}
