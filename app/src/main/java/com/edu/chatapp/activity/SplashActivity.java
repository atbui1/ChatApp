package com.edu.chatapp.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.edu.chatapp.R;
import com.edu.chatapp.api.ApiClient;
import com.edu.chatapp.databinding.ActivitySplashBinding;
import com.edu.chatapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {

    private ActivitySplashBinding mBinding;
    private FirebaseAuth mAuth;

    /** test */
    private ApiClient mApiClient;
    private List<User> mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash);
        mBinding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        
        initialView();
        initialData();
        
    }

    private void initialView() {
        mAuth = FirebaseAuth.getInstance();
        startAnimation();
        checkPermissionApp();
//        checkNextActivity();

        /** test */
        System.out.println("333333333333333333333333333333333333");
//        callApiSendNotify();

    }

    private void initialData() {
    }

    private void checkNextActivity() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            gotoLogin();
        } else {
            gotoMain();
        }
    }

    private void gotoLogin() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
//            Intent intent = new Intent(SplashActivity.this, LoginNewActivity.class);
            startActivity(intent);
            finish();
        }, 3000);
    }
    private void gotoMain() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 3000);
    }

/** check next activity */
    private void startAnimation() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mBinding.imgLogo.animate()
                        .rotationBy(360)
                        .withEndAction(this)
                        .setDuration(3000)
                        .setInterpolator(new LinearInterpolator()).start();
            }
        };
        mBinding.imgLogo.animate()
                .rotationBy(360)
                .withEndAction(runnable)
                .setDuration(3000)
                .setInterpolator(new LinearInterpolator()).start();
    }

    public void checkPermissionApp() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                checkNextActivity();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(getApplicationContext(), "Bạn chưa cấp quyền cho ứng dụng", Toast.LENGTH_SHORT).show();
                checkNextActivity();
            }
        };

        TedPermission.create()
                .setPermissionListener(permissionListener)
                .setDeniedMessage(R.string.str_permission_check)
                .setPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                )
                .check();

    }

    /** test */
    private void callApiSendNotify() {
        mUsers = new ArrayList<>();
        mApiClient = new ApiClient();
        Call<ResponseBody> call = mApiClient.getApiServiceRealtime().getUsersFB();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200 && response.body() != null) {
                    try {
                        String result = response.body().string();
                        System.out.println("************* test **************");
                        System.out.println("qaz result: " + result);
                        JSONObject jsonObject = new JSONObject(result);
                        System.out.println("qaz json object: " + jsonObject);
//                        JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(SplashActivity.this, "get list user success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SplashActivity.this, "get list user failed", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(SplashActivity.this, "not connect to server", Toast.LENGTH_SHORT).show();
            }
        });
    }

}