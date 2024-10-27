package com.example.withme_android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Objects;

public class Admin_HomePage extends AppCompatActivity {

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
        setContentView(R.layout.activity_admin_home_page);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("admin");
        homeIcon = findViewById(R.id.homeIcon);
        searchIcon = findViewById(R.id.searchIcon);
        addPostIcon = findViewById(R.id.addPostIcon);
        smallAvatar = findViewById(R.id.smallAvatar);
        postRv = findViewById(R.id.rv_post);
        noPostsMessage = findViewById(R.id.noPostsMessage); // Adicione a view para exibir mensagens quando não houver posts

        retrieveInfo();

        homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin_HomePage.this, Admin_HomePage.class);
                startActivity(intent);
                finish();
            }
        });

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin_HomePage.this, Admin_SearchPage.class);
                startActivity(intent);
                finish();
            }
        });

        addPostIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin_HomePage.this, Admin_AddPostPage.class);
                startActivity(intent);
                finish();
            }
        });

        smallAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin_HomePage.this, Admin_ProfilePage.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadPosts(Map<String, Boolean> followers) {
        posts = new HashMap<>();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    // handle errors
                    try {
                        User user = userSnapshot.getValue(User.class);
                        if (user != null && user.getPosts() != null) {
                            if ((followers != null && followers.containsKey(user.getId())) ||
                                    Objects.equals(mAuth.getUid(), user.getId())) {
                                for (Map.Entry<String, Post> entry : user.getPosts().entrySet()) {
                                    String postId = entry.getKey();
                                    Post post = entry.getValue();
                                    posts.put(postId, post);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("Err", "onDataChange: ", e);
                    }
                }
                List<Post> postList = new ArrayList<>(posts.values());
                postAdapter = new PostAdapter(Admin_HomePage.this, postList);
                postRv.setLayoutManager(new LinearLayoutManager(Admin_HomePage.this));
                postRv.setAdapter(postAdapter);

                if (posts.isEmpty()) {
                    noPostsMessage.setVisibility(View.VISIBLE);
                    postRv.setVisibility(View.GONE);
                } else {
                    postAdapter = new PostAdapter(Admin_HomePage.this, postList);
                    postRv.setLayoutManager(new LinearLayoutManager(Admin_HomePage.this));
                    postRv.setAdapter(postAdapter);
                    noPostsMessage.setVisibility(View.GONE);
                    postRv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Admin_HomePage.this, "Failed to load posts.", Toast.LENGTH_SHORT).show();
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

                        loadPosts(userProfile.getFollowers());

                        Glide.with(smallAvatar.getContext())
                                .load(userAvatar)
                                .error(R.drawable.round_report_problem_24)
                                .fitCenter()
                                .into(smallAvatar);
                    } else {
                        Toast.makeText(Admin_HomePage.this, "Admin profile is null.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Admin_HomePage.this, "Failed to load admin data.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
