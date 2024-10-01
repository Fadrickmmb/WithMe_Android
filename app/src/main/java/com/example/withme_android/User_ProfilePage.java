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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile_page);

        editProfileBtn= findViewById(R.id.editProfileBtn);
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("users");
        userName = findViewById(R.id.userName);
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
        layoutManager = new LinearLayoutManager(getApplicationContext());
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList);
        personalPostRecView.setAdapter(postAdapter);

        retrieveInfo();
        showPosts();

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
                        String name = userProfile.getName();
                        int nFollowers = userProfile.getNumberFollowers();
                        int nFollowing = userProfile.getNumberFollowing();
                        int nPosts = userProfile.getNumberPosts();

                        String userAvatar = userProfile.getUserPhotoUrl();
                        String bio = userProfile.getUserBio();

                        userName.setText(name);
                        numberOfFollowers.setText(String.valueOf(nFollowers));
                        numberOfFollowing.setText(String.valueOf(nFollowing));
                        numberOfPosts.setText(String.valueOf(nPosts));
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
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(User_ProfilePage.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showPosts() {
        reference.child(mAuth.getUid()).child("posts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    postList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Post post = dataSnapshot.getValue(Post.class);
                        if (post != null) {
                            postList.add(post);
                        }
                    }
                    postAdapter.notifyDataSetChanged();

                    if (postList.isEmpty()) {
                        noPostsMessage.setVisibility(View.VISIBLE);
                        personalPostRecView.setVisibility(View.GONE);
                    } else {
                        noPostsMessage.setVisibility(View.GONE);
                        personalPostRecView.setVisibility(View.VISIBLE);
                    }
                } else {
                    noPostsMessage.setVisibility(View.VISIBLE);
                    personalPostRecView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(User_ProfilePage.this, "Failed to load posts.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
