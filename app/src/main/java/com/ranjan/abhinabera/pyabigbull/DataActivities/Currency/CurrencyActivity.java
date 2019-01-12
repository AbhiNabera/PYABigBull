package com.ranjan.abhinabera.pyabigbull.DataActivities.Currency;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
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
import android.widget.Button;
import android.widget.TextView;

import com.ranjan.abhinabera.pyabigbull.Api.ApiInterface;
import com.ranjan.abhinabera.pyabigbull.Api.RetrofitClient;
import com.ranjan.abhinabera.pyabigbull.Transactions.PurchaseActivity;
import com.ranjan.abhinabera.pyabigbull.R;
import com.ranjan.abhinabera.pyabigbull.Api.Utility;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

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

    String symbol;
    GraphView graphView;
    LineGraphSeries<DataPoint> series;
    long MIN = 0, MAX = 0;

    ArrayList<Long> xAxis = new ArrayList<>();
    ArrayList<Double> yAxis = new ArrayList<>();


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode  == RESULT_OK) {
            //unsuccessful transaction
            Log.d("unsuccessfultransaction", "");
        }else {
            finish();
        }
    }

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

        graphView = (GraphView) findViewById(R.id.graph);

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
                setUpBlankChart();
                getGraphData();
            }
        },20);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCurrency();
                //setUpBlankChart();
                getGraphData();
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
                startActivityForResult(i, 400);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        graphView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CurrencyActivity.this, CurrencyGraphActivity.class);
                intent.putExtra("symbol", symbol);
                startActivity(intent);
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
        lastChange.setText(new Utility().getRoundoffData(currencyObject.get("data").getAsJsonObject().get("CHANGE").getAsString()) + "(" + pchange + "%)");

        currentPrice.setText(new Utility().getRoundoffData(currencyObject.get("data").getAsJsonObject().get("pricecurrent").getAsString()));
        prevClose.setText(new Utility().getRoundoffData(currencyObject.get("data").getAsJsonObject().get("priceprevclose").getAsString()));
        todaysHigh.setText(new Utility().getRoundoffData(currencyObject.get("data").getAsJsonObject().get("HIGH").getAsString()));
        todaysLow.setText(new Utility().getRoundoffData(currencyObject.get("data").getAsJsonObject().get("LOW").getAsString()));

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
                symbol = "USD";
                purchaseId = "USDINR";
                return  apiInterface.getUSDINR();

            case "EURO" :
                symbol = "EUR";
                purchaseId = "EURINR";
                return apiInterface.getEURINR();

            case "POUND" :
                symbol = "GBP";
                purchaseId = "GBPINR";
                return apiInterface.getGBPINR();
        }

        return null;
    }

    public long getDateFromString(String time) {

        DateFormat format;

        try {
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return format.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public Call<JsonObject> getGraphData() {

        Call<JsonObject> call = new RetrofitClient().getCurrencyGraphInterface().getData(new Utility()
                .getINTRADAYCurrencyGraph(symbol, "FX_INTRADAY"));

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                try {

                    if (!call.isCanceled()) {
                        if (response.isSuccessful()) {

                            Log.d("successful", "call cancelled");

                            if (response.body().getAsJsonObject("Time Series FX (5min)") != null) {

                                Log.d("not null", "call cancelled");

                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {

                                        xAxis.clear();
                                        yAxis.clear();

                                        graphView.removeAllSeries();
                                        //series = new LineGraphSeries<>();

                                        JsonObject object = response.body().getAsJsonObject("Time Series FX (5min)");
                                        Set<Map.Entry<String, JsonElement>> entrySet = object.entrySet();

                                        for (Map.Entry<String, JsonElement> entry : entrySet) {

                                            //Log.d("key", entry.getKey()+"");
                                            //Log.d("value", entry.getValue().getAsJsonObject().get("4. close").getAsDouble()+"");

                                            xAxis.add(0, getDateFromString(entry.getKey()));
                                            yAxis.add(0, entry.getValue().getAsJsonObject().get("4. close").getAsDouble());
                                        }

                                        Log.d("size", "" + xAxis.size() + ":" + yAxis.size());

                                        int i = 0;

                                        DataPoint[] dataPoints = new DataPoint[xAxis.size()];

                                        for (i = 0; i < xAxis.size(); i++) {
                                            dataPoints[i] = new DataPoint(xAxis.get(i),
                                                    yAxis.get(i));
                                            //series.appendData(new DataPoint(xAxis.get(i),
                                            //        yAxis.get(i)), true, i);
                                        }

                                        MIN = xAxis.get(0);
                                        MAX = xAxis.get(i - 1);

                                        series = new LineGraphSeries<>(dataPoints);
                                        setUpChart();
                                        graphView.addSeries(series);

                                    }
                                });

                            }

                        } else {
                            //setUpBlankChart();
                            try {
                                Log.d("error", response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {

                        try {
                            Log.d("error", "call cancelled");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
            }
        });

        return call;
    }

    public void setUpChart() {

        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(MIN);
        graphView.getViewport().setMaxX(MAX);
        graphView.getViewport().setXAxisBoundsManual(false);

        graphView.getViewport().setScrollable(false);
        graphView.getViewport().setScalable(false);
        graphView.getViewport().setScrollableY(false);
        graphView.getViewport().setScalableY(false);

        graphView.getGridLabelRenderer().setHorizontalLabelsColor(getResources().getColor(android.R.color.white));
        graphView.getGridLabelRenderer().setVerticalLabelsColor(getResources().getColor(android.R.color.white));
        graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
        //graphView.getGridLabelRenderer().setNumVerticalLabels(18);
        graphView.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graphView.getGridLabelRenderer().setTextSize(getResources().getDimension(R.dimen.graphTextSize));

        series.setColor(getResources().getColor(R.color.greenText));
        series.setBackgroundColor(getResources().getColor(R.color.greenTextAlpha));
        series.setDrawBackground(true);
        series.setThickness(4);
        series.setAnimated(true);
    }

    public void setUpBlankChart() {

        graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
        graphView.getGridLabelRenderer().setHorizontalLabelsColor(getResources().getColor(R.color.colorPrimaryDark));
        graphView.getGridLabelRenderer().setVerticalLabelsColor(getResources().getColor(R.color.colorPrimaryDark));

        graphView.removeAllSeries();
    }

    public void setScrollable() {

        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setScalable(true);
        graphView.getViewport().setScrollableY(true);
        graphView.getViewport().setScalableY(true);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
}
