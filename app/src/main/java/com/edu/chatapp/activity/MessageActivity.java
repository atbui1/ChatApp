package com.edu.chatapp.activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.edu.chatapp.R;
import com.edu.chatapp.adapter.MessageAdapter;
import com.edu.chatapp.api.ApiClient;
import com.edu.chatapp.databinding.ActivityMessageBinding;
import com.edu.chatapp.iclick.IClickMessage;
import com.edu.chatapp.model.Message;
import com.edu.chatapp.model.Notify;
import com.edu.chatapp.model.Sender;
import com.edu.chatapp.model.Token;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends BaseActivity implements IClickMessage {
    private ActivityMessageBinding mBinding;

    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mReference;

    private ValueEventListener mSeenEventListener;

    private List<Message> mMessages;
    private MessageAdapter mMessageAdapter;

    private String strSenderId = "", strReceiveId = "", strAvatarReceiveUrl = "";
    private ApiClient mApiClient;
    boolean checkNotification = false;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        
        initialView();
        initialData();
    }
    @Override
    protected void onResume() {
        super.onResume();
        statusAccount(ConstantKey.STR_STATUS_ON);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mReference.removeEventListener(mSeenEventListener);
        statusAccount(ConstantKey.STR_STATUS_OFF);
    }

    private void callApiSendNotify(Sender sender) {
        mApiClient = new ApiClient();
        Call<ResponseBody> call = mApiClient.getApiServiceFCM().sendNotification(sender);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200 && response.body() != null) {
                    Toast.makeText(MessageActivity.this, "push notification success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MessageActivity.this, "push notification failed", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MessageActivity.this, "not connect to server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initialView() {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        getDataBundle();
        fDatabase();

        mBinding.recyclerChat.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mBinding.recyclerChat.setLayoutManager(layoutManager);

        mMessages = new ArrayList<>();
        mBinding.recyclerChat.setAdapter(mMessageAdapter);

        mBinding.imgBackStack.setOnClickListener(v -> backStack());
        mBinding.edtEnterMessage.setOnClickListener(v -> checkKeyboard());

        mBinding.imgSendMessage.setOnClickListener(v -> {
//            checkNotification = false;
            checkNotification = true;
            String msg = mBinding.edtEnterMessage.getText().toString().trim();
            if (msg.equals("")) {
                return;
            }

            sendMessageWithFirebase(strSenderId, strReceiveId, msg);
            mBinding.edtEnterMessage.setText("");
            checkKeyboard();
        });

        mBinding.imgAvatarReceive.setOnClickListener(v -> goToUserProfile());
    }

    private void initialData() {

    }

    private void backStack() {
        finish();
    }
    private void goToUserProfile() {
        Toast.makeText(this, "go to user profile", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MessageActivity.this, StrangerProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantKey.BUNDLE_KEY_USER_ID, mUser);
        intent.putExtras(bundle);
        startActivity(intent);
    }



    @SuppressLint("SetTextI18n")
    private void getDataBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
//            User user = (User) bundle.getSerializable(ConstantKey.BUNDLE_KEY_USER_ID);
            mUser = (User) bundle.getSerializable(ConstantKey.BUNDLE_KEY_USER_ID);
            if (mUser != null) {
                strReceiveId = mUser.getId();
                mBinding.txtUserReceive.setText(mUser.getUsername());
                if (mUser.getAvatarUrl() != null) {
                    strAvatarReceiveUrl = mUser.getAvatarUrl();
                    Picasso.with(getApplicationContext()).load(mUser.getAvatarUrl())
                            .placeholder(R.drawable.icon_eye_show)
                            .error(R.drawable.icon_eye_hide)
                            .into(mBinding.imgAvatarReceive);
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateUI() {
        if (mMessageAdapter == null) {
            mMessageAdapter = new MessageAdapter(this, mMessages, strAvatarReceiveUrl);
            mBinding.recyclerChat.setAdapter(mMessageAdapter);
        }
        mMessageAdapter.notifyDataSetChanged();
    }

    private void fDatabase() {
        mFirebaseUser = mAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            return;
        }
        strSenderId = mFirebaseUser.getUid();
        mReference = mFirebaseDatabase.getReference(ConstantKey.TBL_USERS).child(strReceiveId);
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                readMessageWithFirebase(strSenderId, strReceiveId, strAvatarReceiveUrl);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        checkSeenStatus(strReceiveId);
    }

    /** send no data*/
    private void sendMessageNoFirebase() {
        String strMessage = mBinding.edtEnterMessage.getText().toString().trim();
        if (TextUtils.isEmpty(strMessage)) {
            return;
        }
        mMessages.add(new Message(strMessage));
        updateUI();
        mBinding.recyclerChat.scrollToPosition(mMessages.size() - 1);
        mBinding.edtEnterMessage.setText("");
    }

    /** check seen status */
    private void checkSeenStatus(final String userIdContact) {
        mReference = mFirebaseDatabase.getReference(ConstantKey.TBL_CHATS);
        mSeenEventListener = mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot o: snapshot.getChildren()) {
                    Message message = o.getValue(Message.class);
                    if (message.getReceiveId().equals(mFirebaseUser.getUid()) && message.getSenderId().equals(userIdContact)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("seenStatus", true);
                        o.getRef().updateChildren(hashMap);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /** send message with data firebase */
    private void sendMessageWithFirebase(String senderId, String receiveId, String msg) {
        mReference = mFirebaseDatabase.getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("senderId", senderId);
        hashMap.put("receiveId", receiveId);
        hashMap.put("message", msg);
        hashMap.put("seenStatus", false);

        mReference.child(ConstantKey.TBL_CHATS).push().setValue(hashMap);

        /** add user to chat fragment */
        DatabaseReference chatRef = mFirebaseDatabase.getReference(ConstantKey.TBL_CHAT_LIST)
                .child(mFirebaseUser.getUid())
                .child(strReceiveId);

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
//                    chatRef.child(STR_ID).setValue(strReceiveId);
                    Map<String, String> map = new HashMap<>();
                    map.put(ConstantKey.STR_ID, strReceiveId);
                    chatRef.setValue(map);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /** get message to push notification */
//        String msgPush = msg;
        mReference = mFirebaseDatabase.getReference(ConstantKey.TBL_USERS).child(mFirebaseUser.getUid());
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (checkNotification == true) {
                    sendNotificationToDevice(receiveId, user.getUsername(), msg);
                }

                checkNotification = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void readMessageWithFirebase(String senderId, String receiveId, String avatarReceive) {
        mMessages = new ArrayList<>();
        mReference = mFirebaseDatabase.getReference(ConstantKey.TBL_CHATS);
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mMessages.clear();
                for (DataSnapshot x: snapshot.getChildren()) {
                    Message message = x.getValue(Message.class);
                    if (message == null) {
                        return;
                    }
                    if (message.getReceiveId().equals(receiveId) && message.getSenderId().equals(senderId) ||
                            message.getReceiveId().equals(senderId) && message.getSenderId().equals(receiveId)) {
                        mMessages.add(message);
                    }

                        mMessageAdapter = new MessageAdapter(MessageActivity.this, mMessages, avatarReceive);
//                        mMessageAdapter = new MessageAdapter(MessageActivity.this, mMessages, strAvatarReceiveUrl);
                        mBinding.recyclerChat.setAdapter(mMessageAdapter);
                    mMessageAdapter.notifyDataSetChanged();
//                    checkReadMessage();
                    checkKeyboard();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void statusAccount(String status) {
        mReference = mFirebaseDatabase.getReference(ConstantKey.TBL_USERS).child(mFirebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        mReference.updateChildren(hashMap);
    }

    /** function push notification */
    private void sendNotificationToDevice(String receiveId, String username, String message) {
        mReference = mFirebaseDatabase.getReference(ConstantKey.TBL_TOKENS);
        Query query = mReference.orderByKey().equalTo(receiveId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot x: snapshot.getChildren()) {
                    Token token = x.getValue(Token.class);
                    Notify notify = new Notify(mFirebaseUser.getUid(),
                            String.valueOf(R.drawable.icon_eye_show),
                            "new message",
                            username + ": " + message,
                            strReceiveId);
                    Sender sender = new Sender(notify, token.getToken());
                    callApiSendNotify(sender);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /** check keyboard -- scroll view pointer at message new */
    private void checkReadMessage() {
        final View rootViewChat = mBinding.activityChatRootView;
        rootViewChat.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                rootViewChat.getWindowVisibleDisplayFrame(rect);
                if (mMessages.size() > 0) {
                    mBinding.recyclerChat.scrollToPosition(mMessages.size() - 1);
                    rootViewChat.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
    }
    private void checkKeyboard() {
        final View rootViewChat = mBinding.activityChatRootView;
        rootViewChat.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                rootViewChat.getWindowVisibleDisplayFrame(rect);
                int heightDiff =rootViewChat.getRootView().getHeight() - rect.height();
                if (heightDiff > 0.25 * rootViewChat.getRootView().getHeight()) {
                    if (mMessages.size() >0) {
                        mBinding.recyclerChat.scrollToPosition(mMessages.size() - 1);
                        rootViewChat.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            }
        });
    }

    @Override
    public void clickMessage(Message message) {
        Toast.makeText(this, "show text: " + message.getMessage(), Toast.LENGTH_SHORT).show();
    }
}