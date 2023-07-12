package com.edu.chatapp.fragment;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.edu.chatapp.R;
import com.edu.chatapp.activity.ChangePasswordActivity;
import com.edu.chatapp.activity.LoginActivity;
import com.edu.chatapp.adapter.PostAdapter;
import com.edu.chatapp.databinding.FragmentProfileBinding;
import com.edu.chatapp.model.Post;
import com.edu.chatapp.model.User;
import com.edu.chatapp.utils.ConstantKey;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding mBinding;
    private View mView;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    //storage
    private StorageReference mStorageReference;


    private List<Post> mPosts;
    private PostAdapter mPostAdapter;

    private ProgressDialog mProgressDialog;

    private String strMediaPath = "", strRenameFilePath = "", strTimeTmp;
    private Uri mUri;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentProfileBinding.inflate(inflater, container, false);
        mView = mBinding.getRoot();

        initialVIew();
        initialData();

        return mView;
    }
    private void initialVIew() {
        checkPermission();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mPosts = new ArrayList<>();
        getFirebaseData();
        mProgressDialog = new ProgressDialog(getActivity());


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mBinding.recyclerPostProfile.setLayoutManager(layoutManager);
        mBinding.recyclerPostProfile.setHasFixedSize(true);
    }

    private void initialData() {
        updateUI();
        mBinding.txtLogout.setOnClickListener(v -> logoutAppFirebase());
        mBinding.imgAvatar.setOnClickListener(v -> selectAvatar());
//        mBinding.txtUpdateAvatar.setOnClickListener(v -> updateAvatar());
        mBinding.txtUpdateAvatar.setOnClickListener(v -> updateAvatarNew());
        mBinding.txtUpdateAccount.setOnClickListener(v -> updateAccount());
        mBinding.txtChangePassword.setOnClickListener(v -> changePassword());


    }

    private void getFirebaseData() {
        if (mAuth.getCurrentUser() != null) {
            mDatabaseReference = mDatabase.getReference(ConstantKey.TBL_USERS).child(mAuth.getCurrentUser().getUid());
            mDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    mBinding.txtEmail.setText("email: " + user.getEmail());
                    mBinding.txtUserName.setText("username: " + user.getUsername());
                    Picasso.with(getActivity()).load(user.getAvatarUrl())
                            .placeholder(R.drawable.icon_eye_show)
                            .error(R.drawable.icon_eye_hide)
                            .into(mBinding.imgAvatar);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), "get info user failed", Toast.LENGTH_SHORT).show();
                }
            });

            /** get data posts */
            Query query = mDatabase.getReference(ConstantKey.TBL_POSTS).orderByChild("pId");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mPosts.clear();
                    for (DataSnapshot o:snapshot.getChildren()) {
                        Post post = o.getValue(Post.class);
                        if (post.getpUid().equals(mAuth.getUid())) {
                            mPosts.add(0, post);
                        }
                    }
                    updateUI();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            mBinding.txtEmail.setText("email default");
            mBinding.txtUserName.setText("full name default");
        }
    }


    private void updateAvatarNew() {
        if (mUri == null) {
            return;
        }
        mFirebaseUser = mAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            return;
        }
        mStorageReference = FirebaseStorage.getInstance().getReference(ConstantKey.TBL_UPLOADS);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(ConstantKey.TBL_UPLOADS);


        StorageReference storageReference = mStorageReference.child(strTimeTmp);
        storageReference.putFile(mUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getContext(), "uploads successfully", Toast.LENGTH_SHORT).show();
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        uploadDataFirebase(uri);
                    }
                });
//                updateAccount();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "err: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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


    private void selectAvatar() {
        Toast.makeText(getActivity(), "select avatar", Toast.LENGTH_SHORT).show();
        openImagePicker();
//        openImagePickerNew();
    }
    private void updateAvatar() {
        if (mUri == null) {
            System.out.println("33333333333333333333333333333333333333");
            return;
        }
//        String timeStamp = String.valueOf(System.currentTimeMillis());

//        File file = new File(strMediaPath);
//        strRenameFilePath = file.getAbsolutePath();
//        String[] file_path_arr = strRenameFilePath.split("\\.");
//        strRenameFilePath = file_path_arr[0] + System.currentTimeMillis() + "." + file_path_arr[1];
        System.out.println("cccccccccccccccccccccccccccccccccccC: " + strRenameFilePath);
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            return;
        }
        mProgressDialog.show();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName("full name 1a")
                .setPhotoUri(Uri.parse(strRenameFilePath))
//                .setPhotoUri(mUri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    mProgressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "update avatar success", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void updateAccount() {
        Toast.makeText(getActivity(), "update account", Toast.LENGTH_SHORT).show();
