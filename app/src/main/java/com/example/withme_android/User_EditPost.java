package com.example.withme_android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class User_EditPost extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView homeIcon, searchIcon, addPostIcon, smallAvatar,editedPicture;
    private EditText newLocation,  newContent;
    private FirebaseAuth mAuth;
    private DatabaseReference reference,postreference;
    private StorageReference storageReference;
    private String postId,userId;
    private Button cancelEditBtn, saveEditBtn, newPostPicture;
    private Uri imageUri;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_edit_post);

        cancelEditBtn = findViewById(R.id.cancelEditBtn);
        saveEditBtn = findViewById(R.id.saveEditBtn);
        homeIcon = findViewById(R.id.homeIcon);
        searchIcon = findViewById(R.id.searchIcon);
        addPostIcon = findViewById(R.id.addPostIcon);
        smallAvatar = findViewById(R.id.smallAvatar);
        newLocation = findViewById(R.id.newLocation);
        newPostPicture = findViewById(R.id.newPostPicture);
        newContent = findViewById(R.id.newContent);
        editedPicture = findViewById(R.id.editedPicture);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("users");
        postreference = reference.child(mAuth.getUid()).child("posts");
        storageReference = FirebaseStorage.getInstance().getReference();
        postId = getIntent().getStringExtra("postId");

        retrieveInfo();
        retrievePostInfo(postId);
        editPost(postId);

        homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_EditPost.this, User_HomePage.class);
                startActivity(intent);
                finish();
            }
        });

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_EditPost.this, User_SearchPage.class);
                startActivity(intent);
                finish();
            }
        });

        addPostIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_EditPost.this, User_AddPostPage.class);
                startActivity(intent);
                finish();
            }
        });

        smallAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_EditPost.this, User_ProfilePage.class);
                startActivity(intent);
                finish();
            }
        });

        cancelEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void editPost(String postId) {

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
                        Toast.makeText(User_EditPost.this, "User profile is null.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(User_EditPost.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void retrievePostInfo(String postId) {
        postreference.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 if (snapshot.exists()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post != null) {
                        String location = post.getLocation();
                        String content = post.getContent();
                        String picture = post.getPostImageUrl();
                        newContent.setText(content);
                        newLocation.setText(location);

                        Glide.with(editedPicture.getContext())
                                .load(picture)
                                .error(R.drawable.round_report_problem_24)
                                .fitCenter()
                                .into(editedPicture);

                        saveEditBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                postreference.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Post post = snapshot.getValue(Post.class);
                                        if(post != null){
                                            String newloc = newLocation.getText().toString();
                                            String newcont = newContent.getText().toString();
                                            if(!newloc.equals(location)){
                                                postreference.child(postId).child("location").setValue(newloc);
                                                newLocation.setText(newloc);
                                            }
                                            if(!newcont.equals(content)){
                                                postreference.child(postId).child("content").setValue(newcont);
                                                newContent.setText(newcont);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(User_EditPost.this, "Error saving post.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                finish();
                            }
                        });
                    } else {
                        Log.d("RetrieveEditPostInfo", "Post not found.");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}