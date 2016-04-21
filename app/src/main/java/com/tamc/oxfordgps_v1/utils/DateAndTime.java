package com.tamc.oxfordgps_v1.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Usuario on 08/04/2015.
 */
public class DateAndTime {

    private Calendar cal;
    private Date date;
    private SimpleDateFormat df;

    public DateAndTime() {
        cal = new GregorianCalendar();
        date = new Date();
    }

    public String getDatePhone() {

        date = cal.getTime();
        df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }

    public String getHourPhone() {
        df = new SimpleDateFormat("HH-mm-ss");
        return df.format(date.getTime());
    }

    public int getHour(){
        return date.getHours();
    }

    public  int getMinute(){
        return  date.getMinutes();
    }

}
