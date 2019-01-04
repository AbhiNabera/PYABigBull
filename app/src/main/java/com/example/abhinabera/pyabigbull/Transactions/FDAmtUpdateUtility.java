package com.example.abhinabera.pyabigbull.Transactions;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.abhinabera.pyabigbull.Api.RetrofitClient;
import com.example.abhinabera.pyabigbull.Api.Utility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by AVINASH on 1/4/2019.
 */

public class FDAmtUpdateUtility {

    Activity activity;

    double TOTAL_AMOUNT = 0;

    long timestamp;
    long lastupdate;
    long nextupdate;
    long firstupdate;
    long starttime;

    long current_timestamp;

    public interface TaskListener{
        public void onComplete();
    }

    public JsonObject getUpdatedAmount(JsonObject object) {

        JsonObjectFormatter jsonObjectFormatter = new JsonObjectFormatter(object.getAsJsonObject("data"));

        try {
            JsonObject fd_ref = object.getAsJsonObject("data")
                    //.getAsJsonObject("Account")
                    .getAsJsonObject("stocks_list")
                    .getAsJsonObject("bought_items")
                    .getAsJsonObject("fixed_deposit");

            current_timestamp = Calendar.getInstance().getTime().getTime() /*+ 20*24*60*60*1000*/;

            Set<Map.Entry<String, JsonElement>> entrySet = fd_ref.entrySet();

            for(Map.Entry<String, JsonElement> entry: entrySet) {

                starttime = entry.getValue().getAsJsonObject().get("starttime").getAsLong();
                timestamp = entry.getValue().getAsJsonObject().get("timestamp").getAsLong();
                lastupdate = entry.getValue().getAsJsonObject().get("lastupdate").getAsLong();
                nextupdate = entry.getValue().getAsJsonObject().get("nextupdate").getAsLong();
                firstupdate = entry.getValue().getAsJsonObject().get("firstupdate").getAsLong();

                double SI = getSimpleInterest(getTotalDayCount(current_timestamp, starttime), entry.getValue());
                double current_value = entry.getValue().getAsJsonObject().get("investment").getAsDouble() + SI;

                lastupdate = getLastUpdate();
                nextupdate = getNextUpdate();

                jsonObjectFormatter.child("stocks_list").child("bought_items").child("fixed_deposit")
                        .child(entry.getKey()).pushValue("lastupdate", lastupdate+"");

                jsonObjectFormatter.child("stocks_list").child("bought_items").child("fixed_deposit")
                        .child(entry.getKey()).pushValue("nextupdate", nextupdate+"");

                jsonObjectFormatter.child("stocks_list").child("bought_items").child("fixed_deposit")
                        .child(entry.getKey()).pushValue("current_value", current_value+"");

                TOTAL_AMOUNT += current_value;

            }

            JsonObject data = new JsonObject();
            data.addProperty("phoneNumber", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().substring(3));
            data.add("Account", object.getAsJsonObject("data"));

            return data;

        }catch (NullPointerException e) {
            e.printStackTrace();

        }

        return null;
    }

    public double getSimpleInterest(long noOfDays, JsonElement object) {

        double SI_F7 = ( object.getAsJsonObject().get("investment")
                .getAsDouble() * (Utility.INTEREST_RATE_FIRST/365) * noOfDays) / 100;

        double SI_S7 = ( object.getAsJsonObject().get("investment")
                .getAsDouble() * (Utility.INTEREST_RATE_SECOND/365) * ((noOfDays-7)>=0?(noOfDays-7):0)) / 100;

        double SI_T15 = ( object.getAsJsonObject().get("investment")
                .getAsDouble() * (Utility.INTEREST_RATE_THIRD/365) * ((noOfDays-15)>=0?(noOfDays-15):0)) / 100;

        Log.d("INTEREST RATES", ""+SI_F7 + " : " + SI_S7 + " : " + SI_T15);

        return (SI_F7 + SI_S7 + SI_T15);
    }

    public long getTotalDayCount(long starttime, long endtime) {

        long msdiff = starttime - endtime;

        if(msdiff < 0) return 0;

        long daysDiff = TimeUnit.DAYS.convert(msdiff, TimeUnit.MILLISECONDS);

        Log.d("days diff", ""+daysDiff+ ":"+ msdiff);

        return daysDiff;
    }

    public long getNextUpdate() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(current_timestamp);
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

    public long getLastUpdate() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(current_timestamp);
        Date today = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

        String next_time = dateFormat.format(today) + " 00:00:00";

        dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

        try {
            today = dateFormat.parse(next_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.d("timestamp", today.getTime() + " : " + Calendar.DAY_OF_YEAR);

        return today.getTime();
    }

    public void executeTransaction(JsonObject object, Activity activity, TaskListener taskListener) {

        this.activity = activity;

        new RetrofitClient().getInterface().performTransaction(object).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if(response.isSuccessful()) {
                    Log.d("data", ""+response.body());
                    //TODO: go to summary
                    pushDataInSP(activity);
                    taskListener.onComplete();
                }else {
                    Toast.makeText(activity, "error occured while updating", Toast.LENGTH_SHORT).show();
                    try {
                        Log.d("txn error", ""+response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(activity, "error occured while updating", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void pushDataInSP(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(Utility.MyPREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("nextupdate", nextupdate+"");
        editor.putString("total_investment", TOTAL_AMOUNT + "");
        editor.apply();
        editor.commit();
    }
}
