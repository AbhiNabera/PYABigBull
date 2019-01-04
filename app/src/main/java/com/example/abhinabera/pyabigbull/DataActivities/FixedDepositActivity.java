package com.example.abhinabera.pyabigbull.DataActivities;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhinabera.pyabigbull.FDPurchaseActivity;
import com.example.abhinabera.pyabigbull.PurchaseActivity;
import com.example.abhinabera.pyabigbull.Api.RetrofitClient;
import com.example.abhinabera.pyabigbull.Api.Utility;
import com.example.abhinabera.pyabigbull.Dialog.ProgressDialog;
import com.example.abhinabera.pyabigbull.JsonObjectFormatter;
import com.example.abhinabera.pyabigbull.R;
import com.example.abhinabera.pyabigbull.SellActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        invest = (Button) findViewById(R.id.invest);

        custom_font = ResourcesCompat.getFont(this, R.font.hammersmithone);

        changeToolbarFont(fixedDepositToolbar, this);

        invest = (Button) findViewById(R.id.invest);
        invest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FixedDepositActivity.this, FDPurchaseActivity.class);
                i.putExtra("type", "FIXED DEPOSIT");
                //i.putExtra("id", purchaseId);
                //i.putExtra("name", id);
                startActivity(i);
                overridePendingTransition(R.anim.enter, R.anim.exit);
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
