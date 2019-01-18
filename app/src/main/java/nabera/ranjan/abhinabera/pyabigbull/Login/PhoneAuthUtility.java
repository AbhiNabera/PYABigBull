package nabera.ranjan.abhinabera.pyabigbull.Login;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneAuthUtility {

    Activity context;
    PhoneAuthCallback phoneAuthCallback;
    String mVerificationId = "";

    static  boolean FLAG = false;

    private FirebaseAuth mAuth;

    public PhoneAuthUtility(Activity context, PhoneAuthCallback phoneAuthCallback) {
        this.context = context;
        this.phoneAuthCallback = phoneAuthCallback;
        mAuth = FirebaseAuth.getInstance();
    }

    public void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {

                phoneAuthCallback.onCodeDetected(code, phoneAuthCredential);
                //verifying the code
                //verifyVerificationCode(code);
            }else {

                phoneAuthCallback.onNullCodeDetected(phoneAuthCredential);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                // ...
                phoneAuthCallback.onInvalidCedentials();

            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                // ...
                phoneAuthCallback.onTooManyRequest();

            } else {

                phoneAuthCallback.onAuthFailed();

            }

            e.printStackTrace();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {

            mVerificationId = s;
            //mResendToken = forceResendingToken;
            phoneAuthCallback.onCodeSent(s, forceResendingToken);

            super.onCodeSent(s, forceResendingToken);//TODO: change
        }
    };

    public void verifyVerificationCode(String otp) {
        try {
            //creating the credential
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
            //signing the user
            signInWithPhoneAuthCredential(credential);
        }catch (Exception e){
            e.printStackTrace();
            phoneAuthCallback.onAuthFailed();
        }
    }

    public void verifyVerificationCode(PhoneAuthCredential phoneAuthCredential) {
        try {
            //signing the user
            signInWithPhoneAuthCredential(phoneAuthCredential);
        }catch (Exception e){
            e.printStackTrace();
            phoneAuthCallback.onAuthFailed();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity

                            phoneAuthCallback.onVerificationSuccessful();

                        } else {

                            //verification unsuccessful.. display an error message

                            String message = "Something is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            phoneAuthCallback.onVerificationError();
                        }
                    }
                });
    }
}
