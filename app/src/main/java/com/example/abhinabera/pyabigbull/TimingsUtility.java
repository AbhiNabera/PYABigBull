package com.example.abhinabera.pyabigbull;

import android.widget.Toast;

import com.example.abhinabera.pyabigbull.Dashboard.MainActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimingsUtility {

    public static boolean indexTradeTimings(){
        String string1 = "09:15:00";
        Date time1 = null;
        try {
            time1 = new SimpleDateFormat("HH:mm:ss").parse(string1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(time1);

        String string2 = "15:30:00";
        Date time2 = null;
        try {
            time2 = new SimpleDateFormat("HH:mm:ss").parse(string2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(time2);
        calendar2.add(Calendar.DATE, 1);

        Date currentTime = Calendar.getInstance().getTime();
        Calendar calendar3 = Calendar.getInstance();
        calendar3.setTime(currentTime);
        calendar3.add(Calendar.DATE, 1);

        Date x = calendar3.getTime();
        if (x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
            return true;
        }
        else
            return false;
    }

    public static boolean commodityTradeTimings(){
        String string1 = "09:00:00";
        Date time1 = null;
        try {
            time1 = new SimpleDateFormat("HH:mm:ss").parse(string1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(time1);

        String string2 = "23:55:00";
        Date time2 = null;
        try {
            time2 = new SimpleDateFormat("HH:mm:ss").parse(string2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(time2);
        calendar2.add(Calendar.DATE, 1);

        Date currentTime = Calendar.getInstance().getTime();
        Calendar calendar3 = Calendar.getInstance();
        calendar3.setTime(currentTime);
        calendar3.add(Calendar.DATE, 1);

        Date x = calendar3.getTime();
        if (x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
            return true;
        }
        else
            return false;
    }

    public static boolean currencyTradeTimings(){
        String string1 = "09:00:00";
        Date time1 = null;
        try {
            time1 = new SimpleDateFormat("HH:mm:ss").parse(string1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(time1);

        String string2 = "17:00:00";
        Date time2 = null;
        try {
            time2 = new SimpleDateFormat("HH:mm:ss").parse(string2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(time2);
        calendar2.add(Calendar.DATE, 1);

        Date currentTime = Calendar.getInstance().getTime();
        Calendar calendar3 = Calendar.getInstance();
        calendar3.setTime(currentTime);
        calendar3.add(Calendar.DATE, 1);

        Date x = calendar3.getTime();
        if (x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
            return true;
        }
        else
            return false;
    }

    public static boolean tradingHolidays(){
       Calendar cal = Calendar.getInstance();
       if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
           return false;
       else
           return true;
    }

    //First five days only for registration
}
