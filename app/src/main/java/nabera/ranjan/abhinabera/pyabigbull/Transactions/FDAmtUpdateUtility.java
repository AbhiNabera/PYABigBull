package nabera.ranjan.abhinabera.pyabigbull.Transactions;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import nabera.ranjan.abhinabera.pyabigbull.Api.RetrofitClient;
import nabera.ranjan.abhinabera.pyabigbull.Api.Utility;
import nabera.ranjan.abhinabera.pyabigbull.Dialog.ProgressDialog;
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

    FragmentActivity activity;

    double TOTAL_AMOUNT = 0;

    ProgressDialog progressDialog;

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

        double avail_bal  = object.getAsJsonObject("data").get("avail_balance").getAsDouble();
        double change = object.getAsJsonObject("data").get("change").getAsDouble();
        double pchange = object.getAsJsonObject("data").get("percentchange").getAsDouble();
        double shares_price = object.getAsJsonObject("data").get("shares_price").getAsDouble();
        double start_balance = object.getAsJsonObject("data").get("start_balance").getAsDouble();

        double SI_CHANGE = 0;
        //double TOTAL_AMOUNT = 0;

        try {
            JsonObject fd_ref = object.getAsJsonObject("data")
                    //.getAsJsonObject("Account")
                    .getAsJsonObject("stocks_list")
                    .getAsJsonObject("bought_items")
                    .getAsJsonObject("fixed_deposit");

            current_timestamp = Calendar.getInstance().getTime().getTime() /*+ 20*24*60*60*1000*/;

            Set<Map.Entry<String, JsonElement>> entrySet = fd_ref.entrySet();

            for(Map.Entry<String, JsonElement> entry: entrySet) {

                double prevcurrent_val = entry.getValue().getAsJsonObject().get("current_value").getAsDouble();

                Log.d("prev_current_val", ""+prevcurrent_val);

                starttime = entry.getValue().getAsJsonObject().get("starttime").getAsLong();
                timestamp = entry.getValue().getAsJsonObject().get("timestamp").getAsLong();
                lastupdate = entry.getValue().getAsJsonObject().get("lastupdate").getAsLong();
                nextupdate = entry.getValue().getAsJsonObject().get("nextupdate").getAsLong();
                firstupdate = entry.getValue().getAsJsonObject().get("firstupdate").getAsLong();

                double SI = getSimpleInterest(getTotalDayCount(current_timestamp, starttime), entry.getValue());
                double current_value = entry.getValue().getAsJsonObject().get("investment").getAsDouble() + SI;

                Log.d("current_val", ""+current_value);

                SI_CHANGE += (current_value - prevcurrent_val);

                Log.d("SI_CHANGE", ""+SI_CHANGE);

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

            //no change in si: return null
            if(SI_CHANGE <= 1 ) return null;

            avail_bal += SI_CHANGE;
            change += SI_CHANGE;

            pchange = ((avail_bal + shares_price - start_balance) / start_balance ) * 100;

            Log.d("values", "avail: " + avail_bal + " : change : "+ change + ": pchange :" + pchange);

            object.getAsJsonObject("data").addProperty("avail_balance", avail_bal + "");
            object.getAsJsonObject("data").addProperty("change", change + "");
            object.getAsJsonObject("data").addProperty("percentchange", pchange + "");

            JsonObject data = new JsonObject();
            data.addProperty("phoneNumber", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().substring(3));
            data.add("Account", object.getAsJsonObject("data"));

            return data;

        }catch (NullPointerException e) {
            e.printStackTrace();

            //JsonObject data = new JsonObject();
            //data.addProperty("phoneNumber", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().substring(3));
            //data.add("Account", object.getAsJsonObject("data"));

            return null;
        }
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

    public void executeTransaction(JsonObject object, FragmentActivity activity, TaskListener taskListener) {

        if(object!=null) {

            this.activity = activity;

            try {
                progressDialog = new Utility().showFragmentDialog("Please wait for fd updation to complete.", activity);
                progressDialog.setCancelable(false);

                new RetrofitClient().getInterface().performTransaction(object).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                        if (response.isSuccessful()) {
                            Log.d("data", "" + response.body());
                            //TODO: go to summary
                            pushDataInSP(activity);
                            progressDialog.dismiss();
                            taskListener.onComplete();
                        } else {
                            Toast.makeText(activity, "error occured while updating", Toast.LENGTH_SHORT).show();
                            try {
                                Log.d("txn error", "" + response.errorBody().string());
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

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            pushDataInSP(activity);
            taskListener.onComplete();
        }
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
