package com.example.withme_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostRvAdapter extends RecyclerView.Adapter<PostRvAdapter.PostViewHolder> {

    private List<Post> postList;
    private Context context;

    // Constructor
    public PostRvAdapter(List<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_child_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);

        // Load the image using Glide
        Glide.with(context)
                .load(post.getImgUrl())
                .into(holder.imgPost);

        holder.tvDescription.setText(post.getDescription());
        holder.tvLikes.setText(post.getLikes() + " Yummy(s)");
        holder.tvComments.setText(post.getComments() + " Comments");
        holder.tvLocation.setText(post.getLocation());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    // ViewHolder class
    public static class PostViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ivProfileImage;
        ImageView imgPost;
        TextView tvDescription;
        TextView tvLikes;
        TextView tvComments;
        TextView tvLocation;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.iv_profile_image);
            imgPost = itemView.findViewById(R.id.iv_post_image);
            tvDescription = itemView.findViewById(R.id.tv_post_description);
            tvLikes = itemView.findViewById(R.id.tv_yummys_count);
            tvComments = itemView.findViewById(R.id.tv_comments_count);
            tvLocation = itemView.findViewById(R.id.tv_location_name);
        }
    }
}
