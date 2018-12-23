package com.example.abhinabera.pyabigbull.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.abhinabera.pyabigbull.Api.RetrofitClient;
import com.example.abhinabera.pyabigbull.Dialog.ProgressDialog;
import com.example.abhinabera.pyabigbull.MainActivity;
import com.example.abhinabera.pyabigbull.R;
import com.example.abhinabera.pyabigbull.Utility;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserNameActivity extends AppCompatActivity {

    EditText userName;
    Button register;

    private String phoneNumber;

    ProgressDialog progressDialog;

    private String prevUsername = "";

    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_username);
        getSupportActionBar().hide();

        userName = (EditText) findViewById(R.id.userName);
        register = (Button) findViewById(R.id.register);

        phoneNumber = getIntent().getStringExtra("phoneNumber");

        sharedPreferences = getSharedPreferences(Utility.MyPREF, MODE_PRIVATE);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Utility.isOnline(UserNameActivity.this)) {
                    if (check()) {

                        if(!prevUsername.equals(userName.getText().toString().trim())) {

                            addPlayer();

                        }else {

                            Intent intent = new Intent(UserNameActivity.this, UserNameActivity.class);
                            intent.putExtra("phoneNumber", phoneNumber);
                            intent.putExtra("userName", userName.getText().toString().trim());
                            startActivity(new Intent(UserNameActivity.this, MainActivity.class));
                        }
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
        //Intent i = new Intent(UserNameActivity.this, LoginActivity.class);
        //startActivity(i);
        //finish();
        //overridePendingTransition(R.anim.enter1, R.anim.exit1);
    }

    public boolean check() {

        if(userName.getText().toString().length()<8) {
            userName.setError("Minimum length of 8 characters required");
            return false;
        }
        return true;
    }

    public void getUserName() {

        progressDialog = new Utility().showDialog("Please wait while we are getting your info", UserNameActivity.this);
        progressDialog.setCancelable(false);

        new RetrofitClient().getInterface().getUserName(phoneNumber).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                progressDialog.dismiss();

                if(response.isSuccessful()) {
                    if(response.body().get("userName")!=null) {
                        prevUsername = response.body().get("userName").getAsString().trim();
                        userName.setText(prevUsername);
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

    public void addPlayer() {

        progressDialog = new Utility().showDialog("Please wait for updation to complete.", UserNameActivity.this);
        progressDialog.setCancelable(false);

        new RetrofitClient().getInterface().addPlayer(phoneNumber, userName.getText().toString().trim()).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if(response.isSuccessful()) {

                    switch (response.body().get("flag").getAsString().trim()) {

                        case "USER_ADDED" :
                            Intent intent = new Intent(UserNameActivity.this, MainActivity.class);
                            intent.putExtra("phoneNumber", phoneNumber);
                            intent.putExtra("userName", userName.getText().toString().trim());

                            pushDataInSP(phoneNumber, userName.getText().toString().trim());

                            startActivity(intent);
                            break;

                        case "USERNAME_EXIST" :
                            userName.setError("Username already exist");
                            //Toast.makeText(UserNameActivity.this, "Username already exist", Toast.LENGTH_SHORT).show();
                            break;
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

