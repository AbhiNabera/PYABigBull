package nabera.ranjan.abhinabera.pyabigbull.Dashboard;

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

import nabera.ranjan.abhinabera.pyabigbull.Api.NetworkCallback;
import nabera.ranjan.abhinabera.pyabigbull.Api.RetrofitClient;
import nabera.ranjan.abhinabera.pyabigbull.DataActivities.Currency.CurrencyActivity;
import nabera.ranjan.abhinabera.pyabigbull.DataActivities.Commodity.CommodityActivity;
import nabera.ranjan.abhinabera.pyabigbull.DataActivities.FixedDepositActivity;
import nabera.ranjan.abhinabera.pyabigbull.DataActivities.Nifty50.NiftyActivity;
import nabera.ranjan.abhinabera.pyabigbull.R;
import nabera.ranjan.abhinabera.pyabigbull.Api.Utility;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class DataFragment extends Fragment {

    private int count = 0;
    private int MAXCOUNT = 5;

    //private static boolean flag = true;

    Response<JsonObject> nifty50, usd, eur, gbp;

    JsonObject gold, silver, crudeoil, userObject;

    CardView niftyCard, goldCard, silverCard, crudeoilCard, dollarCard, euroCard, poundCard, fixedDeposit;

    TextView niftyDate, goldDate, silverDate, crudeoilDate, dollarDate, euroDate, poundDate,
            nifty50Rate, goldRate, silverRate, crudeoilRate, dollarRate, euroRate, poundRate,
            nifty50BoxRate, goldBoxRate, silverBoxRate, crudeoilBoxRate, dollarBoxRate, euroBoxRate, poundBoxRate,
            nifty50BoxPercent, goldBoxPercent, silverBoxPercent, crudeoilBoxPercent, dollarBoxPercent, euroBoxPercent,
            poundBoxPercent, fixedDepositDate, fdinvestment;

    LinearLayout nifty50Box, goldBox, silverBox, crudeoilBox, dollarBox, euroBox, poundBox;

    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onResume() {
        super.onResume();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                count = 0;
                getNifty50();
                getUSDINR();
                getEURINR();
                getGBPINR();
                getTopCommodity();
            }
        },5);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_data, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        niftyCard = (CardView) view.findViewById(R.id.niftyCard);
        goldCard = (CardView) view.findViewById(R.id.goldCard);
        silverCard = (CardView) view.findViewById(R.id.silverCard);
        crudeoilCard = (CardView) view.findViewById(R.id.crudeoilCard);
        dollarCard = (CardView) view.findViewById(R.id.dollarCard);
        euroCard = (CardView) view.findViewById(R.id.euroCard);
        poundCard = (CardView) view.findViewById(R.id.poundCard);
        fixedDeposit = (CardView) view.findViewById(R.id.fixedDepositCard);

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
        fixedDepositDate = (TextView) view.findViewById(R.id.fixedDepositDate);
        fdinvestment = (TextView) view.findViewById(R.id.fdinvestment);

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
                //getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        goldCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), CommodityActivity.class);
                i.putExtra("cardName", "GOLD");
                startActivity(i);
                //getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        silverCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), CommodityActivity.class);
                i.putExtra("cardName", "SILVER");
                startActivity(i);
                //getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        crudeoilCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), CommodityActivity.class);
                i.putExtra("cardName", "CRUDEOIL");
                startActivity(i);
                //getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        dollarCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), CurrencyActivity.class);
                i.putExtra("cardName", "DOLLAR");
                startActivity(i);
                //getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        euroCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), CurrencyActivity.class);
                i.putExtra("cardName", "EURO");
                startActivity(i);
                //getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        poundCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), CurrencyActivity.class);
                i.putExtra("cardName", "POUND");
                startActivity(i);
                //getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        fixedDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), FixedDepositActivity.class);
                i.putExtra("cardName", "FIXED DEPOSIT");
                startActivity(i);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                count = 0;
                getNifty50();
                getUSDINR();
                getEURINR();
                getGBPINR();
                getTopCommodity();
                //getFdInvestment();
                //getUserAccount();//Automated operation cron job
            }
        });

        //getFdInvestment();
        //getUserAccount(); ////Automated operation cron job
    }

    public void setNiftyCard() {

        JsonObject object = nifty50.body().get("indices").getAsJsonObject();
        Utility utility = new Utility();

        niftyDate.setText(""+object.get("lastupdated").getAsString());
        nifty50Rate.setText(utility.getRoundoffData(object.get("lastprice").getAsString().replace(",","")+""));
        nifty50BoxRate.setText(utility.getRoundoffData(object.get("change").getAsString()+""));
        String pchange = utility.getRoundoffData(object.get("percentchange").getAsString());
        nifty50BoxPercent.setText(pchange+"%");
        if(Double.parseDouble(pchange)>=0) {
            nifty50Box.setBackgroundColor(getActivity().getResources().getColor(R.color.greenText));
        }else {
            nifty50Box.setBackgroundColor(getActivity().getResources().getColor(R.color.red));
        }
    }

    public void setGoldCard() {
        JsonObject object = gold;
        Utility utility = new Utility();

        goldDate.setText("MCX: " + (object.get("lastupdate").getAsString()+""));
        goldRate.setText(utility.getRoundoffData(object.get("lastprice").getAsString().replace(",","")+""));
        goldBoxRate.setText(utility.getRoundoffData("" + object.get("change").getAsString()));
        String pchange = utility.getRoundoffData(object.get("percentchange").getAsString());
        goldBoxPercent.setText("" + pchange + "%");

        if(Double.parseDouble(pchange)>=0) {
            goldBox.setBackgroundColor(getActivity().getResources().getColor(R.color.greenText));
        }else {
            goldBox.setBackgroundColor(getActivity().getResources().getColor(R.color.red));
        }
    }

    public void setSilverCard() {
        JsonObject object = silver;
        Utility utility = new Utility();

        silverDate.setText("MCX: " + object.get("lastupdate").getAsString()+"");
        silverRate.setText(utility.getRoundoffData(object.get("lastprice").getAsString().replace(",","")+ ""));
        silverBoxRate.setText(utility.getRoundoffData("" + object.get("change").getAsString()));
        String pchange = utility.getRoundoffData(object.get("percentchange").getAsString());
        silverBoxPercent.setText("" + pchange + "%");

        if(Double.parseDouble(pchange)>=0) {
            silverBox.setBackgroundColor(getActivity().getResources().getColor(R.color.greenText));
        }else {
            silverBox.setBackgroundColor(getActivity().getResources().getColor(R.color.red));
        }
    }

    public void setCrudeoilCard() {
        JsonObject object = crudeoil;
        Utility utility = new Utility();

        crudeoilDate.setText("MCX: " + object.get("lastupdate").getAsString()+"");
        crudeoilRate.setText(utility.getRoundoffData(object.get("lastprice").getAsString().replace(",","")+""));
        crudeoilBoxRate.setText(utility.getRoundoffData("" + object.get("change").getAsString()));
        String pchange = utility.getRoundoffData(object.get("percentchange").getAsString());
        crudeoilBoxPercent.setText("" + pchange + "%");

        if(Double.parseDouble(pchange)>=0) {
            crudeoilBox.setBackgroundColor(getActivity().getResources().getColor(R.color.greenText));
        }else {
            crudeoilBox.setBackgroundColor(getActivity().getResources().getColor(R.color.red));
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
            dollarBox.setBackgroundColor(getActivity().getResources().getColor(R.color.greenText));
        }else {
            dollarBox.setBackgroundColor(getActivity().getResources().getColor(R.color.red));
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
            euroBox.setBackgroundColor(getActivity().getResources().getColor(R.color.greenText));
        }else {
            euroBox.setBackgroundColor(getActivity().getResources().getColor(R.color.red));
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
            poundBox.setBackgroundColor(getActivity().getResources().getColor(R.color.greenText));
        }else {
            poundBox.setBackgroundColor(getActivity().getResources().getColor(R.color.red));
        }
    }

    public void setFDCard() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Utility.MyPREF, MODE_PRIVATE);
        fixedDepositDate.setText(new Utility().getFormattedDate((System.currentTimeMillis()/1000)+""));
        fdinvestment.setText(new Utility().getRoundoffData(sharedPreferences.getString("total_investment", "0.0")));
    }

    public void getNifty50() {

        new NetworkUtility().getNifty50(new NetworkCallback() {
            @Override
            public void onSuccess(Response<JsonObject> response) {
                if(response.isSuccessful()) {
                    nifty50 = response;
                    //Log.d("response NIFTY50", response.body().toString());
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

    public void getTopCommodity() {

        new RetrofitClient().getNifty50Interface().getTopCommodity().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful()) {
                    //Log.d("response COMMODITY", response.body().toString());

                    int i = 0;

                    for(JsonElement element: response.body().get("list").getAsJsonArray()) {

                        switch (element.getAsJsonObject().get("id").getAsString()) {

                            case "GOLD" :
                                i++;
                                gold = element.getAsJsonObject();
                                break;

                            case "SILVER" :
                                i++;
                                silver = element.getAsJsonObject();
                                break;

                            case "CRUDEOIL" :
                                i++;
                                crudeoil = element.getAsJsonObject();
                                break;

                            default:
                                break;
                        }

                        if(i==3) break;
                    }

                    setGoldCard();
                    setSilverCard();
                    setCrudeoilCard();

                }else{
                    try {
                        Log.d("response COMMODITY", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                count++;
                hideSwipeRefresh();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
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
                    //Log.d("response USDINR", response.body().toString());
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
                    //Log.d("response EURINR", response.body().toString());
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
                    //Log.d("response GBPINR", response.body().toString());
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

    /*
    public void getFdInvestment() {

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Utility.MyPREF, MODE_PRIVATE);
        long current_timestamp = Calendar.getInstance().getTime().getTime();

        if(sharedPreferences.getString("nextupdate", null) != null) {
            if(current_timestamp > Long.parseLong(sharedPreferences.getString("nextupdate", null) )) {
                Log.d("calling ", "fd update function");
                getUserAccount();
            }else{
                setFDCard();
            }
        } else {
            getUserAccount();
        }
    }

    public void getUserAccount() {

        new RetrofitClient().getInterface().getUserAccount(FirebaseAuth.getInstance().
                getCurrentUser().getPhoneNumber().substring(3)).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if(response.isSuccessful()) {
                    Log.d("response", response.body()+"");
                    if(response.body().getAsJsonObject("data")!=null) {

                        userObject = response.body();
                        FDAmtUpdateUtility fdAmtUpdateUtility = new FDAmtUpdateUtility();
                        fdAmtUpdateUtility.executeTransaction(fdAmtUpdateUtility.getUpdatedAmount(userObject),
                                getActivity(), new FDAmtUpdateUtility.TaskListener() {
                                    @Override
                                    public void onComplete() {
                                        setFDCard();
                                    }
                                });
                    }else {

                        Toast.makeText(getActivity(), "Internal server error", Toast.LENGTH_SHORT).show();

                    }

                }else {
                    try {
                        Log.d("Fixed deposit error", response.errorBody().string()+"");
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
    */

    public void hideSwipeRefresh() {
        if(count == MAXCOUNT) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public String getAmount() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Utility.MyPREF, MODE_PRIVATE);
        return sharedPreferences.getString("total_investment", null);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
}