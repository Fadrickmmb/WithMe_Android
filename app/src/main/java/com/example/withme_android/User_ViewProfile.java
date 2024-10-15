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

public class User_ViewProfile extends AppCompatActivity {

    private Button editProfileBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private TextView userFullName, numberOfFollowers, numberOfPosts, numberOfYummys,userBio;
    private ImageView homeIcon, searchIcon, addPostIcon, smallAvatar, bigAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_view_profile);

        editProfileBtn= findViewById(R.id.followProfileBtn);
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("users");
        userFullName = findViewById(R.id.userFullName);
        numberOfFollowers = findViewById(R.id.numberOfFollowers);
        numberOfPosts = findViewById(R.id.numberOfPosts);
        numberOfYummys = findViewById(R.id.numberOfYummys);
        homeIcon = findViewById(R.id.homeIcon);
        searchIcon = findViewById(R.id.searchIcon);
        addPostIcon = findViewById(R.id.addPostIcon);
        smallAvatar = findViewById(R.id.smallAvatar);
        bigAvatar = findViewById(R.id.bigAvatar);
        userBio = findViewById(R.id.userBio);


        Intent intent = getIntent();
        String uid = intent.getStringExtra("userUid");

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_ViewProfile.this, User_EditProfile.class);
                startActivity(intent);
                finish();
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

        if (uid != null) {
            reference = FirebaseDatabase.getInstance().getReference("users").child(uid);
            retrieveInfo(uid);
        } else {
            Toast.makeText(this, "User UID not found.", Toast.LENGTH_SHORT).show();
        }

    }

    private void retrieveInfo(String uid) {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if (userProfile != null) {
                    String name = userProfile.getName();
                    String nFollowers = userProfile.getNumberFollowers();
                    String nPosts = userProfile.getNumberPosts();
                    String nYummys = userProfile.getNumberYummys();
                    String bio = userProfile.getUserBio();
                    String userAvatar = userProfile.getUserPhotoUrl();

                    userFullName.setText(name);
                    numberOfFollowers.setText(nFollowers);
                    numberOfPosts.setText(nPosts);
                    numberOfYummys.setText(nYummys);
                    userBio.setText(bio);

                    Glide.with(bigAvatar.getContext())
                            .load(userAvatar)
                            .error(R.drawable.round_report_problem_24)  // Error image
                            .fitCenter()
                            .into(bigAvatar);

                    Glide.with(smallAvatar.getContext())
                            .load(userAvatar)
                            .error(R.drawable.round_report_problem_24)  // Error image
                            .fitCenter()
                            .into(smallAvatar);
                } else {
                    Toast.makeText(User_ViewProfile.this, "User profile data is unavailable.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(User_ViewProfile.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}