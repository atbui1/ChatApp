package com.edu.chatapp.activity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.edu.chatapp.databinding.ActivityForgetPasswordBinding;
import com.edu.chatapp.utils.AppDialogNotify;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends BaseActivity {

    private ActivityForgetPasswordBinding mBinding;
    private AppDialogNotify mAppDialogNotify;
    private FirebaseAuth mAuth;

    public static final String TAG = "ForgetPasswordActivity - tuanba";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityForgetPasswordBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initialView();
        initialData();
    }

    private void initialView() {
        mAuth = FirebaseAuth.getInstance();
        mAppDialogNotify = new AppDialogNotify(this);
    }

    private void initialData() {
        mBinding.btnSubmit.setOnClickListener(v -> sendEmailOrPhone());
    }

    private void sendEmailOrPhone() {
        String strEmailOrPhone = mBinding.edtEmailOrPhone.getText().toString().trim();
        if (strEmailOrPhone.equals("")) {
            Log.i(TAG, "please input email or password");
            String strMsg = "please input email or password";
            String strOption = "close";
            showDialogValid(strMsg, strOption);
            return;
        }

        mAuth.sendPasswordResetEmail(strEmailOrPhone)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finishAffinity();
                    }
                });

    }

    /** show dialog*/
    private void showDialogValid(String txtMsg, String txtOption) {
        mAppDialogNotify.showDialogOneOption(txtMsg, txtOption, "#ff0000", "#6ad79e");
    }
}