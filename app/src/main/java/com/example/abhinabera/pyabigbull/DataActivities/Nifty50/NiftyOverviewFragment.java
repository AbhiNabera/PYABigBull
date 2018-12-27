package com.example.abhinabera.pyabigbull.DataActivities.Nifty50;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhinabera.pyabigbull.Api.NetworkCallback;
import com.example.abhinabera.pyabigbull.Dashboard.NetworkUtility;
import com.example.abhinabera.pyabigbull.R;
import com.google.gson.JsonObject;

import java.io.IOException;

import retrofit2.Response;

public class NiftyOverviewFragment extends Fragment {

    TextView openPrice, prevClose, todaysLow, todaysHigh, wkLow, wkHigh, thirtyDays, fiftyDays, oneFiftyDays, twoHundredDays;
    TextView lastUpdate, lastPrice, lastChange;
    SwipeRefreshLayout refreshLayout;

    private Response<JsonObject> nifty50;

    public NiftyOverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nifty_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lastUpdate = (TextView) view.findViewById(R.id.lastUpdate);
        lastChange = (TextView) view.findViewById(R.id.lastChangeTextView);
        lastPrice = (TextView) view.findViewById(R.id.lastPriceTextView);

        openPrice = (TextView) view.findViewById(R.id.openPriceTextView);
        prevClose = (TextView) view.findViewById(R.id.prevCloseTextView);
        todaysLow = (TextView) view.findViewById(R.id.todaysLowTextView);
        todaysHigh = (TextView) view.findViewById(R.id.todaysHighTextView);
        wkLow = (TextView) view.findViewById(R.id.wkLowTextView);
        wkHigh = (TextView) view.findViewById(R.id.wkHighTextView);

        thirtyDays = (TextView) view.findViewById(R.id.thirtyDaysTextView);
        fiftyDays = (TextView) view.findViewById(R.id.fiftyDaysTextView);
        oneFiftyDays = (TextView) view.findViewById(R.id.oneFiftyDaysTextView);
        twoHundredDays = (TextView) view.findViewById(R.id.twoHunderedDaysTextView);

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNifty50();
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
                getNifty50();
            }
        },20);

    }

    private void setNiftyCard() {
        JsonObject object = nifty50.body().get("indices").getAsJsonObject();

        lastUpdate.setText(""+object.get("lastupdated").getAsString());
        lastPrice.setText(""+object.get("lastprice").getAsString());
        lastChange.setText(""+object.get("change").getAsString() + "(" + object.get("percentchange").getAsString() +"%)");

        if(Double.parseDouble(object.get("percentchange").getAsString()+"")>=0){
            lastChange.setTextColor(getResources().getColor(R.color.greenText));
        }else{
            lastChange.setTextColor(getResources().getColor(R.color.red));
        }

        openPrice.setText(""+object.get("open").getAsString());
        prevClose.setText(""+object.get("prevclose").getAsString());
        todaysHigh.setText(""+object.get("high").getAsString());
        todaysLow.setText(""+object.get("low").getAsString());
        wkHigh.setText(""+object.get("yearlyhigh").getAsString());
        wkLow.setText(""+object.get("yearlylow").getAsString());

        thirtyDays.setText(""+object.get("dayavg30").getAsString());
        fiftyDays.setText(""+object.get("dayavg50").getAsString());
        oneFiftyDays.setText(""+object.get("dayavg150").getAsString());
        twoHundredDays.setText(""+object.get("dayavg200").getAsString());
    }

    public void getNifty50() {

        refreshLayout.setRefreshing(true);

        new NetworkUtility().getNifty50(new NetworkCallback() {
            @Override
            public void onSuccess(Response<JsonObject> response) {

                refreshLayout.setRefreshing(false);

                if(response.isSuccessful()) {
                    nifty50 = response;
                    Log.d("response NIFTY50", response.body().toString());
                    setNiftyCard();
                }else {
                    try {
                        Log.d("response ERR NIFTY50", response.errorBody().string());
                        Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                refreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
