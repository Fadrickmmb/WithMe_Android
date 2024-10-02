package com.example.withme_android;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> postList;
    private Context context;

    public PostAdapter(Context context,List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_post_item, parent, false);
        return new PostAdapter.PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        Log.d("PostAdapter", "Binding post at position " + position + ": " + post.toString());

        holder.postOwnerName.setText(post.getName());
        holder.postLocation.setText(post.getLocation());
        holder.postDate.setText(post.getPostDate());
        holder.yummysNumber.setText(String.valueOf(post.getYummys()));
        holder.commentsNumber.setText(String.valueOf(post.getCommentNumbers()));

        Glide.with(holder.userAvatar.getContext())
                .load(post.getUserPhotoUrl())
                .apply(new RequestOptions().placeholder(R.drawable.small_logo).error(R.drawable.baseline_error_outline_24))
                .into(holder.userAvatar);

        Glide.with(holder.postPicture.getContext())
                .load(post.getPostImageUrl())
                .apply(new RequestOptions().placeholder(R.drawable.small_logo).error(R.drawable.baseline_error_outline_24))
                .into(holder.postPicture);

        holder.postPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,User_PostView.class);
                intent.putExtra("postId",post.getPostId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (postList != null) {
            return postList.size();
        } else {
            return 0;
        }
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView postOwnerName, postLocation, yummysNumber, commentsNumber, postDate;
        ImageView userAvatar, postPicture;
        LinearLayout postMenu;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postOwnerName = itemView.findViewById(R.id.postOwnerName);
            postLocation = itemView.findViewById(R.id.postLocation);
            yummysNumber = itemView.findViewById(R.id.yummysNumber);
            commentsNumber = itemView.findViewById(R.id.commentsNumber);
            postDate = itemView.findViewById(R.id.postDate);
            userAvatar = itemView.findViewById(R.id.userAvatar);
            postPicture = itemView.findViewById(R.id.postPicture);
            postMenu = itemView.findViewById(R.id.postMenu);
        }
    }
}
