package com.example.abhinabera.pyabigbull.UserActivities.Userstocks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.Toast;

import com.example.abhinabera.pyabigbull.Api.RetrofitClient;
import com.example.abhinabera.pyabigbull.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by AVINASH on 12/31/2018.
 */

public class SoldFragment extends Fragment {

    SwipeRefreshLayout refreshLayout;
    RecyclerView recyclerView;

    ArrayList<JsonObject> arrayList;

    SoldStocksRecyclerAdapter stocksRecyclerAdapter;

    public SoldFragment(){}


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("received intent", "");
            getSoldList();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("soldFragment"));

    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sold, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        arrayList = new ArrayList<>();

        recyclerView = (RecyclerView) view.findViewById(R.id.soldRecycler);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        stocksRecyclerAdapter = new SoldStocksRecyclerAdapter(getActivity(), arrayList);

        recyclerView.setAdapter(stocksRecyclerAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSoldList();
            }
        });

        getSoldList();
    }

    public void getSoldList() {

        refreshLayout.setRefreshing(true);

        new RetrofitClient().getInterface().getsellList(FirebaseAuth.getInstance().getCurrentUser()
                .getPhoneNumber().substring(3)).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                refreshLayout.setRefreshing(false);

                try {
                    if (response.isSuccessful()) {

                        arrayList.clear();

                        if (response.body().get("data").getAsJsonObject().get("index").getAsJsonArray().size() != 0) {

                            JsonObject object = new JsonObject();
                            object.addProperty("TYPE", "index");
                            arrayList.add(object);

                            for (JsonElement element : response.body().get("data").getAsJsonObject().get("index").getAsJsonArray()) {
                                arrayList.add(element.getAsJsonObject());
                            }
                        }

                        if (response.body().get("data").getAsJsonObject().get("commodity").getAsJsonArray().size() != 0) {

                            JsonObject object = new JsonObject();
                            object.addProperty("TYPE", "commodity");
                            arrayList.add(object);

                            for (JsonElement element : response.body().get("data").getAsJsonObject().get("commodity").getAsJsonArray()) {
                                arrayList.add(element.getAsJsonObject());
                            }
                        }

                        if (response.body().get("data").getAsJsonObject().get("currency").getAsJsonArray().size() != 0) {

                            JsonObject object = new JsonObject();
                            object.addProperty("TYPE", "currency");
                            arrayList.add(object);

                            for (JsonElement element : response.body().get("data").getAsJsonObject().get("currency").getAsJsonArray()) {
                                arrayList.add(element.getAsJsonObject());
                            }
                        }

                        if (response.body().get("data").getAsJsonObject().get("fixed_deposit").getAsJsonArray().size() != 0) {

                            JsonObject object = new JsonObject();
                            object.addProperty("TYPE", "fixed_deposit");
                            arrayList.add(object);

                            for (JsonElement element : response.body().get("data").getAsJsonObject().get("fixed_deposit").getAsJsonArray()) {
                                arrayList.add(element.getAsJsonObject());
                            }
                        }


                        stocksRecyclerAdapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
            }
        });
    }

}
