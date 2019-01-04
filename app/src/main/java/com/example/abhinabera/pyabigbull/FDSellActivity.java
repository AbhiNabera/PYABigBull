package com.example.abhinabera.pyabigbull;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.abhinabera.pyabigbull.Api.ApiInterface;
import com.example.abhinabera.pyabigbull.Api.RetrofitClient;
import com.example.abhinabera.pyabigbull.Dialog.ProgressDialog;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class FDSellActivity extends AppCompatActivity {

    Toolbar purchaseToolbar;
    Typeface custom_font;
    TextView timeouttv, availBal, totalInvestment, currentValue, investment, transactionCharges, netReturn, profit, profitPer, accBal, netProfit, netProfitPer;
    Button confirm;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_fdsell);
        getSupportActionBar().hide();

        purchaseToolbar = (Toolbar) findViewById(R.id.purchaseToolbar);
        purchaseToolbar.setTitle("BREAK FD");


        confirm = (Button) findViewById(R.id.confirmButton);

        availBal = (TextView) findViewById(R.id.availableBalance);
        totalInvestment = (TextView) findViewById(R.id.totalInvestment);
        currentValue = (TextView) findViewById(R.id.currentStockPrice);
        investment = (TextView) findViewById(R.id.investment);
        transactionCharges = (TextView) findViewById(R.id.transactionCharges);
        netReturn = (TextView) findViewById(R.id.returns);
        profit = (TextView) findViewById(R.id.stockchange);
        profitPer = (TextView) findViewById(R.id.netstockchange);
        accBal = (TextView) findViewById(R.id.accountBalance);
        netProfit = (TextView) findViewById(R.id.netstockchange);
        netProfitPer = (TextView) findViewById(R.id.percentnetstockchange);
        timeouttv = (TextView) findViewById(R.id.timeout);

        purchaseToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        purchaseToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        custom_font = ResourcesCompat.getFont(this, R.font.hammersmithone);

        changeToolbarFont(purchaseToolbar, this);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });


    }

    public void changeToolbarFont(Toolbar toolbar, Activity context) {
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView tv = (TextView) view;
                if (tv.getText().equals(toolbar.getTitle())) {
                    applyFont(tv, context);
                    break;
                }
            }
        }
    }

    public void applyFont(TextView tv, Activity context) {
        tv.setTypeface(custom_font);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_OK);
        finish();
        overridePendingTransition(R.anim.enter1, R.anim.exit1);
    }
}
