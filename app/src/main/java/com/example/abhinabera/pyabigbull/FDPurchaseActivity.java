package com.example.abhinabera.pyabigbull;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.example.abhinabera.pyabigbull.Api.RetrofitClient;
import com.google.gson.JsonObject;

import org.w3c.dom.Text;

public class FDPurchaseActivity extends AppCompatActivity {

    Toolbar fdpurchaseToolbar;
    Typeface custom_font;
    TextView availableBalance, totalInvestment, accBalance, timeout;
    EditText multiplier;
    Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_fdpurchase);
        getSupportActionBar().hide();

        
        fdpurchaseToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.fdpurchaseToolbar);
        fdpurchaseToolbar.setTitle("FIXED DEPOSIT");

        availableBalance = (TextView) findViewById(R.id.availableBalance);
        totalInvestment = (TextView)  findViewById(R.id.totalInvestment);
        accBalance = (TextView) findViewById(R.id.accountBalance);


        multiplier = (EditText) findViewById(R.id.multiplier);
        confirm = (Button) findViewById(R.id.confirmButton);

        timeout = (TextView) findViewById(R.id.timeout);

        fdpurchaseToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        fdpurchaseToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        custom_font = ResourcesCompat.getFont(this, R.font.hammersmithone);

        changeToolbarFont(fdpurchaseToolbar, this);

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
