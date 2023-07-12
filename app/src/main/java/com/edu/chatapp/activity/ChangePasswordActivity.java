package com.edu.chatapp.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.edu.chatapp.databinding.ActivityChangePasswordBinding;
import com.edu.chatapp.utils.AppDialogNotify;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends BaseActivity {

    private ActivityChangePasswordBinding mBinding;
    private FirebaseAuth mAuth;
    private AppDialogNotify mAppDialogNotify;

    public static final String TAG = "ChangePasswordActivity - tuanba";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initialView();
        initialData();
    }

    private void initialView() {
        mAuth = FirebaseAuth.getInstance();
        mAppDialogNotify = new AppDialogNotify(this);
    }

    private void initialData() {
        mBinding.btnSubmit.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String strPasswordNew = mBinding.edtPasswordNew.getText().toString().trim();
        String strConfirmPassword = mBinding.edtConfirmPassword.getText().toString().trim();

        if (!strPasswordNew.equals(strConfirmPassword)) {
            Log.i(TAG, "password wrong...!!!");
            String strMsg = "password wrong...!!!";
            String strOption = "close";
            showDialogValid(strMsg, strOption);
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            return;
        }
        user.updatePassword(strPasswordNew)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ChangePasswordActivity.this, "change password successfully", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void reAuthenticate() {
        FirebaseUser user = mAuth.getCurrentUser();
        AuthCredential credential = EmailAuthProvider
                .getCredential("user@example.com", "password1234");

        user.reauthenticate(credential)
                .addOnCompleteListener(task ->
                        Toast.makeText(ChangePasswordActivity.this, "vui long dang nhap lai de thuc hien chuc nang nay", Toast.LENGTH_SHORT).show());
    }

    /** show dialog*/
    private void showDialogValid(String txtMsg, String txtOption) {
        mAppDialogNotify.showDialogOneOption(txtMsg, txtOption, "#ff0000", "#6ad79e");
    }
}