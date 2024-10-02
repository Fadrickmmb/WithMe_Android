package com.example.withme_android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class User_Followers extends AppCompatActivity {
    private String currentUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private ImageView homeIcon, searchIcon, addPostIcon, smallAvatar, backArrow;
    private RecyclerView followersRecView;
    private FollowerAdapter followerAdapter;
    private List<Follower> followerList;
    private static final String TAG = "User_FollowersActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_followers);

        homeIcon = findViewById(R.id.homeIcon);
        searchIcon = findViewById(R.id.searchIcon);
        addPostIcon = findViewById(R.id.addPostIcon);
        smallAvatar = findViewById(R.id.smallAvatar);
        backArrow = findViewById(R.id.backArrow);
        followersRecView = findViewById(R.id.followersRecView);
        followersRecView.setLayoutManager(new LinearLayoutManager(this));

        followerList = new ArrayList<>();
        followerAdapter = new FollowerAdapter(followerList);
        followersRecView.setAdapter(followerAdapter);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("users");
        currentUserId = mAuth.getCurrentUser().getUid();

        loadFollowers();



        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_Followers.this, User_ProfilePage.class);
                startActivity(intent);
                finish();
            }
        });

        homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_Followers.this, User_HomePage.class);
                startActivity(intent);
                finish();
            }
        });

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_Followers.this, User_SearchPage.class);
                startActivity(intent);
                finish();
            }
        });

        addPostIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_Followers.this, User_AddPostPage.class);
                startActivity(intent);
                finish();
            }
        });

        smallAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_Followers.this, User_ProfilePage.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadFollowers() {
        reference.child(currentUserId).child("followers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followerList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot followerSnapshot : snapshot.getChildren()) {
                        String followerId = followerSnapshot.getKey();
                        getFollowerDetails(followerId);
                    }
                } else {
                    Toast.makeText(User_Followers.this, "No followers found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(User_Followers.this, "Failed to load followers.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "loadFollowers:onCancelled", error.toException());
            }
        });
    }

    private void getFollowerDetails(String followerId) {
        reference.child(followerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User followerUser = snapshot.getValue(User.class);
                if (followerUser != null) {
                    Follower follower = new Follower(followerId, followerUser.getName(), followerUser.getUserPhotoUrl());
                    followerList.add(follower);
                    followerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "getFollowerDetails:onCancelled", error.toException());
            }
        });
    }
}