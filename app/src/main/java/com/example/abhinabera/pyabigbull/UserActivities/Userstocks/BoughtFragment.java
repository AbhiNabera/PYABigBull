package com.example.abhinabera.pyabigbull.UserActivities.Userstocks;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhinabera.pyabigbull.Api.NetworkCallback;
import com.example.abhinabera.pyabigbull.Api.RetrofitClient;
import com.example.abhinabera.pyabigbull.Api.Utility;
import com.example.abhinabera.pyabigbull.Dashboard.NetworkUtility;
import com.example.abhinabera.pyabigbull.R;
import com.example.abhinabera.pyabigbull.SellActivity;
import com.example.abhinabera.pyabigbull.UserActivities.TransactionsHistory.TransactionsHistory;
import com.google.firebase.auth.FirebaseAuth;
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
    private int MAXCOUNT = 6;

    private double index_txn_charges, commodity_txn_charges, currency_txn_charges;

    int REQUEST_CODE = 1;

    int PORTFOLIO_VALUE = 0;

    double ACCOUNT_BALANCE = 0;

    Response<JsonObject> usd, eur, gbp;
    JsonObject gold, silver, crudeoil;
    List<JsonObject> stockList;
    HashMap<String, JsonObject> map, currency, commodity;

    SwipeRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    TextView portfolioValue, accountBal;

    ArrayList<JsonObject> arrayList;

    StocksRecyclerAdapter stocksRecyclerAdapter;

    public BoughtFragment(){}

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE  && resultCode  == RESULT_OK) {

            //unsuccessful transaction

        }else {

            Intent intent = new Intent("soldFragment");
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

            refreshLayout.setRefreshing(true);
            count = 0;
            getTranactionCharges();
            getTopCommodity();
            getEURINR();
            getGBPINR();
            getUSDINR();
            getStockList();

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

        stockList = new ArrayList<>();
        arrayList = new ArrayList<>();

        map = new HashMap<>();
        currency = new HashMap<>();
        commodity = new HashMap<>();

        ACCOUNT_BALANCE = Double.parseDouble(getActivity().getIntent().getStringExtra("acc_bal"));

        accountBal = (TextView) view.findViewById(R.id.accountBalance);
        portfolioValue = (TextView) view.findViewById(R.id.portfolioValue);
        recyclerView = (RecyclerView) view.findViewById(R.id.boughtRecycler);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        stocksRecyclerAdapter = new StocksRecyclerAdapter(getActivity(), arrayList, new StocksRecyclerAdapter.ClickListener() {
            @Override
            public void onItemClick(List<JsonObject> stocks, int position) {
                Intent i = new Intent(getActivity(), SellActivity.class);
                i.putExtra("data", stocks.get(position).getAsJsonObject()+"");
                i.putExtra("type", stocks.get(position).getAsJsonObject()
                        .get("type").getAsString());
                i.putExtra("id", stocks.get(position).getAsJsonObject()
                        .get("id").getAsString());
                i.putExtra("name", stocks.get(position).getAsJsonObject()
                        .get("name").getAsString());
                getActivity().startActivityForResult(i, REQUEST_CODE);
                getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        recyclerView.setAdapter(stocksRecyclerAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                //getBoughtList();
                count = 0;
                getTranactionCharges();
                getTopCommodity();
                getEURINR();
                getGBPINR();
                getUSDINR();
                getStockList();
            }
        });


        refreshLayout.setRefreshing(true);
        count = 0;
        getTranactionCharges();
        getTopCommodity();
        getEURINR();
        getGBPINR();
        getUSDINR();
        getStockList();

    }

    public void getBoughtList() {

        refreshLayout.setRefreshing(true);

        new RetrofitClient().getInterface().getbuyList(FirebaseAuth.getInstance().getCurrentUser()
                .getPhoneNumber().substring(3)).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                refreshLayout.setRefreshing(false);

                if(response.isSuccessful()) {

                    arrayList.clear();

                    if(response.body().get("data").getAsJsonObject().get("index").getAsJsonArray().size() !=0) {

                        JsonObject object = new JsonObject();
                        object.addProperty("TYPE", "index");
                        arrayList.add(object);

                        for (JsonElement element : response.body().get("data").getAsJsonObject().get("index").getAsJsonArray()) {

                            double current_price = Double.parseDouble(map.get(element.getAsJsonObject().get("id").getAsString())
                                    .get("lastvalue").getAsString().trim().replace(",",""));

                            double current_value = current_price*Integer.parseInt(element.getAsJsonObject().
                                    get("qty").getAsString()) /*- index_txn_charges*/ ;

                            double changeamount = current_value - Double.parseDouble(element.getAsJsonObject().
                                    get("total_amount").getAsString()) - index_txn_charges;

                            double pchange = ((current_price*Integer.parseInt(element.getAsJsonObject().
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

                            PORTFOLIO_VALUE+=current_value;
                        }
                    }

                    if(response.body().get("data").getAsJsonObject().get("commodity").getAsJsonArray().size() !=0) {

                        JsonObject object = new JsonObject();
                        object.addProperty("TYPE", "commodity");
                        arrayList.add(object);

                        for (JsonElement element : response.body().get("data").getAsJsonObject().get("commodity").getAsJsonArray()) {

                            double current_price = Double.parseDouble(commodity.get(element.getAsJsonObject().get("id").getAsString())
                                    .get("lastprice").getAsString().trim().replace(",",""));

                            double current_value = current_price*Integer.parseInt(element.getAsJsonObject().
                                    get("qty").getAsString()) /*- index_txn_charges*/;

                            double changeamount = current_value - Double.parseDouble(element.getAsJsonObject().
                                    get("total_amount").getAsString())  - index_txn_charges;

                            double pchange = ((current_price*Integer.parseInt(element.getAsJsonObject().
                                    get("qty").getAsString()) - Double.parseDouble(element.getAsJsonObject().
                                    get("total_amount").getAsString()) - commodity_txn_charges) / Double.parseDouble(element.getAsJsonObject().
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

                            PORTFOLIO_VALUE+=current_value;
                        }
                    }

                    if(response.body().get("data").getAsJsonObject().get("currency").getAsJsonArray().size() !=0) {

                        JsonObject object = new JsonObject();
                        object.addProperty("TYPE", "currency");
                        arrayList.add(object);

                        for (JsonElement element : response.body().get("data").getAsJsonObject().get("currency").getAsJsonArray()) {

                            double current_price = Double.parseDouble(currency.get(element.getAsJsonObject().get("id").getAsString())
                                    .get("data").getAsJsonObject().get("pricecurrent").getAsString().trim().replace(",",""));

                            double current_value = current_price*Integer.parseInt(element.getAsJsonObject().
                                    get("qty").getAsString()) /*- index_txn_charges*/;

                            double changeamount = current_value - Double.parseDouble(element.getAsJsonObject().
                                    get("total_amount").getAsString()) - index_txn_charges;

                            double pchange = ((current_price*Integer.parseInt(element.getAsJsonObject().
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

                            PORTFOLIO_VALUE+=current_value;
                        }
                    }

                    PORTFOLIO_VALUE += ACCOUNT_BALANCE;
                    portfolioValue.setText(new Utility().getRoundoffData(PORTFOLIO_VALUE+""));
                    accountBal.setText(new Utility().getRoundoffData(ACCOUNT_BALANCE+""));
                    
                    stocksRecyclerAdapter.notifyDataSetChanged();

                }else {
                    Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
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

    public void getTranactionCharges() {

        new RetrofitClient().getInterface().getAdminSettings().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if(response.isSuccessful()) {
                    index_txn_charges = Double.parseDouble(response.body().get("data").getAsJsonObject().
                            get("trans_amt_nifty").getAsString().replace(",",""));

                    commodity_txn_charges = Double.parseDouble(response.body().get("data").getAsJsonObject().
                            get("trans_amt_commodity").getAsString().replace(",",""));

                    currency_txn_charges = Double.parseDouble(response.body().get("data").getAsJsonObject().
                            get("trans_amt_currency").getAsString().replace(",",""));
                }

                count++;
                hideSwipeRefresh();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                count++;
                hideSwipeRefresh();
            }
        });
    }

    public void hideSwipeRefresh() {
        if(count == MAXCOUNT) {
            refreshLayout.setRefreshing(false);
            getBoughtList();
        }
    }

}
