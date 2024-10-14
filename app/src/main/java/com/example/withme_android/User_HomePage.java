package com.example.withme_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User_HomePage extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private ImageView homeIcon, searchIcon, addPostIcon, smallAvatar;
    private TextView noPostsMessage;
    private PostAdapter postAdapter;
    private RecyclerView postRv;
    private Map<String, Post> posts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_home_page);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("users");
        homeIcon = findViewById(R.id.homeIcon);
        searchIcon = findViewById(R.id.searchIcon);
        addPostIcon = findViewById(R.id.addPostIcon);
        smallAvatar = findViewById(R.id.smallAvatar);
        postRv = findViewById(R.id.rv_post);
        noPostsMessage = findViewById(R.id.noPostsMessage);

        retrieveInfo();

        homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_HomePage.this, User_HomePage.class);
                startActivity(intent);
                finish();
            }
        });

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_HomePage.this, User_SearchPage.class);
                startActivity(intent);
                finish();
            }
        });

        addPostIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_HomePage.this, User_AddPostPage.class);
                startActivity(intent);
                finish();
            }
        });

        smallAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_HomePage.this, User_ProfilePage.class);
                startActivity(intent);
                finish();
            }
        });

        loadPosts();
    }

    private void loadPosts() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            reference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User currentUserProfile = snapshot.getValue(User.class);

                    if (currentUserProfile != null && currentUserProfile.getFollowing() != null) {
                        Map<String, Boolean> followingList = (Map<String, Boolean>) currentUserProfile.getFollowing();
                        PostsFromFollowedUsers((List<String>) followingList);
                    } else {
                        noPostsMessage.setVisibility(View.VISIBLE);
                        postRv.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(User_HomePage.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void PostsFromFollowedUsers(List<String> followingList) {
        posts = new HashMap<>();

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);

                    if (user != null && user.getPosts() != null) {
                        if (followingList.contains(user.getId())) {
                            for (DataSnapshot postSnapshot : userSnapshot.child("posts").getChildren()) {
                                Post post = postSnapshot.getValue(Post.class);
                                if (post != null) {
                                    posts.put(postSnapshot.getKey(), post);
                                }
                            }
                        }
                    }
                }

                if (posts.isEmpty()) {
                    noPostsMessage.setVisibility(View.VISIBLE);
                    postRv.setVisibility(View.GONE);
                } else {
                    List<Post> postList = new ArrayList<>(posts.values());
                    postAdapter = new PostAdapter(User_HomePage.this, postList);
                    postRv.setLayoutManager(new LinearLayoutManager(User_HomePage.this));
                    postRv.setAdapter(postAdapter);
                    noPostsMessage.setVisibility(View.GONE);
                    postRv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(User_HomePage.this, "Failed to load posts.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void retrieveInfo() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            reference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User userProfile = snapshot.getValue(User.class);
                    if (userProfile != null) {
                        String userAvatar = userProfile.getUserPhotoUrl();

                        Glide.with(smallAvatar.getContext())
                                .load(userAvatar)
                                .error(R.drawable.round_report_problem_24)
                                .fitCenter()
                                .into(smallAvatar);
                    } else {
                        Toast.makeText(User_HomePage.this, "User profile is null.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(User_HomePage.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
