package com.example.abhinabera.pyabigbull;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class SplashScreenActivity extends AppCompatActivity {

    ImageView splashImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();

        splashImage = (ImageView) findViewById(R.id.splashImage);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                splashImage.setImageResource(R.drawable.aspiretoinspirelogo);
            }
        },1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                splashImage.setImageResource(R.drawable.bigbullsplashscreen);
            }
        },4000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent menuIntent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(menuIntent);
                finish();
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        },6000);
    }
}
