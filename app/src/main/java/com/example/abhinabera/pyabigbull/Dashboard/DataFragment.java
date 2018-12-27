package com.example.abhinabera.pyabigbull.Dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.abhinabera.pyabigbull.Api.NetworkCallback;
import com.example.abhinabera.pyabigbull.DataActivities.CrudeoilActivity;
import com.example.abhinabera.pyabigbull.DataActivities.CurrencyActivity;
import com.example.abhinabera.pyabigbull.DataActivities.GoldActivity;
import com.example.abhinabera.pyabigbull.DataActivities.NiftyActivity;
import com.example.abhinabera.pyabigbull.DataActivities.SilverActivity;
import com.example.abhinabera.pyabigbull.R;
import com.example.abhinabera.pyabigbull.Utility;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Response;

public class DataFragment extends Fragment {

    private static int count = 0;
    private static int MAXCOUNT = 7;

    Response<JsonObject> nifty50, gold, silver, crudeoil, usd, eur, gbp;

    CardView niftyCard, goldCard, silverCard, crudeoilCard, dollarCard, euroCard, poundCard;

    TextView niftyDate, goldDate, silverDate, crudeoilDate, dollarDate, euroDate, poundDate,
            nifty50Rate, goldRate, silverRate, crudeoilRate, dollarRate, euroRate, poundRate,
            nifty50BoxRate, goldBoxRate, silverBoxRate, crudeoilBoxRate, dollarBoxRate, euroBoxRate, poundBoxRate,
            nifty50BoxPercent, goldBoxPercent, silverBoxPercent, crudeoilBoxPercent, dollarBoxPercent, euroBoxPercent, poundBoxPercent;

    LinearLayout nifty50Box, goldBox, silverBox, crudeoilBox, dollarBox, euroBox, poundBox;

    SwipeRefreshLayout swipeRefreshLayout;

    String data;

