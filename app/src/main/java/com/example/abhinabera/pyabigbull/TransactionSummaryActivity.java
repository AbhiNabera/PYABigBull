package com.example.abhinabera.pyabigbull;

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
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TransactionSummaryActivity extends AppCompatActivity {

    android.support.v7.widget.Toolbar purchaseToolbar;
    Typeface custom_font;
    TextView availableBalance, companyName, currentStockPrice, totalInvestment, transactionCharges, totalCost,
            accountBalance, timeout, txn_id;
    TextView numberStocks/*, investAmt*/;
    Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_transaction_summary);
        getSupportActionBar().hide();

        purchaseToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.purchaseToolbar);
        purchaseToolbar.setTitle("Trnsaction summary");

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
                finish();
                overridePendingTransition(R.anim.enter1, R.anim.exit1);
            }
        });

        if(!getIntent().getBooleanExtra("success",false)) {

            timeout.setVisibility(View.VISIBLE);

        }else {

            JsonParser parser = new JsonParser();
            JsonObject json = (JsonObject) parser.parse(getIntent().getStringExtra("data"));
            txn_id.setText(""+json.get("txn_id").getAsString());
            availableBalance.setText(""+json.get("Account").getAsJsonObject().get("avail_balance").getAsString());
            totalInvestment.setText(""+json.get("Account").getAsJsonObject().get("investment").getAsString());
            companyName.setText(""+json.get("transaction").getAsJsonObject().get("name").getAsString());
            currentStockPrice.setText(""+json.get("txn_summary").getAsJsonObject().get("buy_price").getAsString());
            numberStocks.setText(""+json.get("txn_summary").getAsJsonObject().get("qty").getAsString());
            //investAmt.setText(""+json.get("txn_summary").getAsJsonObject().get("invest_amt").getAsString());
            transactionCharges.setText(""+json.get("txn_summary").getAsJsonObject().get("txn_amt").getAsString());
            totalCost.setText(""+json.get("txn_summary").getAsJsonObject().get("total_amount").getAsString());
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
