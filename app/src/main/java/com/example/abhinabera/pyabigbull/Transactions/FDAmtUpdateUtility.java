package com.example.abhinabera.pyabigbull.Transactions;

import android.util.Log;

import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by AVINASH on 1/4/2019.
 */

public class FDAmtUpdateUtility {

    public JsonObject getUpdatedAmount(JsonObject object) {

        return object;
    }

    public long getNextUpdae() {

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

        String next_time = dateFormat.format(tomorrow) + " 00:00:00";

        dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

        try {
            tomorrow = dateFormat.parse(next_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.d("timestamp", tomorrow.getTime() + " : " + Calendar.DAY_OF_YEAR);

        return tomorrow.getTime();
    }
}
