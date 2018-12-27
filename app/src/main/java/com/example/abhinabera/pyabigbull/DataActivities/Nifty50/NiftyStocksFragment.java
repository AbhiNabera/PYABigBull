package com.example.abhinabera.pyabigbull.DataActivities.Nifty50;

import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;

import com.example.abhinabera.pyabigbull.Api.RetrofitClient;
import com.example.abhinabera.pyabigbull.R;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NiftyStocksFragment extends Fragment {

    List<JsonObject> stockList;
    private NiftyStocksRecyclerAdapter mAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    public NiftyStocksFragment() {
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
        return inflater.inflate(R.layout.fragment_nifty_stocks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        stockList = new ArrayList<>();

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.niftyStocksRecycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new NiftyStocksRecyclerAdapter(getActivity(), stockList);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        recyclerView.setAdapter(mAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getStockList();
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                getStockList();
            }
        },50);
    }

    public void getStockList() {

        new RetrofitClient().getNifty50Interface().getNify50StockList().enqueue(new Callback<List<JsonObject>>() {
            @Override
            public void onResponse(Call<List<JsonObject>> call, Response<List<JsonObject>> response) {

                swipeRefreshLayout.setRefreshing(false);

                if(response.isSuccessful()) {

                    stockList.clear();
                    stockList.addAll(response.body());
                    mAdapter.notifyDataSetChanged();

                    Log.d("NIFTYStockFragment", stockList.size()+"");

                }else{

                    Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<List<JsonObject>> call, Throwable t) {
                t.printStackTrace();
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}