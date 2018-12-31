package com.example.abhinabera.pyabigbull.UserActivities.TransactionsHistory;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.abhinabera.pyabigbull.R;

import org.w3c.dom.Text;

public class TransactionsHistoryIndi extends AppCompatActivity {

    Toolbar historyIndiToolbar;
    Typeface custom_font;
    TextView txnID, investment, name, price, quantity, transCharge, totalCost;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_transactions_history_indi);
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

        txnID = (TextView) findViewById(R.id.historyTxnId);
        investment = (TextView) findViewById(R.id.historyTotalInvestment);
        name = (TextView) findViewById(R.id.historyCompanyName);
        price = (TextView) findViewById(R.id.historyCurrentStockPrice);
        quantity = (TextView) findViewById(R.id.historyQuantity);
        transCharge = (TextView) findViewById(R.id.historyTransactionCharges);
        totalCost = (TextView) findViewById(R.id.historyTotalCost);


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
