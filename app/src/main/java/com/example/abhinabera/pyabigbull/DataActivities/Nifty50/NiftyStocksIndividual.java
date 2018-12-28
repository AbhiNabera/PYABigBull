package com.example.abhinabera.pyabigbull.DataActivities.Nifty50;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.abhinabera.pyabigbull.Api.RetrofitClient;
import com.example.abhinabera.pyabigbull.Api.Utility;
import com.example.abhinabera.pyabigbull.PurchaseActivity;
import com.example.abhinabera.pyabigbull.R;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NiftyStocksIndividual extends AppCompatActivity {

    SwipeRefreshLayout refreshLayout;

    Button buyStocks;

    android.support.v7.widget.Toolbar stocksIndiToolbar;
    Typeface custom_font;
    TextView bidPrice, offerPrice, prevClose, openPrice, vwap, todaysLow, todaysHigh, wkLow, wkHigh, lPriceBand, uPriceBand,
            lastUpdate, lastChange, lastPrice, volume, mktCap;

    String id;

    JsonObject stockIndvObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_nifty_stocks_individual);
        getSupportActionBar().hide();

        lastUpdate = (TextView) findViewById(R.id.lastUpdate);
        lastChange = (TextView) findViewById(R.id.lastChangeTextView);
        lastPrice = (TextView) findViewById(R.id.lastPriceTextView);
        volume = (TextView) findViewById(R.id.volume);

        bidPrice = (TextView) findViewById(R.id.bidPriceTextView);
        offerPrice = (TextView) findViewById(R.id.offerPriceTextView);
        prevClose = (TextView) findViewById(R.id.prevCloseTextView);
        openPrice = (TextView) findViewById(R.id.openPriceTextView);
        vwap = (TextView) findViewById(R.id.VWAPTextView);
        mktCap = (TextView) findViewById(R.id.mktCapextView);
        todaysLow = (TextView) findViewById(R.id.todaysLowTextView);
        todaysHigh = (TextView) findViewById(R.id.todaysHighTextView);
        wkLow = (TextView) findViewById(R.id.wkLowTextView);
        wkHigh = (TextView) findViewById(R.id.wkHighTextView);
        lPriceBand = (TextView) findViewById(R.id.lPriceBandTextView);
        uPriceBand = (TextView) findViewById(R.id.uPriceBandTextView);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);

        stocksIndiToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.stocksIndiToolbar);
        Intent i = getIntent();
        stocksIndiToolbar.setTitle(i.getExtras().getString("companyName"));

        id = i.getStringExtra("id");

        stocksIndiToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        stocksIndiToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        custom_font = ResourcesCompat.getFont(this, R.font.hammersmithone);

        changeToolbarFont(stocksIndiToolbar, this);

        refreshLayout.setRefreshing(true);
        getStockIndividual();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getStockIndividual();
            }
        });

        buyStocks = (Button) findViewById(R.id.buyStocks);

        buyStocks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(NiftyStocksIndividual.this, PurchaseActivity.class);
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

    public void setStockIndvCard() {

        JsonObject object = stockIndvObject.get("NSE").getAsJsonObject();

        lastUpdate.setText(object.get("lastupdate").getAsString());
        lastChange.setText(object.get("CHG").getAsString() + "(" + object.get("percentchange").getAsString() +"%)");
        lastPrice.setText(object.get("lastvalue").getAsString());
        volume.setText(object.get("volume").getAsString());

        bidPrice.setText(object.get("bidprice").getAsString() + "(" + object.get("bidqty").getAsString() + ")");
        offerPrice.setText(object.get("offerprice").getAsString() + "(" + object.get("offerqty").getAsString() + ")");
        prevClose.setText(object.get("yesterdaysclose").getAsString());
        openPrice.setText(object.get("todaysopen").getAsString());
        vwap.setText(object.get("vwap").getAsString());
        mktCap.setText(object.get("mktcap").getAsString() + "cr");
        todaysHigh.setText(object.get("dayhigh").getAsString());
        todaysLow.setText(object.get("daylow").getAsString());
        wkHigh.setText(object.get("yearlyhigh").getAsString());
        wkLow.setText(object.get("yearlylow").getAsString());
        lPriceBand.setText(object.get("lcprice").getAsString());
        uPriceBand.setText(object.get("ucprice").getAsString());

        if(Double.parseDouble(object.get("percentchange").getAsString()+"")>=0){
            lastChange.setTextColor(getResources().getColor(R.color.greenText));
        }else{
            lastChange.setTextColor(getResources().getColor(R.color.red));
        }
    }

    public void getStockIndividual() {

        new RetrofitClient().getNifty50Interface().getData(new Utility().getStockIndividualUrl(id)).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                refreshLayout.setRefreshing(false);

                if(response.isSuccessful()) {
                    stockIndvObject = response.body();
                    setStockIndvCard();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                t.printStackTrace();
            }
        });
    }

}
