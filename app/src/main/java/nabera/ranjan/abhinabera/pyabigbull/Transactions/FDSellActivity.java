package nabera.ranjan.abhinabera.pyabigbull.Transactions;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import nabera.ranjan.abhinabera.pyabigbull.Api.RetrofitClient;
import nabera.ranjan.abhinabera.pyabigbull.Api.Utility;
import nabera.ranjan.abhinabera.pyabigbull.Dialog.DialogInterface;
import nabera.ranjan.abhinabera.pyabigbull.Dialog.ProgressDialog;
import nabera.ranjan.abhinabera.pyabigbull.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FDSellActivity extends AppCompatActivity {

    Toolbar purchaseToolbar;
    Typeface custom_font;
    TextView timeouttv, availBal, totalInvestment, currentValue, investment, transactionCharges, netReturn, profit, profitPer, accBal, netProfit, netProfitPer;
    Button confirm;

    ProgressDialog progressDialog;

    private String INTENT_DATA;
    JsonObject INVESTMENT_PACKET;
    JsonObject userObject;

    private String type;
    private String product_type;
    private String id;
    private String name;

    CountDownTimer countDownTimer;

    double start_balance;
    double aval_balance;
    double total_investment;
    double current_value;
    int quantity = 0;
    double investmentamt;
    double left_stockinvestment;
    double txn_charges;
    double netreturn;
    double stockchangeamt;
    double percentstockchange;
    double acc_bal;
    double netstockchange;
    double netpercenttockchange;
    double sharesprice;
    int stock_count;
    String txn_id;
    long timestamp;
    int left_quantity = 0;

    double TOTAL_AMOUNT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_fdsell);
        getSupportActionBar().hide();

        purchaseToolbar = (Toolbar) findViewById(R.id.purchaseToolbar);
        purchaseToolbar.setTitle("REDEEM FD");

        type = getIntent().getStringExtra("type");
        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");

        INTENT_DATA = getIntent().getStringExtra("data");
        JsonParser parser = new JsonParser();
        INVESTMENT_PACKET = (JsonObject) parser.parse(INTENT_DATA);

        confirm = (Button) findViewById(R.id.confirmButton);

        availBal = (TextView) findViewById(R.id.availableBalance);
        totalInvestment = (TextView) findViewById(R.id.totalInvestment);
        currentValue = (TextView) findViewById(R.id.currentStockPrice);
        investment = (TextView) findViewById(R.id.investment);
        transactionCharges = (TextView) findViewById(R.id.transactionCharges);
        netReturn = (TextView) findViewById(R.id.returns);
        profit = (TextView) findViewById(R.id.stockchange);
        profitPer = (TextView) findViewById(R.id.stockchange);
        accBal = (TextView) findViewById(R.id.accountBalance);
        netProfit = (TextView) findViewById(R.id.netstockchange);
        netProfitPer = (TextView) findViewById(R.id.percentnetstockchange);
        timeouttv = (TextView) findViewById(R.id.timeout);

        purchaseToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        purchaseToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        custom_font = ResourcesCompat.getFont(this, R.font.hammersmithone);

        changeToolbarFont(purchaseToolbar, this);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(check()) {
                    txn_id = getTransId();
                    executeTransaction(setfddata());
                    countDownTimer.cancel();
                }
            }
        });

        getUserAccount();

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
        setResult(RESULT_OK);
        finish();
        overridePendingTransition(R.anim.enter1, R.anim.exit1);
    }

    public String getTransId() {
        //%B for buy %S for sell
        //timestamp = System.currentTimeMillis();(network set)
        return "txn" + "FD" + timestamp%100000000 + "" + "S";
    }

    public void initializeAmt() {

        start_balance = Double.parseDouble(userObject.get("data").getAsJsonObject().
                get("start_balance").getAsString().replace(",", ""));
        left_quantity = 0;
        stock_count = Integer.parseInt(userObject.get("data").getAsJsonObject().get("stocks_count").getAsString()
                .replace(",",""));
        sharesprice = Double.parseDouble(userObject.get("data").getAsJsonObject().get("shares_price").getAsString()
                .replace(",",""));
        aval_balance = Double.parseDouble(userObject.get("data").getAsJsonObject().get("avail_balance").getAsString().replace(",",""));
        total_investment = Double.parseDouble(userObject.get("data").getAsJsonObject().get("investment").getAsString().replace(",", ""));

        current_value = Double.parseDouble(INVESTMENT_PACKET.get("current_value").getAsString());
        quantity = Integer.parseInt(INVESTMENT_PACKET.get("qty").getAsString());;
        investmentamt = Double.parseDouble(INVESTMENT_PACKET.get("investment").getAsString());

        left_stockinvestment = investmentamt;
        txn_charges = 0.0;
        netreturn = 0;
        stockchangeamt = 0;
        percentstockchange = 0;
        acc_bal = aval_balance;
        netstockchange = Double.parseDouble(userObject.get("data").getAsJsonObject().get("change").getAsString().replace(",",""));
        netpercenttockchange = Double.parseDouble(userObject.get("data").getAsJsonObject().get("percentchange").getAsString().replace(",",""));
    }

    public void updateAmounts() {

        netreturn = current_value;//TODO:logic error

        Log.d("netreturn", netreturn+"");

        stockchangeamt = current_value - investmentamt;

        percentstockchange = (stockchangeamt/investmentamt) * 100;

        acc_bal = aval_balance + netreturn;

        total_investment = Double.parseDouble(userObject.get("data").getAsJsonObject().get("investment").getAsString()
                .replace(",", "")) - investmentamt;

        sharesprice =  Double.parseDouble(userObject.get("data").getAsJsonObject().get("shares_price").getAsString()
                .replace(",", "")) - investmentamt;

        netstockchange = acc_bal + sharesprice - start_balance;

        netpercenttockchange = (netstockchange/start_balance) * 100;

        stock_count = userObject.get("data").getAsJsonObject()
                .get("stocks_count").getAsInt() - quantity;

        left_quantity = INVESTMENT_PACKET.get("qty").getAsInt() - quantity;

        left_stockinvestment = investmentamt - investmentamt;

    }

    public void updateViews() {

        Utility utility = new Utility();
        availBal.setText(utility.getRoundoffData(aval_balance+""));
        totalInvestment.setText(utility.getRoundoffData(total_investment+""));
        currentValue.setText(utility.getRoundoffData(current_value+""));
        investment.setText(utility.getRoundoffData(investmentamt+""));
        netReturn.setText(utility.getRoundoffData(netreturn+""));
        profit.setText(utility.getRoundoffData(stockchangeamt+""));
        profitPer.setText(utility.getRoundoffData(percentstockchange+"")+"%");
        accBal.setText(utility.getRoundoffData(acc_bal+""));
        netProfit.setText(utility.getRoundoffData(netstockchange+""));
        netProfitPer.setText(utility.getRoundoffData(netpercenttockchange+"")+"%");

        if(percentstockchange>=0) {
            profit.setTextColor(getResources().getColor(R.color.greenText));
            profitPer.setTextColor(getResources().getColor(R.color.greenText));
        }else {
            profit.setTextColor(getResources().getColor(R.color.red));
            profitPer.setTextColor(getResources().getColor(R.color.red));
        }

        if(netpercenttockchange>=0) {
            netProfit.setTextColor(getResources().getColor(R.color.greenText));
            netProfitPer.setTextColor(getResources().getColor(R.color.greenText));
        }else {
            netProfit.setTextColor(getResources().getColor(R.color.red));
            netProfitPer.setTextColor(getResources().getColor(R.color.red));
        }
    }

    public boolean check() {

        if(quantity == 0) {
            Toast.makeText(FDSellActivity.this, "Set quantity", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(quantity > INVESTMENT_PACKET.get("qty").getAsInt()) {
            Toast.makeText(FDSellActivity.this, "Invalid quantity", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public JsonObject setfddata() {

        JsonObject account_ref = userObject.get("data").getAsJsonObject();

        JsonObjectFormatter objectFormatter = new JsonObjectFormatter(account_ref);

        JsonObject leaderBoardData = new JsonObject();
        JsonObject leaderBoardTxnData = null; //all qty will be sold so delete all

        JsonObject data = new JsonObject();
        JsonObject transaction = new JsonObject();
        JsonObject txn_history = new JsonObject();

        String imageUrl = (userObject.get("data").getAsJsonObject().get("imageUrl")==null)?null:
                userObject.get("data").getAsJsonObject().get("imageUrl").getAsString();

        //leaderBoardData.addProperty("imageUrl", imageUrl);
        leaderBoardData.addProperty("avail_balance", acc_bal+"");
        leaderBoardData.addProperty("userName", new Utility().getUserName(FDSellActivity.this));
        leaderBoardData.addProperty("txn_id", ""+INVESTMENT_PACKET.get("txn_id").getAsString());
        leaderBoardData.addProperty("start_balance", start_balance);
        leaderBoardData.add("txnData", leaderBoardTxnData);

        account_ref.addProperty("shares_price", sharesprice+"");
        account_ref.addProperty("avail_balance", acc_bal+"");
        account_ref.addProperty("change", netstockchange+"");
        account_ref.addProperty("investment", total_investment+"");
        account_ref.addProperty("percentchange", netpercenttockchange);
        account_ref.addProperty("stocks_count", stock_count);

        transaction.addProperty("buy_price",(investmentamt/quantity)+"");
        transaction.addProperty("sell_price", (current_value/quantity)+"");
        transaction.addProperty("investment_amt", investmentamt+"");
        transaction.addProperty("sold_amount", current_value+"");
        transaction.addProperty("id", id+"");
        transaction.addProperty("name",name+"");
        transaction.addProperty("avail_qty", INVESTMENT_PACKET.get("qty").getAsInt()+"");
        transaction.addProperty("sell_qty", quantity+"");
        transaction.addProperty("timestamp", timestamp);
        transaction.addProperty("net_return", netreturn+"");
        transaction.addProperty("txn_amt", txn_charges+"");
        transaction.addProperty("txn_id", txn_id);
        //transaction.addProperty("invest_amt", invest_price+"");
        transaction.addProperty("return_change", stockchangeamt+"");
        transaction.addProperty("return_percentchange", percentstockchange+"");
        transaction.addProperty("change", netstockchange+"");
        transaction.addProperty("percentchange", netpercenttockchange+"");
        transaction.addProperty("acc_bal", acc_bal);

        txn_history.addProperty("id", "FD");
        txn_history.addProperty("name", "Fixed deposit");
        txn_history.addProperty("timestamp", timestamp);
        txn_history.addProperty("txn_id", txn_id);
        txn_history.addProperty("txn_type", "sell");
        txn_history.addProperty("type", "fixed_deposit");

        txn_history.add("txn_summary", transaction);

        objectFormatter.child("stocks_list")
                .child("sold_items").child("fixed_deposit").pushObject(txn_id, transaction);

        objectFormatter.child("txn_history").pushObject(txn_id, txn_history);

        data.addProperty("phoneNumber", FirebaseAuth.getInstance().getCurrentUser()
                .getPhoneNumber().substring(3));

        if(left_quantity == 0) {

            objectFormatter.child("stocks_list").child("bought_items").child("fixed_deposit")
                    .remove(INVESTMENT_PACKET.get("txn_id").getAsString());

        }else {

            objectFormatter.child("stocks_list").child("bought_items").child("fixed_deposit")
                    .child(INVESTMENT_PACKET.get("txn_id").getAsString()).pushValue("qty", ""+left_quantity);
            objectFormatter.child("stocks_list").child("bought_items").child("fixed_deposit")
                    .child(INVESTMENT_PACKET.get("txn_id").getAsString()).pushValue("total_amount", ""+left_stockinvestment);
        }

        data.add("Account", account_ref);
        data.addProperty("item_type", "fixed_deposit");

        data.add("leaderBoardData", leaderBoardData);

        return data;

    }

    public void statTimer() {

        countDownTimer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                timeouttv.setText("Remaining time..." + millisUntilFinished / 1000 + "s");
            }

            public void onFinish() {
                timeouttv.setText("Session expired");

                new Utility().showDialog("SESSION TIMEOUT",
                        "You took longer than we expected. Please try again", FDSellActivity.this);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },2000);
            }

        }.start();
    }

    public void getUserAccount() {

        progressDialog = new Utility().showDialog("Please wait while we are getting your account info.", FDSellActivity.this);
        progressDialog.setCancelable(false);

        new RetrofitClient().getInterface().getUserAccount(FirebaseAuth.getInstance().
                getCurrentUser().getPhoneNumber().substring(3), "FIXED_DEPOSIT").enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if(response.isSuccessful()) {

                    try {
                        progressDialog.dismiss();
                    }catch (Exception e){}

                    if (response.body().getAsJsonObject("data") != null) {
                        Log.d("response", response.body() + "");

                        userObject = response.body();

                        timestamp = userObject.get("timestamp").getAsLong();

                        initializeAmt();
                        updateAmounts();
                        updateViews();
                        statTimer();

                    } else if (response.body().get("flag") != null) {

                        try {
                            progressDialog.dismiss();
                        }catch (Exception e){}

                        new Utility().showDialog(response.body().get("flag").getAsString(),
                                response.body().get("message").getAsString(), FDSellActivity.this, new DialogInterface() {
                                    @Override
                                    public void onSuccess() {
                                        setResult(RESULT_OK);
                                        finish();
                                    }

                                    @Override
                                    public void onCancel() {
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                });

                    }else {
                        try {
                            progressDialog.dismiss();
                        }catch (Exception e){}
                        Toast.makeText(FDSellActivity.this, "Internal server error", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }

                }else {

                    try {
                        progressDialog.dismiss();
                    }catch (Exception e){}

                    try {
                        Log.d("SellActivity error", response.errorBody().string()+"");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(FDSellActivity.this, "Internal server error", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                try {
                    progressDialog.dismiss();
                }catch (Exception e){}
                Toast.makeText(FDSellActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    public void executeTransaction(JsonObject object) {

        progressDialog = new Utility().showDialog("Please wait for transaction to complete.", FDSellActivity.this);
        progressDialog.setCancelable(false);

        new RetrofitClient().getInterface().performTransaction(object).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                //progressDialog.dismiss();

                if(response.isSuccessful()) {
                    Log.d("data", ""+response.body());
                    //TODO: go to summary
                    pushDataInSP();
                    Intent intent = new Intent(FDSellActivity.this, TransactionsFDSellSumActivity.class);
                    intent.putExtra("success", true);
                    intent.putExtra("data", object.toString());
                    intent.putExtra("type", "sell");
                    intent.putExtra("txn_id", txn_id);
                    intent.putExtra("product_type", "fixed_deposit");
                    intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);

                    progressDialog.check();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                progressDialog.dismiss();
                            }catch (Exception e){}
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }
                    },2000);

                }else {
                    Toast.makeText(FDSellActivity.this, "Intenal server error", Toast.LENGTH_SHORT).show();
                    try {
                        Log.d("txn error", ""+response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(FDSellActivity.this, TransactionSummaryActivity.class);
                    intent.putExtra("success", false);
                    intent.putExtra("data", object.toString());
                    intent.putExtra("type", "sell");
                    intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                try {
                    progressDialog.dismiss();
                }catch (Exception e){}
                Toast.makeText(FDSellActivity.this, "Network error occued", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(FDSellActivity.this, TransactionSummaryActivity.class);
                intent.putExtra("success", false);
                intent.putExtra("data", object.toString());
                intent.putExtra("type", "sell");
                intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
    }

    public void pushDataInSP() {
        SharedPreferences sharedPreferences = getSharedPreferences(Utility.MyPREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //editor.putString("nextupdate", nextupdate+"");
        editor.putString("total_investment", (Double.parseDouble(sharedPreferences
                .getString("total_investment", "0")) - current_value) + "");
        editor.apply();
        editor.commit();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
}
