package com.example.abhinabera.pyabigbull.Transactions;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.abhinabera.pyabigbull.Api.Utility;
import com.example.abhinabera.pyabigbull.R;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TransactionSummaryActivity extends AppCompatActivity {

    android.support.v7.widget.Toolbar purchaseToolbar;
    Typeface custom_font;
    TextView availableBalance, companyName, currentStockPrice, totalInvestment, transactionCharges, totalCost,
            accountBalance, timeout, txn_id;
    TextView numberStocks/*, investAmt*/;
    Button confirm;

    TextView availableBalancetv, companyNametv, currentStockPricetv, buyStockPicetv, investmenttv, totalInvestmenttv,
            transactionChargestv,
            returnstv, changeamounttv, perchangetv, netchangeamounttv, netpecentchangetv, accountBalancetv, timeouttv;
    LinearLayout txnLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if(getIntent().getStringExtra("type").equalsIgnoreCase("buy")) {
            setContentView(R.layout.activity_transaction_summary);
        }else if(getIntent().getStringExtra("type").equalsIgnoreCase("sell")) {
            setContentView(R.layout.activity_sell_summary);
        }

        getSupportActionBar().hide();

        purchaseToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.purchaseToolbar);
        purchaseToolbar.setTitle("Transaction Summary");

        purchaseToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        purchaseToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        custom_font = ResourcesCompat.getFont(this, R.font.hammersmithone);

        changeToolbarFont(purchaseToolbar, this);

        if(getIntent().getStringExtra("type").equalsIgnoreCase("buy")) {
            txn_id = (TextView) findViewById(R.id.txn_id);
            availableBalance = (TextView) findViewById(R.id.availableBalance);
            companyName = (TextView) findViewById(R.id.companyName);
            currentStockPrice = (TextView) findViewById(R.id.currentStockPrice);
            totalInvestment = (TextView) findViewById(R.id.totalInvestment);
            transactionCharges = (TextView) findViewById(R.id.transactionCharges);
            totalCost = (TextView) findViewById(R.id.totalCost);
            accountBalance = (TextView) findViewById(R.id.accountBalance);

            numberStocks = (TextView) findViewById(R.id.numberStocks);
            //investAmt = (TextView) findViewById(R.id.investAmt);
            confirm = (Button) findViewById(R.id.confirmButton);

            timeout = (TextView) findViewById(R.id.timeout);

        }else if(getIntent().getStringExtra("type").equalsIgnoreCase("sell")) {

            availableBalancetv = (TextView) findViewById(R.id.availableBalance);
            companyNametv = (TextView) findViewById(R.id.companyName);
            currentStockPricetv = (TextView) findViewById(R.id.currentStockPrice);
            totalInvestmenttv = (TextView) findViewById(R.id.totalInvestment);
            transactionChargestv = (TextView) findViewById(R.id.transactionCharges);
            returnstv = (TextView) findViewById(R.id.returns);
            accountBalancetv = (TextView) findViewById(R.id.accountBalance);
            buyStockPicetv = (TextView) findViewById(R.id.buyStockPrice);
            investmenttv = (TextView) findViewById(R.id.investment);
            changeamounttv = (TextView) findViewById(R.id.stockchange);
            perchangetv = (TextView) findViewById(R.id.percentstockchange);
            netchangeamounttv = (TextView) findViewById(R.id.netstockchange);
            netpecentchangetv = (TextView) findViewById(R.id.percentnetstockchange);
            txn_id = (TextView) findViewById(R.id.txn_id);

            txnLayout = (LinearLayout) findViewById(R.id.txnLayout);

            numberStocks = (TextView) findViewById(R.id.numberStocks);
            //investAmt = (EditText) findViewById(R.id.investAmt);
            confirm = (Button) findViewById(R.id.confirmButton);

            timeouttv = (TextView) findViewById(R.id.timeout);

            timeout = (TextView) findViewById(R.id.timeouttv);

            timeouttv.setVisibility(View.GONE);

            txnLayout.setVisibility(View.VISIBLE);
        }


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.enter1, R.anim.exit1);
            }
        });

        if(!getIntent().getBooleanExtra("success",false)) {

            timeout.setVisibility(View.VISIBLE);

        }else {

            JsonParser parser = new JsonParser();
            JsonObject json = (JsonObject) parser.parse(getIntent().getStringExtra("data"));

            if(getIntent().getStringExtra("type").equalsIgnoreCase("buy")) {

                JsonObject object = json.getAsJsonObject("Account")
                        .getAsJsonObject("stocks_list")
                        .getAsJsonObject("bought_items")
                        .getAsJsonObject(getIntent().getStringExtra("product_type"))
                        .getAsJsonObject(getIntent().getStringExtra("txn_id"));

                txn_id.setText(""+object.get("txn_id").getAsString());
                availableBalance.setText(""+new Utility().getRoundoffData(json.get("Account").getAsJsonObject().get("avail_balance").getAsString()));
                totalInvestment.setText(""+new Utility().getRoundoffData(json.getAsJsonObject("Account").get("investment").getAsString()));
                companyName.setText(""+object.get("name").getAsString());
                currentStockPrice.setText(""+new Utility().getRoundoffData(object.get("buy_price").getAsString()));
                numberStocks.setText(""+object.get("qty").getAsString());
                transactionCharges.setText(""+new Utility().getRoundoffData(object.get("txn_amt").getAsString()));
                totalCost.setText(""+new Utility().getRoundoffData(object.get("total_amount").getAsString()));

            } else if(getIntent().getStringExtra("type").equalsIgnoreCase("sell")){

                JsonObject object = json.getAsJsonObject("Account")
                        .getAsJsonObject("stocks_list")
                        .getAsJsonObject("sold_items")
                        .getAsJsonObject(getIntent().getStringExtra("product_type"))
                        .getAsJsonObject(getIntent().getStringExtra("txn_id"));

                txn_id.setText(""+object.get("txn_id").getAsString());
                availableBalancetv.setText(""+new Utility().getRoundoffData(json.get("Account").getAsJsonObject().get("avail_balance").getAsString()));
                totalInvestmenttv.setText(""+new Utility().getRoundoffData(json.getAsJsonObject("Account").get("investment").getAsString()));
                companyNametv.setText(""+object.get("name").getAsString());
                currentStockPricetv.setText(""+new Utility().getRoundoffData(object.get("sell_price").getAsString()));
                buyStockPicetv.setText(""+new Utility().getRoundoffData(object.get("buy_price").getAsString()));
                numberStocks.setText(""+object.get("sell_qty").getAsString());
                transactionChargestv.setText(""+new Utility().getRoundoffData(object.get("txn_amt").getAsString()));
                returnstv.setText(""+new Utility().getRoundoffData(object.get("net_return").getAsString()));
                changeamounttv.setText(""+new Utility().getRoundoffData(object.get("return_change").getAsString()));
                perchangetv.setText(""+new Utility().getRoundoffData(object.get("percentchange").getAsString()) + "%");
                netchangeamounttv.setText(""+new Utility().getRoundoffData(object.get("change").getAsString()) + "");
                netpecentchangetv.setText(""+new Utility().getRoundoffData(object.get("percentchange").getAsString()) + "%");
            }
        }
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
        finish();
        overridePendingTransition(R.anim.enter1, R.anim.exit1);
    }
}
