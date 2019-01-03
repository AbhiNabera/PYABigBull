package com.example.abhinabera.pyabigbull.Dashboard;

import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.PrecomputedText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.abhinabera.pyabigbull.Api.RetrofitClient;
import com.example.abhinabera.pyabigbull.Api.Utility;
import com.example.abhinabera.pyabigbull.LeaderBoardActivities.LeaderBoardRecyclerAdapter;
import com.example.abhinabera.pyabigbull.LeaderBoardActivities.LeaderboardObject;
import com.example.abhinabera.pyabigbull.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaderBoardFragment extends Fragment {

    private LeaderBoardRecyclerAdapter mAdapter;
    ImageView userMedal;
    TextView userName, userRank, boxPrice, boxPercent;
    LinearLayout linearLayout;

    SwipeRefreshLayout refreshLayout;

    ArrayList<LeaderboardObject> boardlist;

    LeaderboardObject user_object = new LeaderboardObject();

    private int MY_RANK;

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
                getLeaderboard();
            }
        });

        getLeaderboard();
    }

    public void getLeaderboard() {

        refreshLayout.setRefreshing(true);

        new RetrofitClient().getInterface().getLeaderboard().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                refreshLayout.setRefreshing(false);

                if(response.isSuccessful()) {

                    Type listType = new TypeToken<List<LeaderboardObject>>() {}.getType();
                    List<LeaderboardObject> leaderboardObjects = new Gson().fromJson(response.body()
                            .getAsJsonArray("data"), listType);

                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {


                            Collections.sort(leaderboardObjects, new Comparator<LeaderboardObject>() {
                                @Override
                                public int compare(LeaderboardObject c1, LeaderboardObject c2) {
                                    return Double.compare(c2.getPercentchange(), c1.getPercentchange());
                                }
                            });

                            MY_RANK = 0;

                            for(LeaderboardObject object: leaderboardObjects) {

                                MY_RANK++;

                                if (object.getPhoneNumber().trim().equalsIgnoreCase(FirebaseAuth.getInstance()
                                        .getCurrentUser().getPhoneNumber().substring(3))) {
                                    user_object = object;
                                    break;
                                }
                            }

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    boardlist.clear();
                                    if(leaderboardObjects.size()>10) {
                                        int i = 0;
                                        for (LeaderboardObject object: leaderboardObjects) {
                                            i++;
                                            boardlist.add(object);
                                            if (i == 10) break;;
                                        }
                                    }else {
                                        boardlist.addAll(leaderboardObjects);
                                    }
                                    mAdapter.notifyDataSetChanged();

                                    userName.setText(user_object.getUserName());
                                    userRank.setText(MY_RANK+"");
                                    boxPrice.setText(new Utility().getRoundoffData(user_object.getChange()));
                                    boxPercent.setText(new Utility().getRoundoffData(user_object.getPercentchange()+"")+ "%");

                                    if(user_object.getPercentchange()>=0) {
                                        linearLayout.setBackgroundColor(getResources().getColor(R.color.greenText));
                                    }else {
                                        linearLayout.setBackgroundColor(getResources().getColor(R.color.red));
                                    }
                                }
                            });
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
}
