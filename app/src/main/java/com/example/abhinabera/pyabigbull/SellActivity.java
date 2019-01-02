package com.example.abhinabera.pyabigbull;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellActivity extends AppCompatActivity {

    Toolbar purchaseToolbar;
    Typeface custom_font;
    TextView availableBalancetv, companyNametv, currentStockPricetv, buyStockPicetv, investmenttv, totalInvestmenttv,
            transactionChargestv,
            returnstv, changeamounttv, perchangetv, netchangeamounttv, netpecentchangetv, accountBalancetv, timeouttv;
    EditText numberStocks/*, investAmt*/;
    Button confirm;

    private String INTENT_DATA;
    JsonObject INVESTMENT_PACKET;

    private ProgressDialog progressDialog;

    private ApiInterface apiInterface;

    private String DATA_URL;

    int count = 0;

    private String type;
    private String product_type;
    private String id;
    private String name;

    JsonObject userObject;
    JsonObject stockObject;
    JsonObject adminSettings;

    CountDownTimer countDownTimer;

    double start_balance;
    double aval_balance;
    double total_investment;
    double current_price;
    double buyprice;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sell);
        getSupportActionBar().hide();

        type = getIntent().getStringExtra("type");
        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");

        INTENT_DATA = getIntent().getStringExtra("data");

        JsonParser parser = new JsonParser();
        INVESTMENT_PACKET = (JsonObject) parser.parse(INTENT_DATA);

        apiInterface = new RetrofitClient().getCurrencyInterface();
        purchaseToolbar = (Toolbar) findViewById(R.id.purchaseToolbar);
        purchaseToolbar.setTitle("SELL STOCKS");

        availableBalancetv = (TextView) findViewById(R.id.availableBalance);
        companyNametv = (TextView) findViewById(R.id.companyName);
        currentStockPricetv = (TextView) findViewById(R.id.currentStockPrice);
        totalInvestmenttv = (TextView) findViewById(R.id.totalInvestment);
        transactionChargestv = (TextView) findViewById(R.id.transactionCharges);
        returnstv = (TextView) findViewById(R.id.returns);
        accountBalancetv = (TextView) findViewById(R.id.accountBalance);
        buyStockPicetv = (TextView) findViewById(R.id.buyStockPrice);
        investmenttv = (TextView) findViewById(R.id.investment);
        changeamounttv = (TextView) findViewById(R.id.stockchange);
        perchangetv = (TextView) findViewById(R.id.percentstockchange);
        netchangeamounttv = (TextView) findViewById(R.id.netstockchange);
        netpecentchangetv = (TextView) findViewById(R.id.percentnetstockchange);

        numberStocks = (EditText) findViewById(R.id.numberStocks);
        //investAmt = (EditText) findViewById(R.id.investAmt);
        confirm = (Button) findViewById(R.id.confirmButton);

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
                    //TODO: network call
                    txn_id = getTransId();
                    JsonObject object = setAccountData();
                    Log.d("SellActivity", ""+object);
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
                    if(quantity > INVESTMENT_PACKET.get("qty").getAsInt()) {
                        quantity = INVESTMENT_PACKET.get("qty").getAsInt();
                        numberStocks.setText(INVESTMENT_PACKET.get("qty").getAsInt());
                    } else {
                        updateAmounts();
                        updateViews();
                    }
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
        return "txn" + type.substring(0,2) + timestamp%100000000 + "" + id + "S";
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
        current_price = Double.parseDouble(currentStockPricetv.getText().toString().trim().replace(",", ""));
        buyprice = Double.parseDouble(INVESTMENT_PACKET.get("buy_price").getAsString());
        quantity = Integer.parseInt(INVESTMENT_PACKET.get("qty").getAsString());;
        investmentamt = Double.parseDouble(INVESTMENT_PACKET.get("total_amount").getAsString());
        left_stockinvestment = investmentamt;
        txn_charges = Double.parseDouble(transactionChargestv.getText().toString().trim().replace(",", ""));
        netreturn = 0;
        stockchangeamt = 0;
        percentstockchange = 0;
        acc_bal = Double.parseDouble(accountBalancetv.getText().toString().trim().replace(",", ""));
        netstockchange = Double.parseDouble(userObject.get("data").getAsJsonObject().get("change").getAsString().replace(",",""));
        netpercenttockchange = Double.parseDouble(userObject.get("data").getAsJsonObject().get("percentchange").getAsString().replace(",",""));
    }

    public void updateAmounts() {

        netreturn = current_price*quantity - (quantity==0?0:txn_charges);

        Log.d("netreturn", netreturn+"");
        stockchangeamt = (current_price - buyprice) * quantity - (quantity==0?0:(INVESTMENT_PACKET.get("txn_amt").getAsDouble()
                + txn_charges));

        percentstockchange = (stockchangeamt/investmentamt) * 100;

        acc_bal = aval_balance + netreturn;

        total_investment = Double.parseDouble(userObject.get("data").getAsJsonObject().get("investment").getAsString()
                .replace(",", "")) - buyprice*quantity;

        sharesprice =  Double.parseDouble(userObject.get("data").getAsJsonObject().get("shares_price").getAsString()
                .replace(",", "")) - buyprice*quantity;

        netstockchange = acc_bal + sharesprice - start_balance;

        netpercenttockchange = (netstockchange/start_balance) * 100;

        stock_count = userObject.get("data").getAsJsonObject()
                .get("stocks_count").getAsInt() - quantity;

        left_quantity = INVESTMENT_PACKET.get("qty").getAsInt() - quantity;

        left_stockinvestment = investmentamt - buyprice*quantity;

        /*
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
*/
        Log.d("Amounts", ""+current_price+" : "+netreturn+" : "+aval_balance +" : " + total_investment + " : " + current_price + " : " +
        quantity +" : "+txn_charges+" : "+acc_bal);
    }

    public void updateViews() {

        Utility utility = new Utility();
        returnstv.setText(utility.getRoundoffData(netreturn+""));
        changeamounttv.setText(utility.getRoundoffData(stockchangeamt+""));
        perchangetv.setText(utility.getRoundoffData(percentstockchange+"")+"%");
        accountBalancetv.setText(utility.getRoundoffData(acc_bal+"")+"");
        netchangeamounttv.setText(utility.getRoundoffData(netstockchange+""));
        netpecentchangetv.setText(utility.getRoundoffData(netpercenttockchange+"")+"%");
    }

    public boolean check() {

        if(quantity == 0) {
            Toast.makeText(SellActivity.this, "Set quantity", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(quantity > stock_count) {
            Toast.makeText(SellActivity.this, "Invalid quantity", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public JsonObject setAccountData() {

        String type_key = "";

        JsonObject account_ref = userObject.get("data").getAsJsonObject();

        JsonObjectFormatter jsonformatter = new JsonObjectFormatter(account_ref);

        JsonObject txn_data = new JsonObject();
        JsonObject transaction = new JsonObject();
        JsonObject txn_history = new JsonObject();

        account_ref.addProperty("shares_price", sharesprice+"");
        account_ref.addProperty("avail_balance", acc_bal+"");
        account_ref.addProperty("change", netstockchange+"");
        account_ref.addProperty("investment", total_investment+"");
        account_ref.addProperty("percentchange", netpercenttockchange+"");
        account_ref.addProperty("stocks_count", stock_count);

        switch (type) {

            case "NIFTY":

                type_key = "index";
                product_type = "index";

                txn_history.addProperty("comp_id", id);
                txn_history.addProperty("comp_name", name);
                txn_history.addProperty("ex", "NSE");
                txn_history.addProperty("id", "NIFTY");
                txn_history.addProperty("ind_id", "9");
                txn_history.addProperty("name", "NIFTY50");
                txn_history.addProperty("timestamp", timestamp);
                txn_history.addProperty("txn_id", txn_id);
                txn_history.addProperty("txn_type", "sell");
                txn_history.addProperty("type", "index");

                jsonformatter.child("stocks_list").child("sold_items").child("index").pushValue("ex","NSE");
                jsonformatter.child("stocks_list").child("sold_items").child("index").pushValue("id", "NIFTY");
                jsonformatter.child("stocks_list").child("sold_items").child("index").pushValue("ind_id","9");
                jsonformatter.child("stocks_list").child("sold_items").child("index").pushValue("name", "NIFTY50");
                jsonformatter.child("stocks_list").child("sold_items").child("index").pushValue("type", "index");

                transaction.addProperty("buy_price",buyprice+"");
                transaction.addProperty("sell_price", current_price+"");
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

                break;

            case "COMMODITY" :

                type_key = "commodity";
                product_type = "commodity";

                txn_history.addProperty("ex", "MCX");
                txn_history.addProperty("id", id+"");
                txn_history.addProperty("name", name+"");
                txn_history.addProperty("timestamp", timestamp);
                txn_history.addProperty("txn_id", txn_id);
                txn_history.addProperty("txn_type", "sell");
                txn_history.addProperty("type", "commodity");

                jsonformatter.child("stocks_list").child("sold_items").child("commodity").pushValue("ex","MCX");
                jsonformatter.child("stocks_list").child("sold_items").child("commodity").pushValue("type", "commodity");

                transaction.addProperty("buy_price",buyprice+"");
                transaction.addProperty("sell_price", current_price+"");
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

                break;

            case "CURRENCY" :

                type_key = "currency";
                product_type = "currency";

                txn_history.addProperty("ex", "FOREX");
                txn_history.addProperty("id", id+"");
                txn_history.addProperty("name", name+"");
                txn_history.addProperty("timestamp", timestamp);
                txn_history.addProperty("txn_id", txn_id);
                txn_history.addProperty("txn_type", "sell");
                txn_history.addProperty("type", "currency");

                jsonformatter.child("stocks_list").child("sold_items").child("currency").pushValue("ex","FOREX");
                jsonformatter.child("stocks_list").child("sold_items").child("currency").pushValue("type", "currency");

                transaction.addProperty("buy_price",buyprice+"");
                transaction.addProperty("sell_price", current_price+"");
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

                break;

        }

        txn_history.add("txn_summary", transaction);

        txn_data.addProperty("phoneNumber", FirebaseAuth.getInstance().
                getCurrentUser().getPhoneNumber().substring(3).trim());

        jsonformatter.child("stocks_list")
                .child("sold_items").child(type_key).pushObject(txn_id, transaction);

        jsonformatter.child("txn_history").pushObject(txn_id, txn_history);

        if(left_quantity == 0) {

        	jsonformatter.child("stocks_list").child("bought_items").child(type_key)
                    .remove(INVESTMENT_PACKET.get("txn_id").getAsString());

        }else {

        	jsonformatter.child("stocks_list").child("bought_items").child(type_key)
                    .child(INVESTMENT_PACKET.get("txn_id").getAsString()).pushValue("qty", ""+left_quantity);
        	jsonformatter.child("stocks_list").child("bought_items").child(type_key)
                    .child(INVESTMENT_PACKET.get("txn_id").getAsString()).pushValue("total_amount", ""+left_stockinvestment);
        }

        txn_data.add("Account", account_ref);

        return txn_data;
    }

    public void setPurchaseData() {

        buyStockPicetv.setText(INVESTMENT_PACKET.get("buy_price").getAsString()+"");
        numberStocks.setText(""+INVESTMENT_PACKET.get("qty").getAsString());
        investmenttv.setText(""+INVESTMENT_PACKET.get("total_amount").getAsString());
        companyNametv.setText(""+name);

        if(userObject != null) {

            if (userObject.get("data") != null) {
                availableBalancetv.setText("" + userObject.get("data").getAsJsonObject().get("avail_balance").getAsString());
                totalInvestmenttv.setText("" + userObject.get("data").getAsJsonObject().get("investment").getAsString());
            }
        }

        if(stockObject != null) {
            switch (type) {

                case "NIFTY" :
                    currentStockPricetv.setText(stockObject.get("NSE").getAsJsonObject().
                            get("lastvalue").getAsString().replace(",","")+"");
                    //investAmt.setText(currentStockPrice.getText().toString().replace(",",""));
                    break;

                case "COMMODITY" :
                    //investAmt.setText("100");
                    currentStockPricetv.setText(stockObject.get("lastprice").getAsString()+"");
                    break;

                case "CURRENCY" :
                    //investAmt.setEnabled(false);
                    currentStockPricetv.setText(stockObject.get("data").getAsJsonObject().
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
                    transactionChargestv.setText("" + adminSettings.get("data").getAsJsonObject().
                            get("trans_amt_nifty").getAsString().replace(",",""));
                    break;

                case "COMMODITY" :
                    //investAmt.setText("100");
                    transactionChargestv.setText("" + adminSettings.get("data").getAsJsonObject().
                            get("trans_amt_commodity").getAsString().replace(",",""));
                    break;

                case "CURRENCY" :
                    //investAmt.setEnabled(false);
                    transactionChargestv.setText("" + adminSettings.get("data").getAsJsonObject().
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
            Toast.makeText(SellActivity.this, "Unexpected error occured. " +
                    "Please check your internet connection.", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    public void statTimer() {

        countDownTimer = new CountDownTimer(90000, 1000) {

            public void onTick(long millisUntilFinished) {
                timeouttv.setText("Remaining time..." + millisUntilFinished / 1000 + "s");
            }

            public void onFinish() {
                timeouttv.setText("Session expired");

                new Utility().showDialog("SESSION TIMEOUT",
                        "You took longer than we expected. Please try again", SellActivity.this);

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

        progressDialog = new Utility().showDialog("Please wait while we are getting your account info.", SellActivity.this);
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
                        Log.d("SellActivity error", response.errorBody().string()+"");
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

        progressDialog = new Utility().showDialog("Please wait for transaction to complete.", SellActivity.this);
        progressDialog.setCancelable(false);

        new RetrofitClient().getInterface().performTransaction(object).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                progressDialog.dismiss();

                if(response.isSuccessful()) {
                    Log.d("data", ""+response.body());
                    //TODO: go to summary
                    Intent intent = new Intent(SellActivity.this, TransactionSummaryActivity.class);
                    intent.putExtra("success", true);
                    intent.putExtra("data", object.toString());
                    intent.putExtra("type", "sell");
                    intent.putExtra("txn_id", txn_id);
                    intent.putExtra("product_type", product_type);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }else {
                    Toast.makeText(SellActivity.this, "Intenal server error", Toast.LENGTH_SHORT).show();
                    try {
                        Log.d("txn error", ""+response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(SellActivity.this, TransactionSummaryActivity.class);
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
                Toast.makeText(SellActivity.this, "Network error occued", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SellActivity.this, TransactionSummaryActivity.class);
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
