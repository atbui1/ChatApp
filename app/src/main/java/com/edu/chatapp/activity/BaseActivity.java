package com.edu.chatapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.edu.chatapp.databinding.ActivityBaseBinding;
import com.edu.chatapp.receiver.InternetBroadcastReceiver;
import com.edu.chatapp.utils.ConstantKey;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class BaseActivity extends AppCompatActivity {
    private ActivityBaseBinding mBinding;
    private InternetBroadcastReceiver mInternetBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityBaseBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mInternetBroadcastReceiver = new InternetBroadcastReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mInternetBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(mInternetBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        statusAccount(ConstantKey.STR_STATUS_ON);
    }

    @Override
    protected void onPause() {
        super.onPause();
        statusAccount(ConstantKey.STR_STATUS_OFF);
    }
    /** status account */
    private void statusAccount(String status) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            return;
        }
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference(ConstantKey.TBL_USERS).child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(ConstantKey.STR_STATUS_ACCOUNT, status);
        reference.updateChildren(hashMap);
    }
}