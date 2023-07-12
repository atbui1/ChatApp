package com.edu.chatapp.activity;

import androidx.annotation.NonNull;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.edu.chatapp.databinding.ActivityRegisterBinding;
import com.edu.chatapp.utils.AppDialogNotify;
import com.edu.chatapp.utils.ConstantKey;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends BaseActivity {

    private ActivityRegisterBinding mBinding;
    private View mView;
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

//    public static final String TBL_USERS = "users";

    private AppDialogNotify mAppDialogNotify;
    private ProgressDialog mProgressDialog;
    private String strUsername, strEmail, strPassword, strConfirmPass;
    private String strMsg, strOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_register);
        mBinding = ActivityRegisterBinding.inflate(getLayoutInflater());
        mView = mBinding.getRoot();
        setContentView(mView);
        
        initialView();
        initialData();
    }

    private void initialView() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mAppDialogNotify = new AppDialogNotify(this);
        mProgressDialog = new ProgressDialog(this);
    }

    private void initialData() {
        mBinding.btnRegister.setOnClickListener(v -> registerAccount());
    }

    private void registerAccount() {
        strUsername = mBinding.edtUsername.getText().toString().trim();
        strEmail = mBinding.edtEmail.getText().toString().trim();
        strPassword = mBinding.edtPassword.getText().toString().trim();
        strConfirmPass = mBinding.edtConfirmPassword.getText().toString().trim();

        if (!strPassword.equals(strConfirmPass)) {
            strMsg = "vui long xac nhan mat khau";
            strOption = "cancel 1";
            showDialog(strMsg, strOption);
            return;
        } else if (strUsername.equals("") || strEmail.equals("") || strPassword.equals("") || strConfirmPass.equals("")) {
            strMsg = "vui long dien day du thong tin";
            strOption = "cancel 2";
            showDialog(strMsg, strOption);
            return;
        } else if (strUsername.length() < 2) {
            strMsg = "username phai lon hon 2";
            strOption = "cancel 3";
            showDialog(strMsg, strOption);
            return;
        }
        mProgressDialog.show();

        mAuth.createUserWithEmailAndPassword(strEmail, strPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e("ahihihi", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user.getUid();

                            mReference = mDatabase.getReference(ConstantKey.TBL_USERS).child(userId);
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userId);
                            hashMap.put("username", strUsername);
                            hashMap.put("email", strEmail);
                            hashMap.put("password", strPassword);
                            hashMap.put("avatarUrl", "https://haycafe.vn/wp-content/uploads/2022/03/Anh-co-trang-trung-quoc-tuyet-roi.jpg");

                            mReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finishAffinity();
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity.this, "register user firebase failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


    /** dialog */
    private void showDialog(String txtErr, String txtOption) {
        mAppDialogNotify.showDialogOneOption(txtErr, txtOption, "#ff0000", "#6ad79e");
    }
}