package com.ranjan.abhinabera.pyabigbull.DataActivities.Nifty50;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
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

import com.ranjan.abhinabera.pyabigbull.Api.RetrofitClient;
import com.ranjan.abhinabera.pyabigbull.Api.Utility;
import com.ranjan.abhinabera.pyabigbull.Transactions.PurchaseActivity;
import com.ranjan.abhinabera.pyabigbull.R;
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
import java.util.Date;

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

    GraphView graphView;
    LineGraphSeries<DataPoint> series;
    DataPoint[] dataPoints;

    long MIN, MAX;

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

        graphView = (GraphView) findViewById(R.id.graph);

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

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getGraphData(id);
                getStockIndividual();
            }
        });

        graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
        graphView.getGridLabelRenderer().setHorizontalLabelsColor(getResources().getColor(R.color.colorPrimaryDark));
        graphView.getGridLabelRenderer().setVerticalLabelsColor(getResources().getColor(R.color.colorPrimaryDark));

        graphView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NiftyStocksIndividual.this, NiftyIndvGraphActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("companyName", getIntent().getStringExtra("companyName"));
                startActivity(intent);
            }
        });

        buyStocks = (Button) findViewById(R.id.buyStocks);

        buyStocks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(NiftyStocksIndividual.this, PurchaseActivity.class);
                i.putExtra("type", "NIFTY");
                i.putExtra("id", id);
                i.putExtra("name", getIntent().getStringExtra("companyName"));
                startActivityForResult(i, 200);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        refreshLayout.setRefreshing(true);
        getStockIndividual();
        getGraphData(id);
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
        //super.onBackPressed();
        setResult(RESULT_OK);
        finish();
        //overridePendingTransition(R.anim.enter1, R.anim.exit1);
    }

    public void setStockIndvCard() {

        JsonObject object = stockIndvObject.get("NSE").getAsJsonObject();

        lastUpdate.setText(object.get("lastupdate").getAsString());
        lastChange.setText(new Utility().getRoundoffData(object.get("CHG").getAsString()) + "(" + new Utility().getRoundoffData(object.get("percentchange").getAsString()) +"%)");
        lastPrice.setText(new Utility().getRoundoffData(object.get("lastvalue").getAsString()));
        volume.setText(object.get("volume").getAsString());

        bidPrice.setText(new Utility().getRoundoffData(object.get("bidprice").getAsString()) + "(" + object.get("bidqty").getAsString() + ")");
        offerPrice.setText(new Utility().getRoundoffData(object.get("offerprice").getAsString()) + "(" + object.get("offerqty").getAsString() + ")");
        prevClose.setText(new Utility().getRoundoffData(object.get("yesterdaysclose").getAsString()));
        openPrice.setText(new Utility().getRoundoffData(object.get("todaysopen").getAsString()));
        vwap.setText(new Utility().getRoundoffData(object.get("vwap").getAsString()));
        mktCap.setText(new Utility().getRoundoffData(object.get("mktcap").getAsString()) + "cr");
        todaysHigh.setText(new Utility().getRoundoffData(object.get("dayhigh").getAsString()));
        todaysLow.setText(new Utility().getRoundoffData(object.get("daylow").getAsString()));
        wkHigh.setText(new Utility().getRoundoffData(object.get("yearlyhigh").getAsString()));
        wkLow.setText(new Utility().getRoundoffData(object.get("yearlylow").getAsString()));
        lPriceBand.setText(new Utility().getRoundoffData(object.get("lcprice").getAsString()));
        uPriceBand.setText(new Utility().getRoundoffData(object.get("ucprice").getAsString()));

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

    public long getDateFromString(String time) {

        DateFormat format;

        try {
            time = new SimpleDateFormat("dd MMM yyyy").format(new Date()) + " " +time;
            format = new SimpleDateFormat("dd MMM yyyy HH:mm");
            return format.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public double getDoubleVal(String val) {
        return Double.parseDouble(val.replace("," ,""));
    }

    public void getGraphData(String compId) {

        Log.d("GRAPHURL", new Utility().getNift50IndvGraphURL("1d", compId)+"");

        new RetrofitClient().getNifty50Interface().getData(new Utility().getNift50IndvGraphURL("1d", compId)).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful()) {

                    //Log.d("response", response.body()+"");

                    if(response.body().get("graph").getAsJsonObject().get("values") != null) {

                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {

                                int i = 0;
                                JsonElement lastElement = null;

                                graphView.removeAllSeries();
                                series = new LineGraphSeries<>();

                                //TODO: temp fix
                                long prevTime = 0;
                                String prevDate = "";

                                for (JsonElement element : response.body().get("graph").getAsJsonObject().get("values").getAsJsonArray()) {

                                    long time = getDateFromString(element.getAsJsonObject().get("_time").getAsString());

                                    if (time > prevTime) {
                                        prevTime = time;
                                        prevDate = element.getAsJsonObject().get("_time").getAsString();
                                    } else {
                                        break;
                                    }

                                    if (i == 0) {

                                        MIN = getDateFromString(element.getAsJsonObject().
                                                get("_time").getAsString());

                                    }

                                    lastElement = element;
                                    i++;

                                    series.appendData(new DataPoint(
                                                    time,
                                                    getDoubleVal(element.getAsJsonObject().get("_value").getAsString()))
                                            , true, i);

                                }

                                if(lastElement!=null)
                                    MAX = getDateFromString(lastElement.getAsJsonObject().
                                            get("_time").getAsString());
                                else {
                                    MAX=0;
                                    MIN=0;
                                }

                                NiftyStocksIndividual.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setUpChart();
                                        //series.resetData(dataPoints);
                                        graphView.addSeries(series);
                                        setScrollable();
                                        //progressBar.setVisibility(View.GONE);
                                    }
                                });
                            }
                        });

                    } else {
                        setUpBlankChart();
                        //progressBar.setVisibility(View.GONE);
                    }
                }else {
                    try {
                        Log.d("error", ""+response.errorBody().string());
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
