package com.tamc.oxfordgps_v1.models;

import android.net.Uri;

/**
 * Created by Usuario on 26/05/2015.
 */
public class AlarmModel {
    public static final int LUNES = 0;
    public static final int MARTES = 1;
    public static final int MIERCOLES = 2;
    public static final int JUEVES = 3;
    public static final int VIERNES = 4;
    public static final int SABADO = 5;
    public static final int DOMINGO = 6;


    public long id;
    public int timeHour;
    public int timeMinute;
    private boolean repeatingDays[];
    public boolean repeatWeekly;
    //    public Uri alarmTone;
    public String name;
    public boolean isEnabled;

    public AlarmModel() {
        repeatingDays = new boolean[7];
    }

    public void setRepeatingDay(int dayOfWeek, boolean value) {
        repeatingDays[dayOfWeek] = value;
    }

    public boolean getRepeatingDay(int dayOfWeek) {
        return repeatingDays[dayOfWeek];
    }

}
