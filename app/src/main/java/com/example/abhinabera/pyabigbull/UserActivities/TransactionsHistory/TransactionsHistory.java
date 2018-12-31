package com.example.abhinabera.pyabigbull.UserActivities.TransactionsHistory;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
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

import com.example.abhinabera.pyabigbull.LeaderBoardActivities.LeaderBoardData;
import com.example.abhinabera.pyabigbull.LeaderBoardActivities.LeaderBoardRecyclerAdapter;
import com.example.abhinabera.pyabigbull.R;

public class TransactionsHistory extends AppCompatActivity {

    Toolbar historyToolbar;
    Typeface custom_font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_transactions_history);
        getSupportActionBar().hide();

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

        TransactionsHistoryData itemsData[] = { new TransactionsHistoryData("Asian Paints", "BUY","100", "10000.00"),
                new TransactionsHistoryData("Asian Paints", "BUY", "100", "10000.00"),
                        new TransactionsHistoryData("Asian Paints", "BUY","100", "10000.00")};

        recyclerView.setLayoutManager(new LinearLayoutManager(TransactionsHistory.this));
        TransactionsHistoryRecyclerAdapter mAdapter = new TransactionsHistoryRecyclerAdapter(itemsData);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(TransactionsHistory.this, LinearLayoutManager.VERTICAL));

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
        overridePendingTransition(R.anim.enter1, R.anim.exit1);
    }
}
