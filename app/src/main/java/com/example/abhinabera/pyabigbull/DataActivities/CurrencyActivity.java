package com.example.abhinabera.pyabigbull.DataActivities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.abhinabera.pyabigbull.Api.ApiInterface;
import com.example.abhinabera.pyabigbull.Api.RetrofitClient;
import com.example.abhinabera.pyabigbull.DataActivities.Nifty50.NiftyStocksIndividual;
import com.example.abhinabera.pyabigbull.PurchaseActivity;
import com.example.abhinabera.pyabigbull.R;
import com.example.abhinabera.pyabigbull.Api.Utility;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrencyActivity extends AppCompatActivity {

    SwipeRefreshLayout refreshLayout;

    TextView currentPrice, prevClose, todaysLow, todaysHigh, lastUpdate, lastChange, lastPrice;
    android.support.v7.widget.Toolbar currencyToolbar;
    Typeface custom_font;
    Button buyStocks;

    private String id;

    private String purchaseId;

    private ApiInterface apiInterface;

    private JsonObject currencyObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_currency);
        getSupportActionBar().hide();

        currencyToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.currencyToolbar);
        Intent i = getIntent();
        currencyToolbar.setTitle(i.getExtras().getString("cardName"));

        apiInterface = new RetrofitClient().getCurrencyInterface();

        id = i.getExtras().getString("cardName");

        lastUpdate = (TextView) findViewById(R.id.lastUpdate);
        lastChange = (TextView) findViewById(R.id.lastChangeTextView);
        lastPrice = (TextView) findViewById(R.id.lastPriceTextView);

        currentPrice = (TextView) findViewById(R.id.currentPriceTextView);
        prevClose = (TextView) findViewById(R.id.prevCloseTextView);
        todaysLow = (TextView) findViewById(R.id.todaysLowTextView);
        todaysHigh = (TextView) findViewById(R.id.todaysHighTextView);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);

        currencyToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        currencyToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        custom_font = ResourcesCompat.getFont(this, R.font.hammersmithone);

        changeToolbarFont(currencyToolbar, this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
                getCurrency();
            }
        },20);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCurrency();
            }
        });

        buyStocks = (Button) findViewById(R.id.buyStocks);

        buyStocks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CurrencyActivity.this, PurchaseActivity.class);
                i.putExtra("type", "CURRENCY");
                i.putExtra("id", purchaseId);
                i.putExtra("name", id);
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

    public void setCurrencyCard() {

        Utility utility = new Utility();
        String pchange = utility.getRoundoffData(""+currencyObject.get("data").getAsJsonObject().get("PERCCHANGE").getAsString());

        lastUpdate.setText(utility.getFormattedDate(""+currencyObject.get("data").getAsJsonObject().
                get("lastupd_epoch").getAsString()));
        lastChange.setText(currencyObject.get("data").getAsJsonObject().get("CHANGE").getAsString() + "(" + pchange + "%)");

        currentPrice.setText(currencyObject.get("data").getAsJsonObject().get("pricecurrent").getAsString());
        prevClose.setText(currencyObject.get("data").getAsJsonObject().get("priceprevclose").getAsString());
        todaysHigh.setText(currencyObject.get("data").getAsJsonObject().get("HIGH").getAsString());
        todaysLow.setText(currencyObject.get("data").getAsJsonObject().get("LOW").getAsString());

        if(Double.parseDouble(pchange)>=0) {
            lastChange.setTextColor(getResources().getColor(R.color.greenText));
        }else {
            lastChange.setTextColor(getResources().getColor(R.color.red));
        }
    }

    public void getCurrency() {
        getFunction().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                refreshLayout.setRefreshing(false);

                if(response.isSuccessful()) {
                    currencyObject = response.body();
                    setCurrencyCard();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    public Call<JsonObject> getFunction() {

        switch(id) {

            case "DOLLAR" :
                purchaseId = "USDINR";
                return  apiInterface.getUSDINR();

            case "EURO" :
                purchaseId = "EURINR";
                return apiInterface.getEURINR();

            case "POUND" :
                purchaseId = "GBPINR";
                return apiInterface.getGBPINR();
        }

        return null;
    }
}
