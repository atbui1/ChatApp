package com.edu.chatapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.edu.chatapp.R;
import com.edu.chatapp.activity.PostCreateActivity;
import com.edu.chatapp.adapter.PostAdapter;
import com.edu.chatapp.databinding.FragmentDiaryBinding;
import com.edu.chatapp.model.Post;
import com.edu.chatapp.model.User;
import com.edu.chatapp.utils.ConstantKey;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DiaryFragment extends Fragment implements View.OnClickListener {
    private FragmentDiaryBinding mBinding;
    private View mView;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    private PostAdapter mPostAdapter;
    private List<Post> mPosts;
    private User mUser;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        mBinding = FragmentDiaryBinding.inflate(inflater, container, false);
        mView = mBinding.getRoot();

        initialView();
        initialData();

        return mView;
    }

    private void initialView() {
        getDataFirebase();
        mPosts = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mBinding.recyclerDiary.setLayoutManager(layoutManager);
        mBinding.recyclerDiary.setHasFixedSize(true);
        mBinding.lnlCreatePost.setOnClickListener(this);
    }

    private void initialData() {
//        updateUI();
    }

    private void getDataFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        if (mAuth.getCurrentUser() != null) {
            mReference = mDatabase.getReference(ConstantKey.TBL_USERS).child(mAuth.getCurrentUser().getUid());
            System.out.println("mReference: " + mReference);
            mReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mUser = snapshot.getValue(User.class);
                    Picasso.with(getActivity()).load(mUser.getAvatarUrl())
                            .placeholder(R.drawable.icon_eye_show)
                            .error(R.drawable.icon_eye_hide)
                            .into(mBinding.imgAvatar);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), "get info user failed", Toast.LENGTH_SHORT).show();
                }
            });

            Query query = mDatabase.getReference(ConstantKey.TBL_POSTS).orderByChild("pId");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mPosts.clear();
                    for (DataSnapshot o: snapshot.getChildren()) {
                        Post post = o.getValue(Post.class);
                        mPosts.add(0, post);
                    }
                    updateUI();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), "get post list failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateUI() {
        if (mPostAdapter == null) {
            mPostAdapter = new PostAdapter(mPosts, getContext().getApplicationContext());
            mBinding.recyclerDiary.setAdapter(mPostAdapter);
        }
        mPostAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lnl_create_post:
                goToCreatePost();
                break;
        }
    }

    /** listener */
    private void goToCreatePost() {
        Toast.makeText(getContext(), "goToCreatePost", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), PostCreateActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantKey.BUNDLE_KEY_USER_ID, mUser);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
