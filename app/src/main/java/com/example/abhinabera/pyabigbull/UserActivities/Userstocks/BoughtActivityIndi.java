package com.example.abhinabera.pyabigbull.UserActivities.Userstocks;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.abhinabera.pyabigbull.Api.Utility;
import com.example.abhinabera.pyabigbull.R;
import com.example.abhinabera.pyabigbull.SellActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BoughtActivityIndi extends AppCompatActivity {

    TextView currentValue, investmentAmount;
    Toolbar boughtIndiToolbar;
    Typeface custom_font;
    RecyclerView recyclerView;
    ArrayList<JsonObject> arrayList;
    StocksRecyclerAdapter stocksRecyclerAdapter;
    int REQUEST_CODE = 1;

    private double CURRENT_VALUE = 0, INVESTMENT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_bought_indi);
        getSupportActionBar().hide();

        arrayList = new ArrayList<>();
        Type listType = new TypeToken<List<JsonObject>>() {}.getType();
        arrayList = new Gson().fromJson(getIntent().getStringExtra("data"), listType);
        CURRENT_VALUE = getIntent().getDoubleExtra("current_value", 0);
        INVESTMENT = getIntent().getDoubleExtra("investment", 0);

        Log.d("arrayList", ""+arrayList.size());

        boughtIndiToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.boughtIndiToolbar);
        Intent i = getIntent();
        boughtIndiToolbar.setTitle(i.getExtras().getString("cardName"));

        currentValue = (TextView) findViewById(R.id.currentValue);
        investmentAmount = (TextView) findViewById(R.id.investmentAmount);

        boughtIndiToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        boughtIndiToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        custom_font = ResourcesCompat.getFont(this, R.font.hammersmithone);

        changeToolbarFont(boughtIndiToolbar, this);

        recyclerView = (RecyclerView) findViewById(R.id.boughtRecycler);
        //arrayList = new ArrayList<>();

        currentValue.setText(new Utility().getRoundoffData(CURRENT_VALUE+""));
        investmentAmount.setText(new Utility().getRoundoffData(INVESTMENT+""));

        recyclerView.setLayoutManager(new LinearLayoutManager(BoughtActivityIndi.this));
        stocksRecyclerAdapter = new StocksRecyclerAdapter(BoughtActivityIndi.this, arrayList, new StocksRecyclerAdapter.ClickListener() {
            @Override
            public void onItemClick(List<JsonObject> stocks, int position) {
                Intent i = new Intent(BoughtActivityIndi.this, SellActivity.class);
                i.putExtra("data", stocks.get(position).getAsJsonObject()+"");
                i.putExtra("type", stocks.get(position).getAsJsonObject()
                        .get("type").getAsString());
                i.putExtra("id", stocks.get(position).getAsJsonObject()
                        .get("id").getAsString());
                i.putExtra("name", stocks.get(position).getAsJsonObject()
                        .get("name").getAsString());
                i.putExtra("current_value", CURRENT_VALUE);
                i.putExtra("investment", INVESTMENT);
                startActivityForResult(i, REQUEST_CODE);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        recyclerView.setAdapter(stocksRecyclerAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(BoughtActivityIndi.this, LinearLayoutManager.VERTICAL));
        stocksRecyclerAdapter.notifyDataSetChanged();
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
}
