package com.example.abhinabera.pyabigbull;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhinabera.pyabigbull.Api.ApiInterface;
import com.example.abhinabera.pyabigbull.Api.RetrofitClient;
import com.example.abhinabera.pyabigbull.Api.Utility;
import com.example.abhinabera.pyabigbull.Dialog.ProgressDialog;
import com.example.abhinabera.pyabigbull.Login.OTPActivity;
import com.example.abhinabera.pyabigbull.Login.UserNameActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PurchaseActivity extends AppCompatActivity {

    android.support.v7.widget.Toolbar purchaseToolbar;
    Typeface custom_font;
    TextView availableBalance, companyName, currentStockPrice, totalInvestment, transactionCharges, totalCost,
            accountBalance, timeout;
    EditText numberStocks/*, investAmt*/;
    Button confirm;

    private ProgressDialog progressDialog;

    private ApiInterface apiInterface;

    private String DATA_URL;

    int count = 0;

    private String type;
    private String id;
    private String name;

    JsonObject userObject;
    JsonObject stockObject;
    JsonObject adminSettings;

    CountDownTimer countDownTimer;

    double aval_balance;
    double total_investment;
    double current_price;
    int quantity = 1;
    //double invest_price = 100;
    double txn_charges;
    double total_debit;
    double acc_bal;
    double change;
    double percentchange;
    int stock_count;
    String txn_id;
    long timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_purchase);
        getSupportActionBar().hide();

        type = getIntent().getStringExtra("type");
        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        
        apiInterface = new RetrofitClient().getCurrencyInterface();
        purchaseToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.purchaseToolbar);
        purchaseToolbar.setTitle("BUY STOCKS");

        availableBalance = (TextView) findViewById(R.id.availableBalance);
        companyName = (TextView) findViewById(R.id.companyName);
        currentStockPrice = (TextView) findViewById(R.id.currentStockPrice);
        totalInvestment = (TextView) findViewById(R.id.totalInvestment);
        transactionCharges = (TextView) findViewById(R.id.transactionCharges);
        totalCost = (TextView) findViewById(R.id.totalCost);
        accountBalance = (TextView) findViewById(R.id.accountBalance);

        numberStocks = (EditText) findViewById(R.id.numberStocks);
        //investAmt = (EditText) findViewById(R.id.investAmt);
        confirm = (Button) findViewById(R.id.confirmButton);

        timeout = (TextView) findViewById(R.id.timeout);

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
                    //TODO: network call
                    txn_id = getTransId();
                    JsonObject object = getTransactionData();
                    Log.d("PurchaseActivity", ""+object);
                    countDownTimer.cancel();
                    executeTransaction(object);
                }
            }
        });


        getUserAccount();
        getAdminSettings();
        getCurrentQuote(type, id);

        numberStocks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().isEmpty()) {
                    quantity = 0;
                    updateAmounts();
                    updateViews();

                }else {

                    quantity = Integer.parseInt(editable.toString().trim());
                    updateAmounts();
                    updateViews();
                }
            }
        });

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

    public String getTransId() {
        //%B for buy %S for sell
        timestamp = System.currentTimeMillis();
        return "txn" + type.substring(0,2) + timestamp%100000000 + "" + id + "B";
    }

    public void initializeAmt() {

        aval_balance = Double.parseDouble(userObject.get("data").getAsJsonObject().get("avail_balance").getAsString().replace(",",""));
        total_investment = Double.parseDouble(userObject.get("data").getAsJsonObject().get("investment").getAsString().replace(",", ""));
        current_price = Double.parseDouble(currentStockPrice.getText().toString().trim().replace(",", ""));
        quantity = 1;
        //invest_price = Double.parseDouble(investAmt.getText().toString().trim().replace(",", ""));
        txn_charges = Double.parseDouble(transactionCharges.getText().toString().trim().replace(",", ""));
        total_debit = Double.parseDouble(totalCost.getText().toString().trim().replace(",", ""));
        acc_bal = Double.parseDouble(accountBalance.getText().toString().trim().replace(",", ""));
    }

    public void updateAmounts() {

        //**NO CHANGE: avail_balance, curent price, total_investment

        total_debit = current_price*quantity + txn_charges*(quantity==0?0:1);
        acc_bal = aval_balance - total_debit;
        total_investment = Double.parseDouble(userObject.get("data").getAsJsonObject()
                .get("investment").getAsString()) + current_price*quantity;
        stock_count = Integer.parseInt(userObject.get("data").getAsJsonObject()
                .get("stocks_count").getAsString()) + quantity;

        change = acc_bal + total_investment - Double.parseDouble(userObject.get("data").getAsJsonObject().
                get("start_balance").getAsString().replace(",", ""));
        percentchange = (change/Double.parseDouble(userObject.get("data").getAsJsonObject().
                get("start_balance").getAsString().replace(",", "")))*100;

        //Log.d("Amounts", aval_balance +" : " + total_investment + " : " + current_price + " : " +
        //quantity +" : "+invest_price+" : "+txn_charges+" : "+total_debit+" : "+acc_bal);
    }

    public void updateViews() {
        totalCost.setText(total_debit+"");
        accountBalance.setText(""+ acc_bal);
    }

    public boolean check() {

        if(acc_bal < 0 ) {
            new Utility().showDialog("INSUFFICIENT BALANCE",
                    "You do not have enough balance in your account to buy this.", PurchaseActivity.this);

            return false;
        }

        if(total_debit > 0.5*Double.parseDouble(userObject.get("data").getAsJsonObject().
                get("start_balance").getAsString().replace(",", ""))) {

            new Utility().showDialog("INVALID AMOUNT",
                    "Total purchase amount must be less than 50% of initial alloted amount(" +
                            userObject.get("data").getAsJsonObject().get("start_balance").getAsString()
                            + ").", PurchaseActivity.this);

            return false;
        }

        if(quantity == 0) {
            Toast.makeText(PurchaseActivity.this, "Set quantity", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public JsonObject getTransactionData() {

        JsonObject data = new JsonObject();

        JsonObject account = new JsonObject();
        JsonObject bought_item = new JsonObject();
        JsonObject txn_history = new JsonObject();
        JsonObject transaction = new JsonObject();

        account.addProperty("avail_balance", accountBalance.getText().toString().trim()+"");
        account.addProperty("change", change+"");
        account.addProperty("investment", total_investment+"");
        account.addProperty("percentchange", percentchange+"");
        account.addProperty("stocks_count", stock_count);

        switch (type) {

            case "NIFTY" :
                data.addProperty("product_type", "index");

                txn_history.addProperty("comp_id", id);
                txn_history.addProperty("comp_name", name);
                txn_history.addProperty("ex", "NSE");
                txn_history.addProperty("id", "NIFTY");
                txn_history.addProperty("ind_id", "9");
                txn_history.addProperty("name", "NIFTY50");
                txn_history.addProperty("timestamp", timestamp);
                txn_history.addProperty("txn_id", txn_id);
                txn_history.addProperty("txn_type", "buy");
                txn_history.addProperty("type", "index");

                bought_item.addProperty("ex","NSE");
                bought_item.addProperty("id", "NIFTY");
                bought_item.addProperty("ind_id","9");
                bought_item.addProperty("name", "NIFTY50");
                bought_item.addProperty("type", "index");

                transaction.addProperty("buy_price",current_price+"");
                transaction.addProperty("id", id+"");
                transaction.addProperty("name",name+"");
                transaction.addProperty("qty", quantity+"");
                transaction.addProperty("timestamp", timestamp);
                transaction.addProperty("total_amount", total_debit+"");
                transaction.addProperty("txn_amt", txn_charges+"");
                transaction.addProperty("txn_id", txn_id);
                //transaction.addProperty("invest_amt", invest_price+"");
                transaction.addProperty("change", change+"");
                transaction.addProperty("percentchange", percentchange+"");
                transaction.addProperty("acc_bal", acc_bal);

                break;

            case "COMMODITY" :
                data.addProperty("product_type", "commodity");

                txn_history.addProperty("ex", "MCX");
                txn_history.addProperty("id", id+"");
                txn_history.addProperty("name", name+"");
                txn_history.addProperty("timestamp", timestamp);
                txn_history.addProperty("txn_id", txn_id);
                txn_history.addProperty("txn_type", "buy");
                txn_history.addProperty("type", "commodity");

                bought_item.addProperty("ex","MCX");
                bought_item.addProperty("type", "commodity");

                transaction.addProperty("buy_price",current_price+"");
                transaction.addProperty("id", id+"");
                transaction.addProperty("name",name+"");
                transaction.addProperty("qty", quantity+"");
                transaction.addProperty("timestamp", timestamp);
                transaction.addProperty("total_amount", total_debit+"");
                transaction.addProperty("txn_amt", txn_charges+"");
                transaction.addProperty("txn_id", txn_id);
                //transaction.addProperty("invest_amt", invest_price+"");
                transaction.addProperty("change", change+"");
                transaction.addProperty("percentchange", percentchange+"");
                transaction.addProperty("acc_bal", acc_bal);

                break;

            case "CURRENCY" :
                data.addProperty("product_type", "currency");

                txn_history.addProperty("ex", "FOREX");
                txn_history.addProperty("id", id+"");
                txn_history.addProperty("name", name+"");
                txn_history.addProperty("timestamp", timestamp);
                txn_history.addProperty("txn_id", txn_id);
                txn_history.addProperty("txn_type", "buy");
                txn_history.addProperty("type", "currency");

                bought_item.addProperty("ex","FOREX");
                bought_item.addProperty("type", "currency");

                transaction.addProperty("buy_price",current_price+"");
                transaction.addProperty("id", id+"");
                transaction.addProperty("name",name+"");
                transaction.addProperty("qty", quantity+"");
                transaction.addProperty("timestamp", timestamp);
                transaction.addProperty("total_amount", total_debit+"");
                transaction.addProperty("txn_amt", txn_charges+"");
                transaction.addProperty("txn_id", txn_id);
                //transaction.addProperty("invest_amt", invest_price+"");
                transaction.addProperty("change", change+"");
                transaction.addProperty("percentchange", percentchange+"");
                transaction.addProperty("acc_bal", acc_bal);

                break;
        }

        //txn_history.add("txn_summary", transaction);

        data.addProperty("phoneNumber", FirebaseAuth.getInstance().
                getCurrentUser().getPhoneNumber().substring(3).trim());
        data.addProperty("txn_id", txn_id);
        data.add("Account", account);
        data.add("bought_item", bought_item);
        data.add("transaction", transaction);
        data.add("txn_history", txn_history);
        data.add("txn_summary", transaction);

        return data;
    }

    public void setPurchaseData() {

        numberStocks.setText(""+1);
        companyName.setText(""+name);

        if(userObject != null) {

            if (userObject.get("data") != null) {
                availableBalance.setText("" + userObject.get("data").getAsJsonObject().get("avail_balance").getAsString());
                totalInvestment.setText("" + userObject.get("data").getAsJsonObject().get("investment").getAsString());
            }
        }

        if(stockObject != null) {
            switch (type) {

                case "NIFTY" :
                    currentStockPrice.setText(stockObject.get("NSE").getAsJsonObject().
                            get("lastvalue").getAsString().replace(",","")+"");
                    //investAmt.setText(currentStockPrice.getText().toString().replace(",",""));
                    break;

                case "COMMODITY" :
                    //investAmt.setText("100");
                    currentStockPrice.setText(stockObject.get("lastprice").getAsString()+"");
                    break;

                case "CURRENCY" :
                    //investAmt.setEnabled(false);
                    currentStockPrice.setText(stockObject.get("data").getAsJsonObject().
                            get("pricecurrent").getAsString().replace(",","")+"");
                    //investAmt.setText(currentStockPrice.getText().toString().replace(",",""));
                    break;

            }
        }

        if(adminSettings != null) {

            Log.d("adminsettings", "type " +type);

            switch (type) {

                case "NIFTY" :
                    //investAmt.setEnabled(false);
                    transactionCharges.setText("" + adminSettings.get("data").getAsJsonObject().
                            get("trans_amt_nifty").getAsString().replace(",",""));
                    break;

                case "COMMODITY" :
                    //investAmt.setText("100");
                    transactionCharges.setText("" + adminSettings.get("data").getAsJsonObject().
                            get("trans_amt_commodity").getAsString().replace(",",""));
                    break;

                case "CURRENCY" :
                    //investAmt.setEnabled(false);
                    transactionCharges.setText("" + adminSettings.get("data").getAsJsonObject().
                            get("trans_amt_currency").getAsString().replace(",",""));
                    break;

            }
        }

        if(userObject!=null && stockObject!=null && adminSettings!=null) {

            initializeAmt();
            updateAmounts();
            updateViews();
            statTimer();

        }else {
            Toast.makeText(PurchaseActivity.this, "Unexpected error occured. " +
                    "Please check your internet connection.", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    public void statTimer() {

        countDownTimer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                timeout.setText("Remaining time..." + millisUntilFinished / 1000 + "s");
            }

            public void onFinish() {
                timeout.setText("Session expired");

                new Utility().showDialog("SESSION TIMEOUT",
                        "You took longer than we expected. Please try again", PurchaseActivity.this);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },2000);
            }

        }.start();
    }

    public void getCurrentQuote(String type, String id) {

        switch (type) {

            case "NIFTY" :
                DATA_URL = "jsonapi/stocks/overview&format=json&sc_id="+id+"&ex=N";
                getData(DATA_URL);
                break;

            case "COMMODITY" :
                DATA_URL = "jsonapi/commodity/top_commodity&ex=MCX&format=json";
                getData(DATA_URL);
                break;

            case "CURRENCY" :
                DATA_URL = "pricefeed/notapplicable/currencyspot/%24%24%3B"+ id;
                getCurrencyData(DATA_URL);
                break;
        }
    }

    public void getData(String url) {

        new RetrofitClient().getNifty50Interface().getData(url).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if(response.isSuccessful()) {
                    stockObject = response.body();

                    if(type.equals("COMMODITY")) {

                        for(JsonElement element: response.body().get("list").getAsJsonArray()) {
                            if(element.getAsJsonObject().get("id").getAsString().trim().equalsIgnoreCase(id)) {
                                stockObject = element.getAsJsonObject();
                            }
                        }
                    }

                }else {
                    try {
                        Log.d("Purchase error", response.errorBody().string()+"");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                count++;
                dismissDialog();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                count++;
                dismissDialog();
            }
        });
    }

    public void getCurrencyData(String url) {

        new RetrofitClient().getCurrencyInterface().getData(url).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if(response.isSuccessful()) {
                    stockObject = response.body();
                }else {
                    try {
                        Log.d("Purchase error", response.errorBody().string()+"");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                count++;
                dismissDialog();

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                count++;
                dismissDialog();
            }
        });
    }

    public void getUserAccount() {

        progressDialog = new Utility().showDialog("Please wait while we are getting your account info.", PurchaseActivity.this);
        progressDialog.setCancelable(false);

        new RetrofitClient().getInterface().getUserAccount(FirebaseAuth.getInstance().
                getCurrentUser().getPhoneNumber().substring(3)).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if(response.isSuccessful()) {
                    Log.d("response", response.body()+"");
                    userObject = response.body();
                }else {
                    try {
                        Log.d("Purchase error", response.errorBody().string()+"");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                count++;
                dismissDialog();

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                count++;
                dismissDialog();
            }
        });
    }

    public void getAdminSettings() {

        new RetrofitClient().getInterface().getAdminSettings().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if(response.isSuccessful()) {
                    Log.d("response", response.body()+"");
                    adminSettings = response.body();
                }else {
                    try {
                        Log.d("Purchase error", response.errorBody().string()+"");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                count++;
                dismissDialog();

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                count++;
                dismissDialog();
            }
        });
    }

    public void executeTransaction(JsonObject object) {

        progressDialog = new Utility().showDialog("Please wait for transaction to complete.", PurchaseActivity.this);
        progressDialog.setCancelable(false);

        new RetrofitClient().getInterface().executeTransaction(object).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                progressDialog.dismiss();

                if(response.isSuccessful()) {
                    Log.d("data", ""+response.body());
                    //TODO: go to summary
                    Intent intent = new Intent(PurchaseActivity.this, TransactionSummaryActivity.class);
                    intent.putExtra("success", true);
                    intent.putExtra("data", object.toString());
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }else {
                    Toast.makeText(PurchaseActivity.this, "Intenal server error", Toast.LENGTH_SHORT).show();
                    try {
                        Log.d("txn error", ""+response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(PurchaseActivity.this, TransactionSummaryActivity.class);
                    intent.putExtra("success", false);
                    intent.putExtra("data", object.toString());
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(PurchaseActivity.this, "Network error occued", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PurchaseActivity.this, TransactionSummaryActivity.class);
                intent.putExtra("success", false);
                intent.putExtra("data", object.toString());
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
    }

    public void dismissDialog() {
        if(count == 3) {
            count = 0;
            progressDialog.dismiss();
            setPurchaseData();
        }
    }
}
