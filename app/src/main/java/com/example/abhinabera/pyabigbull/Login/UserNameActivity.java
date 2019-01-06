package com.example.abhinabera.pyabigbull.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.abhinabera.pyabigbull.Api.RetrofitClient;
import com.example.abhinabera.pyabigbull.Dialog.ProgressDialog;
import com.example.abhinabera.pyabigbull.Dashboard.MainActivity;
import com.example.abhinabera.pyabigbull.R;
import com.example.abhinabera.pyabigbull.Api.Utility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserNameActivity extends AppCompatActivity {

    EditText dependentName;
    EditText userName;
    Button register;

    RadioGroup radioGroup;
    RadioButton member, spouse, child;

    private String phoneNumber;

    ProgressDialog progressDialog;

    private String prevUsername = "";

    private SharedPreferences sharedPreferences;

    private String type = "member";

    JsonObject playerData ;
    JsonObject data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_username);
        getSupportActionBar().hide();

        playerData = new JsonObject();
        data = new JsonObject();

        userName = (EditText) findViewById(R.id.userName);
        register = (Button) findViewById(R.id.register);
        dependentName = (EditText) findViewById(R.id.dependentName);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        member = (RadioButton) findViewById(R.id.member);
        spouse = (RadioButton) findViewById(R.id.spouse);
        child = (RadioButton) findViewById(R.id.child);

        phoneNumber = getIntent().getStringExtra("phoneNumber");

        sharedPreferences = getSharedPreferences(Utility.MyPREF, MODE_PRIVATE);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                //Log.d("position", ""+i);
                if(radioGroup.getCheckedRadioButtonId() == R.id.member) {
                    type = "member";
                    playerData.addProperty("type", "member");
                    dependentName.setVisibility(View.GONE);

                } else if(radioGroup.getCheckedRadioButtonId() == R.id.spouse) {
                    type = "spouse";
                    playerData.addProperty("type", "spouse");
                    dependentName.setVisibility(View.VISIBLE);
                    dependentName.setHint("Enter husband's name");
                    dependentName.setText("");

                } else if(radioGroup.getCheckedRadioButtonId() == R.id.child) {
                    type = "child";
                    playerData.addProperty("type", "child");
                    dependentName.setVisibility(View.VISIBLE);
                    dependentName.setHint("Enter father's name");
                    dependentName.setText("");

                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Utility.isOnline(UserNameActivity.this)) {
                    if (check()) {

                        playerData.addProperty("type", type);
                        playerData.addProperty("phoneNumber", phoneNumber);
                        playerData.addProperty("userName", userName.getText().toString().trim());

                        data.addProperty("type", type);
                        data.addProperty("userName", userName.getText().toString().trim());

                        if(!type.equalsIgnoreCase("member")) {
                            data.addProperty("dependentName", dependentName.getText().toString().trim());
                        } else {
                            data.remove("dependentName");
                        }
                        playerData.add("userData", data);
                        Log.d("player data", playerData+"");
                        addPlayer(playerData);
                    }
                } else {

                    new Utility().showDialog("NO INTERNET",
                            "Please check your internet connection.", UserNameActivity.this);
                }
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getUserName();
            }
        },100);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public boolean check() {

        if(userName.getText().toString().trim().length()<3) {
            userName.setError("Minimum length of 3 characters required");
            return false;
        }

        if(type.equalsIgnoreCase("spouse") || type.equalsIgnoreCase("child")) {
            if(dependentName.getText().toString().trim().isEmpty()) {
                dependentName.setError("This field can't be empty");
                return false;
            }
        }
        return true;
    }

    public void getUserName() {

        progressDialog = new Utility().showDialog("Please wait while we are getting your info", UserNameActivity.this);
        progressDialog.setCancelable(false);

        new RetrofitClient().getInterface().getUserNameData(phoneNumber).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                progressDialog.dismiss();

                Log.d("response", response.body()+"");

                if(response.isSuccessful()) {
                    if(response.body().get("userData")!=null) {
                        prevUsername = response.body().getAsJsonObject("userData").get("userName").getAsString().trim();
                        userName.setText(prevUsername);

                        if(response.body().getAsJsonObject("userData").get("type").getAsString().trim().equalsIgnoreCase("member")) {
                            member.setChecked(true);
                            dependentName.setVisibility(View.GONE);
                        }else {
                            if(response.body().getAsJsonObject("userData").get("type").getAsString().trim().equalsIgnoreCase("spouse")) {
                                spouse.setChecked(true);
                            }else if(response.body().getAsJsonObject("userData").get("type").getAsString().trim().equalsIgnoreCase("child")) {
                                child.setChecked(true);
                            }

                            dependentName.setVisibility(View.VISIBLE);
                            dependentName.setText(response.body().getAsJsonObject("userData").get("dependentName").getAsString()+"");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();
            }
        });
    }

    public void addPlayer(JsonObject data) {

        progressDialog = new Utility().showDialog("Please wait for updation to complete.", UserNameActivity.this);
        progressDialog.setCancelable(false);

        new RetrofitClient().getInterface().addPlayer(playerData).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if(response.isSuccessful()) {

                    Log.d("response", response.body()+"");

                    switch (response.body().get("flag").getAsString().trim()) {

                        case "USER_ADDED" :
                            Intent intent = new Intent(UserNameActivity.this, MainActivity.class);
                            intent.putExtra("phoneNumber", phoneNumber);
                            intent.putExtra("userName", userName.getText().toString().trim());

                            pushDataInSP(phoneNumber, userName.getText().toString().trim());

                            startActivity(intent);
                            finish();
                            break;

                        case "USERNAME_EXIST" :
                            userName.setError("Username already exists");
                            //Toast.makeText(UserNameActivity.this, "Username already exist", Toast.LENGTH_SHORT).show();
                            break;

                        case "USER_DISABLED" : new Utility().showDialog("ACCOUNT DISABLED",
                                "Your account has been disabled and you can't login until it is enabled again. " +
                                        "Please contact your admin.", UserNameActivity.this);
                        break;

                        case "INTERNAL SERVER ERROR" :
                            Toast.makeText(UserNameActivity.this, "internal server error", Toast.LENGTH_SHORT).show();
                            break;
                    }

                }else {
                    try {
                        Log.d("error", response.errorBody().string());
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

    public void pushDataInSP(String phoneNumber, String userName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phoneNumber", phoneNumber);
        editor.putString("userName", userName);
        editor.apply();
        editor.commit();
    }
}

