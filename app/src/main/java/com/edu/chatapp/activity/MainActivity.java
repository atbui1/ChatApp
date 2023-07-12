package com.edu.chatapp.activity;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.edu.chatapp.R;
import com.edu.chatapp.adapter.ViewPagerAdapter;
import com.edu.chatapp.animation.DepthPageTransformer;
import com.edu.chatapp.databinding.ActivityMainBinding;
import com.edu.chatapp.model.Message;
import com.edu.chatapp.model.Token;
import com.edu.chatapp.utils.ConstantKey;
import com.google.android.material.badge.BadgeDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class MainActivity extends BaseActivity {

    public static final int MY_REQUEST_CODE = 123;

    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_MESSAGE = 1;
    private static final int FRAGMENT_NOTIFY = 2;
    private static final int FRAGMENT_CART = 3;
    private static final int FRAGMENT_PERSON = 4;

    public static final String TAG = "MainActivity - tuanba";

    private ActivityMainBinding mBinding;

    private View mView;
    private String strPhoneNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       mBinding = ActivityMainBinding.inflate(getLayoutInflater());
       mView = mBinding.getRoot();
       setContentView(mView);
       
       initialView();
       initialData();
        
    }

    private void initialView() {
        getTokenDevice();
        getDataBundle();

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
//        getNumberChats();

        mBinding.viewPaper.setAdapter(viewPagerAdapter);
//        animation view paper
        mBinding.viewPaper.setPageTransformer(new DepthPageTransformer());
//        xu ly su kien vuot phai trai chuyen man hinh
        mBinding.viewPaper.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        mBinding.bottomNavigation.getMenu().findItem(R.id.nav_home).setChecked(true);
                        break;
                    case 1:
                        mBinding.bottomNavigation.getMenu().findItem(R.id.nav_message).setChecked(true);
                        break;
                    case 2:
                        mBinding.bottomNavigation.getMenu().findItem(R.id.nav_notification).setChecked(true);
                        break;
                    case 3:
                        mBinding.bottomNavigation.getMenu().findItem(R.id.nav_contacts).setChecked(true);
                        break;
                    case 4:
                        mBinding.bottomNavigation.getMenu().findItem(R.id.nav_personal).setChecked(true);
                        break;
                }
            }
        });

//        xu ly su kien click vao item bottom navigation
        mBinding.bottomNavigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    mBinding.viewPaper.setCurrentItem(0);
                    break;
                case R.id.nav_message:
                    mBinding.viewPaper.setCurrentItem(1);
                    break;
                case R.id.nav_notification:
                    mBinding.viewPaper.setCurrentItem(2);
                    break;
                case R.id.nav_contacts:
                    mBinding.viewPaper.setCurrentItem(3);
                    break;
                case R.id.nav_personal:
                    mBinding.viewPaper.setCurrentItem(4);
                    break;
            }
            return true;
        });
    }

    private void initialData() {
    }

    private void getDataBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            strPhoneNumber = bundle.getString(ConstantKey.KEY_PHONE_NUMBER);
            Toast.makeText(this, "sdt: " + strPhoneNumber, Toast.LENGTH_SHORT).show();
        }
    }

    private void getTokenDevice() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    // Get new FCM registration token
                    String token = task.getResult();
                    pushTokenToFireBase(token);
                    // Log and toast
                    Log.i(TAG, "token main activity: " + token);
                });
    }
    private void pushTokenToFireBase(String strToken) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            Log.e(TAG, "not found user firebase");
            return;
        }
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(ConstantKey.TBL_TOKENS);
        Token token = new Token(strToken);
        databaseReference.child(firebaseUser.getUid()).setValue(token);
        Log.i(TAG, "push token successfully on main activity");
    }

    private void getNumberChats() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            Log.e(TAG, "Not found user firebase");
            return;
        }
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(ConstantKey.TBL_CHATS);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int unRead = 0;
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    Message message = dataSnapshot.getValue(Message.class);
                    if (message.getReceiveId().equals(firebaseUser.getUid()) && !message.isSeenStatus()) {
                        unRead++;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}