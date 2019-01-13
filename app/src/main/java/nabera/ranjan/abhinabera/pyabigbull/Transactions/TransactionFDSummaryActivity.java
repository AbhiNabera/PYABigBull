package nabera.ranjan.abhinabera.pyabigbull.Transactions;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import nabera.ranjan.abhinabera.pyabigbull.Api.Utility;
import nabera.ranjan.abhinabera.pyabigbull.R;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TransactionFDSummaryActivity extends AppCompatActivity {

    android.support.v7.widget.Toolbar purchaseToolbar;
    Typeface custom_font;
    TextView txnID, availBal, totalInvestment, investment, txnCharges, currentValue, timeout;
    Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_transaction_fdsummary);
        getSupportActionBar().hide();

        purchaseToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.purchaseToolbar);
        purchaseToolbar.setTitle("Transaction Summary");

        txnID = (TextView) findViewById(R.id.txn_id);
        availBal = (TextView) findViewById(R.id.availableBalance);
        totalInvestment = (TextView) findViewById(R.id.totalInvestment);
        investment = (TextView) findViewById(R.id.investment);
        txnCharges = (TextView) findViewById(R.id.transactionCharges);
        currentValue = (TextView) findViewById(R.id.currentValue);
        timeout = (TextView) findViewById(R.id.timeout);

        timeout.setVisibility(View.GONE);

        confirm = (Button) findViewById(R.id.confirmButton);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent i = new Intent(TransactionFDSummaryActivity.this, MainActivity.class);
                //startActivity(i);
                finish();
                overridePendingTransition(R.anim.enter1, R.anim.exit1);
            }
        });

        purchaseToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        purchaseToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        custom_font = ResourcesCompat.getFont(this, R.font.hammersmithone);

        changeToolbarFont(purchaseToolbar, this);

        if(!getIntent().getBooleanExtra("success",false)) {

            timeout.setVisibility(View.VISIBLE);
        }

        else {

            JsonParser parser = new JsonParser();
            JsonObject json = (JsonObject) parser.parse(getIntent().getStringExtra("data"));

            if (getIntent().getStringExtra("type").equalsIgnoreCase("buy")) {

                JsonObject object = json.getAsJsonObject("Account")
                        .getAsJsonObject("stocks_list")
                        .getAsJsonObject("bought_items")
                        .getAsJsonObject(getIntent().getStringExtra("product_type"))
                        .getAsJsonObject(getIntent().getStringExtra("txn_id"));

                txnID.setText("" + object.get("txn_id").getAsString());
                availBal.setText("" + new Utility().getRoundoffData(json.get("Account").getAsJsonObject().get("avail_balance").getAsString()));
                totalInvestment.setText("" + new Utility().getRoundoffData(json.getAsJsonObject("Account").get("investment").getAsString()));
                investment.setText("" + new Utility().getRoundoffData(object.get("investment").getAsString()));
                currentValue.setText("" + new Utility().getRoundoffData(object.get("current_value").getAsString()));

            }
        }


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

    @Override
    public void onDestroy(){
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
}
