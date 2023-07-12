package com.edu.chatapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.edu.chatapp.activity.MessageActivity;
import com.edu.chatapp.adapter.ChatAdapter;
import com.edu.chatapp.adapter.ContactsAdapter;
import com.edu.chatapp.databinding.FragmentChatBinding;
import com.edu.chatapp.iclick.IClickChat;
import com.edu.chatapp.model.Chat;
import com.edu.chatapp.model.ChatList;
import com.edu.chatapp.model.Message;
import com.edu.chatapp.model.Token;
import com.edu.chatapp.model.User;
import com.edu.chatapp.utils.ConstantKey;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatFragment extends Fragment implements IClickChat {

    private FragmentChatBinding mBinding;
    private View mView;

    FirebaseAuth mAuth;
    FirebaseUser mFirebaseUser;
    FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mReference;

    private ChatAdapter mChatAdapter;
    private List<User> mUsers;
    private User mUser;
    private List<ChatList> mChatLists;

    private ValueEventListener mSeenEventListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentChatBinding.inflate(inflater, container, false);
        mView = mBinding.getRoot();

        initialVIew();
        initialData();

        return mView;
    }
    private void initialVIew() {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mUsers = new ArrayList<>();
        mChatLists = new ArrayList<>();

        mBinding.recyclerChat.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mBinding.recyclerChat.setLayoutManager(layoutManager);



        readFirebaseData();
        updateUI();

    }

    private void initialData() {
    }

    private void updateUI() {
        mChatAdapter = new ChatAdapter(mUsers, getContext(), true, this);
        mBinding.recyclerChat.setAdapter(mChatAdapter);
        mChatAdapter.notifyDataSetChanged();
    }


    private void readFirebaseData() {
        mFirebaseUser = mAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            return;
        }
        mReference = FirebaseDatabase.getInstance().getReference(ConstantKey.TBL_CHAT_LIST).child(mFirebaseUser.getUid());
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChatLists.clear();
                for (DataSnapshot x: snapshot.getChildren()) {
                    ChatList chatList = x.getValue(ChatList.class);
                    mChatLists.add(chatList);
                }
                readMessageFirebase();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
//    private void readFirebaseDataGetMessage() {
//        mFirebaseUser = mAuth.getCurrentUser();
//        if (mFirebaseUser == null) {
//            return;
//        }
//        System.out.println("-------------------------------------------------------------------------------------------------------------------");
//        System.out.println("userid firebase: " + mFirebaseUser.getUid());
//        mReference = FirebaseDatabase.getInstance().getReference(TBL_CHAT_LIST);//.child(mFirebaseUser.getUid());
//
//        mReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                mChatLists.clear();
//                for (DataSnapshot x: snapshot.getChildren()) {
//                    ChatList chatList = x.getValue(ChatList.class);
//                    mChatLists.add(chatList);
//                }
//                System.out.println("SSSSSSSSSSSSSSSSSSSS chat list size: " + mChatLists.size());
//                readMessageFirebase();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }

    private void readMessageFirebase() {
        mUsers = new ArrayList<>();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            return;
        }
        mReference = mFirebaseDatabase.getReference(ConstantKey.TBL_USERS);
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot o: snapshot.getChildren()) {
                    mUser = o.getValue(User.class);
                    for (ChatList chatList : mChatLists) {
                        if (mUser.getId().equals(chatList.getId())) {
                            mUsers.add(mUser);
                        }
                    }
                }
//                    mChatAdapter = new ChatAdapter(mUsers, getContext(), true, new IClickChat() {
//                        @Override
//                        public void clickChat(User user) {
//                            Intent intent = new Intent(getContext(), MessageActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            Bundle bundle = new Bundle();
//                            bundle.putSerializable(BUNDLE_KEY_USER_ID, user);
//                            intent.putExtras(bundle);
//                            startActivity(intent);
//                        }
//                    });
//                mChatAdapter = new ChatAdapter(mUsers, getContext(), true, getContext());
//                    mBinding.recyclerChat.setAdapter(mChatAdapter);
//                mChatAdapter.notifyDataSetChanged();
                updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void clickChat(User user) {
        Intent intent = new Intent(getContext(), MessageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantKey.BUNDLE_KEY_USER_ID, user);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    /** dhashdklasjdfkasjlkdfsail;jdlaksl */
    private void checkSeenStatus(final String userIdContact) {
        System.out.println("check seen status chat fragment");
        mReference = mFirebaseDatabase.getReference(ConstantKey.TBL_CHATS);
        mSeenEventListener = mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot o: snapshot.getChildren()) {
                    Message message = o.getValue(Message.class);
                    if (message.getReceiveId().equals(mFirebaseUser.getUid()) && message.getSenderId().equals(userIdContact)) {
//                        HashMap<String, Object> hashMap = new HashMap<>();
//                        hashMap.put("seenStatus", true);
//                        o.getRef().updateChildren(hashMap);
                        System.out.println("11  checkSeenStatus *****************************************************************************************************");
                        System.out.println("message.getReceiveId(): " + message.getReceiveId());
                        System.out.println("mFirebaseUser.getUid(): " + mFirebaseUser.getUid());
                        System.out.println("message.getSenderId(): " + message.getSenderId());
                        System.out.println("userIdContact: " + userIdContact);
                        System.out.println("22  checkSeenStatus *****************************************************************************************************");
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
