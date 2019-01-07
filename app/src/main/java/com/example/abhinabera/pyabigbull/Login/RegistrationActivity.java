package com.example.abhinabera.pyabigbull.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.abhinabera.pyabigbull.R;
import com.example.abhinabera.pyabigbull.Api.Utility;

public class RegistrationActivity extends AppCompatActivity {

    EditText mobileNumber;
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registration);
        getSupportActionBar().hide();

        mobileNumber = (EditText) findViewById(R.id.mobileNumber);
        register = (Button) findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Utility.isOnline(RegistrationActivity.this)) {
                    if (check()) {
                        Intent i = new Intent(RegistrationActivity.this, OTPActivity.class);
                        i.putExtra("phoneNumber", mobileNumber.getText().toString().trim());
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        pushDataInSP(mobileNumber.getText().toString().trim());
                        startActivity(i);
                        //finish();
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                } else {

                    new Utility().showDialog("NO INTERNET",
                            "Please check your internet connection.", RegistrationActivity.this);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
        //startActivity(i);
        //finish();
        //overridePendingTransition(R.anim.enter1, R.anim.exit1);
    }

    public boolean check() {

        if(mobileNumber.getText().toString().length() != 10) {
            mobileNumber.setError("Enter a valid mobile number");
            return false;
        }
        return true;
    }

    public void pushDataInSP(String phoneNumber) {
        SharedPreferences sharedPreferences = getSharedPreferences(Utility.MyPREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phoneNumber", phoneNumber);
        editor.apply();
        editor.commit();
    }
}
