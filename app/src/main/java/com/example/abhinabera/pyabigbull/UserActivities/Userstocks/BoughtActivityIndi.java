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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.abhinabera.pyabigbull.R;
import com.example.abhinabera.pyabigbull.SellActivity;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class BoughtActivityIndi extends AppCompatActivity {

    Toolbar boughtIndiToolbar;
    Typeface custom_font;
    RecyclerView recyclerView;
    ArrayList<JsonObject> arrayList;
    StocksRecyclerAdapter stocksRecyclerAdapter;
    int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_bought_indi);
        getSupportActionBar().hide();

        boughtIndiToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.boughtIndiToolbar);
        Intent i = getIntent();
        boughtIndiToolbar.setTitle(i.getExtras().getString("cardName"));

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
        arrayList = new ArrayList<>();

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
                startActivityForResult(i, REQUEST_CODE);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        recyclerView.setAdapter(stocksRecyclerAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(BoughtActivityIndi.this, LinearLayoutManager.VERTICAL));


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
