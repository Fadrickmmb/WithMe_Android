package com.example.withme_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class User_AddPostPage extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private ImageView homeIcon, searchIcon, addPostIcon, smallAvatar, bigAvatar;
    private TextView userName;
    private Button btnPost;

    private User userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_add_post_page);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("users");
        homeIcon = findViewById(R.id.homeIcon);
        searchIcon = findViewById(R.id.searchIcon);
        addPostIcon = findViewById(R.id.addPostIcon);
        smallAvatar = findViewById(R.id.smallAvatar);
        bigAvatar = findViewById(R.id.iv_profile);
        userName = findViewById(R.id.tv_user_name);
        btnPost = findViewById(R.id.btn_post);

        retrieveInfo();

        homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_AddPostPage.this, User_HomePage.class);
                startActivity(intent);
                finish();
            }
        });

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_AddPostPage.this, User_SearchPage.class);
                startActivity(intent);
                finish();
            }
        });

        addPostIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_AddPostPage.this, User_AddPostPage.class);
                startActivity(intent);
                finish();
            }
        });

        smallAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_AddPostPage.this, User_ProfilePage.class);
                startActivity(intent);
                finish();
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPost();
            }
        });
    }

    private void createPost() {

    }

    private void retrieveInfo() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            reference.child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User userProfile = snapshot.getValue(User.class);
                    if (userProfile != null) {
                        userData = userProfile;

                        String name = userProfile.getName();
                        String userAvatar = userProfile.getUserPhotoUrl();

                        userName.setText(name);

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
                // hi
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(User_AddPostPage.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}