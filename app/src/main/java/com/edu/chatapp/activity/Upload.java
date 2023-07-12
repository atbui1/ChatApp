package com.edu.chatapp.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;

public class Upload {
    private Uri mUri;
    private String strMediaPath = "", strRenameFilePath = "";
    public String strTimeTmp = "";

    private Activity activity;

    private ImageView aImageView;


    public Upload(Activity activity) {
        this.activity = activity;
    }

    public void openImagePickerNew(ImageView imageView) {
        TedBottomPicker.with((FragmentActivity) activity)
                .show(new TedBottomSheetDialogFragment.OnImageSelectedListener() {
                    @Override
                    public void onImageSelected(Uri uri) {
                        try {

//                            mBinding.imgAvatar.setDrawingCacheEnabled(true);
//                            mBinding.imgAvatar.buildDrawingCache();
//                            Bitmap bitmap = ((BitmapDrawable) mBinding.imgAvatar.getDrawable()).getBitmap();
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                            imageView.setImageBitmap(bitmap);


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


                            strRenameFilePath = file_path_arr[0] + strTimeTmp + "." + file_path_arr[1];

                            System.out.println("----------- **************** ---------------------");
                            System.out.println("media strTimeTmp: " + strTimeTmp);
                            System.out.println("media uri: " + mUri);
                            System.out.println("media pathStorage: " + pathStorage);
                            System.out.println("media strMediaPath: " + strMediaPath);
                            System.out.println("media fileMedia: " + fileMedia);
                            System.out.println("media strRenameFilePath: " + strRenameFilePath);
                            System.out.println("----------- **************** ---------------------");
                            Toast.makeText(activity, "uri upload: " + mUri, Toast.LENGTH_SHORT).show();

                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
    }

    public void uploadDataRealtimeFB(Uri uri, String table, String tableKey) {
        Toast.makeText(activity, "uploadDataFirebase", Toast.LENGTH_SHORT).show();
        /** insert to tbl uploads */
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            return;
        }
        /** insert uri's file - picture into tbl uploads db realtime */
        DatabaseReference UploadReference = FirebaseDatabase.getInstance().getReference(table).child(tableKey);
//        HashMap<String, String> hashMapUpload = new HashMap<>();
//        hashMapUpload.put("uploadId", tableKey);
//        hashMapUpload.put("uploadName", tableKey);
//        hashMapUpload.put("uploadUri", uri.toString());
//        hashMapUpload.put("uploadUid", firebaseUser.getUid());
//        UploadReference.setValue(hashMapUpload);

        com.edu.chatapp.model.Upload upload = new com.edu.chatapp.model.Upload(table, table, uri.toString(), firebaseUser.getUid());
        UploadReference.setValue(upload);

    }

    public void uploadDataStorage(String table) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            System.out.println("uploadStorage null user");
            return;
        }
        StorageReference storageRef = FirebaseStorage.getInstance().getReference(table);
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(table);


        StorageReference storageRefUpload = storageRef.child(strTimeTmp);
        storageRefUpload.putFile(mUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                /** insert file - picture into storage db fb */
                storageRefUpload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //call function upload file - picture into real time db fb
//                        uploadDataRealtimeFB(uri, table, strTimeTmp);

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, "err: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

}
