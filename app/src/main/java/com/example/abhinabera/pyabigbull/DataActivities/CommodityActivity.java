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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.abhinabera.pyabigbull.Api.RetrofitClient;
import com.example.abhinabera.pyabigbull.R;
import com.example.abhinabera.pyabigbull.Api.Utility;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommodityActivity extends AppCompatActivity {

    android.support.v7.widget.Toolbar commodityToolbar;
    Spinner commodityDateSpinner;
    Typeface custom_font;
    TextView bidPrice, offerPrice, openInterest, OIChange, highPrice, lowPrice, open, previousClose,
            lastUpdate, lastChange, lastPrice, volume;

    SwipeRefreshLayout swipeRefreshLayout;

    List<String> expiryList;
    JsonArray jsonObjects;
    Response<JsonObject> resp;

    String id;

    int pos = 0;

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

        expiryList = new ArrayList<>();

        id = i.getExtras().getString("cardName");

        lastUpdate = (TextView) findViewById(R.id.lastUpdate);
        lastChange = (TextView) findViewById(R.id.lastChangeTextView);
        lastPrice = (TextView) findViewById(R.id.lastPriceTextView);
        volume = (TextView) findViewById(R.id.volume);

        bidPrice = (TextView) findViewById(R.id.bidPriceTextView);
        offerPrice = (TextView) findViewById(R.id.offerPriceTextView);
        openInterest = (TextView) findViewById(R.id.openInterestTextView);
        OIChange = (TextView) findViewById(R.id.OIChangeTextView);
        highPrice = (TextView) findViewById(R.id.highPriceTextView);
        lowPrice = (TextView) findViewById(R.id.lowPriceTextView);
        open = (TextView) findViewById(R.id.openTextView);
        previousClose = (TextView) findViewById(R.id.previousCloseTextView);
        commodityDateSpinner = (Spinner) findViewById(R.id.commoditySpinner);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);

        commodityToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        commodityToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        custom_font = ResourcesCompat.getFont(this, R.font.hammersmithone);

        changeToolbarFont(commodityToolbar, this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                getData();
            }
        },20);

        commodityDateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                swipeRefreshLayout.setRefreshing(true);
                pos = i;
                getExpiryData(expiryList.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getExpiryData(expiryList.get(commodityDateSpinner.getSelectedItemPosition()));
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

    public void setCommodityCard() {
        JsonElement element = jsonObjects.get(pos);
        lastUpdate.setText(element.getAsJsonObject().get("ent_date").getAsString());
        volume.setText(element.getAsJsonObject().get("volume").getAsString());
        lastChange.setText(element.getAsJsonObject().get("change").getAsString() + "(" +
                element.getAsJsonObject().get("percentchange").getAsString() + "%)");
        lastPrice.setText(element.getAsJsonObject().get("lastprice").getAsString());

        bidPrice.setText(element.getAsJsonObject().get("comd_details").getAsJsonObject()
                .get("bidprice").getAsString() + "(" + element.getAsJsonObject().get("comd_details").getAsJsonObject()
                .get("bidqty").getAsString() + ")");
        offerPrice.setText(element.getAsJsonObject().get("comd_details").getAsJsonObject()
                .get("offerprice").getAsString() + "(" + element.getAsJsonObject().get("comd_details").getAsJsonObject()
                .get("offerqty").getAsString() + ")");

        openInterest.setText(element.getAsJsonObject().get("comd_details").getAsJsonObject()
                .get("open_int").getAsString());
        OIChange.setText(element.getAsJsonObject().get("comd_details").getAsJsonObject()
                .get("oi_change").getAsString() + "(" + element.getAsJsonObject().get("comd_details").getAsJsonObject()
                .get("oi_percchg").getAsString() + "%)");
        highPrice.setText(element.getAsJsonObject().get("comd_details").getAsJsonObject()
                .get("high").getAsString());
        lowPrice.setText(element.getAsJsonObject().get("comd_details").getAsJsonObject()
                .get("low").getAsString());
        open.setText(element.getAsJsonObject().get("comd_details").getAsJsonObject()
                .get("open").getAsString());
        previousClose.setText(element.getAsJsonObject().get("comd_details").getAsJsonObject()
                .get("prev_close").getAsString());

        if(Double.parseDouble(element.getAsJsonObject().get("percentchange").getAsString()+"")>=0){
            lastChange.setTextColor(getResources().getColor(R.color.greenText));
        }else{
            lastChange.setTextColor(getResources().getColor(R.color.red));
        }

        if(Double.parseDouble(element.getAsJsonObject().get("comd_details").getAsJsonObject()
                .get("oi_percchg").getAsString()+"")>=0){
            OIChange.setTextColor(getResources().getColor(R.color.greenText));
        }else{
            OIChange.setTextColor(getResources().getColor(R.color.red));
        }
    }

    public void setList() {

        expiryList.clear();

        for(JsonElement element: jsonObjects) {
            expiryList.add(element.getAsJsonObject().get("comd_exp").getAsString());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, expiryList);
        commodityDateSpinner.setAdapter(adapter);
    }

    public void getData() {

        new RetrofitClient().getNifty50Interface().getData(new Utility().getCommodityURL(id)).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                swipeRefreshLayout.setRefreshing(false);

                if(response.isSuccessful()) {
                    Log.d("COMMODITY DATA", response.body().toString()+"");
                    resp = response;

                    jsonObjects = resp.body().get("comd_list").getAsJsonObject().get("item").getAsJsonArray();

                    setList();

                    setCommodityCard();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void getExpiryData(String expiry) {

        new RetrofitClient().getNifty50Interface().getData(new Utility().getCommodityExpiryURL(id, expiry)).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                swipeRefreshLayout.setRefreshing(false);

                if(response.isSuccessful()) {
                    Log.d("COMMODITY EXPIRY DATA", response.body().toString()+"");

                    resp = response;

                    jsonObjects = resp.body().get("comd_list").getAsJsonObject().get("item").getAsJsonArray();

                    setCommodityCard();

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
