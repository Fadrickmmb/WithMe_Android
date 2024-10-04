package com.example.withme_android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import java.util.Date;
import java.util.List;

public class Post_CommentPage extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private Button btnAddComment;
    private CommentAdapter commentAdapter;
    private RecyclerView commentsRv;
    private List<Comment> comments;
    private User userData;
    private ImageView homeIcon, searchIcon, addPostIcon, smallAvatar;
    private EditText etComment;
    private String postId;
    private String postOwnerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comment_page);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("users");
        btnAddComment = findViewById(R.id.send);
        commentsRv = findViewById(R.id.rv_comment);
        etComment = findViewById(R.id.etComment);

        comments = new ArrayList<>();

        homeIcon = findViewById(R.id.homeIcon);
        searchIcon = findViewById(R.id.searchIcon);
        addPostIcon = findViewById(R.id.addPostIcon);
        smallAvatar = findViewById(R.id.smallAvatar);

        postId = getIntent().getStringExtra("postId");
        postOwnerId = getIntent().getStringExtra("postOwnerId");

        homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Post_CommentPage.this, User_HomePage.class);
                startActivity(intent);
                finish();
            }
        });

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Post_CommentPage.this, User_SearchPage.class);
                startActivity(intent);
                finish();
            }
        });

        addPostIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Post_CommentPage.this, User_AddPostPage.class);
                startActivity(intent);
                finish();
            }
        });

        smallAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Post_CommentPage.this, User_ProfilePage.class);
                startActivity(intent);
                finish();
            }
        });

        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndAddComment();
            }
        });

        retrieveInfo();

        retrieveComments();

        commentsRv.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(comments, this);
        commentsRv.setAdapter(commentAdapter);
    }

    private void validateAndAddComment() {
        String comment = etComment.getText().toString().trim();
        if (comment.isEmpty()) {
            Toast.makeText(Post_CommentPage.this, "Input comment", Toast.LENGTH_SHORT).show();
            return;
        }

        // add comment to comment list and refresh list
        Comment newComment = new Comment(userData.getName(), comment, new Date().toString(), userData.getUserPhotoUrl());

        reference.child(postOwnerId).child("posts").child(postId).child("comments")
                .push().setValue(newComment)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        retrieveComments();
                        etComment.setText("");
                        Toast.makeText(this, "Comment added successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to add comment", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(err -> {
                    Log.e("Post_CommentPage", "addComment: ", err);
                    Toast.makeText(this, "Failed to add comment", Toast.LENGTH_SHORT).show();
                });
    }


    private void retrieveComments() {
        comments.clear();
        reference.child(postOwnerId).child("posts").child(postId)
                .child("comments").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                            Comment comment = commentSnapshot.getValue(Comment.class);
                            comments.add(comment);
                        }
                        commentAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Post_CommentPage.this, "Failed to get comments.", Toast.LENGTH_SHORT).show();
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
                        userData = userProfile;
                        String userAvatar = userProfile.getUserPhotoUrl();

                        Glide.with(smallAvatar.getContext())
                                .load(userAvatar)
                                .error(R.drawable.round_report_problem_24)
                                .fitCenter()
                                .into(smallAvatar);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Post_CommentPage.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}