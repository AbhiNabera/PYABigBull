package com.example.abhinabera.pyabigbull.UserActivities.TransactionsHistory;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.abhinabera.pyabigbull.Api.Utility;
import com.example.abhinabera.pyabigbull.R;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.w3c.dom.Text;

public class TransactionsHistoryIndiSale extends AppCompatActivity {

    android.support.v7.widget.Toolbar historyIndiToolbar;
    Typeface custom_font;
    TextView txnID, name, bprice, sqty, sprice, netReturn, gainOrLoss, gainORLossPer, txnCharges, netGainOrLoss, netGainOrLossPer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_transactions_history_indi_sale);
        getSupportActionBar().hide();

        historyIndiToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.historyIndiToolbar);

        historyIndiToolbar.setTitle("SUMMARY");

        historyIndiToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        historyIndiToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        custom_font = ResourcesCompat.getFont(this, R.font.hammersmithone);

        changeToolbarFont(historyIndiToolbar, this);

        JsonParser parser = new JsonParser();
        JsonObject transIndHis = (JsonObject) parser.parse(getIntent().getStringExtra("transactionHistory"));

        JsonObject transIndHisSum = transIndHis.get("txn_summary").getAsJsonObject();

        txnID = (TextView) findViewById(R.id.historyTxnId);
        name = (TextView) findViewById(R.id.historyCompanyName);
        bprice = (TextView) findViewById(R.id.historyCurrentStockPrice);
        sqty = (TextView) findViewById(R.id.historyNumberStocks);
        sprice = (TextView) findViewById(R.id.historySellPrice);
        netReturn = (TextView) findViewById(R.id.historyNetReturn);
        gainOrLoss = (TextView) findViewById(R.id.historyGainOrLoss);
        gainORLossPer = (TextView) findViewById(R.id.historyGainOrLossPercent);
        txnCharges = (TextView) findViewById(R.id.historyTransactionCharges);
        netGainOrLoss = (TextView) findViewById(R.id.historyNetGainOrLoss);
        netGainOrLossPer = (TextView) findViewById(R.id.historyGainOrLossPercent);


        txnID.setText(transIndHisSum.get("txn_id").getAsString());
        name.setText(transIndHisSum.get("name").getAsString());
        bprice.setText(new Utility().getRoundoffData(transIndHisSum.get("buy_price").getAsString()));
        sqty.setText(transIndHisSum.get("sell_qty").getAsString());
        sprice.setText(new Utility().getRoundoffData(transIndHisSum.get("sell_price").getAsString()));
        netReturn.setText(new Utility().getRoundoffData(transIndHisSum.get("net_return").getAsString()));
        if (transIndHisSum.get("return_change").getAsString().startsWith("-")){
            gainOrLoss.setTextColor(getResources().getColor(R.color.red));
            gainORLossPer.setTextColor(getResources().getColor(R.color.red));
        }
        gainOrLoss.setText(new Utility().getRoundoffData(transIndHisSum.get("return_change").getAsString()));
        gainORLossPer.setText(new Utility().getRoundoffData(transIndHisSum.get("return_percentchange").getAsString()));
        if(transIndHisSum.get("change").getAsString().startsWith("-")){
            netGainOrLoss.setTextColor(getResources().getColor(R.color.red));
            netGainOrLossPer.setTextColor(getResources().getColor(R.color.red));
        }
        netGainOrLoss.setText(new Utility().getRoundoffData(transIndHisSum.get("change").getAsString()));
        netGainOrLossPer.setText(new Utility().getRoundoffData(transIndHisSum.get("percentchange").getAsString()));
        txnCharges.setText(new Utility().getRoundoffData(transIndHisSum.get("txn_amt").getAsString()));

    }

    public void changeToolbarFont(android.support.v7.widget.Toolbar toolbar, Activity context) {
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
