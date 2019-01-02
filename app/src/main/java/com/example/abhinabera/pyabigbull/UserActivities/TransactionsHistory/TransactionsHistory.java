package com.example.abhinabera.pyabigbull.UserActivities.TransactionsHistory;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhinabera.pyabigbull.Api.RetrofitClient;
import com.example.abhinabera.pyabigbull.LeaderBoardActivities.LeaderBoardData;
import com.example.abhinabera.pyabigbull.LeaderBoardActivities.LeaderBoardRecyclerAdapter;
import com.example.abhinabera.pyabigbull.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionsHistory extends AppCompatActivity {

    Toolbar historyToolbar;
    Typeface custom_font;
    SwipeRefreshLayout refreshLayout;
    TransactionsHistoryRecyclerAdapter mAdapter;

    JsonArray transactions;
    ArrayList<JsonObject> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_transactions_history);
        getSupportActionBar().hide();

        transactions = new JsonArray();
        arrayList = new ArrayList<>();

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);

        historyToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.historyToolbar);
        Intent i = getIntent();
        historyToolbar.setTitle(i.getExtras().getString("name"));

        historyToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        historyToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        custom_font = ResourcesCompat.getFont(this, R.font.hammersmithone);

        changeToolbarFont(historyToolbar, this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.historyRecycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(TransactionsHistory.this));
        mAdapter = new TransactionsHistoryRecyclerAdapter(TransactionsHistory.this,
                arrayList);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(TransactionsHistory.this, LinearLayoutManager.VERTICAL));

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTransactionHistory();
            }
        });

        refreshLayout.setRefreshing(true);
        getTransactionHistory();
    }

    public void changeToolbarFont(Toolbar toolbar, Activity context) {
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView tv = (TextView) view;
                if (tv.getText().equals(toolbar.getTitle())) {
                    applyFont(tv, context);
                    break;
                }
            }
        }
    }

    public void applyFont(TextView tv, Activity context) {
        tv.setTypeface(custom_font);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        //overridePendingTransition(R.anim.enter1, R.anim.exit1);
    }

    public void getTransactionHistory() {

        refreshLayout.setRefreshing(true);

        new RetrofitClient().getInterface().getTxnHistory(FirebaseAuth.getInstance().getCurrentUser().
                getPhoneNumber().substring(3)).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                refreshLayout.setRefreshing(false);

                if(response.isSuccessful()) {

                    arrayList.clear();
                    transactions = response.body().get("data").getAsJsonArray();
                    for (JsonElement jsonElement: transactions) {
                        arrayList.add(jsonElement.getAsJsonObject());
                    }
                    mAdapter.notifyDataSetChanged();

                }else {
                    Toast.makeText(TransactionsHistory.this, "Network error", Toast.LENGTH_SHORT).show();
                }

                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(TransactionsHistory.this, "Network error", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
                refreshLayout.setRefreshing(false);
            }
        });
    }
}
