package com.example.withme_android;

import android.content.Context;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference3 = database.getReference("reminders");
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String postId = reference3.push().getKey();
    private List<Post> postList;
    private Context context;

    public PostAdapter(List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_post_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.postOwnerName.setText(post.getName());
        holder.postLocation.setText(post.getLocation());
        String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(post.getPostDate());
        holder.postDate.setText(formattedDate);
        holder.yummysNumber.setText(String.valueOf(post.getYummys()));

        Glide.with(context)
                .load(post.getUserPhotoUrl())
                .apply(new RequestOptions().placeholder(R.drawable.small_logo).error(R.drawable.baseline_error_outline_24))
                .into(holder.userAvatar);

        Glide.with(context)
                .load(post.getPostImageUrl())
                .apply(new RequestOptions().placeholder(R.drawable.small_logo).error(R.drawable.baseline_error_outline_24))
                .into(holder.postPicture);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView postOwnerName, postLocation, yummysNumber, commentsNumber, postDate;
        ImageView userAvatar, postPicture;
        LinearLayout postMenu;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postOwnerName = itemView.findViewById(R.id.userName);
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