    JsonObject object;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_data, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Utility.MyPREF, Context.MODE_PRIVATE);
        data = sharedPreferences.getString("expiry",null);
        JsonParser jsonParser = new JsonParser();
        object = (JsonObject) jsonParser.parse(data);

        niftyCard = (CardView) view.findViewById(R.id.niftyCard);
        goldCard = (CardView) view.findViewById(R.id.goldCard);
        silverCard = (CardView) view.findViewById(R.id.silverCard);
        crudeoilCard = (CardView) view.findViewById(R.id.crudeoilCard);
        dollarCard = (CardView) view.findViewById(R.id.dollarCard);
        euroCard = (CardView) view.findViewById(R.id.euroCard);
        poundCard = (CardView) view.findViewById(R.id.poundCard);

        niftyDate = (TextView) view.findViewById(R.id.niftyDate);
        goldDate = (TextView) view.findViewById(R.id.goldDate);
        silverDate = (TextView) view.findViewById(R.id.silverDate);
        crudeoilDate = (TextView) view.findViewById(R.id.crudeoilDate);
        dollarDate = (TextView) view.findViewById(R.id.dollarDate);
        euroDate = (TextView) view.findViewById(R.id.euroDate);
        poundDate = (TextView) view.findViewById(R.id.poundDate);
        nifty50Rate = (TextView) view.findViewById(R.id.nifty50Rate);
        goldRate = (TextView) view.findViewById(R.id.goldRate);
        silverRate = (TextView) view.findViewById(R.id.silverRate);
        crudeoilRate = (TextView) view.findViewById(R.id.crudeoilRate);
        dollarRate = (TextView) view.findViewById(R.id.dollarRate);
        euroRate = (TextView) view.findViewById(R.id.euroRate);
        poundRate = (TextView) view.findViewById(R.id.poundRate);
        nifty50BoxRate = (TextView) view.findViewById(R.id.nifty50BoxRate);
        goldBoxRate = (TextView) view.findViewById(R.id.goldBoxRate);
        silverBoxRate = (TextView) view.findViewById(R.id.silverBoxRate);
        crudeoilBoxRate = (TextView) view.findViewById(R.id.crudeoilBoxRate);
        dollarBoxRate = (TextView) view.findViewById(R.id.dollarBoxRate);
        euroBoxRate = (TextView) view.findViewById(R.id.euroBoxRate);
        poundBoxRate = (TextView) view.findViewById(R.id.poundBoxRate);
        nifty50BoxPercent = (TextView) view.findViewById(R.id.nifty50BoxPercent);
        goldBoxPercent = (TextView) view.findViewById(R.id.goldBoxPercent);
        silverBoxPercent = (TextView) view.findViewById(R.id.silverBoxPercent);
        crudeoilBoxPercent = (TextView) view.findViewById(R.id.crudeoilBoxPercent);
        dollarBoxPercent = (TextView) view.findViewById(R.id.dollarBoxPercent);
        euroBoxPercent = (TextView) view.findViewById(R.id.euroBoxPercent);
        poundBoxPercent = (TextView) view.findViewById(R.id.poundBoxPercent);

        nifty50Box = (LinearLayout) view.findViewById(R.id.nifty50Box);
        goldBox = (LinearLayout) view.findViewById(R.id.goldBox);
        silverBox = (LinearLayout) view.findViewById(R.id.silverBox);
        crudeoilBox = (LinearLayout) view.findViewById(R.id.crudeoilBox);
        dollarBox = (LinearLayout) view.findViewById(R.id.dollarBox);
        euroBox = (LinearLayout) view.findViewById(R.id.euroBox);
        poundBox = (LinearLayout) view.findViewById(R.id.poundBox);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);

        niftyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), NiftyActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        goldCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), GoldActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        silverCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SilverActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        crudeoilCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), CrudeoilActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        dollarCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), CurrencyActivity.class);
                i.putExtra("cardName", "Dollar");
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        euroCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), CurrencyActivity.class);
                i.putExtra("cardName", "Euro");
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        poundCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), CurrencyActivity.class);
                i.putExtra("cardName", "Pound");
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                count = 0;
                getNifty50();
                getUSDINR();
                getEURINR();
                getGBPINR();
                getGold();
                getSilver();
                getCrudeoil();

            }
        },50);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                count = 0;
                getNifty50();
                getUSDINR();
                getEURINR();
                getGBPINR();
                getGold();
                getSilver();
                getCrudeoil();
            }
        });

    }

    public void setNiftyCard() {

        JsonObject object = nifty50.body().get("indices").getAsJsonObject();
        Utility utility = new Utility();

        niftyDate.setText(""+object.get("lastupdated").getAsString());
        nifty50Rate.setText(/*utility.getRoundoffData*/(object.get("lastprice").getAsString()+""));
        nifty50BoxRate.setText(utility.getRoundoffData(object.get("change").getAsString()+""));
        String pchange = utility.getRoundoffData(object.get("percentchange").getAsString());
        nifty50BoxPercent.setText(pchange+"%");
        if(Double.parseDouble(pchange)>=0) {
            nifty50Box.setBackgroundColor(getResources().getColor(R.color.greenText));
        }else {
            nifty50Box.setBackgroundColor(getResources().getColor(R.color.red));
        }
    }

    public void setGoldCard() {
        JsonObject object = gold.body().get("data").getAsJsonObject();
        Utility utility = new Utility();

        goldDate.setText("MCX: " + utility.getFormattedDate(object.get("lastupd_epoch").getAsString()+""));
        goldRate.setText("" + /*utility.getRoundoffData*/(object.get("pricecurrent").getAsString()+""));
        goldBoxRate.setText(utility.getRoundoffData("" + object.get("pricechange").getAsString()));
        String pchange = utility.getRoundoffData(object.get("pricepercentchange").getAsString());
        goldBoxPercent.setText("" + pchange + "%");

        if(Double.parseDouble(pchange)>=0) {
            goldBox.setBackgroundColor(getResources().getColor(R.color.greenText));
        }else {
            goldBox.setBackgroundColor(getResources().getColor(R.color.red));
        }
    }

    public void setSilverCard() {
        JsonObject object = silver.body().get("data").getAsJsonObject();
        Utility utility = new Utility();

        silverDate.setText("MCX: " + utility.getFormattedDate(object.get("lastupd_epoch").getAsString()));
        silverRate.setText(/*utility.getRoundoffData*/("" + object.get("pricecurrent").getAsString()));
        silverBoxRate.setText(utility.getRoundoffData("" + object.get("pricechange").getAsString()));
        String pchange = utility.getRoundoffData(object.get("pricepercentchange").getAsString());
        silverBoxPercent.setText("" + pchange + "%");

        if(Double.parseDouble(pchange)>=0) {
            silverBox.setBackgroundColor(getResources().getColor(R.color.greenText));
        }else {
            silverBox.setBackgroundColor(getResources().getColor(R.color.red));
        }
    }

    public void setCrudeoilCard() {
        JsonObject object = crudeoil.body().get("data").getAsJsonObject();
        Utility utility = new Utility();

        crudeoilDate.setText("MCX: " + utility.getFormattedDate(object.get("lastupd_epoch").getAsString()));
        crudeoilRate.setText(/*utility.getRoundoffData*/("" + object.get("pricecurrent").getAsString()));
        crudeoilBoxRate.setText(utility.getRoundoffData("" + object.get("pricechange").getAsString()));
        String pchange = utility.getRoundoffData(object.get("pricepercentchange").getAsString());
        crudeoilBoxPercent.setText("" + pchange + "%");

        if(Double.parseDouble(pchange)>=0) {
            crudeoilBox.setBackgroundColor(getResources().getColor(R.color.greenText));
        }else {
            crudeoilBox.setBackgroundColor(getResources().getColor(R.color.red));
        }
    }

    public void setDollarCard() {
        JsonObject object = usd.body().get("data").getAsJsonObject();
        Utility utility = new Utility();

        dollarDate.setText(utility.getFormattedDate(""+object.get("lastupd_epoch").getAsString()));
        dollarRate.setText(utility.getRoundoffData(""+object.get("pricecurrent").getAsString()));
        dollarBoxRate.setText(utility.getRoundoffData(""+object.get("CHANGE").getAsString()));
        String pchange = utility.getRoundoffData(""+object.get("PERCCHANGE").getAsString());
        dollarBoxPercent.setText(pchange+"%");


        if(Double.parseDouble(pchange)>=0) {
            dollarBox.setBackgroundColor(getResources().getColor(R.color.greenText));
        }else {
            dollarBox.setBackgroundColor(getResources().getColor(R.color.red));
        }
    }

    public void setEuroCard() {
        JsonObject object = eur.body().get("data").getAsJsonObject();
        Utility utility = new Utility();

        euroDate.setText(utility.getFormattedDate(""+object.get("lastupd_epoch").getAsString()));
        euroRate.setText(utility.getRoundoffData(""+object.get("pricecurrent").getAsString()));
        euroBoxRate.setText(utility.getRoundoffData(""+object.get("CHANGE").getAsString()));
        String pchange = utility.getRoundoffData(""+object.get("PERCCHANGE").getAsString());
        euroBoxPercent.setText(pchange+"%");


        if(Double.parseDouble(pchange)>=0) {
            euroBox.setBackgroundColor(getResources().getColor(R.color.greenText));
        }else {
            euroBox.setBackgroundColor(getResources().getColor(R.color.red));
        }
    }

    public void setPoundCard() {
        JsonObject object = gbp.body().get("data").getAsJsonObject();
        Utility utility = new Utility();

        poundDate.setText(utility.getFormattedDate(""+object.get("lastupd_epoch").getAsString()));
        poundRate.setText(utility.getRoundoffData(""+object.get("pricecurrent").getAsString()));
        poundBoxRate.setText(utility.getRoundoffData(""+object.get("CHANGE").getAsString()));
        String pchange = utility.getRoundoffData(""+object.get("PERCCHANGE").getAsString());
        poundBoxPercent.setText(pchange+"%");

        if(Double.parseDouble(pchange)>=0) {
            poundBox.setBackgroundColor(getResources().getColor(R.color.greenText));
        }else {
            poundBox.setBackgroundColor(getResources().getColor(R.color.red));
        }
    }

    public void getNifty50() {

        new NetworkUtility().getNifty50(new NetworkCallback() {
            @Override
            public void onSuccess(Response<JsonObject> response) {
                if(response.isSuccessful()) {
                    nifty50 = response;
                    Log.d("response NIFTY50", response.body().toString());
                    setNiftyCard();
                }else {
                    try {
                        Log.d("response ERR NIFTY50", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                count++;
                hideSwipeRefresh();
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                count++;
                hideSwipeRefresh();
            }
        });
    }

    public void getGold() {

        new NetworkUtility().getGold(getGOLDexpiry(), new NetworkCallback() {
            @Override
            public void onSuccess(Response<JsonObject> response) {
                if(response.isSuccessful()) {
                    gold = response;
                    Log.d("response GOLD", response.body().toString());
                    setGoldCard();
                }else {
                    try {
                        Log.d("response ERR GOLD", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                count++;
                hideSwipeRefresh();
            }

            @Override
            public void onError(Throwable t) {
                count++;
                hideSwipeRefresh();
                t.printStackTrace();
            }
        });
    }

    public void getSilver() {

        new NetworkUtility().getSilver(getSILVERexpiry(), new NetworkCallback() {
            @Override
            public void onSuccess(Response<JsonObject> response) {
                if(response.isSuccessful()) {
                    silver = response;
                    Log.d("response SILVER", response.body().toString());
                    setSilverCard();
                }else {
                    try {
                        Log.d("response ERR SILVER", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                count++;
                hideSwipeRefresh();
            }

            @Override
            public void onError(Throwable t) {
                count++;
                hideSwipeRefresh();
                t.printStackTrace();
            }
        });
    }

    public void getCrudeoil() {

        new NetworkUtility().getCrudeoil(getCRUDEOILexpiry(), new NetworkCallback() {
            @Override
            public void onSuccess(Response<JsonObject> response) {
                if(response.isSuccessful()) {
                    crudeoil = response;
                    Log.d("response CRUDEOIL", response.body().toString());
                    setCrudeoilCard();
                }else {
                    try {
                        Log.d("response ERR CRUDEOIL", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                count++;
                hideSwipeRefresh();
            }

            @Override
            public void onError(Throwable t) {
                count++;
                hideSwipeRefresh();
                t.printStackTrace();
            }
        });
    }

    public void getUSDINR() {

        new NetworkUtility().getUSDINR(new NetworkCallback() {
            @Override
            public void onSuccess(Response<JsonObject> response) {
                if(response.isSuccessful()) {
                    usd = response;
                    Log.d("response USDINR", response.body().toString());
                    setDollarCard();
                }else {
                    try {
                        Log.d("response ERR USDINR", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                count++;
                hideSwipeRefresh();
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                count++;
                hideSwipeRefresh();
            }
        });
    }

    public void getEURINR() {

        new NetworkUtility().getEURINR(new NetworkCallback() {
            @Override
            public void onSuccess(Response<JsonObject> response) {
                if(response.isSuccessful()) {
                    eur = response;
                    Log.d("response EURINR", response.body().toString());
                    setEuroCard();
                }else {
                    try {
                        Log.d("response ERR EURINR", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                count++;
                hideSwipeRefresh();
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                count++;
                hideSwipeRefresh();
            }
        });
    }

    public void getGBPINR() {

        new NetworkUtility().getGBPINR(new NetworkCallback() {
            @Override
            public void onSuccess(Response<JsonObject> response) {
                if(response.isSuccessful()) {
                    gbp = response;
                    Log.d("response GBPINR", response.body().toString());
                    setPoundCard();
                }else {
                    try {
                        Log.d("response ERR GBPINR", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                count++;
                hideSwipeRefresh();
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                count++;
                hideSwipeRefresh();
            }
        });
    }

    public void hideSwipeRefresh() {
        if(count == MAXCOUNT) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public String getGOLDexpiry() {
        Long epoch = System.currentTimeMillis();
        JsonArray array = object.get("expiry").getAsJsonObject().get("GOLD").getAsJsonArray();
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMMyyyy");
        int i = 0;
        for( i =0; i<array.size(); i++) {
            try {
                Date date = sdf.parse(array.get(i).getAsString());
                if(date.getTime()>epoch) break;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return array.get(i).getAsString();
    }

    public String getSILVERexpiry() {
        Long epoch = System.currentTimeMillis();
        JsonArray array = object.get("expiry").getAsJsonObject().get("SILVER").getAsJsonArray();
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMMyyyy");
        int i=0;
        for(i=0; i<array.size(); i++) {
            try {
                Date date = sdf.parse(array.get(i).getAsString());
                if(date.getTime()>epoch) break;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return array.get(i).getAsString();
    }

    public String getCRUDEOILexpiry() {
        Long epoch = System.currentTimeMillis();
        JsonArray array = object.get("expiry").getAsJsonObject().get("CRUDEOIL").getAsJsonArray();
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMMyyyy");
        int i =0;
        for(i=0; i<array.size(); i++) {
            try {
                Date date = sdf.parse(array.get(i).getAsString());
                if(date.getTime()>epoch) break;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return array.get(i).getAsString();
    }

}
