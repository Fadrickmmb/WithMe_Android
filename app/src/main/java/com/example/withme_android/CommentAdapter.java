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
import com.bumptech.glide.request.RequestOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.PostViewHolder> {
    private List<Comment> commentList;
    private Context context;

    public CommentAdapter(List<Comment> commentList, Context context) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        holder.commentOwnerName.setText(comment.getName());
        holder.commentText.setText(comment.getText());

        if (comment.getDate() != null) {
            String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date(comment.getDate()));
            holder.commentDate.setText(formattedDate);
        } else {
            holder.commentDate.setText("Unknown");
        }

        Glide.with(context)
                .load(comment.getUserAvatar())
                .apply(new RequestOptions().placeholder(R.drawable.small_logo).error(R.drawable.baseline_error_outline_24))
                .into(holder.userAvatar);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView commentOwnerName, commentDate, commentText;
        ImageView userAvatar;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            commentOwnerName = itemView.findViewById(R.id.postOwnerName);
            commentDate = itemView.findViewById(R.id.commentDate);
            userAvatar = itemView.findViewById(R.id.userAvatar);
            commentText = itemView.findViewById(R.id.commentData);
        }
    }
}
