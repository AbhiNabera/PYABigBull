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
import android.widget.Button;
import android.widget.TextView;

import com.example.abhinabera.pyabigbull.R;

public class FixedDepositActivity extends AppCompatActivity {

    android.support.v7.widget.Toolbar fixedDepositToolbar;
    Typeface custom_font;
    Button invest;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_fixed_deposit);
        getSupportActionBar().hide();
        
        fixedDepositToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.fixedDepositToolbar);
        Intent i = getIntent();
        fixedDepositToolbar.setTitle(i.getExtras().getString("cardName"));

        fixedDepositToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        fixedDepositToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        custom_font = ResourcesCompat.getFont(this, R.font.hammersmithone);

        changeToolbarFont(fixedDepositToolbar, this);

        invest.setOnClickListener(new View.OnClickListener() {
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
        //finish();
        //overridePendingTransition(R.anim.enter1, R.anim.exit1);
    }
}
