package com.example.abhinabera.pyabigbull.Transactions;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.abhinabera.pyabigbull.Api.RetrofitClient;
import com.example.abhinabera.pyabigbull.Api.Utility;
import com.example.abhinabera.pyabigbull.Dialog.ProgressDialog;
import com.example.abhinabera.pyabigbull.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FDPurchaseActivity extends AppCompatActivity {

    Toolbar fdpurchaseToolbar;
    Typeface custom_font;
    TextView availableBalance, totalInvestment, accBalance, timeout;
    EditText multiplier;
    Button confirm;

    private ProgressDialog progressDialog;
    JsonObject userObject;

    double fdamount = 0;
    int fdqty = 1;
    double shares_price;
    double avail_balance;
    double acc_balance = 0;
    double investment;
    double change;
    double percentchange;
    int stocks_count;
    long timestamp ;
    long lastupdate;
    long nextupdate;
    String txn_id;

    double FD_TOTAL = 0;
    double fd_total = 0;
    double TOTAL_AMOUNT = 0;

    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_fdpurchase);
        getSupportActionBar().hide();

        
        fdpurchaseToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.fdpurchaseToolbar);
        fdpurchaseToolbar.setTitle("FIXED DEPOSIT");

        availableBalance = (TextView) findViewById(R.id.availableBalance);
        totalInvestment = (TextView)  findViewById(R.id.totalInvestment);
        accBalance = (TextView) findViewById(R.id.accountBalance);


        multiplier = (EditText) findViewById(R.id.multiplier);
        confirm = (Button) findViewById(R.id.confirmButton);

        timeout = (TextView) findViewById(R.id.timeout);

        fdpurchaseToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        fdpurchaseToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        custom_font = ResourcesCompat.getFont(this, R.font.hammersmithone);

        changeToolbarFont(fdpurchaseToolbar, this);

        multiplier.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().trim().isEmpty()) {
                    fdqty = 0;
                    updateAmount();
                    setViews();
                }else {
                    fdqty = Integer.parseInt(multiplier.getText().toString().trim());
                    updateAmount();
                    setViews();
                }
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //setfddata();
                if(check()) {
                    txn_id = getTransId();
                    countDownTimer.cancel();
                    //setfddata();
                    executeTransaction(setfddata());
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
        super.onBackPressed();
        setResult(RESULT_OK);
        finish();
        overridePendingTransition(R.anim.enter1, R.anim.exit1);
    }

    public boolean check() {

        if(acc_balance < 0) {
            Toast.makeText(FDPurchaseActivity.this, "Invalid amount", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(fdqty==0) {
            Toast.makeText(FDPurchaseActivity.this, "Set quantity", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(FD_TOTAL > 0.5*Double.parseDouble(userObject.get("data").getAsJsonObject().get("start_balance").getAsString()
                .replace(",",""))) {

            new Utility().showDialog("INVALID AMOUNT",
                    "Total investment must be less than 50% of initial alloted amount(" +
                            userObject.get("data").getAsJsonObject().get("start_balance").getAsString()
                            + ").", FDPurchaseActivity.this);

            return false;
        }

       return true;
    }

    public String getTransId() {
        //%B for buy %S for sell
        timestamp = System.currentTimeMillis();
        return "txn" + "FD" + timestamp%100000000 + "B";
    }

    public void initializeAmount() {

        avail_balance = Double.parseDouble(userObject.get("data").getAsJsonObject().get("avail_balance").getAsString()
                .replace(",",""));
        shares_price = Double.parseDouble(userObject.get("data").getAsJsonObject().get("shares_price").getAsString()
                .replace(",",""));
        investment = Double.parseDouble(userObject.get("data").getAsJsonObject().get("investment").getAsString()
                .replace(",",""));
        change = Double.parseDouble(userObject.get("data").getAsJsonObject().get("change").getAsString()
                .replace(",",""));
        percentchange = Double.parseDouble(userObject.get("data").getAsJsonObject().get("percentchange").getAsString()
                .replace(",",""));
        stocks_count = Integer.parseInt(userObject.get("data").getAsJsonObject().get("stocks_count").getAsString()
                .replace(",",""));
    }

    public void updateAmount() {

        fdamount = Utility.BASE_AMT * fdqty; //check if base price is set by admin
        acc_balance = avail_balance - fdamount;
        investment = Double.parseDouble(userObject.get("data").getAsJsonObject().get("investment").getAsString()
                .replace(",","")) + fdamount;
        shares_price = Double.parseDouble(userObject.get("data").getAsJsonObject().get("shares_price").getAsString()
                .replace(",","")) + fdamount;
        stocks_count = Integer.parseInt(userObject.get("data").getAsJsonObject().get("stocks_count").getAsString()
                .replace(",","")) + fdqty;

        FD_TOTAL = fd_total + fdamount;
    }

    public void setViews() {
        if(userObject != null) {

            availableBalance.setText(new Utility().getRoundoffData(avail_balance+""));
            totalInvestment.setText(new Utility().getRoundoffData(fdamount+""));
            accBalance.setText(new Utility().getRoundoffData(acc_balance+""));

        }else {
            finish();
            Toast.makeText(FDPurchaseActivity.this, "Error occured", Toast.LENGTH_SHORT).show();
        }
    }

    public JsonObject setfddata() {

        nextupdate = getNextUpdae();

        JsonObject account_ref = userObject.get("data").getAsJsonObject();

        JsonObjectFormatter objectFormatter = new JsonObjectFormatter(account_ref);

        JsonObject data = new JsonObject();
        JsonObject transaction = new JsonObject();
        JsonObject txn_history = new JsonObject();

        account_ref.addProperty("shares_price", shares_price+"");
        account_ref.addProperty("avail_balance", acc_balance+"");
        account_ref.addProperty("investment", investment+"");
        account_ref.addProperty("stocks_count", stocks_count);

        transaction.addProperty("name", "Fixed deposit");
        transaction.addProperty("id", "FD");
        transaction.addProperty("txn_id", txn_id);
        transaction.addProperty("investment", fdamount+"");
        transaction.addProperty("total_amount", fdamount+"");
        transaction.addProperty("current_value", fdamount+"");

        transaction.addProperty("timestamp", timestamp+"");
        transaction.addProperty("lastupdate", lastupdate+"");
        transaction.addProperty("starttime", lastupdate+"");
        transaction.addProperty("qty", fdqty+"");
        transaction.addProperty("firstupdate", ""+nextupdate);
        transaction.addProperty("nextupdate", ""+nextupdate);

        txn_history.addProperty("id", "FD");
        txn_history.addProperty("name", "Fixed deposit");
        txn_history.addProperty("timestamp", timestamp);
        txn_history.addProperty("txn_id", txn_id);
        txn_history.addProperty("txn_type", "buy");
        txn_history.addProperty("type", "fixed_deposit");

        txn_history.add("txn_summary", transaction);

        objectFormatter.child("stocks_list")
                .child("bought_items").child("fixed_deposit").pushObject(txn_id, transaction);

        objectFormatter.child("txn_history").pushObject(txn_id, txn_history);

        data.addProperty("phoneNumber", FirebaseAuth.getInstance().getCurrentUser()
                .getPhoneNumber().substring(3));
        data.add("Account", account_ref);

        //Log.d("FDPurchase data", data+"");

        return data;

    }

    public long getNextUpdae() {

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(timestamp);
        calendar2.add(Calendar.DAY_OF_YEAR, 1);

        Date date = calendar2.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

        String next_time = dateFormat.format(date) + " 00:00:00";

        dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

        try {
            date = dateFormat.parse(next_time);
            lastupdate = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        Date today = calendar.getTime();

        calendar.add(Calendar.DAY_OF_YEAR, 2);
        Date tomorrow = calendar.getTime();

        dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

        next_time = dateFormat.format(tomorrow) + " 00:00:00";

        dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");

        try {
            tomorrow = dateFormat.parse(next_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.d("timestamp", tomorrow.getTime() + " : " + Calendar.DAY_OF_YEAR);

        return tomorrow.getTime();
    }

    public void getUserAccount() {

        progressDialog = new Utility().showDialog("Please wait while we are getting your account info.",
                FDPurchaseActivity.this);
        progressDialog.setCancelable(false);

        new RetrofitClient().getInterface().getUserAccount(FirebaseAuth.getInstance().
                getCurrentUser().getPhoneNumber().substring(3)).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if(response.isSuccessful()) {
                    Log.d("response", response.body()+"");
                    userObject = response.body();

                    try {
                        JsonObject fd_ref = userObject.getAsJsonObject("data")
                                //.getAsJsonObject("Account")
                                .getAsJsonObject("stocks_list")
                                .getAsJsonObject("bought_items")
                                .getAsJsonObject("fixed_deposit");

                        Set<Map.Entry<String, JsonElement>> entrySet = fd_ref.entrySet();

                        FD_TOTAL = 0;

                        for(Map.Entry<String, JsonElement> entry: entrySet) {

                            FD_TOTAL += entry.getValue().getAsJsonObject().get("investment").getAsDouble();
                            TOTAL_AMOUNT+= entry.getValue().getAsJsonObject().get("current_value").getAsDouble();
                        }

                        fd_total = FD_TOTAL;

                        Log.d("FD_TOTAL", ""+FD_TOTAL);

                    }catch (NullPointerException e) {
                        e.printStackTrace();
                        FD_TOTAL = 0;
                        fd_total = 0;
                        TOTAL_AMOUNT = 0;
                    }

                    initializeAmount();
                    updateAmount();
                    setViews();
                    statTimer();

                    //new FDAmtUpdateUtility().getUpdatedAmount(userObject);

                }else {
                    try {
                        Log.d("Fixed deposit error", response.errorBody().string()+"");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                progressDialog.dismiss();
            }
        });
    }

    public void statTimer() {

        countDownTimer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                timeout.setText("Remaining time..." + millisUntilFinished / 1000 + "s");
            }

            public void onFinish() {
                timeout.setText("Session expired");

                new Utility().showDialog("SESSION TIMEOUT",
                        "You took longer than we expected. Please try again", FDPurchaseActivity.this);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },2000);
            }

        }.start();
    }

    public void executeTransaction(JsonObject object) {

        progressDialog = new Utility().showDialog("Please wait for transaction to complete.", FDPurchaseActivity.this);
        progressDialog.setCancelable(false);

        new RetrofitClient().getInterface().performTransaction(object).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                //progressDialog.dismiss();

                if(response.isSuccessful()) {
                    Log.d("data", ""+response.body());
                    //TODO: go to summary
                    pushDataInSP();
                    Intent intent = new Intent(FDPurchaseActivity.this, TransactionFDSummaryActivity.class);
                    intent.putExtra("success", true);
                    intent.putExtra("data", object.toString());
                    intent.putExtra("type", "buy");
                    intent.putExtra("txn_id", txn_id);
                    intent.putExtra("product_type", "fixed_deposit");

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
                    Toast.makeText(FDPurchaseActivity.this, "Intenal server error", Toast.LENGTH_SHORT).show();
                    try {
                        Log.d("txn error", ""+response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(FDPurchaseActivity.this, TransactionSummaryActivity.class);
                    intent.putExtra("success", false);
                    intent.putExtra("data", object.toString());
                    intent.putExtra("type", "buy");
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(FDPurchaseActivity.this, "Network error occued", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(FDPurchaseActivity.this, TransactionSummaryActivity.class);
                intent.putExtra("success", false);
                intent.putExtra("data", object.toString());
                intent.putExtra("type", "buy");
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
        editor.putString("total_investment", (TOTAL_AMOUNT+fdamount) + "");
        editor.apply();
        editor.commit();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
}
