package nabera.ranjan.abhinabera.pyabigbull.Login;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import nabera.ranjan.abhinabera.pyabigbull.Dialog.ProgressDialog;
import nabera.ranjan.abhinabera.pyabigbull.R;
import nabera.ranjan.abhinabera.pyabigbull.Api.Utility;

import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class OTPActivity extends AppCompatActivity {

    EditText otp;
    Button verify;
    TextView timer;
    TextView retry;

    private String phoneNumber;

    private ProgressDialog progressDialog;

    PhoneAuthUtility phoneAuthUtility;

    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_otp);
        getSupportActionBar().hide();

        otp = (EditText) findViewById(R.id.otp);
        verify = (Button) findViewById(R.id.verify);
        timer = (TextView) findViewById(R.id.timer);
        retry = (TextView) findViewById(R.id.retry);

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.setText("Retrying");
                statTimer();
                performAuth();
            }
        });

        phoneNumber = getIntent().getStringExtra("phoneNumber");

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Utility.isOnline(OTPActivity.this)) {
                    if (verify.getText().toString().trim().equalsIgnoreCase("VERIFY")) {
                        if (check()) {
                            //progressDialog.dismiss();
                            progressDialog = new Utility().showDialog("Please wait while we are verifying OTP", OTPActivity.this);
                            progressDialog.setCancelable(false);
                            phoneAuthUtility.verifyVerificationCode(otp.getText().toString().trim());
                        }
                    } else {

                        //progressDialog = new Utility().showDialog("Please wait for the otp", OTPActivity.this);
                        //progressDialog.setCancelable(false);
                        timer.setText("Retrying");
                        statTimer();
                        performAuth();

                    }
                } else {
                    new Utility().showDialog("NO INTERNET",
                            "Please check your internet connection.", OTPActivity.this);
                }
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //progressDialog = new Utility().showDialog("Please wait for the otp", OTPActivity.this);
                //progressDialog.setCancelable(false);
                statTimer();
                performAuth();

            }
        }, 100);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Intent i = new Intent(OTPActivity.this, LoginActivity.class);
        //startActivity(i);
        //finish();
        //overridePendingTransition(R.anim.enter1, R.anim.exit1);
    }

    public boolean check() {

        if (otp.getText().toString().length() != 6) {
            otp.setError("Enter a valid OTP");
            return false;
        }
        return true;
    }

    public void performAuth() {

        phoneAuthUtility = new PhoneAuthUtility(OTPActivity.this, new PhoneAuthCallback() {
            @Override
            public void onCodeDetected(String code, PhoneAuthCredential phoneAuthCredential) {
                //otp detected automatically

                countDownTimer.cancel();
                timer.setText("Verfying...");

                otp.setText(code);
                verify.setText("VERIFY");
                verify.setEnabled(true);
                verify.setBackground(getDrawable(R.drawable.button_border));

                //progressDialog.dismiss();
                progressDialog = new Utility().showDialog("Please wait while we are verifying OTP", OTPActivity.this);
                progressDialog.setCancelable(false);

                phoneAuthUtility.verifyVerificationCode(code);

            }

            @Override
            public void onNullCodeDetected(PhoneAuthCredential phoneAuthCredential) {
                //ask user to manually enter otp
                //new Utility().showDialog("ENTER OTP",
                 //       "Please manually enter the OTP received on +91" + phoneNumber + ".", OTPActivity.this);
                verify.setText("VERIFY");
                verify.setEnabled(true);
                verify.setBackground(getDrawable(R.drawable.button_border));

                countDownTimer.cancel();
                timer.setText("Verfying...");

                progressDialog = new Utility().showDialog("Please wait while we are verifying OTP", OTPActivity.this);
                progressDialog.setCancelable(false);

                phoneAuthUtility.verifyVerificationCode(phoneAuthCredential);
            }

            @Override
            public void onAuthFailed() {
                Toast.makeText(OTPActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                countDownTimer.cancel();
                timer.setText("Retry...");
            }

            @Override
            public void onInvalidCedentials() {
                Toast.makeText(OTPActivity.this, "Invalid credential", Toast.LENGTH_SHORT).show();
                countDownTimer.cancel();
                timer.setText("Retry...");
            }

            @Override
            public void onTooManyRequest() {
                Toast.makeText(OTPActivity.this, "Too many request", Toast.LENGTH_SHORT).show();
                countDownTimer.cancel();
                timer.setText("Retry...");
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                Toast.makeText(OTPActivity.this, "Code sent", Toast.LENGTH_SHORT).show();
                Log.d("Code", "" + s);

                verify.setText("VERIFY");
                verify.setEnabled(true);
                verify.setBackground(getDrawable(R.drawable.button_border));

                countDownTimer.cancel();
                timer.setText("Enter OTP sent at " + phoneNumber);
            }

            @Override
            public void onVerificationSuccessful() {
                //TODO: go to next activity
                progressDialog.dismiss();
                Intent intent = new Intent(OTPActivity.this, UserNameActivity.class);
                intent.putExtra("phoneNumber", phoneNumber);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onVerificationError() {
                try {
                    progressDialog.dismiss();
                    Toast.makeText(OTPActivity.this, "Verification error", Toast.LENGTH_SHORT).show();

                }catch (IllegalStateException e) {
                    e.printStackTrace();
                }

                countDownTimer.cancel();
                timer.setText("Retry...");
            }
        });

        phoneAuthUtility.sendVerificationCode(phoneNumber);
    }

    public void statTimer() {

        countDownTimer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText("Waiting for OTP..." + millisUntilFinished / 1000 + "s");
                //verify.setEnabled(false);
                //verify.setBackground(getDrawable(R.drawable.button_border_disabled));
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                timer.setText("Retry...");
                //verify.setText("RETRY");
                //verify.setEnabled(true);
                //verify.setBackground(getDrawable(R.drawable.button_border));
            }

        }.start();
    }
}
