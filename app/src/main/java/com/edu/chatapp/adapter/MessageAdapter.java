package com.edu.chatapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.edu.chatapp.R;
import com.edu.chatapp.iclick.IClickMessage;
import com.edu.chatapp.model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private final List<Message> mMessages;
    private Context context;
    private IClickMessage iClickMessage;
    final private String strAvatarReceiveUrl;

//    public MessageAdapter(List<Message> mMessages, IClickMessage iClickMessage, String strAvatarReceiveUrl) {
//        this.mMessages = mMessages;
//        this.iClickMessage = iClickMessage;
//        this.strAvatarReceiveUrl = strAvatarReceiveUrl;
//    }

    public MessageAdapter(Context context, List<Message> mMessages, String strAvatarReceiveUrl) {
        this.context = context;
        this.mMessages = mMessages;
        this.strAvatarReceiveUrl = strAvatarReceiveUrl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView;
        if (viewType == MSG_TYPE_RIGHT) {
            itemView = layoutInflater.inflate(R.layout.item_row_message_right, parent, false);
        } else {
            itemView = layoutInflater.inflate(R.layout.item_row_message_left, parent, false);
        }
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Message message = mMessages.get(position);
        if (message == null) {
            return;
        }

        holder.txtMessage.setText(message.getMessage());

        /** check for last message */
        if (position == mMessages.size() - 1) {
            if (message.isSeenStatus()) {
                holder.txtSeenStatus.setText("seen");
            } else {
                holder.txtSeenStatus.setText("delivered");
            }
        } else {
            holder.txtSeenStatus.setVisibility(View.GONE);
        }


        if (!strAvatarReceiveUrl.equals("")) {
            Picasso.with(context).load(strAvatarReceiveUrl)
                    .placeholder(R.drawable.icon_eye_show)
                    .error(R.drawable.icon_eye_hide)
                    .into(holder.imgAvatarLeft);
        } else {
            holder.imgAvatarLeft.setImageResource(R.mipmap.img_logo_shop);
        }



        holder.txtMessage.setOnClickListener(v -> iClickMessage.clickMessage(message));
    }

    @Override
    public int getItemCount() {
        if (mMessages != null) {
            return mMessages.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessage, txtSeenStatus;
        ImageView imgAvatarLeft;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.txt_message);
            txtSeenStatus = itemView.findViewById(R.id.txt_seen_status);
            imgAvatarLeft = itemView.findViewById(R.id.img_avatar_receive);
        }
    }

    /** check layout view */
    @Override
    public int getItemViewType(int position) {
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mFirebaseUser == null) {
            return MSG_TYPE_LEFT;
        }
        if (mMessages.get(position).getSenderId().equals(mFirebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
