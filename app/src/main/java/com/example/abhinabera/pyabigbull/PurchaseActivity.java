package com.example.abhinabera.pyabigbull;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PurchaseActivity extends AppCompatActivity {

    android.support.v7.widget.Toolbar purchaseToolbar;
    Typeface custom_font;
    TextView availableBalance, companyName, currentStockPrice, totalInvestment, transactionCharges, totalCost, accountBalance;
    EditText numberStocks;
    Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_purchase);
        getSupportActionBar().hide();

        purchaseToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.purchaseToolbar);
        purchaseToolbar.setTitle("BUY STOCKS");

        availableBalance = (TextView) findViewById(R.id.availableBalance);
        companyName = (TextView) findViewById(R.id.companyName);
        currentStockPrice = (TextView) findViewById(R.id.currentStockPrice);
        totalInvestment = (TextView) findViewById(R.id.totalInvestment);
        transactionCharges = (TextView) findViewById(R.id.transactionCharges);
        totalCost = (TextView) findViewById(R.id.totalCost);
        accountBalance = (TextView) findViewById(R.id.accountBalance);

        numberStocks = (EditText) findViewById(R.id.numberStocks);
        confirm = (Button) findViewById(R.id.confirmButton);

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
        finish();
        overridePendingTransition(R.anim.enter1, R.anim.exit1);
    }
}
