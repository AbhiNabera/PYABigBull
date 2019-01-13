package nabera.ranjan.abhinabera.pyabigbull.DataActivities.Currency;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import nabera.ranjan.abhinabera.pyabigbull.Api.RetrofitClient;
import nabera.ranjan.abhinabera.pyabigbull.Api.Utility;
import nabera.ranjan.abhinabera.pyabigbull.R;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrencyGraphActivity extends AppCompatActivity {

    GraphView graphView;
    Toolbar toolbar;
    TextView titletext;
    Spinner periodSpinner;
    ProgressBar progressBar;

    int selection = 0;

    String symbol;
    LineGraphSeries<DataPoint> series;
    DataPoint[] dataPoints;

    private Call<JsonObject> prevCall;

    long MIN, MAX;

    String[] periods = {"Interday", "Daily", "Weekly", "Monthly"};
    String[] periodId = {"FX_INTRADAY","FX_DAILY","FX_WEEKLY","FX_MONTHLY"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_nifty_graph);
        getSupportActionBar().hide();

        symbol = getIntent().getStringExtra("symbol");

        toolbar = (Toolbar) findViewById(R.id.graphToolbar);
        graphView = (GraphView) findViewById(R.id.graph);
        titletext = (TextView) findViewById(R.id.title);
        periodSpinner = (Spinner) findViewById(R.id.periodSpinner);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.small_spinner_item, periods);
        periodSpinner.setAdapter(adapter);

        periodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(selection!=i) {
                    if(prevCall!=null) {
                        prevCall.cancel();
                    }
                    selection = i;
                    prevCall = getGraphData(selection);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        getGraphData(0);

        setUpBlankChart();

        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    return getXAxisLabel((long) value);
                } else {
                    // show currency for y values
                    return super.formatLabel(value, isValueX);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //finish();
        //overridePendingTransition(R.anim.enter1, R.anim.exit1);
    }

    public String getXAxisLabel(long value) {

        try {
            switch (selection) {

                case 0:
                    return new SimpleDateFormat("HH:mm").format(new Date(value));

                case 1:
                    return new SimpleDateFormat("dd MMM").format(new Date(value));

                case 2:
                    return new SimpleDateFormat("dd MMM").format(new Date(value));

                case 3:
                    return new SimpleDateFormat("dd MMM").format(new Date(value));

                case 4:
                    return new SimpleDateFormat("MMM").format(new Date(value));

                default:
                    return new SimpleDateFormat("MMM yyyy").format(new Date(value));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";

    }

    public long getDateFromString(String time) {

        DateFormat format;

        try {
            switch (selection) {

                case 0:
                    format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    return format.parse(time).getTime();

                case 1:
                    format = new SimpleDateFormat("yyyy-MM-dd");
                    return format.parse(time).getTime();

                default:
                    format = new SimpleDateFormat("yyyy-MM-dd");
                    return format.parse(time).getTime();

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public double getDoubleVal(String val) {
        return Double.parseDouble(val.replace("," ,""));
    }

    public Call<JsonObject> getGraphData(int selection) {

        progressBar.setVisibility(View.VISIBLE);

        Call<JsonObject> call = new RetrofitClient().getCurrencyGraphInterface().getData(new Utility()
                .getCurrencyGraph(symbol, periodId[selection]));

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if(!call.isCanceled()) {
                    if (response.isSuccessful()) {

                        if (selection == 0) {

                            if(response.body().getAsJsonObject("Time Series FX (5min)")!=null) {

                                Log.d("not null", "call cancelled");

                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {

                                        ArrayList<Long> xAxis = new ArrayList<>();
                                        ArrayList<Double> yAxis = new ArrayList<>();

                                        xAxis.clear();
                                        yAxis.clear();

                                        graphView.removeAllSeries();
                                        //series = new LineGraphSeries<>();

                                        JsonObject object = response.body().getAsJsonObject("Time Series FX (5min)");
                                        Set<Map.Entry<String, JsonElement>> entrySet = object.entrySet();

                                        for(Map.Entry<String, JsonElement> entry: entrySet) {

                                            //Log.d("key", entry.getKey()+"");
                                            //Log.d("value", entry.getValue().getAsJsonObject().get("4. close").getAsDouble()+"");

                                            xAxis.add( 0, getDateFromString(entry.getKey()));
                                            yAxis.add( 0, entry.getValue().getAsJsonObject().get("4. close").getAsDouble());
                                        }

                                        Log.d("size", ""+xAxis.size()+":"+yAxis.size());

                                        int i=0;

                                        DataPoint[] dataPoints = new DataPoint[xAxis.size()];

                                        for(i=0; i< xAxis.size(); i++) {
                                            dataPoints[i] = new DataPoint(xAxis.get(i),
                                                    yAxis.get(i));
                                            //series.appendData(new DataPoint(xAxis.get(i),
                                            //        yAxis.get(i)), true, i);
                                        }

                                        MIN = xAxis.get(0);
                                        MAX = xAxis.get(i-1);

                                        series = new LineGraphSeries<>(dataPoints);
                                        setUpChart();
                                        graphView.addSeries(series);

                                    }
                                });

                            }

                        }else {

                            if(response.body().getAsJsonObject("Time Series FX ("+periods[selection]+")")!=null) {

                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {

                                        ArrayList<Long> xAxis = new ArrayList<>();
                                        ArrayList<Double> yAxis = new ArrayList<>();

                                        xAxis.clear();
                                        yAxis.clear();

                                        graphView.removeAllSeries();
                                        //series = new LineGraphSeries<>();

                                        JsonObject object = response.body().getAsJsonObject("Time Series FX ("+periods[selection]+")");
                                        Set<Map.Entry<String, JsonElement>> entrySet = object.entrySet();

                                        for(Map.Entry<String, JsonElement> entry: entrySet) {

                                            //Log.d("key", entry.getKey()+"");
                                            //Log.d("value", entry.getValue().getAsJsonObject().get("4. close").getAsDouble()+"");

                                            xAxis.add( 0, getDateFromString(entry.getKey()));
                                            yAxis.add( 0, entry.getValue().getAsJsonObject().get("4. close").getAsDouble());
                                        }

                                        Log.d("size", ""+xAxis.size()+":"+yAxis.size());

                                        int i=0;

                                        DataPoint[] dataPoints = new DataPoint[xAxis.size()];

                                        for(i=0; i< xAxis.size(); i++) {
                                            dataPoints[i] = new DataPoint(xAxis.get(i),
                                                    yAxis.get(i));
                                            //series.appendData(new DataPoint(xAxis.get(i),
                                            //        yAxis.get(i)), true, i);
                                        }

                                        MIN = xAxis.get(0);
                                        MAX = xAxis.get(i-1);

                                        series = new LineGraphSeries<>(dataPoints);
                                        setUpChart();
                                        graphView.addSeries(series);

                                    }
                                });

                            }

                        }
                    }
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
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
        graphView.getGridLabelRenderer().setTextSize(getResources().getDimension(R.dimen.graphTextSize));

        series.setColor(getResources().getColor(R.color.greenText));
        series.setBackgroundColor(getResources().getColor(R.color.greenTextAlpha));
        series.setDrawBackground(true);
        series.setThickness(4);
        series.setAnimated(true);
    }

    public void setUpBlankChart() {

        graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
        graphView.getGridLabelRenderer().setHorizontalLabelsColor(getResources().getColor(R.color.colorPrimary));
        graphView.getGridLabelRenderer().setVerticalLabelsColor(getResources().getColor(R.color.colorPrimary));

        graphView.removeAllSeries();
    }

    public void setScrollable() {

        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setScalable(true);
        //graphView.getViewport().setScrollableY(true);
        //graphView.getViewport().setScalableY(true);

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
}
