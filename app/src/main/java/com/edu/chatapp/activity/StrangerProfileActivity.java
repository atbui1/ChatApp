package com.edu.chatapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.edu.chatapp.R;
import com.edu.chatapp.databinding.ActivityStrangerProfileBinding;
import com.edu.chatapp.model.User;
import com.edu.chatapp.utils.ConstantKey;
import com.squareup.picasso.Picasso;

public class StrangerProfileActivity extends AppCompatActivity {

    private ActivityStrangerProfileBinding mBinding;
    public String strReceiveId = "", strAvatarReceiveUrl = "";
    private User mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityStrangerProfileBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());


        initialView();
        initialData();
    }

    private void initialView() {
        getDataBundle();
    }

    private void initialData() {
        mBinding.imgBackStack.setOnClickListener(v -> finish());
        mBinding.txtAddFriend.setOnClickListener(v-> Toast.makeText(this, "add friend: " + mUser.getId(), Toast.LENGTH_SHORT).show());
    }

    private void getDataBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
              mUser = (User) bundle.getSerializable(ConstantKey.BUNDLE_KEY_USER_ID);
            if (mUser != null) {
                strReceiveId = mUser.getId();
                mBinding.txtUserName.setText(mUser.getUsername());
                mBinding.txtEmail.setText(mUser.getEmail());
                if (mUser.getAvatarUrl() != null) {
                    strAvatarReceiveUrl = mUser.getAvatarUrl();
                    Picasso.with(getApplicationContext()).load(mUser.getAvatarUrl())
                            .placeholder(R.drawable.icon_eye_show)
                            .error(R.drawable.icon_eye_hide)
                            .into(mBinding.imgAvatar);
                }
            }
        }
    }
}