package com.edu.chatapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.edu.chatapp.databinding.FragmentNotificationBinding;


public class NotificationFragment extends Fragment {

    private FragmentNotificationBinding mBinding;
    private View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentNotificationBinding.inflate(inflater, container, false);
        mView = mBinding.getRoot();

        initialVIew();
        initialData();

        return mView;
    }
    private void initialVIew() {
    }

    private void initialData() {
    }
}
