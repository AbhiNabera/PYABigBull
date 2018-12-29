package com.example.abhinabera.pyabigbull.Api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.abhinabera.pyabigbull.Dialog.MyDialog;
import com.example.abhinabera.pyabigbull.Dialog.ProgressDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Utility {

    public static String URL = "https://us-central1-pyabigbull.cloudfunctions.net";

    public static String MONEY_CONTROL_NIFTY50_URL = "https://appfeeds.moneycontrol.com";

    public static String MONEY_CONTROL_CURRENCY_URL = "https://priceapi.moneycontrol.com";

    public static String MyPREF = "PAYBigBullPref";


    public String getCommodityURL(String id) {
        URL = "jsonapi/commodity/details&format=json&ex=MCX&symbol=" + id;
        return URL;
    }

    public String getCommodityExpiryURL(String id, String expiry) {
        URL = "jsonapi/commodity/details&format=json&ex=MCX&symbol=" + id + "&expdt=" + expiry;
        return URL;
    }

    public String getStockIndividualUrl(String id) {
        return "jsonapi/stocks/overview&format=json&sc_id="+id+"&ex=N";
    }

    public String getNift50GraphURL(String range) {
        return "jsonapi/market/graph&format=&ind_id=9&range=" + range + "&type=area";
    }

    public String getNift50IndvGraphURL(String range, String id) {
        return "jsonapi/stocks/graph&format=json&range="+ range +"&type=area&ex=&sc_id=" +id;
    }

    public String getCommodityGraphURL(String type, String symbol, String expdt) {
        return "jsonapi/commodity/chart&format=json&type="+ type +"&symbol="+ symbol +"&expdt="+expdt+"&ex=MCX";
    }

    public ProgressDialog showDialog(String msg, AppCompatActivity activity) {
        ProgressDialog progressDialog = new ProgressDialog();

        Bundle bundle = new Bundle();
        bundle.putString("dialog_msg", msg);
        progressDialog.setArguments(bundle);
        try {
            progressDialog.show(activity.getSupportFragmentManager(), "1234");
        }catch (IllegalStateException e) {
            e.printStackTrace();
        }

        return progressDialog;
    }

    public void showDialog(final String header, String msg, AppCompatActivity activity){
        final MyDialog myDialog = new MyDialog();

        myDialog.setOptionSelectListener(new MyDialog.OptionSelectListener() {
            @Override
            public void onPositive() {


                myDialog.dismiss();
            }

            @Override
            public void onNegative() {
                myDialog.dismiss();
            }

            @Override
            public void onOption(String args) {

            }
        });

        Bundle bundle = new Bundle();
        bundle.putString("dialog_header", header);
        bundle.putString("dialog_msg", msg);
        myDialog.setArguments(bundle);
        try {
            myDialog.show(activity.getSupportFragmentManager(), "1234");
        }catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public static boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

    public String getRoundoffData(String value) {
        String val = String.format("%.2f",( Float.parseFloat(value.replace(",",""))));
        return val;
    }

    public String getFormattedDate(String epoch) {
        Log.d("EPOCH TIME", ""+epoch);
        int offset = TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("IST"));
        return sdf.format(new Date(Long.parseLong(epoch.trim()) * 1000L + offset));
    }
}
