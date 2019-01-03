package com.example.abhinabera.pyabigbull.DataActivities.Nifty50;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhinabera.pyabigbull.Api.RetrofitClient;
import com.example.abhinabera.pyabigbull.Api.Utility;
import com.example.abhinabera.pyabigbull.R;
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
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NiftyIndvGraphActivity extends AppCompatActivity {

    GraphView graphView;
    Toolbar toolbar;
    TextView titletext;
    Spinner periodSpinner;
    ProgressBar progressBar;

    int selection = 0;
    String compId;

    LineGraphSeries<DataPoint> series;
    DataPoint[] dataPoints;

    long MIN, MAX;

    String[] periods = {"1 day", "5 days", "1 month", "3 months", "6 months", "1 year", "2 years", "5 years", "Max"};
    String[] periodId = {"1d","5d","1m","3m","6m","1yr", "2yr", "5yr", "max"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_nifty_graph);
        getSupportActionBar().hide();

        compId = getIntent().getStringExtra("id");

        toolbar = (Toolbar) findViewById(R.id.graphToolbar);
        graphView = (GraphView) findViewById(R.id.graph);
        titletext = (TextView) findViewById(R.id.title);
        periodSpinner = (Spinner) findViewById(R.id.periodSpinner);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        titletext.setText(getIntent().getStringExtra("companyName"));

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
                    selection = i;
                    getGraphData(selection, compId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        getGraphData(0, compId);

        //setUpChart();
        graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
        graphView.getGridLabelRenderer().setHorizontalLabelsColor(getResources().getColor(R.color.colorPrimary));
        graphView.getGridLabelRenderer().setVerticalLabelsColor(getResources().getColor(R.color.colorPrimary));

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
                    time = new SimpleDateFormat("dd MMM yyyy").format(new Date()) + " " +time;
                    format = new SimpleDateFormat("dd MMM yyyy HH:mm");
                    return format.parse(time).getTime();

                case 1:
                    format = new SimpleDateFormat("dd MMM yyyy HH:mm");
                    return format.parse(time).getTime();

                default:
                    format = new SimpleDateFormat("dd MMM yyyy");
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

    public void getGraphData(int selection, String compId) {

        progressBar.setVisibility(View.VISIBLE);

        new RetrofitClient().getNifty50Interface().getData(new Utility().getNift50IndvGraphURL(periodId[selection],
                compId)).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if(response.isSuccessful()) {

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

                                NiftyIndvGraphActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setUpChart();
                                        //series.resetData(dataPoints);
                                        graphView.addSeries(series);
                                        setScrollable();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                            }
                        });

                    } else {
                        setUpBlankChart();
                        progressBar.setVisibility(View.GONE);
                    }

                }else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(NiftyIndvGraphActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(NiftyIndvGraphActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
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
        //graphView.getViewport().setScrollableY(true);
        //graphView.getViewport().setScalableY(true);

    }
}
