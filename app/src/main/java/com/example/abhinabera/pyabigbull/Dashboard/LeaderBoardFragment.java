package com.example.abhinabera.pyabigbull.Dashboard;

import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.abhinabera.pyabigbull.Api.RetrofitClient;
import com.example.abhinabera.pyabigbull.LeaderBoardActivities.LeaderBoardRecyclerAdapter;
import com.example.abhinabera.pyabigbull.R;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaderBoardFragment extends Fragment {

    private LeaderBoardRecyclerAdapter mAdapter;
    ImageView userMedal;
    TextView userName, userRank, boxPrice, boxPercent;

    SwipeRefreshLayout refreshLayout;

    ArrayList<JsonObject> boardlist;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_leader_board, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        boardlist = new ArrayList<>();

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);

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

        getLeaderboard();
    }

    public void getLeaderboard() {

        refreshLayout.setRefreshing(true);

        new RetrofitClient().getInterface().getLeaderboard().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                refreshLayout.setRefreshing(false);

                if(response.isSuccessful()) {

                    boardlist.clear();

                    for (JsonElement element: response.body().getAsJsonArray("data")) {
                        boardlist.add(0, element.getAsJsonObject());
                    }

                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                t.printStackTrace();
            }
        });
    }
}
