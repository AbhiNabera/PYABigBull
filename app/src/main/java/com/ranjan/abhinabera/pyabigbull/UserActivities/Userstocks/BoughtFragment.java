package com.ranjan.abhinabera.pyabigbull.UserActivities.Userstocks;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ranjan.abhinabera.pyabigbull.Api.NetworkCallback;
import com.ranjan.abhinabera.pyabigbull.Api.RetrofitClient;
import com.ranjan.abhinabera.pyabigbull.Api.Utility;
import com.ranjan.abhinabera.pyabigbull.Dashboard.NetworkUtility;
import com.ranjan.abhinabera.pyabigbull.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by AVINASH on 12/31/2018.
 */

public class BoughtFragment extends Fragment {

    private int count = 0;
    private int MAXCOUNT = 5;

    private double index_txn_charges, commodity_txn_charges, currency_txn_charges;

    int REQUEST_CODE = 1;
    double PORTFOLIO_VALUE = 0;
    double ACCOUNT_BALANCE = 0;
    double TOTAL_VALUE = 0;

    private double NIFTY_INVESTMENT = 0;
    private double GOLD_INVESTMENT = 0;
    private double SILVER_INVESTMENT = 0;
    private double CRUDEOIL_INVESTMENT = 0;
    private double CURRENCY_INVESTMENT = 0;
    private double FD_INVESTMENT = 0;

    private double NIFTY_CURRENTVALUE = 0;
    private double GOLD_CURRENTVALUE = 0;
    private double SILVER_CURRENTVALUE = 0;
    private double CRUDEOIL_CURRENTVALUE = 0;
    private double CURRENCY_CURRENTVALUE = 0;
    private double FD_CURRENTVALUE = 0;

    Response<JsonObject> usd, eur, gbp;
    JsonObject gold, silver, crudeoil;
    List<JsonObject> stockList;
    HashMap<String, JsonObject> map, currency, commodity;

    SwipeRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    CardView niftyCard, goldCard, silverCard, crudeOilCard, currencyCard, fixedDepositCard;
    TextView portfolioValue, accountBal, totalValue;

    TextView niftyCV, niftyProfit, niftyProfitPer, goldCV, goldProfit, goldProfitPer, silverCV, silverProfit, silverProfitPer,
                crudeOilCV, crudeOilProfit, crudeOilProfitPer, currencyCV, currencyProfit, currencyProfitPer, fixedDepositCV, fixedDepositProfit,
                fixedDepositProfitPer;
    
    LinearLayout niftyBox, goldBox, silverBox, crudeOilBox, currencyBox, fixedDepositBox;

    ArrayList<JsonObject> arrayList, niftyList, goldList, silverList, crudeoilList, currencyList, fdList;

    StocksRecyclerAdapter stocksRecyclerAdapter;


    public void getLoadFragmentData() {

        NIFTY_INVESTMENT = 0;
        GOLD_INVESTMENT = 0;
        SILVER_INVESTMENT = 0;
        CRUDEOIL_INVESTMENT = 0;
        CURRENCY_INVESTMENT = 0;
        FD_INVESTMENT = 0;

        NIFTY_CURRENTVALUE = 0;
        GOLD_CURRENTVALUE = 0;
        SILVER_CURRENTVALUE = 0;
        CRUDEOIL_CURRENTVALUE = 0;
        CURRENCY_CURRENTVALUE = 0;
        FD_CURRENTVALUE = 0;

        refreshLayout.setRefreshing(true);
        count = 0;
        //ACCOUNT_BALANCE = 0;
        PORTFOLIO_VALUE = 0;
        TOTAL_VALUE = 0;
        getTopCommodity();
        getEURINR();
        getGBPINR();
        getUSDINR();
        getStockList();
    }

