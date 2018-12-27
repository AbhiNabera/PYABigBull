package com.example.abhinabera.pyabigbull.DataActivities;

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
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.abhinabera.pyabigbull.R;

import java.util.ArrayList;
import java.util.List;

public class CommodityActivity extends AppCompatActivity {

    android.support.v7.widget.Toolbar commodityToolbar;
    Spinner commodityDateSpinner;
    Typeface custom_font;
    TextView bidPrice, offerPrice, openInterest, OIChange, highPrice, lowPrice, open, previousClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_commodity);
        getSupportActionBar().hide();

        commodityToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.commodityToolbar);
        Intent i = getIntent();
        commodityToolbar.setTitle(i.getExtras().getString("cardName"));

        bidPrice = (TextView) findViewById(R.id.bidPriceTextView);
        offerPrice = (TextView) findViewById(R.id.offerPriceTextView);
        openInterest = (TextView) findViewById(R.id.openInterestTextView);
        OIChange = (TextView) findViewById(R.id.OIChangeTextView);
        highPrice = (TextView) findViewById(R.id.highPriceTextView);
        lowPrice = (TextView) findViewById(R.id.lowPriceTextView);
        open = (TextView) findViewById(R.id.openTextView);
        previousClose = (TextView) findViewById(R.id.previousCloseTextView);
        commodityDateSpinner = (Spinner) findViewById(R.id.commoditySpinner);

        List<String> list = new ArrayList<String>();
        list.add("Select Date");
        list.add("Date 1");
        list.add("Date 2");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, list);
        commodityDateSpinner.setAdapter(adapter);

        commodityToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        commodityToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        custom_font = ResourcesCompat.getFont(this, R.font.hammersmithone);

        changeToolbarFont(commodityToolbar, this);

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
