package com.edu.chatapp.activity;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.edu.chatapp.R;
import com.edu.chatapp.databinding.ActivityOtpBinding;
import com.edu.chatapp.utils.AppDialogNotify;
import com.edu.chatapp.utils.ConstantKey;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OtpActivity extends BaseActivity {
    private ActivityOtpBinding mBinding;
    private AppDialogNotify appDialogNotify;
    private FirebaseAuth mAuth;
    private String strPhoneNumber = "", strVerificationId = "", strOtp = "";
    private PhoneAuthProvider.ForceResendingToken mForceResendingToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityOtpBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initialView();
        initialData();
    }

    private void initialView() {
        getDataBundle();
        mAuth = FirebaseAuth.getInstance();
        mBinding.txtTitle.setText(R.string.str_send_otp);
        appDialogNotify = new AppDialogNotify(this);
        mBinding.btnSendOtp.setOnClickListener(v -> checkValueOtp());
        mBinding.txtGetOtpAgain.setOnClickListener(v -> getOtpAgain());
    }

    private void initialData() {
    }

    private void getDataBundle() {
        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();
        strPhoneNumber = bundle.getString(ConstantKey.KEY_PHONE_NUMBER);
        strVerificationId = bundle.getString(ConstantKey.KEY_VERIFICATION_ID);
    }

    private void checkValueOtp() {
        strOtp = mBinding.edtOtp.getText().toString().trim();
        if (strOtp.equals("")) {
            showDialogEmpty();
        } else {
            Toast.makeText(this, "otp code: " + strOtp, Toast.LENGTH_SHORT).show();
            sendOtpFirebase(strOtp);
        }
    }

    private void sendOtpFirebase(String strOtp) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(strVerificationId, strOtp);
        signInWithPhoneAuthCredential(credential);

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            // Update UI
                            goToMain(user.getPhoneNumber());
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                showDialogOtpFailed();
                            }
                        }
                    }
                });
    }

    private void getOtpAgain() {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(strPhoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setForceResendingToken(mForceResendingToken)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(PhoneAuthCredential credential) {
                                signInWithPhoneAuthCredential(credential);
                            }

                            @Override
                            public void onVerificationFailed(FirebaseException e) {

                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId,
                                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                super.onCodeSent(verificationId, token);
                                strVerificationId = verificationId;
                                mForceResendingToken = token;
                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    /** intent */
    private void goToMain(String phoneNumber) {
        Intent intent = new Intent(OtpActivity.this, MainActivity.class);
        intent.putExtra(ConstantKey.KEY_PHONE_NUMBER, phoneNumber);
        startActivity(intent);
    }

    /** show dialog */
    private void showDialogEmpty() {
        String msgFail = "Vui long nhap otp";
        String txtOption = "Close otp";
        appDialogNotify.showDialogOneOption(msgFail, txtOption, "#ff0000", "#6ad79e");
    }
    private void showDialogOtpFailed() {
        String msgFail = "ma otp sai";
        String txtOption = "Close otp";
        appDialogNotify.showDialogOneOption(msgFail, txtOption, "#ff0000", "#6ad79e");
    }
}