package com.edu.chatapp.activity;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.edu.chatapp.databinding.ActivityVerifyPhoneNumberBinding;
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

public class VerifyPhoneNumberActivity extends BaseActivity {
//    public static final String KEY_PHONE_NUMBER = "KEY_PHONE_NUMBER";
//    public static final String KEY_VERIFICATION_ID = "KEY_VERIFICATION_ID";
    private ActivityVerifyPhoneNumberBinding mBinding;
    private View mView;
    private AppDialogNotify appDialogNotify;
    private String strContent = "", strCancel = "Cancel";
    private FirebaseAuth mAuth;
    private String strPhoneNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityVerifyPhoneNumberBinding.inflate(getLayoutInflater());
        mView = mBinding.getRoot();
        setContentView(mView);

        initialView();
        initialData();
    }

    private void initialView() {
        mAuth = FirebaseAuth.getInstance();
        appDialogNotify = new AppDialogNotify(this);
        mBinding.btnSendPhoneNumber.setOnClickListener(v -> sendPhoneNumber());
    }

    private void initialData() {
    }

    private void sendPhoneNumber() {
        strPhoneNumber = mBinding.edtVerifyPhoneNumber.getText().toString().trim();
        if (strPhoneNumber.equals("")) {
            System.out.println("phone number empty: " + strPhoneNumber);
            showDialogEmpty();
            return;
        }
        if (strPhoneNumber.length() > 15){
            System.out.println("phone number sai dinh dang: " + strPhoneNumber);
            showDialogPhoneNumberWrong();
            return;
        }
        System.out.println("111111111111 ***************************** 1111111111111111111111111111");
        callApiGetOTP(strPhoneNumber);
    }

    private void callApiGetOTP(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(PhoneAuthCredential credential) {
                                signInWithPhoneAuthCredential(credential);
                            }

                            @Override
                            public void onVerificationFailed(FirebaseException e) {
                                showDialogOtpFailed();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId,
                                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                super.onCodeSent(verificationId, token);
                                goToOtp(phoneNumber, verificationId);
                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

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
                                System.out.println("otp ******************************************************* sai roi");
                                showDialogOtpFailed();
                            }
                        }
                    }
                });
    }

    /** intent */
    private void goToMain(String phoneNumber) {
        Intent intent = new Intent(VerifyPhoneNumberActivity.this, MainActivity.class);
        intent.putExtra(ConstantKey.KEY_PHONE_NUMBER, phoneNumber);
        startActivity(intent);
    }
    private void goToOtp(String phoneNumber, String verificationId) {
        Intent intent = new Intent(VerifyPhoneNumberActivity.this, OtpActivity.class);
        intent.putExtra(ConstantKey.KEY_PHONE_NUMBER, phoneNumber);
        intent.putExtra(ConstantKey.KEY_VERIFICATION_ID, verificationId);
        startActivity(intent);
    }

    /** dialog */
    private void showDialogEmpty() {
        String msgFail = "Vui long nhap sdt";
        String txtOption = "Close sdt 1";
        appDialogNotify.showDialogOneOption(msgFail, txtOption, "#ff0000", "#6ad79e");
    }
    private void showDialogPhoneNumberWrong() {
        String msgFail = "Vui long nhap dung sdt";
        String txtOption = "Close sdt 2";
        appDialogNotify.showDialogOneOption(msgFail, txtOption, "#ff0000", "#6ad79e");
    }
    private void showDialogOtpFailed() {
        String msgFail = "Khong the thuc hien chuc nang nay";
        String txtOption = "Close sdt 3";
        appDialogNotify.showDialogOneOption(msgFail, txtOption, "#ff0000", "#6ad79e");
    }
    private void showDialogTwo() {
        String msgFail = "Vui long nhap otp";
        String txtOption = "Close otp";
        appDialogNotify.showDialogTwoOption(msgFail, txtOption, "#00ffff", "#ff0000", "#6ad79e");
    }
}