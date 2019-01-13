package nabera.ranjan.abhinabera.pyabigbull.Transactions;

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

import nabera.ranjan.abhinabera.pyabigbull.Api.ApiInterface;
import nabera.ranjan.abhinabera.pyabigbull.Api.RetrofitClient;
import nabera.ranjan.abhinabera.pyabigbull.Api.Utility;
import nabera.ranjan.abhinabera.pyabigbull.Dialog.DialogInterface;
import nabera.ranjan.abhinabera.pyabigbull.Dialog.ProgressDialog;
import nabera.ranjan.abhinabera.pyabigbull.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

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

    private double CURRENT_INVESTMENT = 0;

    int count = 0;

    private String type;
    private String product_type;
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
    double shares_price;
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
                    JsonObject object = setTransactionData();
                    Log.d("PurchaseActivity", ""+object);
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
        setResult(RESULT_OK);
        finish();
        overridePendingTransition(R.anim.enter1, R.anim.exit1);
    }

    public String getTransId() {
        //%B for buy %S for sell
        //timestamp = System.currentTimeMillis();
        return "txn" + type.substring(0,2) + timestamp%100000000 + "" + id + "B";
    }

    public void initializeAmt() {

        shares_price = Double.parseDouble(userObject.get("shares_price").getAsString().replace(",",""));
        aval_balance = Double.parseDouble(userObject.get("avail_balance").getAsString().replace(",",""));
        total_investment = Double.parseDouble(userObject.get("investment").getAsString().replace(",", ""));
        current_price = Double.parseDouble(currentStockPrice.getText().toString().trim().replace(",", ""));
        quantity = 1;
        //invest_price = Double.parseDouble(investAmt.getText().toString().trim().replace(",", ""));
        txn_charges = Double.parseDouble(transactionCharges.getText().toString().trim().replace(",", ""));
        total_debit = Double.parseDouble(totalCost.getText().toString().trim().replace(",", ""));
        acc_bal = Double.parseDouble(accountBalance.getText().toString().trim().replace(",", ""));

        if(companyName.getText().toString().trim().equalsIgnoreCase("SILVER")) {
            current_price = current_price * 0.1;
            currentStockPrice.setText(new Utility().getRoundoffData(current_price+"")+"("+ "100gms" +")");
        } else if(companyName.getText().toString().trim().equalsIgnoreCase("GOLD")) {
            currentStockPrice.setText(new Utility().getRoundoffData(current_price+"")+"("+ "10gms" +")");
        } else if(companyName.getText().toString().trim().equalsIgnoreCase("CRUDEOIL")) {
            currentStockPrice.setText(new Utility().getRoundoffData(current_price+"")+"("+ "1bbl" +")");
        }
    }

    public void updateAmounts() {

        //**NO CHANGE: avail_balance, curent price, total_investment

        shares_price = Double.parseDouble(userObject.get("shares_price").
                getAsString().replace(",","")) + current_price*quantity;

        total_debit = current_price*quantity + txn_charges*(quantity==0?0:1);
        acc_bal = aval_balance - total_debit;

        total_investment = Double.parseDouble(userObject
                .get("investment").getAsString()) + total_debit;

        stock_count = Integer.parseInt(userObject
                .get("stocks_count").getAsString()) + quantity;

        change = acc_bal + shares_price - Double.parseDouble(userObject.
                get("start_balance").getAsString().replace(",", ""));

        percentchange = (change/Double.parseDouble(userObject.
                get("start_balance").getAsString().replace(",", "")))*100;

        //Log.d("Amounts", aval_balance +" : " + total_investment + " : " + current_price + " : " +
        //quantity +" : "+invest_price+" : "+txn_charges+" : "+total_debit+" : "+acc_bal);
    }

    public void updateViews() {
        totalCost.setText(new Utility().getRoundoffData(total_debit+""));
        accountBalance.setText(new Utility().getRoundoffData(""+ acc_bal));
    }

    public boolean check() {

        if(acc_bal < 0 ) {
            new Utility().showDialog("INSUFFICIENT BALANCE",
                    "You do not have enough balance in your account to buy this.", PurchaseActivity.this);

            return false;
        }

        if((CURRENT_INVESTMENT+total_debit) > 0.5*Double.parseDouble(userObject.
                get("start_balance").getAsString().replace(",", ""))) {

            new Utility().showDialog("INVALID AMOUNT",
                    "Total investment must be less than 50% of initial alloted amount(" +
                            userObject.get("start_balance").getAsString()
                            + ").", PurchaseActivity.this);

            return false;
        }

        if(quantity == 0) {
            Toast.makeText(PurchaseActivity.this, "Set quantity", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public JsonObject setTransactionData() {

        String type_key = "";

        JsonObject account_ref = userObject;

        JsonObjectFormatter jsonformatter = new JsonObjectFormatter(account_ref);

        JsonObject data = new JsonObject();

        JsonObject leaderBoardData = new JsonObject();
        JsonObject leaderBoardTxnData = new JsonObject();

        JsonObject txn_history = new JsonObject();
        JsonObject transaction = new JsonObject();

        String imageUrl = (userObject.get("imageUrl")==null)?null:userObject.get("imageUrl").getAsString();

        //leaderBoardData.addProperty("imageUrl", imageUrl);
        leaderBoardData.addProperty("avail_balance", acc_bal+"");
        leaderBoardData.addProperty("txn_id", ""+txn_id);
        leaderBoardData.addProperty("userName", new Utility().getUserName(PurchaseActivity.this));
        leaderBoardData.addProperty("start_balance", Double.parseDouble(userObject.get("start_balance").getAsString()
                .replace(",","")) + "");

        account_ref.addProperty("shares_price", shares_price+"");
        account_ref.addProperty("avail_balance", /*accountBalance.getText().toString().trim()*/acc_bal+"");
        account_ref.addProperty("change", change+"");
        account_ref.addProperty("investment", total_investment+"");
        account_ref.addProperty("percentchange", percentchange);
        account_ref.addProperty("stocks_count", stock_count);

        switch (type) {

            case "NIFTY" :
                type_key = "index";
                product_type = type_key;

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

                jsonformatter.child("stocks_list").child("bought_items").child("index").pushValue("ex","NSE");
                jsonformatter.child("stocks_list").child("bought_items").child("index").pushValue("id", "NIFTY");
                jsonformatter.child("stocks_list").child("bought_items").child("index").pushValue("ind_id","9");
                jsonformatter.child("stocks_list").child("bought_items").child("index").pushValue("name", "NIFTY50");
                jsonformatter.child("stocks_list").child("bought_items").child("index").pushValue("type", "index");

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
                type_key = "commodity";
                product_type = type_key;

                txn_history.addProperty("ex", "MCX");
                txn_history.addProperty("id", id+"");
                txn_history.addProperty("name", name+"");
                txn_history.addProperty("timestamp", timestamp);
                txn_history.addProperty("txn_id", txn_id);
                txn_history.addProperty("txn_type", "buy");
                txn_history.addProperty("type", "commodity");

                jsonformatter.child("stocks_list").child("bought_items").child("commodity").pushValue("ex","MCX");
                jsonformatter.child("stocks_list").child("bought_items").child("commodity").pushValue("type", "commodity");

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
                type_key = "currency";
                product_type = type_key;

                txn_history.addProperty("ex", "FOREX");
                txn_history.addProperty("id", id+"");
                txn_history.addProperty("name", name+"");
                txn_history.addProperty("timestamp", timestamp);
                txn_history.addProperty("txn_id", txn_id);
                txn_history.addProperty("txn_type", "buy");
                txn_history.addProperty("type", "currency");

                jsonformatter.child("stocks_list").child("bought_items").child("currency").pushValue("ex","FOREX");
                jsonformatter.child("stocks_list").child("bought_items").child("currency").pushValue("type", "currency");

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

        txn_history.add("txn_summary", transaction);

        leaderBoardTxnData.addProperty("id", ""+id);
        leaderBoardTxnData.addProperty("qty", ""+quantity);
        leaderBoardTxnData.addProperty("total_amount", ""+total_debit);
        leaderBoardTxnData.addProperty("type", ""+type_key);

        leaderBoardData.add("txnData", leaderBoardTxnData);

        data.addProperty("phoneNumber", FirebaseAuth.getInstance().
                getCurrentUser().getPhoneNumber().substring(3).trim());
        
        data.add("Account", account_ref);

        jsonformatter.child("stocks_list")
                .child("bought_items").child(type_key).pushObject(txn_id, transaction);

        jsonformatter.child("txn_history").pushObject(txn_id, txn_history);

        data.addProperty("item_type", type_key);

        data.add("leaderBoardData", leaderBoardData);

        return data;
    }

    public void setPurchaseData() {

        numberStocks.setText(""+1);
        numberStocks.setSelection(numberStocks.getText().length());
        companyName.setText(""+name);

        Utility utility = new Utility();

        if(userObject != null) {

            if (userObject != null) {
                availableBalance.setText(utility.getRoundoffData("" + userObject.get("avail_balance").getAsString()));
                totalInvestment.setText(utility.getRoundoffData("" + userObject.get("investment").getAsString()));
            }
        }

        if(stockObject != null) {
            switch (type) {

                case "NIFTY" :
                    currentStockPrice.setText(new Utility().getRoundoffData(stockObject.get("NSE").getAsJsonObject().
                            get("lastvalue").getAsString().replace(",",""))+"");
                    //investAmt.setText(currentStockPrice.getText().toString().replace(",",""));
                    break;

                case "COMMODITY" :
                    //investAmt.setText("100");
                    currentStockPrice.setText(new Utility().getRoundoffData(stockObject.get("lastprice").getAsString()
                            .replace(",",""))+"");
                    break;

                case "CURRENCY" :
                    //investAmt.setEnabled(false);
                    currentStockPrice.setText(new Utility().getRoundoffData(stockObject.get("data").getAsJsonObject().
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
                    transactionCharges.setText("" + new Utility().getRoundoffData(adminSettings.
                            get("trans_amt_nifty").getAsString().replace(",","")));
                    break;

                case "COMMODITY" :
                    //investAmt.setText("100");
                    transactionCharges.setText("" + new Utility().getRoundoffData(adminSettings.
                            get("trans_amt_commodity").getAsString().replace(",","")));
                    break;

                case "CURRENCY" :
                    //investAmt.setEnabled(false);
                    transactionCharges.setText("" + new Utility().getRoundoffData(adminSettings.
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
            Toast.makeText(PurchaseActivity.this, "Unexpected error occured. " +
                    "Please check your internet connection.", Toast.LENGTH_SHORT).show();
            setPurchaseData();
            setResult(RESULT_OK);
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

        new RetrofitClient().getInterface().getUserTxnData(FirebaseAuth.getInstance().
                getCurrentUser().getPhoneNumber().substring(3), type).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if(response.isSuccessful()) {

                    if (response.body().getAsJsonObject("data") != null) {

                        //Log.d("response", response.body() + "");

                        timestamp = response.body().get("timestamp").getAsLong();

                        adminSettings = response.body().getAsJsonObject("data").getAsJsonObject("admin_settings");
                        userObject = response.body().getAsJsonObject("data").getAsJsonObject("Account");

                        //TODO: set limit amounts
                        try {
                            JsonObject fd_ref = userObject;

                            if (type.equalsIgnoreCase("nifty")) {

                                fd_ref = userObject
                                        .getAsJsonObject("stocks_list")
                                        .getAsJsonObject("bought_items")
                                        .getAsJsonObject("index");

                            } else if (type.equalsIgnoreCase("commodity")) {

                                fd_ref = userObject
                                        .getAsJsonObject("stocks_list")
                                        .getAsJsonObject("bought_items")
                                        .getAsJsonObject("commodity");

                            } else if (type.equalsIgnoreCase("currency")) {

                                fd_ref = userObject
                                        .getAsJsonObject("stocks_list")
                                        .getAsJsonObject("bought_items")
                                        .getAsJsonObject("currency");
                            }

                            Set<Map.Entry<String, JsonElement>> entrySet = fd_ref.entrySet();

                            CURRENT_INVESTMENT = 0;

                            for (Map.Entry<String, JsonElement> entry : entrySet) {

                                if (entry.getKey().contains("txn")) {

                                    if (type.equalsIgnoreCase("commodity")) {

                                        switch (id) {

                                            case "GOLD":
                                                if (entry.getKey().contains("GOLD")) {
                                                    CURRENT_INVESTMENT +=
                                                            entry.getValue().getAsJsonObject().get("total_amount").getAsDouble();
                                                }
                                                break;

                                            case "SILVER":
                                                if (entry.getKey().contains("SILVER")) {
                                                    CURRENT_INVESTMENT +=
                                                            entry.getValue().getAsJsonObject().get("total_amount").getAsDouble();
                                                }
                                                break;

                                            case "CRUDEOIL":
                                                if (entry.getKey().contains("CRUDEOIL")) {
                                                    CURRENT_INVESTMENT +=
                                                            entry.getValue().getAsJsonObject().get("total_amount").getAsDouble();
                                                }
                                                break;

                                        }
                                    } else {
                                        CURRENT_INVESTMENT +=
                                                entry.getValue().getAsJsonObject().get("total_amount").getAsDouble();
                                    }
                                }
                            }

                            Log.d("TOTAL", "" + CURRENT_INVESTMENT);

                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            CURRENT_INVESTMENT = 0;
                            CURRENT_INVESTMENT = 0;
                        }

                        count++;
                        dismissDialog();

                    } else if(response.body().get("flag")!=null) {

                        progressDialog.dismiss();

                        new Utility().showDialog(response.body().get("flag").getAsString(),
                                response.body().get("message").getAsString(), PurchaseActivity.this, new DialogInterface() {
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
                        Toast.makeText(PurchaseActivity.this, "Internal server error", Toast.LENGTH_SHORT).show();
                        count++;
                        dismissDialog();
                    }

                }else {

                    count++;
                    dismissDialog();

                    try {
                        Log.d("Purchase error", response.errorBody().string() + "");
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

        progressDialog = new Utility().showDialog("Please wait for transaction to complete.", PurchaseActivity.this);
        progressDialog.setCancelable(false);

        new RetrofitClient().getInterface().performTransaction(object).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                //progressDialog.dismiss();

                if(response.isSuccessful()) {
                    Log.d("data", ""+response.body());
                    //TODO: go to summary
                    Intent intent = new Intent(PurchaseActivity.this, TransactionSummaryActivity.class);
                    intent.putExtra("success", true);
                    intent.putExtra("data", object.toString());
                    intent.putExtra("type", "buy");
                    intent.putExtra("txn_id", txn_id);
                    intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                    intent.putExtra("product_type", product_type);

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
                    Toast.makeText(PurchaseActivity.this, "Intenal server error", Toast.LENGTH_SHORT).show();
                    try {
                        Log.d("txn error", ""+response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(PurchaseActivity.this, TransactionSummaryActivity.class);
                    intent.putExtra("success", false);
                    intent.putExtra("data", object.toString());
                    intent.putExtra("type", "buy");
                    startActivity(intent);
                    intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                    //setResult(RESULT_OK);
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
                intent.putExtra("type", "buy");
                //setResult(RESULT_OK);
                intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
    }

    public void dismissDialog() {
        if(count == 2) {
            count = 0;
            progressDialog.dismiss();
            if(userObject!=null && stockObject!=null) {
                setPurchaseData();
            }else {
                Toast.makeText(PurchaseActivity.this, "Unexpected error occured. " +
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
