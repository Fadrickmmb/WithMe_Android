package com.example.withme_android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class User_AddPostPage extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private ImageView homeIcon, searchIcon, addPostIcon, smallAvatar, bigAvatar;
    private TextView userName;
    private Button btnPost;
    private EditText etLocation, etContent;

    private User userData;
    private ImageView ivPostImg;
    private Uri postImgUri;
    private StorageReference storageReference;

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

        etLocation = findViewById(R.id.locationName);
        etContent = findViewById(R.id.et_content);
        ivPostImg = findViewById(R.id.iv_add_photo);

        storageReference = FirebaseStorage.getInstance().getReference("posts");

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

        ivPostImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPhoto();
            }
        });

    }

    private void selectPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void createPost() {
        String location = etLocation.getText().toString();
        String content = etContent.getText().toString();

        String postId = java.util.UUID.randomUUID().toString();

        if (postImgUri == null || postImgUri.toString().isEmpty()) {
            Toast.makeText(this, "Please select a photo", Toast.LENGTH_SHORT).show();
            return;
        }

        if (location.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference fileReference = storageReference.child(postId + ".jpg");

        fileReference.putFile(postImgUri).addOnSuccessListener(taskSnapshot -> {
            fileReference.getDownloadUrl().addOnSuccessListener(uri -> {

                Post post = new Post(postId, content, mAuth.getUid(), uri.toString(), new Date().toString(),
                        userData.getName(), location, 0, userData.getUserPhotoUrl(), new HashMap<>());

                Map<String, Post> posts = userData.getPosts();
                if (posts == null) {
                    posts = new HashMap<>();
                }
                posts.put(postId, post);
                userData.setPosts(posts);

                if (userData.getNumberPosts() == null) {
                    //userData.setNumberPosts(1);
                } else {
                    userData.setNumberPosts(userData.getNumberPosts() + 1);
                }

                reference.child(mAuth.getUid()).setValue(userData).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Post created successfully", Toast.LENGTH_SHORT).show();
                        clearFields();
                    } else {
                        Toast.makeText(this, "Failed to create post", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(err -> {
                    Log.e("User_AddPostPage", "createPost: ", err);
                    Toast.makeText(this, "Failed to create post", Toast.LENGTH_SHORT).show();
                });
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
        });
    }

    private void clearFields() {
        etLocation.setText("");
        etContent.setText("");
        ivPostImg.setImageResource(R.drawable.baseline_person_24);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            postImgUri = data.getData();
            ivPostImg.setImageURI(postImgUri);
        }
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