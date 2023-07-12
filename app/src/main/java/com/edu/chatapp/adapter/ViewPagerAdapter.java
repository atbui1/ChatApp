package com.edu.chatapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.edu.chatapp.fragment.DiaryFragment;
import com.edu.chatapp.fragment.ChatFragment;
import com.edu.chatapp.fragment.NotificationFragment;
import com.edu.chatapp.fragment.ContactsFragment;
import com.edu.chatapp.fragment.ProfileFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new DiaryFragment();
            case 1:
                return new ChatFragment();
            case 2:
                return new NotificationFragment();
            case 3:
                return new ContactsFragment();
            case 4:
                return new ProfileFragment();
            default:
                return new DiaryFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
