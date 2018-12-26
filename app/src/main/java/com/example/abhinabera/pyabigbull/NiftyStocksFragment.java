package com.example.abhinabera.pyabigbull;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NiftyStocksFragment extends Fragment {

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

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.niftyStocksRecycler);

        NiftyStocksData itemsData[] = { new NiftyStocksData("Company", "0.0000", "0.00", "0.00", "0.00"), new NiftyStocksData("Company", "0.0000", "0.00", "0.00", "0.00")};
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        NiftyStocksRecyclerAdapter mAdapter = new NiftyStocksRecyclerAdapter(itemsData);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        recyclerView.setAdapter(mAdapter);
    }
}