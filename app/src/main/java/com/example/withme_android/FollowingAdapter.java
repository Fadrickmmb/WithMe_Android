package com.example.withme_android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.FollowerViewHolder> {
    private List<Follower> followingList;

    public FollowingAdapter(List<Follower> followerList) {
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
        Glide.with(holder.profileImageView.getContext())
                .load(follower.getUserPhotoUrl())
                .placeholder(R.drawable.baseline_person_24)
                .into(holder.profileImageView);
    }

    @Override
    public int getItemCount() {
        return followingList != null ? followingList.size() : 0;
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
