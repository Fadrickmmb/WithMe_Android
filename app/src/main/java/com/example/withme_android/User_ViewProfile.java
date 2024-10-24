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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class User_ViewProfile extends AppCompatActivity {

    private Button followProfileBtn, backProfileBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference reference, currUserRef, visUserRef;
    private TextView userFullName, numberOfFollowers, numberOfPosts, numberOfFollowing,userBio,noPostsMessage;
    private ImageView homeIcon, searchIcon, addPostIcon, smallAvatar, bigAvatar;
    private List<Post> postList;
    private PostAdapter postAdapter;
    private RecyclerView visitedPostRecView;
    private LinearLayoutManager layoutManager;
    private String currentUserId,visitedUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_view_profile);

        backProfileBtn = findViewById(R.id.backProfileBtn);
        followProfileBtn = findViewById(R.id.followProfileBtn);
        mAuth = FirebaseAuth.getInstance();
        userFullName = findViewById(R.id.userFullName);
        numberOfFollowers = findViewById(R.id.numberOfFollowers);
        numberOfPosts = findViewById(R.id.numberOfPosts);
        numberOfFollowing = findViewById(R.id.numberOfFollowing);
        homeIcon = findViewById(R.id.homeIcon);
        noPostsMessage = findViewById(R.id.noPostsMessage);
        searchIcon = findViewById(R.id.searchIcon);
        addPostIcon = findViewById(R.id.addPostIcon);
        smallAvatar = findViewById(R.id.smallAvatar);
        bigAvatar = findViewById(R.id.bigAvatar);
        userBio = findViewById(R.id.userBio);
        reference = FirebaseDatabase.getInstance().getReference("users");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        currUserRef = reference.child(currentUserId);

        visitedPostRecView = findViewById(R.id.visitedPostRecView);

        layoutManager = new LinearLayoutManager(this);
        visitedPostRecView.setLayoutManager(layoutManager);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(this,postList);
        visitedPostRecView.setAdapter(postAdapter);

        retrieveInfo(currentUserId);

        visitedUserId = getIntent().getStringExtra("visitedUserId");
        if(visitedUserId != null) {
            visUserRef = reference.child(visitedUserId);
            retrieveVisitedInfo(visitedUserId);
            checkFollowStatus();
        } else {
            Toast.makeText(User_ViewProfile.this,"Error loading user profile.", Toast.LENGTH_SHORT).show();
        }

        backProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        followProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFollowStatus();
            }
        });

        homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_ViewProfile.this, User_HomePage.class);
                startActivity(intent);
                finish();
            }
        });

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_ViewProfile.this, User_SearchPage.class);
                startActivity(intent);
                finish();
            }
        });

        addPostIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_ViewProfile.this, User_AddPostPage.class);
                startActivity(intent);
                finish();
            }
        });

        smallAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_ViewProfile.this, User_ProfilePage.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void retrieveVisitedInfo(String visitedUserId) {
        visUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User visitedUser = snapshot.getValue(User.class);
                if(visitedUser !=  null) {
                    String name = visitedUser.getName();
                    Map<String, Boolean> followers = visitedUser.getFollowers();
                    Map<String, Boolean> following = visitedUser.getFollowing();

                    String userAvatar = visitedUser.getUserPhotoUrl();
                    String bio = visitedUser.getUserBio();

                    userFullName.setText(name);
                    if(followers != null){
                        numberOfFollowers.setText(String.valueOf(followers.size()));
                    } else {
                        numberOfFollowers.setText("0");
                    }
                    if(following != null){
                        numberOfFollowing.setText(String.valueOf(following.size()));
                    } else {
                        numberOfFollowing.setText("0");
                    }
                    userBio.setText(bio);

                    if (userAvatar != null && !userAvatar.isEmpty()) {
                        Glide.with(bigAvatar.getContext())
                                .load(userAvatar)
                                .error(R.drawable.baseline_person_24)
                                .fitCenter()
                                .into(bigAvatar);
                    } else {
                        bigAvatar.setImageResource(R.drawable.baseline_person_24);
                    }
                    // When I am not seeing my own posts, i need to call it in another method.
                    retrieveVisitedUserPosts(visitedUserId);
                } else {
                    Toast.makeText(User_ViewProfile.this, "User data not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(User_ViewProfile.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void retrieveVisitedUserPosts(String visitedUserId){
        DatabaseReference postsReference = FirebaseDatabase.getInstance().getReference("users").child(visitedUserId).child("posts");
        postsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.hasChildren()){
                    postList.clear();
                    for(DataSnapshot postSnapshot : snapshot.getChildren()){
                        Post post = postSnapshot.getValue(Post.class);
                        if(post != null){
                            postList.add(post);
                        }
                    }
                    if (!postList.isEmpty()) {
                        postAdapter.notifyDataSetChanged();
                        numberOfPosts.setText(String.valueOf(postList.size()));
                        noPostsMessage.setVisibility(View.GONE);
                        visitedPostRecView.setVisibility(View.VISIBLE);
                    } else {
                        numberOfPosts.setText("0");
                        noPostsMessage.setVisibility(View.VISIBLE);
                        visitedPostRecView.setVisibility(View.GONE);
                    }
                } else {
                    numberOfPosts.setText("0");
                    noPostsMessage.setVisibility(View.VISIBLE);
                    visitedPostRecView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void checkFollowStatus() {
        currUserRef.child("following").child(visitedUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            followProfileBtn.setText("Unfollow");
                        } else {
                            followProfileBtn.setText("Follow");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void changeFollowStatus(){
        DatabaseReference followingReference = currUserRef.child("following").child(visitedUserId);
        DatabaseReference followersReference = visUserRef.child("followers").child(currentUserId);

        followingReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    followingReference.removeValue();
                    followersReference.removeValue();
                    followProfileBtn.setText("Follow");
                } else {
                    followingReference.setValue(true);
                    followersReference.setValue(true);
                    followProfileBtn.setText("Unfollow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void retrieveInfo(String currentUserId) {
        currUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User visitedUser = snapshot.getValue(User.class);
                if(visitedUser !=  null) {
                    String userAvatar = visitedUser.getUserPhotoUrl();

                    Glide.with(smallAvatar.getContext())
                            .load(userAvatar)
                            .error(R.drawable.round_report_problem_24)
                            .fitCenter()
                            .into(smallAvatar);
                } else {
                    Toast.makeText(User_ViewProfile.this, "User data not found.", Toast.LENGTH_SHORT).show();
                    Log.e("UserProfile", "User profile is null.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(User_ViewProfile.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}