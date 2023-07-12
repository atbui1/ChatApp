package com.edu.chatapp.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.edu.chatapp.R;
import com.edu.chatapp.databinding.ActivityLoginBinding;
import com.edu.chatapp.databinding.ActivityLoginNewBinding;
import com.edu.chatapp.model.User;
import com.edu.chatapp.presenter.LoginPresenter;
import com.edu.chatapp.utils.AppDialogNotify;
import com.edu.chatapp.utils.AppSharedPreferences;
import com.edu.chatapp.utils.LanguageManager;
import com.edu.chatapp.view.ILoginView;
import com.google.firebase.auth.FirebaseAuth;

public class LoginNewActivity extends BaseActivity implements View.OnClickListener, ILoginView {

    private ActivityLoginNewBinding mBinding;
    private FirebaseAuth mAuth;
    private String strMsg, strOption;
    private ProgressDialog mProgressDialog;
    private AppDialogNotify mAppDialogNotify;
    private LanguageManager mLanguageManager;
    private AppSharedPreferences mAppSharedPreferences;

    private LoginPresenter mLoginPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityLoginNewBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initialView();
        initialData();
    }

    private void initialView() {
        mAuth = FirebaseAuth.getInstance();
        mProgressDialog = new ProgressDialog(this);
        mAppDialogNotify = new AppDialogNotify(this);

        mLanguageManager = new LanguageManager(this);
        mAppSharedPreferences = new AppSharedPreferences(this);

        mBinding.btnLogin.setOnClickListener(this);
        mBinding.txtRegister.setOnClickListener(this);
        mBinding.txtVerifyPhoneNumber.setOnClickListener(this);
        mBinding.txtForgetPassword.setOnClickListener(this);
        mBinding.imgLanguageVi.setOnClickListener(this);
        mBinding.imgLanguageEn.setOnClickListener(this);

        checkDefaultLanguage();

        mLoginPresenter = new LoginPresenter(this, this, getApplication());
    }

    private void initialData() {
    }


    /** function listener*/
    private void login() {
        String email = mBinding.edtUsername.getText().toString().trim();
        String password = mBinding.edtPassword.getText().toString().trim();
        if (email.length() < 3) {
            strMsg = " email phai lon hon 3";
            strOption = "close";
            showDialogValid(strMsg, strOption);
            return;
        } else if (password.length() < 3) {
            strMsg = " password phai lon hon 3";
            strOption = "cancel";
            showDialogValid(strMsg, strOption);
            return;
        }
//        mProgressDialog.show();
//        Toast.makeText(this, "username: " + email + "\npassword: " + password, Toast.LENGTH_SHORT).show();
//        mAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, task -> {
//                    mProgressDialog.dismiss();
//                    if (task.isSuccessful()) {
//                        // Sign in success, update UI with the signed-in user's information
//                        Intent intent = new Intent(LoginNewActivity.this, MainActivity.class);
////                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
//                    } else {
//                        // If sign in fails, display a message to the user.
//                        strMsg = "vui long dang nhap";
//                        strOption = "close";
//                        showDialogValid(strMsg, strOption);
//                    }
//                });

//        mLoginPresenter.loginFB(email, password);
    }
    private void register() {
        Intent intent = new Intent(LoginNewActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void verifyPhoneNumber() {
        Intent intent = new Intent(LoginNewActivity.this, VerifyPhoneNumberActivity.class);
        startActivity(intent);
    }

    private void forgetPassword() {
        Toast.makeText(this, "forget password", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginNewActivity.this, ForgetPasswordActivity.class);
        startActivity(intent);
    }

    /** dialog */
    private void showDialogValid(String txtMsg, String txtOption) {
        mAppDialogNotify.showDialogOneOption(txtMsg, txtOption, "#ff0000", "#6ad79e");
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.txt_register:
                register();
                break;
            case R.id.txt_verify_phone_number:
                verifyPhoneNumber();
                break;
            case R.id.txt_forget_password:
                forgetPassword();
                break;
            case R.id.img_language_en:
                changeLanguageToEN();
                break;
            case R.id.img_language_vi:
                changeLanguageToVI();
                break;
        }
    }

    private void changeLanguageToEN() {
        Toast.makeText(this, "English", Toast.LENGTH_SHORT).show();
        mLanguageManager.updateResource("en");
        recreate();
    }
    private void changeLanguageToVI() {
        Toast.makeText(this, "Vietnamese", Toast.LENGTH_SHORT).show();
        mLanguageManager.updateResource("vi");
        recreate();
    }

    private void checkDefaultLanguage() {
        String codeQAZ = mAppSharedPreferences.getStringValue("KEY_LANGUAGE");
        System.out.println("AAAAAAAAAAAAAAAAAAAAAA abc: " + codeQAZ);

        Toast.makeText(this, "ahihi: " + codeQAZ, Toast.LENGTH_SHORT).show();

        if (codeQAZ != "" && codeQAZ != null) {
            if (codeQAZ.equals("en")) {
                Toast.makeText(this, "11111111: " + codeQAZ, Toast.LENGTH_SHORT).show();
                mBinding.imgLanguageEn.setVisibility(View.GONE);
                mBinding.imgLanguageVi.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "2222222: " + codeQAZ, Toast.LENGTH_SHORT).show();
                mBinding.imgLanguageEn.setVisibility(View.VISIBLE);
                mBinding.imgLanguageVi.setVisibility(View.GONE);
            }

            mLanguageManager.updateResource(codeQAZ);
        }
    }

    @Override
    public void loginViewSuccess(User user) {
        Toast.makeText(this, "login fb success", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "login fb success user: " + user, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loginViewFailed(String msg) {
        Toast.makeText(this, "login fb failed", Toast.LENGTH_SHORT).show();

    }
}