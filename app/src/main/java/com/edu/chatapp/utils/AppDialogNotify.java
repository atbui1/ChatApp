package com.edu.chatapp.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

import com.edu.chatapp.databinding.DialogOneOptionBinding;
import com.edu.chatapp.iclick.IClickDialog;

public class AppDialogNotify {

    private DialogOneOptionBinding mBinding;
    private View mView;
    private Context context;
    private Dialog dialog;

    public AppDialogNotify(Context context) {
        this.context = context;
    }


    public void showDialogOneOption(String txtErr, String txtOption, String bgColor, String txtColor) {
        dialog = new Dialog(context);
        mBinding = DialogOneOptionBinding.inflate(dialog.getLayoutInflater());
        mView = mBinding.getRoot();
        dialog.setContentView(mView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        mBinding.txtDetailErr.setText(txtErr);
        mBinding.btnClose.setText(txtOption);
        mBinding.btnClose.setBackgroundColor(Color.parseColor(bgColor));
        mBinding.btnClose.setTextColor(Color.parseColor(txtColor));
        mBinding.btnOk.setVisibility(View.GONE);
        mBinding.edtOtp.setVisibility(View.GONE);
        mBinding.btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
    public void showDialogTwoOption(String txtErr, String txtOptionClose, String txtOptionOk, String bgColor, String txtColor) {
        dialog = new Dialog(context);
        mBinding = DialogOneOptionBinding.inflate(dialog.getLayoutInflater());
        mView = mBinding.getRoot();
        dialog.setContentView(mView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        mBinding.txtDetailErr.setText(txtErr);
        mBinding.btnClose.setText(txtOptionClose);
        mBinding.btnClose.setBackgroundColor(Color.parseColor(bgColor));
        mBinding.btnClose.setTextColor(Color.parseColor(txtColor));
        mBinding.btnOk.setTextColor(Color.parseColor(txtOptionOk));
        mBinding.btnClose.setOnClickListener(v -> dialog.dismiss());

//        mBinding.btnOk.setOnClickListener(v -> eventDialog());

        dialog.show();
    }

//    private void eventDialog() {
//        String edt = mBinding.edtOtp.getText().toString().trim();
//        iClickDialog.clickAppDialogSuccess(edt);
//    }
}