//        mDatabaseReference = mDatabase.getReference(ConstantKey.TBL_USERS).child(mAuth.getUid());
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put(ConstantKey.STR_AVATAR_URL, "https://firebasestorage.googleapis.com/v0/b/chatapp-aadeb.appspot.com/o/uploads%2F1684303018170?alt=media&token=4091a39d-05b2-48b8-9627-086f20895680");
//        mDatabaseReference.updateChildren(hashMap);
    }
    private void changePassword() {
        Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
        startActivity(intent);
    }

    private void uploadDataFirebase(Uri uri) {
        Toast.makeText(getActivity(), "uploadDataFirebase", Toast.LENGTH_SHORT).show();
        System.out.println("uploadDataFirebase: " + uri);
        /** insert to tbl uploads */
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(ConstantKey.TBL_UPLOADS).child(strTimeTmp);
        HashMap<String, String> hashMapUpload = new HashMap<>();
        hashMapUpload.put("uploadId", strTimeTmp);
        hashMapUpload.put("uploadName", strTimeTmp);
        hashMapUpload.put("uploadUri", uri.toString());
        hashMapUpload.put("uploadUid", mAuth.getUid());
        mDatabaseReference.setValue(hashMapUpload);

        /** update image url to tbl users */
        mDatabaseReference = mDatabase.getReference(ConstantKey.TBL_USERS).child(mAuth.getUid());
        HashMap<String, Object> hashMapAvatar = new HashMap<>();
        hashMapAvatar.put(ConstantKey.STR_AVATAR_URL, uri.toString());
        mDatabaseReference.updateChildren(hashMapAvatar);

        /** update image url to tbl post */
        mDatabaseReference = mDatabase.getReference(ConstantKey.TBL_POSTS);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mPosts.clear();
                HashMap<String, Object> hashMapPost = new HashMap<>();
                hashMapPost.put(ConstantKey.STR__POST_AVATAR_URL, uri.toString());
                for (DataSnapshot o: snapshot.getChildren()) {
                    Post post = o.getValue(Post.class);
                    if (mAuth.getCurrentUser().getUid().equals(post.getpUid())) {
                        mDatabaseReference.child(post.getpId()).updateChildren(hashMapPost);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void logoutAppFirebase() {
        mDatabaseReference = mDatabase.getReference("users").child(mAuth.getCurrentUser().getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", "offline");
        mDatabaseReference.updateChildren(hashMap);
        mAuth.signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
//        getActivity().finishAffinity();
    }

    private void updateUI() {
        if (mPostAdapter == null) {
            mPostAdapter = new PostAdapter(mPosts, getContext().getApplicationContext());
            mBinding.recyclerPostProfile.setAdapter(mPostAdapter);
        }
        mPostAdapter.notifyDataSetChanged();
    }

    private void openImagePicker() {
        TedBottomPicker.with(getActivity())
                .show(new TedBottomSheetDialogFragment.OnImageSelectedListener() {
                    @Override
                    public void onImageSelected(Uri uri) {
                        // here is selected image uri
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                            mBinding.imgAvatar.setImageBitmap(bitmap);

                            File pathStorage = Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES);
                            File fileStorage = new File(pathStorage, ".jpg");
                            FileOutputStream fo;
                            try {
                                pathStorage.mkdirs();
                                fo = new FileOutputStream(fileStorage);
                                fo.write(bytes.toByteArray());
                                fo.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

//                            strMediaPath = fileStorage.getAbsolutePath();

                            mUri = uri;

                            strTimeTmp = String.valueOf(System.currentTimeMillis());
                            File fileMediaPath = new File(fileStorage.getAbsolutePath());
                            strRenameFilePath = fileMediaPath.getAbsolutePath();
                            String[] file_path_arr = strRenameFilePath.split("\\.");
                            strRenameFilePath = file_path_arr[0] + strTimeTmp + "." + file_path_arr[1];

                            System.out.println("----------- **************** ---------------------");
                            System.out.println("link tu camera");
                            System.out.println("media uri: " + mUri);
                            System.out.println("media pathStorage: " + pathStorage);
                            System.out.println("media strMediaPath: " + fileStorage.getAbsolutePath());
                            System.out.println("media fileMedia: " + fileMediaPath);
                            System.out.println("media strRenameFilePath: " + strRenameFilePath);
                            System.out.println("----------- **************** ---------------------");
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
    }


    private void openImagePickerNew() {
        TedBottomPicker.with(getActivity())
                .show(new TedBottomSheetDialogFragment.OnImageSelectedListener() {
                    @Override
                    public void onImageSelected(Uri uri) {
                        // here is selected image uri
                        try {

//                            mBinding.imgAvatar.setDrawingCacheEnabled(true);
//                            mBinding.imgAvatar.buildDrawingCache();
//                            Bitmap bitmap = ((BitmapDrawable) mBinding.imgAvatar.getDrawable()).getBitmap();
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                            mBinding.imgAvatar.setImageBitmap(bitmap);


                            File pathStorage = Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES);
                            File fileStorage = new File(pathStorage, ".jpg");
                            FileOutputStream fo;
                            try {
                                pathStorage.mkdirs();
                                fo = new FileOutputStream(fileStorage);
                                fo.write(bytes.toByteArray());
                                fo.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            strMediaPath = fileStorage.getAbsolutePath();

                            mUri = uri;

                            File fileMedia = new File(strMediaPath);
                            strRenameFilePath = fileMedia.getAbsolutePath();
                            String[] file_path_arr = strRenameFilePath.split("\\.");
                            strRenameFilePath = file_path_arr[0] + System.currentTimeMillis() + "." + file_path_arr[1];

                            System.out.println("----------- **************** ---------------------");
                            System.out.println("link tu camera");
                            System.out.println("media uri: " + mUri);
                            System.out.println("media pathStorage: " + pathStorage);
                            System.out.println("media strMediaPath: " + strMediaPath);
                            System.out.println("media fileMedia: " + fileMedia);
                            System.out.println("media strRenameFilePath: " + strRenameFilePath);
                            System.out.println("----------- **************** ---------------------");
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
    }
    /** check permission */
    private void checkPermission() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
//                openImagePicker();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(getActivity(), "Bạn chưa cấp quyền cho ứng dụng", Toast.LENGTH_SHORT).show();
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

}
