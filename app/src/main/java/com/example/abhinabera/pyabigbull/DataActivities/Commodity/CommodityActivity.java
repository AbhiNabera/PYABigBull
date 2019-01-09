package com.example.abhinabera.pyabigbull.DataActivities.Commodity;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhinabera.pyabigbull.Api.RetrofitClient;
import com.example.abhinabera.pyabigbull.Login.RegistrationActivity;
import com.example.abhinabera.pyabigbull.Transactions.PurchaseActivity;
import com.example.abhinabera.pyabigbull.R;
import com.example.abhinabera.pyabigbull.Api.Utility;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    Button buyStocks;

    SwipeRefreshLayout swipeRefreshLayout;

    List<String> expiryList;
    JsonArray jsonObjects;
    Response<JsonObject> resp;

    String id;

    int pos = 0;

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

        graphView = (GraphView) findViewById(R.id.graph);

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
                Log.d("EXPIRY", expiryList.get(i));
                getExpiryData(expiryList.get(i));
                getGraphData(id, expiryList.get(pos));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!expiryList.isEmpty()) {
                    getExpiryData(expiryList.get(commodityDateSpinner.getSelectedItemPosition()));
                    getGraphData(id, expiryList.get(pos));
                }else{
                    swipeRefreshLayout.setRefreshing(true);
                    getData();
                }
            }
        });

        buyStocks = (Button) findViewById(R.id.buyStocks);

        buyStocks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CommodityActivity.this, PurchaseActivity.class);
                i.putExtra("type", "COMMODITY");
                i.putExtra("id", id);
                i.putExtra("name", id);
                startActivityForResult(i, 300);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
        graphView.getGridLabelRenderer().setHorizontalLabelsColor(getResources().getColor(R.color.colorPrimaryDark));
        graphView.getGridLabelRenderer().setVerticalLabelsColor(getResources().getColor(R.color.colorPrimaryDark));

        graphView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CommodityActivity.this, CommodityGraphActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("expdt", expiryList.get(pos)+"");
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

    public void setCommodityCard() {

        if(jsonObjects.get(pos).getAsJsonObject().getAsJsonObject("comd_details")!=null) {

            JsonElement element = jsonObjects.get(pos);
            lastUpdate.setText(element.getAsJsonObject().get("ent_date").getAsString());
            volume.setText(element.getAsJsonObject().get("volume").getAsString());
            lastChange.setText(new Utility().getRoundoffData(element.getAsJsonObject().get("change").getAsString()) + "(" +
                    new Utility().getRoundoffData(element.getAsJsonObject().get("percentchange").getAsString().replace(",", "")) + "%)");
            lastPrice.setText(new Utility().getRoundoffData(element.getAsJsonObject().get("lastprice").getAsString().replace(",", "")));

            bidPrice.setText(new Utility().getRoundoffData(element.getAsJsonObject().get("comd_details").getAsJsonObject()
                    .get("bidprice").getAsString().replace(",", "")) + "(" + element.getAsJsonObject().get("comd_details").getAsJsonObject()
                    .get("bidqty").getAsString() + ")");
            offerPrice.setText(new Utility().getRoundoffData(element.getAsJsonObject().get("comd_details").getAsJsonObject()
                    .get("offerprice").getAsString().replace(",", "")) + "(" + element.getAsJsonObject().get("comd_details").getAsJsonObject()
                    .get("offerqty").getAsString() + ")");

            openInterest.setText(new Utility().getRoundoffData(element.getAsJsonObject().get("comd_details").getAsJsonObject()
                    .get("open_int").getAsString().replace(",", "")));
            OIChange.setText(new Utility().getRoundoffData(element.getAsJsonObject().get("comd_details").getAsJsonObject()
                    .get("oi_change").getAsString().replace(",", "")) + "(" + element.getAsJsonObject().get("comd_details").getAsJsonObject()
                    .get("oi_percchg").getAsString() + "%)");
            highPrice.setText(new Utility().getRoundoffData(element.getAsJsonObject().get("comd_details").getAsJsonObject()
                    .get("high").getAsString().replace(",", "")));
            lowPrice.setText(new Utility().getRoundoffData(element.getAsJsonObject().get("comd_details").getAsJsonObject()
                    .get("low").getAsString().replace(",", "")));
            open.setText(new Utility().getRoundoffData(element.getAsJsonObject().get("comd_details").getAsJsonObject()
                    .get("open").getAsString().replace(",", "")));
            previousClose.setText(new Utility().getRoundoffData(element.getAsJsonObject().get("comd_details").getAsJsonObject()
                    .get("prev_close").getAsString().replace(",", "")));

            if (Double.parseDouble(element.getAsJsonObject().get("percentchange").getAsString() + "") >= 0) {
                lastChange.setTextColor(getResources().getColor(R.color.greenText));
            } else {
                lastChange.setTextColor(getResources().getColor(R.color.red));
            }

            if (Double.parseDouble(element.getAsJsonObject().get("comd_details").getAsJsonObject()
                    .get("oi_percchg").getAsString() + "") >= 0) {
                OIChange.setTextColor(getResources().getColor(R.color.greenText));
            } else {
                OIChange.setTextColor(getResources().getColor(R.color.red));
            }

        } else {

            pos = pos + 1;
            commodityDateSpinner.setSelection(pos);
            if(pos < jsonObjects.size()) {
                setCommodityCard();
            }else {
                finish();
                Toast.makeText(CommodityActivity.this, "Error occured", Toast.LENGTH_SHORT).show();
            }
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

        swipeRefreshLayout.setRefreshing(true);

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

                    //getGraphData(id, expiryList.get(pos));
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

        swipeRefreshLayout.setRefreshing(true);
        //Log.d("URL", new Utility().getCommodityExpiryURL(id.trim(), expiry.trim())+"");

        new RetrofitClient().getNifty50Interface().getData(new Utility().getCommodityExpiryURL(id.trim(), expiry.trim())).enqueue(new Callback<JsonObject>() {
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

    public void getGraphData(String symbol, String expdt) {

        new RetrofitClient().getNifty50Interface().getData(new Utility().getCommodityGraphURL(
                "i", symbol, expdt
        )).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                try {

                    if (response.isSuccessful()) {

                        if (response.body().get("graph").getAsJsonObject().get("values") != null) {

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

                                    if (lastElement != null)
                                        MAX = getDateFromString(lastElement.getAsJsonObject().
                                                get("_time").getAsString());
                                    else {
                                        MAX = 0;
                                        MIN = 0;
                                    }

                                    CommodityActivity.this.runOnUiThread(new Runnable() {
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

                    } else {

                        setUpBlankChart();
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
