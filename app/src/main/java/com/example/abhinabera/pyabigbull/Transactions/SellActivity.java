package com.example.abhinabera.pyabigbull.Transactions;

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
import com.example.abhinabera.pyabigbull.Dialog.DialogInterface;
import com.example.abhinabera.pyabigbull.Dialog.ProgressDialog;
import com.example.abhinabera.pyabigbull.R;
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
                        //Log.d("qty", quantity+"");
                        numberStocks.setText("");
                        Toast.makeText(SellActivity.this, "invalid quantity", Toast.LENGTH_SHORT).show();
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
        //super.onBackPressed();
        setResult(RESULT_OK);
        finish();
        overridePendingTransition(R.anim.enter1, R.anim.exit1);
    }

    public String getTransId() {
        //%B for buy %S for sell
        timestamp = System.currentTimeMillis();
        return "txn" + type.substring(0,2) + timestamp%100000000 + "" + id + "S";
    }

    public void initializeAmt() {

        start_balance = Double.parseDouble(userObject.
                        get("start_balance").getAsString().replace(",", ""));
        left_quantity = 0;
        stock_count = Integer.parseInt(userObject.get("stocks_count").getAsString()
                .replace(",",""));
        sharesprice = Double.parseDouble(userObject.get("shares_price").getAsString()
                .replace(",",""));
        aval_balance = Double.parseDouble(userObject.get("avail_balance").getAsString().replace(",",""));
        total_investment = Double.parseDouble(userObject.get("investment").getAsString().replace(",", ""));
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
        netstockchange = Double.parseDouble(userObject.get("change").getAsString().replace(",",""));
        netpercenttockchange = Double.parseDouble(userObject.get("percentchange").getAsString().replace(",",""));

        if(companyNametv.getText().toString().trim().equalsIgnoreCase("SILVER")) {
            current_price = current_price * 0.1;
            currentStockPricetv.setText(new Utility().getRoundoffData(current_price+"")+"("+ "100gms" +")");
        } else if(companyNametv.getText().toString().trim().equalsIgnoreCase("GOLD")) {
            currentStockPricetv.setText(new Utility().getRoundoffData(current_price+"")+"("+ "10gms" +")");
        } else if(companyNametv.getText().toString().trim().equalsIgnoreCase("CRUDEOIL")) {
            currentStockPricetv.setText(new Utility().getRoundoffData(current_price+"")+"("+ "1bbl" +")");
        }
    }

    public void updateAmounts() {

        netreturn = current_price*quantity - (quantity==0?0:txn_charges);

        Log.d("netreturn", netreturn+"");
        stockchangeamt = (current_price - buyprice) * quantity - (quantity==0?0:(INVESTMENT_PACKET.get("txn_amt").getAsDouble()
                + txn_charges));

        percentstockchange = (stockchangeamt/investmentamt) * 100;

        acc_bal = aval_balance + netreturn;

        total_investment = Double.parseDouble(userObject.get("investment").getAsString()
                .replace(",", "")) - buyprice*quantity;

        sharesprice =  Double.parseDouble(userObject.get("shares_price").getAsString()
                .replace(",", "")) - buyprice*quantity;

        netstockchange = acc_bal + sharesprice - start_balance;

        netpercenttockchange = (netstockchange/start_balance) * 100;

        stock_count = userObject
                .get("stocks_count").getAsInt() - quantity;

        left_quantity = INVESTMENT_PACKET.get("qty").getAsInt() - quantity;

        left_stockinvestment = investmentamt - buyprice*quantity;

    }

    public void updateViews() {

        Utility utility = new Utility();
        returnstv.setText(utility.getRoundoffData(netreturn+""));
        changeamounttv.setText(utility.getRoundoffData(stockchangeamt+""));
        perchangetv.setText(utility.getRoundoffData(percentstockchange+"")+"%");
        accountBalancetv.setText(utility.getRoundoffData(acc_bal+"")+"");
        netchangeamounttv.setText(utility.getRoundoffData(netstockchange+""));
        netpecentchangetv.setText(utility.getRoundoffData(netpercenttockchange+"")+"%");

        if(percentstockchange>=0) {
            changeamounttv.setTextColor(getResources().getColor(R.color.greenText));
            perchangetv.setTextColor(getResources().getColor(R.color.greenText));
        }else {
            changeamounttv.setTextColor(getResources().getColor(R.color.red));
            perchangetv.setTextColor(getResources().getColor(R.color.red));
        }

        if(netpercenttockchange>=0) {
            netchangeamounttv.setTextColor(getResources().getColor(R.color.greenText));
            netpecentchangetv.setTextColor(getResources().getColor(R.color.greenText));
        }else {
            netchangeamounttv.setTextColor(getResources().getColor(R.color.red));
            netpecentchangetv.setTextColor(getResources().getColor(R.color.red));
        }
    }

    public boolean check() {

        if(quantity == 0) {
            Toast.makeText(SellActivity.this, "Set quantity", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(quantity > INVESTMENT_PACKET.get("qty").getAsInt()) {
            Toast.makeText(SellActivity.this, "Invalid quantity", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public JsonObject setAccountData() {

        String type_key = "";

        JsonObject account_ref = userObject;

        JsonObjectFormatter jsonformatter = new JsonObjectFormatter(account_ref);

        JsonObject txn_data = new JsonObject();
        JsonObject transaction = new JsonObject();
        JsonObject txn_history = new JsonObject();

        account_ref.addProperty("shares_price", sharesprice+"");
        account_ref.addProperty("avail_balance", acc_bal+"");
        account_ref.addProperty("change", netstockchange+"");
        account_ref.addProperty("investment", total_investment+"");
        account_ref.addProperty("percentchange", netpercenttockchange);
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
        txn_data.addProperty("item_type", type_key);

        return txn_data;
    }

    public void setPurchaseData() {

        buyStockPicetv.setText(new Utility().getRoundoffData(INVESTMENT_PACKET.get("buy_price").getAsString())+"");
        numberStocks.setText(""+INVESTMENT_PACKET.get("qty").getAsString());
        numberStocks.setSelection(numberStocks.getText().length());
        investmenttv.setText(""+new Utility().getRoundoffData(INVESTMENT_PACKET.get("total_amount").getAsString()));
        companyNametv.setText(""+name);

        if(userObject != null) {

            {
                availableBalancetv.setText("" + new Utility().getRoundoffData(userObject.get("avail_balance").getAsString()));
                totalInvestmenttv.setText("" + new Utility().getRoundoffData(userObject.get("investment").getAsString()));
            }
        }

        if(stockObject != null) {
            switch (type) {

                case "NIFTY" :
                    currentStockPricetv.setText(new Utility().getRoundoffData(stockObject.get("NSE").getAsJsonObject().
                            get("lastvalue").getAsString().replace(",",""))+"");
                    //investAmt.setText(currentStockPrice.getText().toString().replace(",",""));
                    break;

                case "COMMODITY" :
                    //investAmt.setText("100");
                    currentStockPricetv.setText(new Utility().getRoundoffData(stockObject.get("lastprice").getAsString())+"");
                    break;

                case "CURRENCY" :
                    //investAmt.setEnabled(false);
                    currentStockPricetv.setText(new Utility().getRoundoffData(stockObject.get("data").getAsJsonObject().
                            get("pricecurrent").getAsString().replace(",",""))+"");
                    //investAmt.setText(currentStockPrice.getText().toString().replace(",",""));
                    break;
            }
        }

        if(adminSettings != null) {

            Log.d("adminsettings", "type " +type);

            switch (type) {

                case "NIFTY" :
                    //investAmt.setEnabled(false);
                    transactionChargestv.setText("" + new Utility().getRoundoffData(adminSettings.
                            get("trans_amt_nifty").getAsString().replace(",","")));
                    break;

                case "COMMODITY" :
                    //investAmt.setText("100");
                    transactionChargestv.setText("" + new Utility().getRoundoffData(adminSettings.
                            get("trans_amt_commodity").getAsString().replace(",","")));
                    break;

                case "CURRENCY" :
                    //investAmt.setEnabled(false);
                    transactionChargestv.setText("" + new Utility().getRoundoffData(adminSettings.
                            get("trans_amt_currency").getAsString().replace(",","")));
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
            setResult(RESULT_OK);
            finish();
        }

    }

    public void statTimer() {

        countDownTimer = new CountDownTimer(60000, 1000) {

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

        new RetrofitClient().getInterface().getUserTxnData(FirebaseAuth.getInstance().
                getCurrentUser().getPhoneNumber().substring(3), type).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if(response.isSuccessful()) {

                    if(response.body().getAsJsonObject("data") != null) {
                        //Log.d("response", response.body() + "");

                        count++;
                        dismissDialog();

                        adminSettings = response.body().getAsJsonObject("data").getAsJsonObject("admin_settings");
                        userObject = response.body().getAsJsonObject("data").getAsJsonObject("Account");

                    }else if(response.body().get("flag")!=null) {

                        progressDialog.dismiss();

                        new Utility().showDialog(response.body().get("flag").getAsString(),
                                response.body().get("message").getAsString(), SellActivity.this, new DialogInterface() {
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

                    } else {
                        Toast.makeText(SellActivity.this, "Internal server error", Toast.LENGTH_SHORT).show();
                        count++;
                        dismissDialog();
                    }

                }else {

                    count++;
                    dismissDialog();

                    try {
                        Log.d("SellActivity error", response.errorBody().string() + "");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

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

                //progressDialog.dismiss();

                if(response.isSuccessful()) {
                    Log.d("data", ""+response.body());
                    //TODO: go to summary
                    Intent intent = new Intent(SellActivity.this, TransactionSummaryActivity.class);
                    intent.putExtra("success", true);
                    intent.putExtra("data", object.toString());
                    intent.putExtra("type", "sell");
                    intent.putExtra("txn_id", txn_id);
                    intent.putExtra("product_type", product_type);
                    intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);

                    progressDialog.check();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }
                    },2000);

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
                    intent.putExtra("type", "sell");
                    intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                    startActivity(intent);
                    //setResult(RESULT_OK);
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
                intent.putExtra("type", "sell");
                intent.putExtra("data", object.toString());
                intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(intent);
                //setResult(RESULT_OK);
                finish();
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
    }

    public void dismissDialog() {
        if(count == 2) {
            count = 0;
            progressDialog.dismiss();
            if(userObject!=null && stockObject != null) {
                setPurchaseData();
            }else {
                Toast.makeText(SellActivity.this, "Unexpected error occured. " +
                        "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
}
