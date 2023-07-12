package com.edu.chatapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.edu.chatapp.R;
import com.edu.chatapp.iclick.IClickContacts;
import com.edu.chatapp.model.Chat;
import com.edu.chatapp.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    private List<User> users;
    private Context context;
    private IClickContacts iClickContacts;
    private boolean isChatStatus;

    public ContactsAdapter(List<User> users, Context context, IClickContacts iClickContacts, boolean isChatStatus) {
        this.users = users;
        this.context = context;
        this.iClickContacts = iClickContacts;
        this.isChatStatus = isChatStatus;
    }

    @NonNull
    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_row_contacts, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsAdapter.ViewHolder holder, int position) {
        User user = users.get(position);
        if (user == null) {
            return;
        }

        holder.txtUsername.setText(user.getUsername());
        holder.txtEmail.setText(user.getEmail());

        if (isChatStatus) {
            holder.imgStatus.setVisibility(View.VISIBLE);
            if (user.getStatus().equals("online")) {
                holder.imgStatus.setImageResource(R.drawable.icon_online);
//                holder.imgStatus.setImageResource(R.drawable.icon_eye_show);
            } else {
                holder.imgStatus.setImageResource(R.drawable.icon_offline);
//                holder.imgStatus.setImageResource(R.drawable.icon_eye_hide);
            }
        } else {
            holder.imgStatus.setVisibility(View.GONE);
        }

        if (user.getAvatarUrl() != null) {
            Picasso.with(context).load(user.getAvatarUrl())
                    .placeholder(R.drawable.icon_eye_show)
                    .error(R.drawable.icon_eye_hide)
                    .into(holder.imgAvatar);
        } else {
            holder.imgAvatar.setImageResource(R.mipmap.img_logo_shop);
        }

        holder.imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickContacts.clickContacts(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (users != null) {
            return users.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtUsername, txtEmail;
        ImageView imgAvatar, imgStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUsername = itemView.findViewById(R.id.txt_user_name);
            txtEmail = itemView.findViewById(R.id.txt_email);
            imgAvatar = itemView.findViewById(R.id.img_avatar);
            imgStatus = itemView.findViewById(R.id.img_status);
        }
    }
}
