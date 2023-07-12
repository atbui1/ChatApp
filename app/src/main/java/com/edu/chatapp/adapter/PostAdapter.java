package com.edu.chatapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.edu.chatapp.R;
import com.edu.chatapp.iclick.IClickContacts;
import com.edu.chatapp.iclick.IClickPost;
import com.edu.chatapp.model.Post;
import com.edu.chatapp.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private List<Post> mPosts;
    private Context context;
    private IClickPost iClickPost;
//    private boolean isChatStatus;

    public PostAdapter(List<Post> mPosts, Context context) {
        this.mPosts = mPosts;
        this.context = context;
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_row_post, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
        Post post = mPosts.get(position);
        if (post == null) {
            return;
        }

        holder.txtUsername.setText(post.getpUsername());
        holder.txtTime.setText(post.getpTime());
//        holder.txtLikes.setText(post.getpLike());
//        holder.txtComments.setText(post.getpComment() + " comments");
        holder.txtLikes.setText("111");
        holder.txtComments.setText("222 comments");
        holder.txtTitle.setText(post.getpTitle());


//        if (isChatStatus) {
//            holder.imgStatus.setVisibility(View.VISIBLE);
//            if (user.getStatus().equals("online")) {
//                holder.imgStatus.setImageResource(R.drawable.icon_online);
//            } else {
//                holder.imgStatus.setImageResource(R.drawable.icon_offline);
//            }
//        } else {
//            holder.imgStatus.setVisibility(View.GONE);
//        }

//        if (user.getAvatarUrl() != null) {
//            Picasso.with(context).load(user.getAvatarUrl())
//                    .placeholder(R.drawable.icon_eye_show)
//                    .error(R.drawable.icon_eye_hide)
//                    .into(holder.imgAvatar);
//        } else {
//            holder.imgAvatar.setImageResource(R.mipmap.img_logo_shop);
//        }


        if (post.getpAvatar() != null) {
            Picasso.with(context).load(post.getpAvatar())
                    .placeholder(R.drawable.icon_eye_show)
                    .error(R.drawable.icon_love)
                    .into(holder.imgAvatar);
        } else {
            holder.imgAvatar.setImageResource(R.mipmap.img_logo_shop);
        }

        holder.imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                iClickPost.clickUser(post);
                Toast.makeText(context, "post username: " + post.getpUsername(), Toast.LENGTH_SHORT).show();
            }
        });

        if (post.getpImage() != null) {
            Picasso.with(context).load(post.getpImage())
                    .placeholder(R.drawable.image_load_default)
                    .error(R.drawable.icon_eye_hide)
                    .into(holder.imgPostPicture);
        } else {
            holder.imgAvatar.setImageResource(R.mipmap.img_logo_shop);
        }

        holder.imgPostPicture.setOnClickListener(v -> {
            Toast.makeText(context, "this is picTure: " + post.getpImage(), Toast.LENGTH_SHORT).show();
        });

        holder.lnlPostOption.setOnClickListener(v -> {
            Toast.makeText(context, "this is lnl post Option", Toast.LENGTH_SHORT).show();
        });

        holder.imgLikeDefault.setOnClickListener(v -> {
            Toast.makeText(context, "like default -> active", Toast.LENGTH_SHORT).show();
            holder.imgLikeDefault.setVisibility(View.GONE);
            holder.imgLikeActive.setVisibility(View.VISIBLE);
        });
        holder.imgLikeActive.setOnClickListener(v -> {
            Toast.makeText(context, "like active -> default", Toast.LENGTH_SHORT).show();
            holder.imgLikeDefault.setVisibility(View.VISIBLE);
            holder.imgLikeActive.setVisibility(View.GONE);
        });
        holder.imgComment.setOnClickListener(v -> {
            Toast.makeText(context, "this is comment", Toast.LENGTH_SHORT).show();
        });
        holder.imgShare.setOnClickListener(v -> {
            Toast.makeText(context, "this is comment", Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    public int getItemCount() {
        if (mPosts != null) {
            return mPosts.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtUsername, txtTitle, txtTime, txtLikes, txtComments;
        ImageView imgAvatar, imgStatus, imgPostPicture, imgLikeDefault, imgLikeActive, imgComment, imgShare;
        LinearLayout lnlPostOption;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUsername = itemView.findViewById(R.id.txt_user_name);
            txtTitle = itemView.findViewById(R.id.txt_post_title);
            txtTime = itemView.findViewById(R.id.txt_post_time);
            txtLikes = itemView.findViewById(R.id.txt_post_like_quantity);
            txtComments = itemView.findViewById(R.id.txt_post_comment_quantity);

            imgAvatar = itemView.findViewById(R.id.img_avatar);
            imgStatus = itemView.findViewById(R.id.img_status);
            imgPostPicture = itemView.findViewById(R.id.img_post_picture);
            imgLikeDefault = itemView.findViewById(R.id.img_like_default);
            imgLikeActive = itemView.findViewById(R.id.img_like_active);
            imgComment = itemView.findViewById(R.id.img_comment);
            imgShare = itemView.findViewById(R.id.img_share);

            lnlPostOption = itemView.findViewById(R.id.lnl_post_option);

        }
    }
}
