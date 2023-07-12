package com.edu.chatapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.edu.chatapp.R;
import com.edu.chatapp.iclick.IClickChat;
import com.edu.chatapp.model.Message;
import com.edu.chatapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    public static final String TBL_CHATS = "chats";
    public static final String STR_DEFAULT_LAST_MSG = "default";
    public static String STR_STATUS_MSG = "da xem";
    private List<User> users;
    private Context context;
    private IClickChat iClickChat;
    private boolean isChatStatus;
    private String mLastMessage;
    private List<Message> messageList;

    public ChatAdapter(List<User> users, Context context, boolean isChatStatus) {
        this.users = users;
        this.context = context;
        this.isChatStatus = isChatStatus;
    }
    public ChatAdapter(List<User> users, Context context, IClickChat iClickChat) {
        this.users = users;
        this.context = context;
        this.iClickChat = iClickChat;
    }

    public ChatAdapter(List<User> users, Context context, boolean isChatStatus, IClickChat iClickChat) {
        this.users = users;
        this.context = context;
        this.isChatStatus = isChatStatus;
        this.iClickChat = iClickChat;
        this.messageList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_row_chat, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        User user = users.get(position);
        if (user == null) {
            return;
        }

        holder.txtName.setText(user.getUsername());
//        holder.txtMessageTime.setText("time: " + position + " min");

        /** status on off */
        if (isChatStatus) {
            holder.imgStatus.setVisibility(View.VISIBLE);
            if (user.getStatus().equals("online")) {
                holder.imgStatus.setImageResource(R.drawable.icon_online);
            } else {
                holder.imgStatus.setImageResource(R.drawable.icon_offline);
            }
        } else {
            holder.imgStatus.setVisibility(View.GONE);
        }
        /** last message */
        if (isChatStatus) {
            getLastMessage(user.getId(), holder.txtLastMessage);
//            getLastMessage(user.getId(), holder.txtLastMessage, holder.txtMessageStatus);
        } else {
            holder.txtLastMessage.setVisibility(View.GONE);
        }

        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        System.out.println("11a: " + STR_STATUS_MSG);
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

        if (user.getAvatarUrl() != null) {
            Picasso.with(context).load(user.getAvatarUrl())
                    .placeholder(R.drawable.icon_eye_show)
                    .error(R.drawable.icon_eye_hide)
                    .into(holder.imgAvatar);
        } else {
            holder.imgAvatar.setImageResource(R.mipmap.img_logo_shop);
        }

        holder.imgAvatar.setOnClickListener(v -> iClickChat.clickChat(user));
    }

    @Override
    public int getItemCount() {
        if (users != null) {
            return users.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtLastMessage, txtMessageTime, txtMessageStatus;
        ImageView imgAvatar, imgStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txt_name);
            txtLastMessage = itemView.findViewById(R.id.txt_last_message);
            txtMessageTime = itemView.findViewById(R.id.txt_message_time);
            txtMessageStatus = itemView.findViewById(R.id.txt_message_status);
            imgAvatar = itemView.findViewById(R.id.img_avatar);
            imgStatus = itemView.findViewById(R.id.img_status);
        }
    }

    /** get last message*/
    private void getLastMessage(final String userId, final TextView txtLastMsg){
        mLastMessage = STR_DEFAULT_LAST_MSG;
//        STR_STATUS_MSG = STR_STATUS_MSG_NO_STATUS;
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            return;
        }
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(TBL_CHATS);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot x: snapshot.getChildren()) {
                    Message message = x.getValue(Message.class);
                    messageList.add(message);
                    /** check consider
                     * A send message to B
                     * show last message on B
                     * A ReceiveId == B id (id login === id firebase)
                     * A senderId == A user's id clicked (id's user A on chat list show)
                     * show last message on A --> opposite show B
                     */
                    if (message.getReceiveId().equals(firebaseUser.getUid()) && message.getSenderId().equals(userId) || //show b
                            message.getReceiveId().equals(userId) && message.getSenderId().equals(firebaseUser.getUid())) { // show A

                        mLastMessage = message.getMessage();
                    }
                }
                /** check display last message */
                switch (mLastMessage) {
                    case STR_DEFAULT_LAST_MSG:
                        txtLastMsg.setText("No message");
                        break;
                    default:
                        txtLastMsg.setText(mLastMessage);
                        break;
                }
                mLastMessage = STR_DEFAULT_LAST_MSG;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