    public BoughtFragment(){}

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (/*requestCode == REQUEST_CODE  && */resultCode  == RESULT_OK) {

            //unsuccessful transaction
            Log.d("unsuccessfultransaction", "");

        }else {

            Intent intent = new Intent("soldFragment");
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

            getLoadFragmentData();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bought, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fdList = new ArrayList<>();
        stockList = new ArrayList<>();
        arrayList = new ArrayList<>();
        niftyList = new ArrayList<>();
        goldList = new ArrayList<>();
        silverList = new ArrayList<>();
        crudeoilList = new ArrayList<>();
        currencyList = new ArrayList<>();

        map = new HashMap<>();
        currency = new HashMap<>();
        commodity = new HashMap<>();

        //ACCOUNT_BALANCE = Double.parseDouble(getActivity().getIntent().getStringExtra("acc_bal"));

        accountBal = (TextView) view.findViewById(R.id.accountBalance);
        portfolioValue = (TextView) view.findViewById(R.id.portfolioValue);
        totalValue = (TextView) view.findViewById(R.id.totalValue);
        //recyclerView = (RecyclerView) view.findViewById(R.id.boughtRecycler);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);

        niftyCard = (CardView) view.findViewById(R.id.niftyCard);
        goldCard = (CardView) view.findViewById(R.id.goldCard);
        silverCard = (CardView) view.findViewById(R.id.silverCard);
        crudeOilCard = (CardView) view.findViewById(R.id.crudeOilCard);
        currencyCard = (CardView) view.findViewById(R.id.currencyCard);
        fixedDepositCard = (CardView) view.findViewById(R.id.fixedDepositCard);

        niftyBox = (LinearLayout) view.findViewById(R.id.niftyBox);
        goldBox = (LinearLayout) view.findViewById(R.id.goldBox);
        silverBox = (LinearLayout) view.findViewById(R.id.silverBox);
        crudeOilBox = (LinearLayout) view.findViewById(R.id.crudeOilBox);
        currencyBox = (LinearLayout) view.findViewById(R.id.currencyBox);
        fixedDepositBox = (LinearLayout) view.findViewById(R.id.fixedDepositBox);

        niftyCV = (TextView) view.findViewById(R.id.nifty50CurrentValue);
        niftyProfit = (TextView) view.findViewById(R.id.niftyBoxProfit);
        niftyProfitPer = (TextView) view.findViewById(R.id.niftyBoxProfitPercent);
        goldCV = (TextView) view.findViewById(R.id.goldCurrentValue);
        goldProfit = (TextView) view.findViewById(R.id.goldBoxProfit);
        goldProfitPer = (TextView) view.findViewById(R.id.goldBoxProfitPercent);
        silverCV = (TextView) view.findViewById(R.id.silverCurrentValue);
        silverProfit = (TextView) view.findViewById(R.id.silverBoxProfit);
        silverProfitPer = (TextView) view.findViewById(R.id.silverBoxProfitPercent);
        crudeOilCV = (TextView) view.findViewById(R.id.crudeOilCurrentValue);
        crudeOilProfit = (TextView) view.findViewById(R.id.crudeOilBoxProfit);
        crudeOilProfitPer = (TextView) view.findViewById(R.id.crudeOilBoxProfitPercent);
        currencyCV = (TextView) view.findViewById(R.id.currencyCurrentValue);
        currencyProfit = (TextView) view.findViewById(R.id.currencyBoxProfit);
        currencyProfitPer = (TextView) view.findViewById(R.id.currencyBoxProfitPercent);
        fixedDepositCV = (TextView) view.findViewById(R.id.fixedDepositCurrentValue);
        fixedDepositProfit = (TextView) view.findViewById(R.id.fixedDepositBoxProfit);
        fixedDepositProfitPer = (TextView) view.findViewById(R.id.fixedDepositBoxProfitPercent);
        
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLoadFragmentData();
            }
        });


        getLoadFragmentData();

        niftyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!refreshLayout.isRefreshing()) {
                    Intent i = new Intent(getActivity(), BoughtActivityIndi.class);
                    i.putExtra("cardName", "NIFTY50");
                    Gson gson = new Gson();
                    i.putExtra("data", gson.toJson(niftyList) + "");
                    i.putExtra("current_value", NIFTY_CURRENTVALUE);
                    i.putExtra("investment", NIFTY_INVESTMENT);
                    startActivityForResult(i, REQUEST_CODE);
                }
            }
        });

        goldCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!refreshLayout.isRefreshing()) {
                    Intent i = new Intent(getActivity(), BoughtActivityIndi.class);
                    i.putExtra("cardName", "GOLD");
                    Gson gson = new Gson();
                    i.putExtra("data", gson.toJson(goldList) + "");
                    i.putExtra("current_value", GOLD_CURRENTVALUE);
                    i.putExtra("investment", GOLD_INVESTMENT);
                    startActivityForResult(i, REQUEST_CODE);
                }
            }
        });

        silverCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!refreshLayout.isRefreshing()) {
                    Intent i = new Intent(getActivity(), BoughtActivityIndi.class);
                    i.putExtra("cardName", "SILVER");
                    Gson gson = new Gson();
                    i.putExtra("data", gson.toJson(silverList) + "");
                    i.putExtra("current_value", SILVER_CURRENTVALUE);
                    i.putExtra("investment", SILVER_INVESTMENT);
                    startActivityForResult(i, REQUEST_CODE);
                }
            }
        });

        crudeOilCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!refreshLayout.isRefreshing()) {
                    Intent i = new Intent(getActivity(), BoughtActivityIndi.class);
                    i.putExtra("cardName", "CRUDEOIL");
                    Gson gson = new Gson();
                    i.putExtra("data", gson.toJson(crudeoilList) + "");
                    i.putExtra("current_value", CRUDEOIL_CURRENTVALUE);
                    i.putExtra("investment", CRUDEOIL_INVESTMENT);
                    startActivityForResult(i, REQUEST_CODE);
                }
            }
        });

        currencyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!refreshLayout.isRefreshing()) {
                    Intent i = new Intent(getActivity(), BoughtActivityIndi.class);
                    i.putExtra("cardName", "CURRENCY");
                    Gson gson = new Gson();
                    i.putExtra("data", gson.toJson(currencyList) + "");
                    i.putExtra("current_value", CURRENCY_CURRENTVALUE);
                    i.putExtra("investment", CURRENCY_INVESTMENT);
                    startActivityForResult(i, REQUEST_CODE);
                }
            }
        });

        fixedDepositCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!refreshLayout.isRefreshing()) {
                    Intent i = new Intent(getActivity(), BoughtActivityIndi.class);
                    i.putExtra("cardName", "FIXED DEPOSIT");
                    Gson gson = new Gson();
                    i.putExtra("data", gson.toJson(fdList) + "");
                    i.putExtra("current_value", FD_CURRENTVALUE);
                    i.putExtra("investment", FD_INVESTMENT);
                    startActivityForResult(i, REQUEST_CODE);
                }
            }
        });

    }

    public void getBoughtfragmentData() {

        refreshLayout.setRefreshing(true);

        new RetrofitClient().getInterface().getBoughtFragmentData(FirebaseAuth.getInstance().getCurrentUser()
                .getPhoneNumber().substring(3)).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                try {

                    if (response.isSuccessful()) {

                        if (response.body().getAsJsonObject("data") != null) {

                            fdList.clear();
                            niftyList.clear();
                            goldList.clear();
                            silverList.clear();
                            crudeoilList.clear();
                            currencyList.clear();
                            arrayList.clear();

                            ACCOUNT_BALANCE = response.body().getAsJsonObject("data").get("avail_balance").getAsDouble();

                            JsonObject bought_items = response.body().getAsJsonObject("data").get("bought_items").getAsJsonObject();
                            JsonObject admin_settings = response.body().getAsJsonObject("data").get("admin_settings").getAsJsonObject();

                            index_txn_charges = Double.parseDouble(admin_settings.
                                    get("trans_amt_nifty").getAsString().replace(",", ""));

                            commodity_txn_charges = Double.parseDouble(admin_settings.
                                    get("trans_amt_commodity").getAsString().replace(",", ""));

                            currency_txn_charges = Double.parseDouble(admin_settings.
                                    get("trans_amt_currency").getAsString().replace(",", ""));


                            if (bought_items.get("index").getAsJsonArray().size() != 0) {

                                JsonObject object = new JsonObject();
                                object.addProperty("TYPE", "index");
                                arrayList.add(object);

                                for (JsonElement element : bought_items.get("index").getAsJsonArray()) {

                                    double current_price = Double.parseDouble(map.get(element.getAsJsonObject().get("id").getAsString())
                                            .get("lastvalue").getAsString().trim().replace(",", ""));

                                    double current_value = current_price * Integer.parseInt(element.getAsJsonObject().
                                            get("qty").getAsString()) /*- index_txn_charges*/;

                                    double changeamount = current_value - Double.parseDouble(element.getAsJsonObject().
                                            get("total_amount").getAsString()) - index_txn_charges;

                                    double pchange = ((current_price * Integer.parseInt(element.getAsJsonObject().
                                            get("qty").getAsString()) - Double.parseDouble(element.getAsJsonObject().
                                            get("total_amount").getAsString()) - index_txn_charges) / Double.parseDouble(element.getAsJsonObject().
                                            get("total_amount").getAsString())) * 100;

                                    element.getAsJsonObject().
                                            addProperty("current_price", current_price);

                                    element.getAsJsonObject().
                                            addProperty("pchange", pchange);

                                    element.getAsJsonObject().
                                            addProperty("current_value", current_value);

                                    element.getAsJsonObject().
                                            addProperty("changeamount", changeamount);

                                    element.getAsJsonObject().
                                            addProperty("type", "NIFTY");

                                    arrayList.add(element.getAsJsonObject());

                                    PORTFOLIO_VALUE += current_value;

                                    NIFTY_CURRENTVALUE += current_value;
                                    NIFTY_INVESTMENT += element.getAsJsonObject().
                                            get("total_amount").getAsDouble();

                                    niftyList.add(element.getAsJsonObject());
                                }
                            }

                            if (bought_items.get("commodity").getAsJsonArray().size() != 0) {

                                JsonObject object = new JsonObject();
                                object.addProperty("TYPE", "commodity");
                                arrayList.add(object);

                                for (JsonElement element : bought_items.get("commodity").getAsJsonArray()) {

                                    double current_price = Double.parseDouble(commodity.get(element.getAsJsonObject().get("id").getAsString())
                                            .get("lastprice").getAsString().trim().replace(",", ""));

                                    if (element.getAsJsonObject().get("name").getAsString().equalsIgnoreCase("SILVER")) {

                                        current_price = current_price * 0.1;
                                    }

                                    double current_value = current_price * Integer.parseInt(element.getAsJsonObject().
                                            get("qty").getAsString()) /*- index_txn_charges*/;

                                    double changeamount = current_value - Double.parseDouble(element.getAsJsonObject().
                                            get("total_amount").getAsString()) - commodity_txn_charges;

                                    double pchange = ((changeamount) / Double.parseDouble(element.getAsJsonObject().
                                            get("total_amount").getAsString())) * 100;

                                    element.getAsJsonObject().
                                            addProperty("current_price", current_price);

                                    element.getAsJsonObject().
                                            addProperty("current_value", current_value);

                                    element.getAsJsonObject().
                                            addProperty("changeamount", changeamount);

                                    element.getAsJsonObject().
                                            addProperty("pchange", pchange);

                                    element.getAsJsonObject().
                                            addProperty("type", "COMMODITY");

                                    arrayList.add(element.getAsJsonObject());

                                    PORTFOLIO_VALUE += current_value;

                                    if (element.getAsJsonObject().get("name").getAsString().equalsIgnoreCase("GOLD")) {

                                        GOLD_CURRENTVALUE += current_value;
                                        GOLD_INVESTMENT += element.getAsJsonObject().
                                                get("total_amount").getAsDouble();

                                        goldList.add(element.getAsJsonObject());

                                    } else if (element.getAsJsonObject().get("name").getAsString().equalsIgnoreCase("SILVER")) {

                                        SILVER_CURRENTVALUE += current_value;
                                        SILVER_INVESTMENT += element.getAsJsonObject().
                                                get("total_amount").getAsDouble();
                                        silverList.add(element.getAsJsonObject());

                                    } else if (element.getAsJsonObject().get("name").getAsString().equalsIgnoreCase("CRUDEOIL")) {

                                        CRUDEOIL_CURRENTVALUE += current_value;
                                        CRUDEOIL_INVESTMENT += element.getAsJsonObject().
                                                get("total_amount").getAsDouble();
                                        crudeoilList.add(element.getAsJsonObject());
                                    }
                                }
                            }

                            if (bought_items.get("currency").getAsJsonArray().size() != 0) {

                                JsonObject object = new JsonObject();
                                object.addProperty("TYPE", "currency");
                                arrayList.add(object);

                                for (JsonElement element : bought_items.get("currency").getAsJsonArray()) {

                                    double current_price = Double.parseDouble(currency.get(element.getAsJsonObject().get("id").getAsString())
                                            .get("data").getAsJsonObject().get("pricecurrent").getAsString().trim().replace(",", ""));

                                    double current_value = current_price * Integer.parseInt(element.getAsJsonObject().
                                            get("qty").getAsString()) /*- index_txn_charges*/;

                                    double changeamount = current_value - Double.parseDouble(element.getAsJsonObject().
                                            get("total_amount").getAsString()) - currency_txn_charges;

                                    double pchange = ((current_price * Integer.parseInt(element.getAsJsonObject().
                                            get("qty").getAsString()) - Double.parseDouble(element.getAsJsonObject().
                                            get("total_amount").getAsString()) - currency_txn_charges) / Double.parseDouble(element.getAsJsonObject().
                                            get("total_amount").getAsString())) * 100;

                                    element.getAsJsonObject().
                                            addProperty("current_price", current_price);

                                    element.getAsJsonObject().
                                            addProperty("pchange", pchange);

                                    element.getAsJsonObject().
                                            addProperty("current_value", current_value);

                                    element.getAsJsonObject().
                                            addProperty("changeamount", changeamount);

                                    element.getAsJsonObject().
                                            addProperty("type", "CURRENCY");

                                    arrayList.add(element.getAsJsonObject());

                                    PORTFOLIO_VALUE += current_value;

                                    CURRENCY_CURRENTVALUE += current_value;
                                    CURRENCY_INVESTMENT += element.getAsJsonObject().
                                            get("total_amount").getAsDouble();

                                    currencyList.add(element.getAsJsonObject());
                                }
                            }

                            if (bought_items.get("fixed_deposit").getAsJsonArray().size() != 0) {

                                JsonObject object = new JsonObject();
                                object.addProperty("TYPE", "fixed_deposit");
                                arrayList.add(object);

                                for (JsonElement element : bought_items.get("fixed_deposit").getAsJsonArray()) {

                                    double current_value = element.getAsJsonObject().
                                            get("current_value").getAsDouble();

                                    double investment = element.getAsJsonObject().
                                            get("investment").getAsDouble();

                                    double changeamount = current_value - investment;

                                    double pchange = (changeamount / investment) * 100;

                                    element.getAsJsonObject().
                                            addProperty("current_price", current_value);

                                    element.getAsJsonObject().
                                            addProperty("pchange", pchange);

                                    element.getAsJsonObject().
                                            addProperty("current_value", current_value);

                                    element.getAsJsonObject().
                                            addProperty("changeamount", changeamount);

                                    element.getAsJsonObject().
                                            addProperty("type", "fixed_deposit");

                                    element.getAsJsonObject().addProperty("txn_amt", 0.0);
                                    element.getAsJsonObject().addProperty("total_amount", element.getAsJsonObject().
                                            get("investment").getAsDouble());
                                    element.getAsJsonObject().addProperty("buy_price", Utility.BASE_AMT + "");
                                    element.getAsJsonObject().addProperty("name", "FD");
                                    element.getAsJsonObject().addProperty("id", "FD");

                                    arrayList.add(element.getAsJsonObject());

                                    PORTFOLIO_VALUE += current_value;

                                    FD_CURRENTVALUE += current_value;
                                    FD_INVESTMENT += element.getAsJsonObject().
                                            get("investment").getAsDouble();

                                    fdList.add(element.getAsJsonObject());
                                }
                            }

                            //PORTFOLIO_VALUE += ACCOUNT_BALANCE;
                            TOTAL_VALUE = PORTFOLIO_VALUE + ACCOUNT_BALANCE;

                            portfolioValue.setText(new Utility().getRoundoffData(PORTFOLIO_VALUE + ""));
                            accountBal.setText(new Utility().getRoundoffData(ACCOUNT_BALANCE + ""));
                            totalValue.setText(new Utility().getRoundoffData(TOTAL_VALUE + ""));

                            niftyCV.setText(new Utility().getRoundoffData("" + NIFTY_CURRENTVALUE));
                            goldCV.setText(new Utility().getRoundoffData("" + GOLD_CURRENTVALUE));
                            silverCV.setText(new Utility().getRoundoffData("" + SILVER_CURRENTVALUE));
                            crudeOilCV.setText(new Utility().getRoundoffData("" + CRUDEOIL_CURRENTVALUE));
                            currencyCV.setText(new Utility().getRoundoffData("" + CURRENCY_CURRENTVALUE));
                            fixedDepositCV.setText(new Utility().getRoundoffData("" + FD_CURRENTVALUE));

                            niftyProfit.setText(new Utility().getRoundoffData("" + (NIFTY_CURRENTVALUE - NIFTY_INVESTMENT)));
                            goldProfit.setText(new Utility().getRoundoffData("" + (GOLD_CURRENTVALUE - GOLD_INVESTMENT)));
                            silverProfit.setText(new Utility().getRoundoffData("" + (SILVER_CURRENTVALUE - SILVER_INVESTMENT)));
                            crudeOilProfit.setText(new Utility().getRoundoffData("" + (CRUDEOIL_CURRENTVALUE - CRUDEOIL_INVESTMENT)));
                            currencyProfit.setText(new Utility().getRoundoffData("" + (CURRENCY_CURRENTVALUE - CURRENCY_INVESTMENT)));
                            fixedDepositProfit.setText(new Utility().getRoundoffData("" + (FD_CURRENTVALUE - FD_INVESTMENT)));

                            niftyProfitPer.setText(new Utility().getRoundoffData("" + ((NIFTY_CURRENTVALUE - NIFTY_INVESTMENT) / NIFTY_INVESTMENT * 100)) + "%");
                            goldProfitPer.setText(new Utility().getRoundoffData("" + ((GOLD_CURRENTVALUE - GOLD_INVESTMENT) / GOLD_INVESTMENT * 100)) + "%");
                            silverProfitPer.setText(new Utility().getRoundoffData("" + ((SILVER_CURRENTVALUE - SILVER_INVESTMENT) / SILVER_INVESTMENT * 100)) + "%");
                            crudeOilProfitPer.setText(new Utility().getRoundoffData("" + ((CRUDEOIL_CURRENTVALUE - CRUDEOIL_INVESTMENT) / CRUDEOIL_INVESTMENT * 100)) + "%");
                            currencyProfitPer.setText(new Utility().getRoundoffData("" + ((CURRENCY_CURRENTVALUE - CURRENCY_INVESTMENT) / CURRENCY_INVESTMENT * 100)) + "%");
                            fixedDepositProfitPer.setText(new Utility().getRoundoffData("" + ((FD_CURRENTVALUE - FD_INVESTMENT) / FD_INVESTMENT * 100)) + "%");

                            if (NIFTY_INVESTMENT == 0) {
                                niftyProfitPer.setText("0.00%");
                            }
                            if (GOLD_INVESTMENT == 0) {
                                goldProfitPer.setText("0.00%");
                            }
                            if (SILVER_INVESTMENT == 0) {
                                silverProfitPer.setText("0.00%");
                            }
                            if (CRUDEOIL_INVESTMENT == 0) {
                                crudeOilProfitPer.setText("0.00%");
                            }
                            if (CURRENCY_INVESTMENT == 0) {
                                currencyProfitPer.setText("0.00%");
                            }
                            if (FD_INVESTMENT == 0) {
                                fixedDepositProfitPer.setText("0.00%");
                            }
                            if (Double.parseDouble(niftyProfit.getText().toString()) >= 0) {
                                niftyBox.setBackgroundColor(getActivity().getResources().getColor(R.color.greenText));
                            } else
                                niftyBox.setBackgroundColor(getActivity().getResources().getColor(R.color.red));
                            if (Double.parseDouble(goldProfit.getText().toString()) >= 0) {
                                goldBox.setBackgroundColor(getActivity().getResources().getColor(R.color.greenText));
                            } else
                                goldBox.setBackgroundColor(getActivity().getResources().getColor(R.color.red));
                            if (Double.parseDouble(silverProfit.getText().toString()) >= 0) {
                                silverBox.setBackgroundColor(getActivity().getResources().getColor(R.color.greenText));
                            } else
                                silverBox.setBackgroundColor(getActivity().getResources().getColor(R.color.red));
                            if (Double.parseDouble(crudeOilProfit.getText().toString()) >= 0) {
                                crudeOilBox.setBackgroundColor(getActivity().getResources().getColor(R.color.greenText));
                            } else
                                crudeOilBox.setBackgroundColor(getActivity().getResources().getColor(R.color.red));
                            if (Double.parseDouble(currencyProfit.getText().toString()) >= 0) {
                                currencyBox.setBackgroundColor(getActivity().getResources().getColor(R.color.greenText));
                            } else
                                currencyBox.setBackgroundColor(getActivity().getResources().getColor(R.color.red));
                            if (Double.parseDouble(fixedDepositProfit.getText().toString()) >= 0) {
                                fixedDepositBox.setBackgroundColor(getActivity().getResources().getColor(R.color.greenText));
                            } else
                                fixedDepositBox.setBackgroundColor(getActivity().getResources().getColor(R.color.red));

                        } else {
                            Toast.makeText(getActivity(), "Internal server error", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e) {
                    e.printStackTrace();
                }

                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
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
                                commodity.put("GOLD", gold);
                                break;

                            case "SILVER" :
                                i++;
                                silver = element.getAsJsonObject();
                                commodity.put("SILVER", silver);
                                break;

                            case "CRUDEOIL" :
                                i++;
                                crudeoil = element.getAsJsonObject();
                                commodity.put("CRUDEOIL", crudeoil);
                                break;

                            default:
                                break;
                        }

                        if(i==3) break;
                    }

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
                    currency.put("USDINR", usd.body());
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
                    currency.put("EURINR", eur.body());
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
                    currency.put("GBPINR", gbp.body());
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

    public void getStockList() {

        new RetrofitClient().getNifty50Interface().getNify50StockList().enqueue(new Callback<List<JsonObject>>() {
            @Override
            public void onResponse(Call<List<JsonObject>> call, Response<List<JsonObject>> response) {

                if(response.isSuccessful()) {

                    stockList.clear();
                    stockList.addAll(response.body());

                    map.clear();

                    for(JsonObject object: stockList) {
                        map.put(object.get("id").getAsString(), object);
                    }

                    Log.d("NIFTYStockFragment", stockList.size()+"");

                }else{

                    Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();

                }

                count++;
                hideSwipeRefresh();
            }

            @Override
            public void onFailure(Call<List<JsonObject>> call, Throwable t) {
                t.printStackTrace();
                count++;
                hideSwipeRefresh();
                Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void hideSwipeRefresh() {
        Log.d("count", ""+count);
        if(count == MAXCOUNT) {
            if(usd!=null && eur != null && gbp != null && gold != null && silver != null && crudeoil != null && stockList != null) {
                refreshLayout.setRefreshing(false);
                getBoughtfragmentData();
            }else {
                refreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "error occured ", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
