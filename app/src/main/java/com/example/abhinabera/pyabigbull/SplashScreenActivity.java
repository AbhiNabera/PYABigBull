package com.example.abhinabera.pyabigbull;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.abhinabera.pyabigbull.Api.RetrofitClient;
import com.example.abhinabera.pyabigbull.Api.Utility;
import com.example.abhinabera.pyabigbull.Dashboard.MainActivity;
import com.example.abhinabera.pyabigbull.Login.UserNameActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreenActivity extends AppCompatActivity {

    ImageView splashImage;

    ProgressBar progressBar;

    private static final int PERMISSION_REQUEST_CODE = 200;

    int PERMISSION_ALL = 1;

    String[] PERMISSIONS = {
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_SMS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();

        splashImage = (ImageView) findViewById(R.id.splashImage);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        else{
            nextActivity();
            //getCommodityExpiry();
        }

    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSION_ALL) {
            if (grantResults.length > 0) {

                boolean network = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean wifi = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                boolean internet = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                boolean sms = grantResults[3] == PackageManager.PERMISSION_GRANTED;

                if(!network || !wifi || !internet || !sms) {

                    ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);

                }
                else {
                    nextActivity();
                    //getCommodityExpiry();
                }

            }
            else {

                //ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);

            }
        }
    }

    public void nextActivity(){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(FirebaseAuth.getInstance().getCurrentUser()!=null) {

                    if(getUserName()!=null) {

                        checkifUserEnabled();
                        //startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));

                    }else {

                        Intent intent = new Intent(SplashScreenActivity.this, UserNameActivity.class);
                        intent.putExtra("phoneNumber", getPhoneNumber());
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        finish();

                    }

                }else {

                    Intent menuIntent = new Intent(SplashScreenActivity.this, IntroActivity.class);
                    startActivity(menuIntent);
                    finish();
                    overridePendingTransition(R.anim.enter, R.anim.exit);

                }
            }
        },00);
    }

    public String getUserName() {
        SharedPreferences sharedPreferences = getSharedPreferences(Utility.MyPREF, MODE_PRIVATE);
        return sharedPreferences.getString("userName", null);
    }

    public String getPhoneNumber() {
        SharedPreferences sharedPreferences = getSharedPreferences(Utility.MyPREF, MODE_PRIVATE);
        return sharedPreferences.getString("phoneNumber", "");
    }

    @Deprecated
    public void getCommodityExpiry() {

        progressBar.setVisibility(View.VISIBLE);

        new RetrofitClient().getInterface().getCommodityExpiry().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                progressBar.setVisibility(View.GONE);
                if(response.isSuccessful()) {
                    pushDataInSP(response.body().toString());
                    nextActivity();
                }else{
                    Toast.makeText(SplashScreenActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                t.printStackTrace();
                Toast.makeText(SplashScreenActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void pushDataInSP(String data) {
        SharedPreferences sharedPreferences = getSharedPreferences(Utility.MyPREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("expiry", data);
        editor.apply();
        editor.commit();
    }

    public void checkifUserEnabled() {

        progressBar.setVisibility(View.VISIBLE);

        String phoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        Log.d("phoneNumber", phoneNumber);

        new RetrofitClient().getInterface().checkifActive(phoneNumber.substring(3)).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {

                    if (response.body().get("isActive").toString().equalsIgnoreCase("null")) {
                        new Utility().showDialog("ACCOUNT 404",
                                "Your account does not exist." +
                                        "Please contact your admin.", SplashScreenActivity.this);
                    } else {
                        if (response.body().get("isActive").getAsBoolean()) {
                            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                            finish();
                        } else {
                            new Utility().showDialog("ACCOUNT DISABLED",
                                    "Your account has been disabled and you can't login until it is enabled again. " +
                                            "Please contact your admin.", SplashScreenActivity.this);
                        }
                    }

                } else {
                    Toast.makeText(SplashScreenActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                t.printStackTrace();
                Toast.makeText(SplashScreenActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
