package com.edu.chatapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.edu.chatapp.R;
import com.edu.chatapp.databinding.ActivityPostCreateBinding;
import com.edu.chatapp.model.User;
import com.edu.chatapp.utils.ConstantKey;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class PostCreateActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityPostCreateBinding mBinding;
    //firebase
    private DatabaseReference mReference;
    private ProgressDialog mProgressDialog;
    private User mUser;
    private String strReceiveId = "";

    private Uri mTestUri;

    private Upload mUploadCustom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_post_create);
        mBinding = ActivityPostCreateBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initialView();
        initialData();
    }

    private void initialView() {
        getDataBundle();
        mUploadCustom = new Upload(this);

        mProgressDialog = new ProgressDialog(this);
        mBinding.imgBackStack.setOnClickListener(this);
        mBinding.lnlPostCreate.setOnClickListener(this);
        mBinding.imgPicture.setOnClickListener(this);
    }

    private void initialData() {
    }

    private void getDataBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mUser = (User) bundle.getSerializable(ConstantKey.BUNDLE_KEY_USER_ID);
            if (mUser != null) {
                strReceiveId = mUser.getId();
//                mBinding.txtUserName.setText(mUser.getUsername());
//                mBinding.txtEmail.setText(mUser.getEmail());
//                if (mUser.getAvatarUrl() != null) {
//                    strAvatarReceiveUrl = mUser.getAvatarUrl();
//                    Picasso.with(getApplicationContext()).load(mUser.getAvatarUrl())
//                            .placeholder(R.drawable.icon_eye_show)
//                            .error(R.drawable.icon_eye_hide)
//                            .into(mBinding.imgAvatar);
//                }
                System.out.println("************************* user *****************************");
                System.out.println("usr firebase: " + mUser);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back_stack:
                pressBackStack();
                break;
            case R.id.lnl_post_create:
//                postCreate1();
//                postCreate2();

                uploadDataStorageQQQ();
                break;
            case R.id.img_picture:
                openImageStorage();
                break;
        }
    }

    /**
     * listener
     */
    private void pressBackStack() {
        finish();
    }

    private void openImageStorage() {
        mUploadCustom.openImagePickerNew(mBinding.imgPicture);
        Toast.makeText(this, "uri posts: " + mTestUri, Toast.LENGTH_SHORT).show();
        System.out.println("openImageStorageUri: " + mTestUri);
        System.out.println("openImageStoragestrtime: " + mUploadCustom.strTimeTmp);
    }

    private void postCreate1() {
        mUploadCustom.uploadDataStorage(ConstantKey.TBL_UPLOADS);
    }

    public void uploadDataStorageQQQ() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            System.out.println("uploadStorage null user");
            return;
        }
        System.out.println("uploadDataStorage str time: " + mUploadCustom.strTimeTmp);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference(ConstantKey.TBL_UPLOADS);
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(table);


        StorageReference storageRefUpload = storageRef.child(mUploadCustom.strTimeTmp);
        storageRefUpload.putFile(mTestUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                /** insert file - picture into storage db fb */
                storageRefUpload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //call function upload file - picture into real time db fb
//                        uploadDataRealtimeFB(uri, table, strTimeTmp);
                        addNewUploads(uri);
                        addNewPosts(uri);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "err: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("err--------: " + e.getMessage());
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                System.out.println("progress: " + progress);
            }
        });
    }

    private void addNewUploads(Uri uriServer) {

        DatabaseReference UploadReference = FirebaseDatabase.getInstance().getReference(ConstantKey.TBL_POSTS).child(mUploadCustom.strTimeTmp);
        HashMap<String, String> hashMapUpload = new HashMap<>();
//        hashMapUpload.put("uploadId", tableKey);
//        hashMapUpload.put("uploadName", tableKey);
        hashMapUpload.put("uploadId", mUploadCustom.strTimeTmp);
        hashMapUpload.put("uploadName", mUploadCustom.strTimeTmp);
        hashMapUpload.put("uploadUri", uriServer.toString());
        hashMapUpload.put("uploadUid", mUser.getId());
        UploadReference.setValue(hashMapUpload);
    }

    private void addNewPosts(Uri uriServer) {
        Toast.makeText(this, "post post post", Toast.LENGTH_SHORT).show();
        mProgressDialog.show();

        String timeStamp = String.valueOf(System.currentTimeMillis());
        String title = mBinding.edtPostTitle.getText().toString().trim();

        mReference = FirebaseDatabase.getInstance().getReference(ConstantKey.TBL_POSTS).child(timeStamp);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("pId", timeStamp);
        hashMap.put("pUsername", mUser.getUsername());
        hashMap.put("pEmail", mUser.getEmail());
        hashMap.put("pTime", timeStamp);
        hashMap.put("pAvatar", mUser.getAvatarUrl());
        hashMap.put("pTitle", title);
        hashMap.put("pUid", mUser.getId());
        hashMap.put("pImage", uriServer.toString());
        mReference.setValue(hashMap).addOnSuccessListener(unused -> {
            mProgressDialog.dismiss();
            Intent intent = new Intent(PostCreateActivity.this, MainActivity.class);
            startActivity(intent);
            finishAffinity();
        }).addOnFailureListener(e -> {
            mProgressDialog.dismiss();
            Toast.makeText(PostCreateActivity.this, "add new post failed", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     private void addNewPost() {
     mReference = FirebaseDatabase.getInstance().getReference(ConstantKey.TBL_POSTS).child(timeStamp);
     HashMap<String, String> hashMap = new HashMap<>();
     hashMap.put("pId", timeStamp);
     hashMap.put("pUsername", mUser.getUsername());
     hashMap.put("pEmail", mUser.getEmail());
     hashMap.put("pTime", timeStamp);
     hashMap.put("pAvatar", mUser.getAvatarUrl());
     hashMap.put("pTitle", title);
     hashMap.put("pUid", mUser.getId());
     hashMap.put("pImage", "https://images2.thanhnien.vn/528068263637045248/2023/4/6/5-16807849779301497498942.jpeg");
     mReference.setValue(hashMap).addOnSuccessListener(unused -> {
     mProgressDialog.dismiss();
     Intent intent = new Intent(PostCreateActivity.this, MainActivity.class);
     startActivity(intent);
     finishAffinity();
     }).addOnFailureListener(e -> {
     mProgressDialog.dismiss();
     Toast.makeText(PostCreateActivity.this, "add new post failed", Toast.LENGTH_SHORT).show();
     });
     }
     */

    /**
     * function add new post successfully
     */
    private void postCreate2() {
        Toast.makeText(this, "post post post", Toast.LENGTH_SHORT).show();
        mProgressDialog.show();

        String timeStamp = String.valueOf(System.currentTimeMillis());
        String title = mBinding.edtPostTitle.getText().toString().trim();

        mReference = FirebaseDatabase.getInstance().getReference(ConstantKey.TBL_POSTS).child(timeStamp);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("pId", timeStamp);
        hashMap.put("pUsername", mUser.getUsername());
        hashMap.put("pEmail", mUser.getEmail());
        hashMap.put("pTime", timeStamp);
        hashMap.put("pAvatar", mUser.getAvatarUrl());
        hashMap.put("pTitle", title);
        hashMap.put("pUid", mUser.getId());
        hashMap.put("pImage", "https://images2.thanhnien.vn/528068263637045248/2023/4/6/5-16807849779301497498942.jpeg");
        mReference.setValue(hashMap).addOnSuccessListener(unused -> {
            mProgressDialog.dismiss();
            Intent intent = new Intent(PostCreateActivity.this, MainActivity.class);
            startActivity(intent);
            finishAffinity();
        }).addOnFailureListener(e -> {
            mProgressDialog.dismiss();
            Toast.makeText(PostCreateActivity.this, "add new post failed", Toast.LENGTH_SHORT).show();
        });
    }
}