package com.example.withme_android;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.FollowerViewHolder> {
    private Context context;
    private List<Follower> followingList;

    public FollowingAdapter(Context context, List<Follower> followerList) {
        this.context = context;
        this.followingList = followerList;
    }

    @Override
    public FollowerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.following_item, parent, false);
        return new FollowerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FollowerViewHolder holder, int position) {
        Follower follower = followingList.get(position);
        holder.nameTextView.setText(follower.getName());
        String photoUrl = follower.getUserPhotoUrl();

        if(photoUrl != null && !photoUrl.isEmpty()) {
            Glide.with(holder.profileImageView.getContext())
                    .load(photoUrl)
                    .placeholder(R.drawable.baseline_person_24)
                    .into(holder.profileImageView);
        } else {
            holder.profileImageView.setImageResource(R.drawable.baseline_person_24);
        }

        holder.nameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String visited = follower.getId();
                Intent intent = new Intent(context, User_ViewProfile.class);
                intent.putExtra("visitedUserId",visited);
                context.startActivity(intent);

            }
        });

        holder.profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String visited = follower.getId();
                Intent intent = new Intent(context, User_ViewProfile.class);
                intent.putExtra("visitedUserId",visited);
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        if(followingList != null) {
            return followingList.size();
        } else {
            return 0;
        }
    }

    public static class FollowerViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public ImageView profileImageView;

        public FollowerViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.followerName);
            profileImageView = itemView.findViewById(R.id.followerProfileImage);
        }
    }
}
