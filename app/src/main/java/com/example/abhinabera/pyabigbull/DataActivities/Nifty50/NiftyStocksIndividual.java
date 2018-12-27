package com.example.abhinabera.pyabigbull.DataActivities.Nifty50;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.abhinabera.pyabigbull.R;

public class NiftyStocksIndividual extends AppCompatActivity {

    android.support.v7.widget.Toolbar stocksIndiToolbar;
    Typeface custom_font;
    TextView bidPrice, offerPrice, prevClose, openPrice, vwap, todaysLow, todaysHigh, wkLow, wkHigh, lPriceBand, uPriceBand;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_nifty_stocks_individual);
        getSupportActionBar().hide();

        bidPrice = (TextView) findViewById(R.id.bidPriceTextView);
        offerPrice = (TextView) findViewById(R.id.offerPriceTextView);
        prevClose = (TextView) findViewById(R.id.prevCloseTextView);
        openPrice = (TextView) findViewById(R.id.openPriceTextView);
        vwap = (TextView) findViewById(R.id.VWAPTextView);
        todaysLow = (TextView) findViewById(R.id.todaysLowTextView);
        todaysHigh = (TextView) findViewById(R.id.todaysHighTextView);
        wkLow = (TextView) findViewById(R.id.wkLowTextView);
        wkHigh = (TextView) findViewById(R.id.wkHighTextView);
        lPriceBand = (TextView) findViewById(R.id.lPriceBandTextView);
        uPriceBand = (TextView) findViewById(R.id.uPriceBandTextView);

        stocksIndiToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.stocksIndiToolbar);
        Intent i = getIntent();
        stocksIndiToolbar.setTitle(i.getExtras().getString("companyName"));

        stocksIndiToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        stocksIndiToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        custom_font = ResourcesCompat.getFont(this, R.font.hammersmithone);

        changeToolbarFont(stocksIndiToolbar, this);
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
