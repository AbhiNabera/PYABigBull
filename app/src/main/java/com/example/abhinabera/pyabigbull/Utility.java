package com.example.abhinabera.pyabigbull;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.abhinabera.pyabigbull.Dialog.MyDialog;
import com.example.abhinabera.pyabigbull.Dialog.ProgressDialog;

public class Utility {

    //EXPIRY(MCX): GOLD-> 2 MONTHS, SILVER-> 3 MONTHS, CRUDE OIL -> 1 MONTH
    public static String URL = "https://us-central1-pyabigbull.cloudfunctions.net";

    public static String MONEY_CONTROL_NIFTY50_URL = "http://appfeeds.moneycontrol.com";

    public static String MONEY_CONTROL_CURRENCY_URL = "https://priceapi.moneycontrol.com";

    public static String MyPREF = "PAYBigBullPref";

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

}
