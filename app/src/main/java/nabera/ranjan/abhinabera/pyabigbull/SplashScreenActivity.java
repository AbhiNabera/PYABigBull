package nabera.ranjan.abhinabera.pyabigbull;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
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

import nabera.ranjan.abhinabera.pyabigbull.Api.RetrofitClient;
import nabera.ranjan.abhinabera.pyabigbull.Api.Utility;
import nabera.ranjan.abhinabera.pyabigbull.Dashboard.MainActivity;
import nabera.ranjan.abhinabera.pyabigbull.Dialog.DialogInterface;
import nabera.ranjan.abhinabera.pyabigbull.Login.RegistrationActivity;
import nabera.ranjan.abhinabera.pyabigbull.Login.UserNameActivity;
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
            Manifest.permission.READ_SMS,
            Manifest.permission.WRITE_SETTINGS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
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

        progressBar.setVisibility(View.GONE);

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
                //boolean writeSettings = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                boolean writeExt = grantResults[5] == PackageManager.PERMISSION_GRANTED;
                boolean readExt = grantResults[6] == PackageManager.PERMISSION_GRANTED;
                boolean cam = grantResults[7] == PackageManager.PERMISSION_GRANTED;

                if(!network || !wifi || !internet || !sms || !writeExt || !readExt || !cam) {

                    ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);

                    Toast.makeText(SplashScreenActivity.this, "Please grant all the permissions.", Toast.LENGTH_SHORT).show();
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

        Log.d("next", "Activity");

        if(Utility.isOnline(SplashScreenActivity.this)) {

            //progressBar.setVisibility(View.VISIBLE);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    setAutoTime();

                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {

                        if (getUserName() != null) {

                            checkifUserEnabled();
                            //startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));

                        } else {

                            Intent intent = new Intent(SplashScreenActivity.this, UserNameActivity.class);
                            intent.putExtra("phoneNumber", getPhoneNumber());
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                            finish();

                        }

                    } else {

                        Intent menuIntent = new Intent(SplashScreenActivity.this, IntroActivity.class);
                        startActivity(menuIntent);
                        finish();
                        overridePendingTransition(R.anim.enter, R.anim.exit);

                    }
                }
            }, 00);
        }else {
            new Utility().showDialog("NO INTERNET",
                    "Check your internet connection." +
                            "Switch your internet connection and open the app again.", SplashScreenActivity.this,
                    new DialogInterface() {
                        @Override
                        public void onSuccess() {
                            finish();
                        }

                        @Override
                        public void onCancel() {
                            finish();
                        }
                    });
        }
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

                    if(response.body().get("isActive") != null) {
                        if (response.body().get("isActive").toString().equalsIgnoreCase("null")) {
                            new Utility().showDialog("ACCOUNT 404",
                                    "Your account does not exist." +
                                            "Continue to login again.", SplashScreenActivity.this,
                                    new DialogInterface() {
                                        @Override
                                        public void onSuccess() {
                                            startActivity(new Intent(SplashScreenActivity.this, RegistrationActivity.class));
                                            finish();
                                        }

                                        @Override
                                        public void onCancel() {
                                            startActivity(new Intent(SplashScreenActivity.this, RegistrationActivity.class));
                                            finish();
                                        }
                                    });

                            FirebaseAuth.getInstance().signOut();

                        } else {
                            if (response.body().get("isActive").getAsBoolean()) {
                                int versionCode = getVersioncode();

                                Log.d("versionCode", ""+versionCode);

                                if(versionCode == 0) {
                                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                                    finish();

                                }else if(response.body().get("versionCode").getAsInt() != versionCode) {

                                    new Utility().showDialog("UPDATE",
                                            "A new version of app is available on play store. " +
                                                    "Do you want to update now?", SplashScreenActivity.this,
                                            new DialogInterface() {
                                                @Override
                                                public void onSuccess() {

                                                    final String appPackageName = getPackageName();
                                                    try {
                                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                                    } catch (android.content.ActivityNotFoundException anfe) {
                                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                    }
                                                    finish();
                                                }

                                                @Override
                                                public void onCancel() {
                                                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                                                    finish();
                                                }
                                            });

                                }else {
                                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                                    finish();
                                }
                                
                            } else {
                                new Utility().showDialog("ACCOUNT DISABLED",
                                        "Your account has been disabled and you can't login until it is enabled again. " +
                                                "Please contact your admin.", SplashScreenActivity.this, new DialogInterface() {
                                            @Override
                                            public void onSuccess() {
                                                finish();
                                            }

                                            @Override
                                            public void onCancel() {
                                                finish();
                                            }
                                        });
                                //FirebaseAuth.getInstance().signOut();
                            }
                        }

                    }else {
                        Toast.makeText(SplashScreenActivity.this, "Internal server error", Toast.LENGTH_SHORT).show();
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
                finish();
            }
        });
    }

    public void setAutoTime() {
        
        String timeSettings = android.provider.Settings.System.getString(
                this.getContentResolver(),
                android.provider.Settings.System.AUTO_TIME);
        if (timeSettings.contentEquals("0")) {
            android.provider.Settings.System.putString(
                    this.getContentResolver(),
                    android.provider.Settings.System.AUTO_TIME, "1");
        }

        Log.d("Date", System.currentTimeMillis()+"");

    }

    public int getVersioncode() {

        int version = 0;

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
}
