package com.tamc.oxfordgps_v1.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.tamc.oxfordgps_v1.MainActivity;
import com.tamc.oxfordgps_v1.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class Gps extends Fragment {

    private static final String TAG = "Gps";
    /*SharedPreferences*/
    SharedPreferences sp;
    private SharedPreferences.Editor edit;
    /*---*/

    @InjectView(R.id.Btn_power)
    ImageButton btn_power;
    @InjectView(R.id.Btn_openconfig)
    ImageButton btn_openconfig;
    @InjectView(R.id.Btn_openLog)
    ImageButton btn_openlog;

    private boolean isPowerGps;
    private LocationManager locationManager;
    private boolean isGpsEnabled;

    public Gps() {

    }

    ClicksGps clicksGps;

    public interface ClicksGps {
        public void onClickButtonPower();
        public void onClickButtonOpenLog();
        public void onClickButtonOpenConfig();
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            clicksGps = (ClicksGps) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() ESTAS EN GPS ");
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gps, container, false);
        ButterKnife.inject(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        loadsavePreferences();
        Log.d(TAG, "onActivityCreated()");
        if (isPowerGps) {
            Log.d(TAG, "onActivityCreated() > Boton esta activo.");
            btn_power.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.back_verde));
            btn_power.setImageResource(R.drawable.location_on);
        } else {
            Log.d(TAG, "onActivityCreated() > Boton esta apagado.");
            btn_power.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.back_rojo));
            btn_power.setImageResource(R.drawable.location_off);
        }
        MainActivity.setShowToolBar(false, "");
    }

    @OnClick(R.id.Btn_power)
    public void Btn_power() {
        loadsavePreferences();
        Log.d(TAG, "Btn_power() Se precioso Boton Power ***");
        if(isGpsEnabled()){
            Log.d(TAG, "Btn_power() Se precioso Boton Power > GPS HABILITADO ***");
            if (isPowerGps) {
                Log.d(TAG, "Btn_power() Se precioso Boton Power > GPS HABILITADO > Rastreo estuvo prendido ahora esta apagado ***");
                btn_power.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.back_rojo));
                btn_power.setImageResource(R.drawable.location_off);
            } else {
                Log.d(TAG, "Btn_power() Se precioso Boton Power > GPS HABILITADO > Rastreo estuvo apagado ahora se Prendio ***");
                btn_power.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.back_verde));
                btn_power.setImageResource(R.drawable.location_on);
            }
            clicksGps.onClickButtonPower();
        }else{
            Log.d(TAG, "Btn_power() Se precioso Boton Power > GPS DESHABILITADO ***");
            Toast.makeText(getActivity(), "GPS deshabilitado. Porfavor Activelo.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadsavePreferences() {
        isPowerGps = sp.getBoolean("POWER_GPS", false);
    }

    private boolean isGpsEnabled() {
        isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        edit = sp.edit();
        edit.putBoolean("GPS_ENABLED", isGpsEnabled);
        edit.commit();
        return isGpsEnabled;
    }

    @OnClick(R.id.Btn_openLog)
    public void Btn_openlog() {
        clicksGps.onClickButtonOpenLog();
    }

    @OnClick(R.id.Btn_openconfig)
    public void Btn_openconfig() {
        MainActivity.showCroutonCancel();
        clicksGps.onClickButtonOpenConfig();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        if (!isGpsEnabled()){
            Toast.makeText(getActivity(), "GPS deshabilitado. Porfavor Activelo.", Toast.LENGTH_SHORT).show();
        }
    }
}
