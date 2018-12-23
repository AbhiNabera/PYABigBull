package com.example.abhinabera.pyabigbull.Login;

import com.google.firebase.auth.PhoneAuthProvider;

public interface PhoneAuthCallback {

    public void onCodeDetected(String code);

    public void onNullCodeDetected();

    public void onAuthFailed();

    public void onInvalidCedentials();

    public void onTooManyRequest();

    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken);

    public void onVerificationSuccessful();

    public void onVerificationError();
}
