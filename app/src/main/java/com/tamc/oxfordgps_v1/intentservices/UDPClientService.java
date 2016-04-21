package com.tamc.oxfordgps_v1.intentservices;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.tamc.oxfordgps_v1.services.GpsService;
import com.tamc.oxfordgps_v1.utils.DateAndTime;
import com.tamc.oxfordgps_v1.utils.RunServicesByBattery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPClientService extends IntentService {

    private static final String TAG = "UDPClientService";
    public String ip;
    public int port;
    public String dni, email, latitud, longitud, fecha, hora, message;
    private DateAndTime dateAndTime = null;
//    private RunServicesByBattery runServicesByBattery = null;

    private DatagramSocket socket;
    private InetAddress local;
    private DatagramPacket packet;

    /*SharedPreferences*/
    private SharedPreferences sp;
    private SharedPreferences.Editor edit;
    /*---*/
    private String numInterval;

    public UDPClientService() {
        super("UDPClientService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() ***");
//        runServicesByBattery = new RunServicesByBattery(getApplicationContext(), GpsService.class, TAG);
        dateAndTime = new DateAndTime();
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d(TAG, "onLowMemory() **** ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand()");
        super.onStartCommand(intent, startId, startId);
        return START_NOT_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent arg0) {

//        runServicesByBattery.activateService();

        Log.d(TAG, "-------------------------------------------------------------");
        showInformation();
        getInformation();
//        try {
            Log.d(TAG, "onHandleIntent() Antigua LAT/LON/PRE: " + sp.getString("OLDLATITUD", "0.0") + ", " + sp.getString("OLDLONGITUD", "0.0") + ", " + sp.getString("OLDPRECISION", "0.0") + " metros. **/** Nueva LAT/LON/PRE: " + sp.getString("LATITUD", "0.0") + " - " + sp.getString("LONGITUD", "0.0") + ", " + sp.getString("OLDPRECISION", "0.0") + " metros.");
            Log.d(TAG, "Enviando >>> Intervalo: ( " + sp.getString("INTERVAL", "") + " )minutos. Server: " + ip + ". Puerto: " + port + " / Mensaje: " + message);
//            socket = new DatagramSocket();
//            local = InetAddress.getByName(ip);
//            packet = new DatagramPacket(message.getBytes(), message.length(), local, port);
//            socket.setBroadcast(true);
//            socket.send(packet);
//            Log.d(TAG, "Socket  > send ");
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//            Log.d(TAG, "UnknownHostException ");
//        } catch (SocketException e) {
//            e.printStackTrace();
//            Log.d(TAG, "SocketException ");
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.d(TAG, "IOException ");
//        }finally {
//            if(socket != null){
//                Log.d(TAG, "Socket != null > Close ");
//                socket.close();
//            }
//        }

        Log.d(TAG, "-------------------------------------------------------------");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() *** ");
        Toast.makeText(getApplicationContext(), "UDPClient destruido", Toast.LENGTH_SHORT).show();
    }

    private void getInformation(){
        ip = sp.getString("IP", "00.00.00.00");
        port = Integer.parseInt(sp.getString("PORT", "0000"));
        message = sp.getString("DNI", "") + ";" + sp.getString("CORREO", "") + ";" + sp.getString("LATITUD", "0.0") + ";" + sp.getString("LONGITUD", "0.0") + ";" + dateAndTime.getDatePhone() + "-" + dateAndTime.getHourPhone();
        numInterval = sp.getString("INTERVAL", "0");
    }

    private void showInformation(){
        Log.d(TAG, "onHandleIntent() Dni: " + sp.getString("DNI", "") + ", CORREO: " + sp.getString("CORREO", ""));
        Log.d(TAG, "onHandleIntent() CACHEDLATITUD: " + sp.getString("CACHEDLATITUD", "0.0") + ", CACHEDLONGITUD: " + sp.getString("CACHEDLONGITUD", "0.0") + ", CACHEDPRECISION: " + sp.getString("CACHEDPRECISION", "0.0"));
        Log.d(TAG, "onHandleIntent() LATITUD: " + sp.getString("LATITUD", "0.0") + ", LONGITUD: " + sp.getString("LONGITUD", "0.0") + ", PRECISION: " + sp.getString("PRECISION", "0.0"));
    }

}
