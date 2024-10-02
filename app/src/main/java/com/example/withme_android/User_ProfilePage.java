package com.example.withme_android;

import static com.google.firebase.appcheck.internal.util.Logger.TAG;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.List;
import java.util.Map;

public class User_ProfilePage extends AppCompatActivity {

    private Button editProfileBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private TextView userName, numberOfFollowers, numberOfPosts, numberOfFollowing,userBio,noPostsMessage;
    private ImageView homeIcon, searchIcon, addPostIcon, smallAvatar, bigAvatar;
    private List<Post> postList;
    private PostAdapter postAdapter;
    private RecyclerView personalPostRecView;
    private LinearLayoutManager layoutManager;
    private LinearLayout followersLayout,followingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile_page);

        editProfileBtn = findViewById(R.id.editProfileBtn);
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("users");
        userName = findViewById(R.id.userName);
        followersLayout = findViewById(R.id.followersLayout);
        followingLayout = findViewById(R.id.followingLayout);
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
        personalPostRecView = findViewById(R.id.personalPostRecView);

        layoutManager = new LinearLayoutManager(this);
        personalPostRecView.setLayoutManager(layoutManager);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList);
        personalPostRecView.setAdapter(postAdapter);

        retrieveInfo();

        followersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_ProfilePage.this, User_Followers.class);
                startActivity(intent);
                finish();
            }
        });

        followingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_ProfilePage.this, User_Following.class);
                startActivity(intent);
                finish();
            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_ProfilePage.this, User_EditProfile.class);
                startActivity(intent);
                finish();
            }
        });

        homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_ProfilePage.this, User_HomePage.class);
                startActivity(intent);
                finish();
            }
        });

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_ProfilePage.this, User_SearchPage.class);
                startActivity(intent);
                finish();
            }
        });

        addPostIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_ProfilePage.this, User_AddPostPage.class);
                startActivity(intent);
                finish();
            }
        });

        smallAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_ProfilePage.this, User_ProfilePage.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void retrieveInfo() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            reference.child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User userProfile = snapshot.getValue(User.class);
                    if (userProfile != null) {
                        Log.d("UserProfile", "User profile retrieved: " + userProfile.toString());
                        String name = userProfile.getName();
                        Long nFollowers = userProfile.getNumberFollowers();
                        Long nFollowing = userProfile.getNumberFollowing();

                        String userAvatar = userProfile.getUserPhotoUrl();
                        String bio = userProfile.getUserBio();

                        userName.setText(name);
                        numberOfFollowers.setText(String.valueOf(nFollowers));
                        numberOfFollowing.setText(String.valueOf(nFollowing));
                        userBio.setText(bio);

                        Glide.with(bigAvatar.getContext())
                                .load(userAvatar)
                                .error(R.drawable.round_report_problem_24)
                                .fitCenter()
                                .into(bigAvatar);

                        Glide.with(smallAvatar.getContext())
                                .load(userAvatar)
                                .error(R.drawable.round_report_problem_24)
                                .fitCenter()
                                .into(smallAvatar);

                        Map<String, Post> postsMap = userProfile.getPosts();
                        Log.d("UserProfile", "Posts Map: " + postsMap);

                        if (postsMap != null && !postsMap.isEmpty()) {
                            postList.clear();
                            postList.addAll(postsMap.values());
                            postAdapter.notifyDataSetChanged();

                            numberOfPosts.setText(String.valueOf(postList.size()));
                            noPostsMessage.setVisibility(View.GONE);
                            personalPostRecView.setVisibility(View.VISIBLE);
                        } else {
                            numberOfPosts.setText("0");
                            noPostsMessage.setVisibility(View.VISIBLE);
                            personalPostRecView.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(User_ProfilePage.this, "User data not found.", Toast.LENGTH_SHORT).show();
                        Log.e("UserProfile", "User profile is null.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(User_ProfilePage.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                    Log.e("User_ProfilePage", "onCancelled: ", error.toException());
                }
            });
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            Log.e("User_ProfilePage", "User is null.");
        }
    }
}