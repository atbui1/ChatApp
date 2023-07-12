package com.edu.chatapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edu.chatapp.activity.MessageActivity;
import com.edu.chatapp.adapter.ContactsAdapter;
import com.edu.chatapp.databinding.FragmentContactsBinding;
import com.edu.chatapp.iclick.IClickContacts;
import com.edu.chatapp.model.User;
import com.edu.chatapp.utils.ConstantKey;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment implements IClickContacts {
    private FragmentContactsBinding mBinding;
    private View mView;
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mReference;



    private ContactsAdapter mContactsAdapter;
    private List<User> mUsers;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentContactsBinding.inflate(inflater, container, false);
        mView = mBinding.getRoot();

        initialVIew();
        initialData();

        return mView;
    }

    private void initialVIew() {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        System.out.println("message initial view 111111111111111111111111111111111111111111111111111");
        mBinding.recyclerContacts.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mBinding.recyclerContacts.setLayoutManager(layoutManager);
        mUsers = new ArrayList<>();

        readFirebaseData();

        mBinding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchLiveText(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void searchLiveText(String s) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        Query query = mFirebaseDatabase.getReference(ConstantKey.TBL_USERS).orderByChild(ConstantKey.STR_USERNAME)
                .startAfter(s)
                .endAt(s + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot o: snapshot.getChildren()) {
                    User user = o.getValue(User.class);
                    if (!user.getId().equals(firebaseUser.getUid())) {
                        mUsers.add(user);
                    }
                }

                updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initialData() {
    }

    private void updateUI() {
        System.out.println("IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII size: " + mUsers.size());
        if (mContactsAdapter == null) {
            mContactsAdapter = new ContactsAdapter(mUsers, getContext().getApplicationContext(), this, true);

            mBinding.recyclerContacts.setAdapter(mContactsAdapter);
        }
        mContactsAdapter.notifyDataSetChanged();
    }

    private void readFirebaseData() {
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String idOfUserLogin = firebaseUser.getUid();
        mReference = mFirebaseDatabase.getReference(ConstantKey.TBL_USERS);
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                if (mBinding.edtSearch.getText().toString().trim().equals("")) {
                    for (DataSnapshot x: snapshot.getChildren()) {
                        User user = x.getValue(User.class);
                        if (!idOfUserLogin.equals(user.getId())) {
                            mUsers.add(user);
                        }
                    }
                }
                updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void clickContacts(User user) {
        Intent intent = new Intent(getContext(), MessageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantKey.BUNDLE_KEY_USER_ID, user);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}