package com.example.abhinabera.pyabigbull;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    CardView niftyCard, goldCard, silverCard, crudeoilCard, dollarCard, euroCard, poundCard;
    

     TextView niftyDate, goldDate, silverDate, crudeoilDate, dollarDate, euroDate, poundDate, 
             nifty50Rate, goldRate, silverRate, crudeoilRate, dollarRate, euroRate, poundRate, 
             nifty50BoxRate, goldBoxRate, silverBoxRate, crudeoilBoxRate, dollarBoxRate, euroBoxRate, poundBoxRate, 
             nifty50BoxPercent, goldBoxPercent, silverBoxPercent, crudeoilBoxPercent, dollarBoxPercent, euroBoxPercent, poundBoxPercent;

    LinearLayout nifty50Box, goldBox, silverBox, crudeoilBox, dollarBox, euroBox, poundBox;
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        
        niftyCard = (CardView) findViewById(R.id.niftyCard);
        goldCard = (CardView) findViewById(R.id.goldCard);
        silverCard = (CardView) findViewById(R.id.silverCard);
        crudeoilCard = (CardView) findViewById(R.id.crudeoilCard);
        dollarCard = (CardView) findViewById(R.id.dollarCard);
        euroCard = (CardView) findViewById(R.id.euroCard);
        poundCard = (CardView) findViewById(R.id.poundCard);

        niftyDate = (TextView) findViewById(R.id.niftyDate);
        goldDate = (TextView) findViewById(R.id.goldDate);
        silverDate = (TextView) findViewById(R.id.silverDate);
        crudeoilDate = (TextView) findViewById(R.id.crudeoilDate);
        dollarDate = (TextView) findViewById(R.id.dollarDate);
        euroDate = (TextView) findViewById(R.id.euroDate);
        poundDate = (TextView) findViewById(R.id.poundDate);
        nifty50Rate = (TextView) findViewById(R.id.nifty50Rate);
        goldRate = (TextView) findViewById(R.id.goldRate);
        silverRate = (TextView) findViewById(R.id.silverRate);
        crudeoilRate = (TextView) findViewById(R.id.crudeoilRate);
        dollarRate = (TextView) findViewById(R.id.dollarRate);
        euroRate = (TextView) findViewById(R.id.euroRate);
        poundRate = (TextView) findViewById(R.id.poundRate);
        nifty50BoxRate = (TextView) findViewById(R.id.nifty50BoxRate);
        goldBoxRate = (TextView) findViewById(R.id.goldBoxRate);
        silverBoxRate = (TextView) findViewById(R.id.silverBoxRate);
        crudeoilBoxRate = (TextView) findViewById(R.id.crudeoilBoxRate);
        dollarBoxRate = (TextView) findViewById(R.id.dollarBoxRate);
        euroBoxRate = (TextView) findViewById(R.id.euroBoxRate);
        poundBoxRate = (TextView) findViewById(R.id.poundBoxRate);
        nifty50BoxPercent = (TextView) findViewById(R.id.nifty50BoxPercent);
        goldBoxPercent = (TextView) findViewById(R.id.goldBoxPercent);
        silverBoxPercent = (TextView) findViewById(R.id.silverBoxPercent);
        crudeoilBoxPercent = (TextView) findViewById(R.id.crudeoilBoxPercent);
        dollarBoxPercent = (TextView) findViewById(R.id.dollarBoxPercent);
        euroBoxPercent = (TextView) findViewById(R.id.euroBoxPercent);
        poundBoxPercent = (TextView) findViewById(R.id.poundBoxPercent);

        nifty50Box = (LinearLayout) findViewById(R.id.nifty50Box);
        goldBox = (LinearLayout) findViewById(R.id.goldBox);
        silverBox = (LinearLayout) findViewById(R.id.silverBox);
        crudeoilBox = (LinearLayout) findViewById(R.id.crudeoilBox);
        dollarBox = (LinearLayout) findViewById(R.id.dollarBox);
        euroBox = (LinearLayout) findViewById(R.id.euroBox);
        poundBox = (LinearLayout) findViewById(R.id.poundBox);

        niftyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, NiftyActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        goldCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, GoldActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        silverCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, SilverActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        crudeoilCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, CrudeoilActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        dollarCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, DollarActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        euroCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, EuroActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        poundCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, PoundActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

    }
}
