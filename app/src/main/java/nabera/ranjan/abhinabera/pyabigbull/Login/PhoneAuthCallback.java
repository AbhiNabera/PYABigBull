package nabera.ranjan.abhinabera.pyabigbull.Login;

import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public interface PhoneAuthCallback {

    public void onCodeDetected(String code, PhoneAuthCredential phoneAuthCredential);

    public void onNullCodeDetected(PhoneAuthCredential phoneAuthCredential);

    public void onAuthFailed();

    public void onInvalidCedentials();

    public void onTooManyRequest();

    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken);

    public void onVerificationSuccessful();

    public void onVerificationError();
}
