package com.example.abhinabera.pyabigbull.Dashboard;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.example.abhinabera.pyabigbull.Api.NetworkCallback;
import com.example.abhinabera.pyabigbull.Api.RetrofitClient;
import com.example.abhinabera.pyabigbull.Api.Utility;
import com.example.abhinabera.pyabigbull.Dashboard.LeaderBoardActivities.LeaderBoardRecyclerAdapter;
import com.example.abhinabera.pyabigbull.Dashboard.LeaderBoardActivities.LeaderboardObject;
import com.example.abhinabera.pyabigbull.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaderBoardFragment extends Fragment {

    private int count = 0;
    private int MAXCOUNT = 5;

    List<JsonObject> stockList;
    JsonObject usd, eur, gbp;
    JsonObject gold, silver, crudeoil;

    private LeaderBoardRecyclerAdapter mAdapter;
    ImageView userMedal;
    TextView userName, userRank, boxPrice, boxPercent;
    LinearLayout linearLayout;

    SwipeRefreshLayout refreshLayout;

    ArrayList<JsonObject> boardlist;

    ArrayList<JsonObject> leaderboardlist;

    JsonObject user_object = new JsonObject();

    private int MY_RANK;

    HashMap<String, JsonObject> map, currency, commodity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_leader_board, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        boardlist = new ArrayList<>();
        leaderboardlist = new ArrayList<>();

        stockList = new ArrayList<>();
        map = new HashMap<>();
        currency = new HashMap<>();
        commodity = new HashMap<>();

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        linearLayout = (LinearLayout) view.findViewById(R.id.leaderBoardBox);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.leaderBoardRecycler);
        userName = (TextView) view.findViewById(R.id.userNameText);
        userRank = (TextView) view.findViewById(R.id.userRankText);
        boxPrice = (TextView) view.findViewById(R.id.leaderBoardBoxPrice);
        boxPercent = (TextView) view.findViewById(R.id.leaderBoardBoxPercent);
        userMedal = (ImageView) view.findViewById(R.id.userMedal);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new LeaderBoardRecyclerAdapter(getActivity(), boardlist);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //getLeaderboard();
                refreshLayout.setRefreshing(true);
                count = 0;
                getTopCommodity();
                getStockList();
                getEURINR();
                getGBPINR();
                getUSDINR();
            }
        });

        //getLeaderboard();
        refreshLayout.setRefreshing(true);
        count = 0;
        getTopCommodity();
        getStockList();
        getEURINR();
        getGBPINR();
        getUSDINR();
    }

    public void getLeaderboard() {

        refreshLayout.setRefreshing(true);

        new RetrofitClient().getInterface().getLeaderboard().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if(response.isSuccessful()) {

                    Type listType = new TypeToken<List<JsonObject>>() {}.getType();
                    List<JsonObject> leaderboardObjects = new Gson().fromJson(response.body()
                            .getAsJsonArray("data"), listType);

                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                long timestart = System.currentTimeMillis();

                                for (JsonObject object : leaderboardObjects) {

                                    //TODO: add portfolio value
                                    object.addProperty("portfolio_value", getPortfolioValue(object));
                                    object.addProperty("net_worth",
                                            (object.get("portfolio_value").getAsDouble()
                                                    + object.get("avail_balance").getAsDouble()));
                                    object.addProperty("current_change",
                                            ((object.get("portfolio_value").getAsDouble()
                                                    + object.get("avail_balance").getAsDouble())
                                                    - object.get("start_balance").getAsDouble()));

                                    object.addProperty("current_pchange",
                                            (object.get("current_change").getAsDouble()
                                                    / object.get("start_balance").getAsDouble()) * 100);
                                }


                                Collections.sort(leaderboardObjects, new Comparator<JsonObject>() {
                                    @Override
                                    public int compare(JsonObject c1, JsonObject c2) {
                                        return Double.compare(c2.get("current_pchange").getAsDouble(),
                                                c1.get("current_pchange").getAsDouble());
                                    }
                                });

                                MY_RANK = 0;

                                for (JsonObject object : leaderboardObjects) {

                                    MY_RANK++;

                                    if (object.get("phoneNumber").getAsString().trim().equalsIgnoreCase(FirebaseAuth
                                            .getInstance().getCurrentUser().getPhoneNumber().substring(3))) {
                                        user_object = object;
                                        break;
                                    }
                                }

                                long endtime = System.currentTimeMillis();

                                Log.d("time elasped", "" + (endtime - timestart));

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        refreshLayout.setRefreshing(false);

                                        boardlist.clear();
                                        if (leaderboardObjects.size() > 10) {
                                            int i = 0;
                                            for (JsonObject object : leaderboardObjects) {
                                                i++;
                                                boardlist.add(object);
                                                if (i == 10) break;
                                                ;
                                            }
                                        } else {
                                            boardlist.addAll(leaderboardObjects);
                                        }
                                        mAdapter.notifyDataSetChanged();

                                        userName.setText(user_object.get("userName").getAsString());
                                        userRank.setText(MY_RANK + "");
                                        boxPrice.setText(new Utility().getRoundoffData(user_object.
                                                get("current_change").getAsDouble() + "") + "");
                                        boxPercent.setText(new Utility().getRoundoffData(user_object
                                                .get("current_pchange").getAsDouble() + "") + "%");

                                        if (user_object.get("current_change").getAsDouble() >= 0) {
                                            linearLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.greenText));
                                        } else {
                                            linearLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.red));
                                        }
                                    }
                                });
                            }catch (NullPointerException e){
                                e.printStackTrace();
                            }catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                t.printStackTrace();
            }
        });
    }

    public double getPortfolioValue(JsonObject object) {

        double PORTFOLIO_VALUE = 0;

        if(object.get("index").getAsJsonArray() != null) {

            for (JsonElement element : object.get("index").getAsJsonArray()) {

                double current_price = Double.parseDouble(map.get(element.getAsJsonObject().get("id").getAsString().trim())
                        .get("lastvalue").getAsString().trim().replace(",",""));

                double current_value = current_price*Integer.parseInt(element.getAsJsonObject().
                        get("qty").getAsString()) /*- index_txn_charges*/ ;

                PORTFOLIO_VALUE+=current_value;
            }
        }

        if(object.get("commodity").getAsJsonArray() != null) {

            for (JsonElement element : object.get("commodity").getAsJsonArray()) {

                //Log.d("Commodity", element.getAsJsonObject().get("id").getAsString()+"");

                double current_price = Double.parseDouble(commodity.get(element.getAsJsonObject().get("id").getAsString())
                        .get("lastprice").getAsString().trim().replace(",",""));

                if(element.getAsJsonObject().get("id").getAsString().equalsIgnoreCase("SILVER")) {

                    current_price = current_price*0.1;
                }

                double current_value = current_price*Integer.parseInt(element.getAsJsonObject().
                        get("qty").getAsString()) /*- index_txn_charges*/;

                PORTFOLIO_VALUE+=current_value;
            }
        }

        if(object.get("currency").getAsJsonArray() != null) {

           for (JsonElement element : object.get("currency").getAsJsonArray()) {

                double current_price = Double.parseDouble(currency.get(element.getAsJsonObject().get("id").getAsString())
                        .get("data").getAsJsonObject().get("pricecurrent").getAsString().trim().replace(",",""));

                double current_value = current_price*Integer.parseInt(element.getAsJsonObject().
                        get("qty").getAsString()) /*- index_txn_charges*/;

                PORTFOLIO_VALUE+=current_value;

           }
        }

        if(object.get("fixed_deposit").getAsJsonArray().size() !=0) {

            for (JsonElement element : object.get("fixed_deposit").getAsJsonArray()) {

                double current_value = element.getAsJsonObject().
                        get("current_value").getAsDouble() ;

                PORTFOLIO_VALUE+=current_value;

            }
        }

        Log.d("PORTFOLIO VALUE", PORTFOLIO_VALUE+"");

        return PORTFOLIO_VALUE ;
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
                                //Log.d("Commodity", "GOLD");
                                i++;
                                gold = element.getAsJsonObject();
                                commodity.put("GOLD", gold);
                                break;

                            case "SILVER" :
                                //Log.d("Commodity", "SILVER");
                                i++;
                                silver = element.getAsJsonObject();
                                commodity.put("SILVER", silver);
                                break;

                            case "CRUDEOIL" :
                                //Log.d("Commodity", "CRUDEOIL");
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
                    usd = response.body();
                    currency.put("USDINR", usd);
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
                    eur = response.body();
                    currency.put("EURINR", eur);
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
                    gbp = response.body();
                    currency.put("GBPINR", gbp);
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
        if(count == MAXCOUNT) {
            if(usd!=null && eur != null && gbp != null && gold != null && silver != null && crudeoil != null && stockList != null) {
                refreshLayout.setRefreshing(false);
                getLeaderboard();
            }else {
                refreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "error occured ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
}
