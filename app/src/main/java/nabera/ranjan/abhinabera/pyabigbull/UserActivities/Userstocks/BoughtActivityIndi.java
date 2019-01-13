package nabera.ranjan.abhinabera.pyabigbull.UserActivities.Userstocks;

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

import nabera.ranjan.abhinabera.pyabigbull.Api.Utility;
import nabera.ranjan.abhinabera.pyabigbull.Transactions.FDSellActivity;
import nabera.ranjan.abhinabera.pyabigbull.Transactions.SellActivity;
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
    int REQUEST_CODE = 2;

    private double CURRENT_VALUE = 0, INVESTMENT = 0;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (/*requestCode == REQUEST_CODE  && */resultCode  == RESULT_OK) {

            //unsuccessful transaction
            Log.d("unsuccessfultransaction", "");

        }else {
            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(nabera.ranjan.abhinabera.pyabigbull.R.layout.activity_bought_indi);
        getSupportActionBar().hide();

        arrayList = new ArrayList<>();
        Type listType = new TypeToken<List<JsonObject>>() {}.getType();
        arrayList = new Gson().fromJson(getIntent().getStringExtra("data"), listType);
        CURRENT_VALUE = getIntent().getDoubleExtra("current_value", 0);
        INVESTMENT = getIntent().getDoubleExtra("investment", 0);

        Log.d("arrayList", ""+arrayList.size());

        boughtIndiToolbar = (android.support.v7.widget.Toolbar) findViewById(nabera.ranjan.abhinabera.pyabigbull.R.id.boughtIndiToolbar);
        Intent i = getIntent();
        boughtIndiToolbar.setTitle(i.getExtras().getString("cardName"));

        currentValue = (TextView) findViewById(nabera.ranjan.abhinabera.pyabigbull.R.id.currentValue);
        investmentAmount = (TextView) findViewById(nabera.ranjan.abhinabera.pyabigbull.R.id.investmentAmount);

        boughtIndiToolbar.setNavigationIcon(getResources().getDrawable(nabera.ranjan.abhinabera.pyabigbull.R.drawable.ic_action_back));
        boughtIndiToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        custom_font = ResourcesCompat.getFont(this, nabera.ranjan.abhinabera.pyabigbull.R.font.hammersmithone);

        changeToolbarFont(boughtIndiToolbar, this);

        recyclerView = (RecyclerView) findViewById(nabera.ranjan.abhinabera.pyabigbull.R.id.boughtRecycler);
        //arrayList = new ArrayList<>();

        currentValue.setText(new Utility().getRoundoffData(CURRENT_VALUE+""));
        investmentAmount.setText(new Utility().getRoundoffData(INVESTMENT+""));

        recyclerView.setLayoutManager(new LinearLayoutManager(BoughtActivityIndi.this));
        stocksRecyclerAdapter = new StocksRecyclerAdapter(BoughtActivityIndi.this, arrayList, new StocksRecyclerAdapter.ClickListener() {
            @Override
            public void onItemClick(List<JsonObject> stocks, int position) {
                Intent i;
                if(stocks.get(position).getAsJsonObject()
                        .get("id").getAsString().equalsIgnoreCase("FD")) {
                    i = new Intent(BoughtActivityIndi.this, FDSellActivity.class);
                }else {
                    i = new Intent(BoughtActivityIndi.this, SellActivity.class);
                }
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
                overridePendingTransition(nabera.ranjan.abhinabera.pyabigbull.R.anim.enter, nabera.ranjan.abhinabera.pyabigbull.R.anim.exit);
                //finish();
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
        //super.onBackPressed();
        setResult(RESULT_OK);
        finish();
        //overridePendingTransition(R.anim.enter1, R.anim.exit1);
    }
}
