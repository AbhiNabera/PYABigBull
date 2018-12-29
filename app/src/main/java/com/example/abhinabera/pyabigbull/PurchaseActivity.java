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

import com.example.abhinabera.pyabigbull.Api.ApiInterface;
import com.example.abhinabera.pyabigbull.Api.RetrofitClient;
import com.google.gson.JsonObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PurchaseActivity extends AppCompatActivity {

    android.support.v7.widget.Toolbar purchaseToolbar;
    Typeface custom_font;
    TextView availableBalance, companyName, currentStockPrice, totalInvestment, transactionCharges, totalCost, accountBalance;
    EditText numberStocks;
    Button confirm;

    private ApiInterface apiInterface;

    private String DATA_URL;

    private String type;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_purchase);
        getSupportActionBar().hide();

        type = getIntent().getStringExtra("type");
        id = getIntent().getStringExtra("id");
        
        apiInterface = new RetrofitClient().getCurrencyInterface();

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

    public void getCurrentQuote(String type, String id) {

        switch (type) {

            case "NIFTY" :
                DATA_URL = "jsonapi/stocks/overview&format=json&sc_id="+id+"&ex=N";
                getData(DATA_URL);
                break;

            case "COMMODITY" :
                DATA_URL = "jsonapi/commodity/top_commodity&ex=MCX&format=json";
                getData(DATA_URL);
                break;

            case "CURRENCY" :
                DATA_URL = "pricefeed/notapplicable/currencyspot/%24%24%3B"+ id;
                getCurrencyData(DATA_URL);
                break;
        }
    }

    public void getData(String url) {

        new RetrofitClient().getNifty50Interface().getData(url).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful()) {

                }else {
                    try {
                        Log.d("Purchase error", response.errorBody().string()+"");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void getCurrencyData(String url) {

        new RetrofitClient().getCurrencyInterface().getData(url).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful()) {

                }else {
                    try {
                        Log.d("Purchase error", response.errorBody().string()+"");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
