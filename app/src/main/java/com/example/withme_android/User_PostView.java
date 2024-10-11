package com.example.withme_android;

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

public class User_PostView extends AppCompatActivity {
    private ImageView homeIcon, searchIcon, addPostIcon, smallAvatar,userAvatar,postPicture;
    private TextView postOwnerName,locationName,yummysNumber,commentsNumber,postDate, postContent;
    private FirebaseAuth mAuth;
    private DatabaseReference reference,postreference;
    private String postId;
    private Button backBtn;
    private RecyclerView commentPostRecView;
    private LinearLayoutManager layoutManager;
    private List<Comment> commentList;
    private CommentAdapter commentAdapter;
    private LinearLayout postMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_post_view);

        homeIcon = findViewById(R.id.homeIcon);
        searchIcon = findViewById(R.id.searchIcon);
        addPostIcon = findViewById(R.id.addPostIcon);
        smallAvatar = findViewById(R.id.smallAvatar);
        mAuth = FirebaseAuth.getInstance();
        postOwnerName = findViewById(R.id.postOwnerName);
        locationName = findViewById(R.id.locationName);
        yummysNumber = findViewById(R.id.yummysNumber);
        commentsNumber = findViewById(R.id.commentsNumber);
        postDate = findViewById(R.id.postDate);
        postPicture = findViewById(R.id.postPicture);
        userAvatar = findViewById(R.id.userAvatar);
        commentsNumber = findViewById(R.id.commentsNumber);
        commentPostRecView = findViewById(R.id.commentPostRecView);
        layoutManager = new LinearLayoutManager(this);
        commentPostRecView.setLayoutManager(layoutManager);
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList);
        commentPostRecView.setAdapter(commentAdapter);

        backBtn = findViewById(R.id.backBtn);
        postContent = findViewById(R.id.postContent);
        postreference = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid()).child("posts");
        postId = getIntent().getStringExtra("postId");

        retrieveSinglePostInfo(postId);
        retrieveComments(postId,commentPostRecView);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_PostView.this, User_HomePage.class);
                startActivity(intent);
                finish();
            }
        });

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_PostView.this, User_SearchPage.class);
                startActivity(intent);
                finish();
            }
        });

        addPostIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_PostView.this, User_AddPostPage.class);
                startActivity(intent);
                finish();
            }
        });

        smallAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_PostView.this, User_ProfilePage.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void retrieveSinglePostInfo(String postId) {
        postreference.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post != null) {
                        String postImageUrl = post.getPostImageUrl();
                        String date = post.getPostDate();
                        String name = post.getName();
                        String location = post.getLocation();
                        int yummys = post.getYummys();
                        String userPhotoUrl = post.getUserPhotoUrl();
                        String content = post.getContent();

                        postContent.setText(content);
                        postOwnerName.setText(name);
                        yummysNumber.setText(String.valueOf(yummys));
                        locationName.setText(location);
                        postDate.setText(date);
                        commentsNumber.setText(String.valueOf(post.getCommentNumbers()));


                        Glide.with(userAvatar.getContext())
                                .load(userPhotoUrl)
                                .error(R.drawable.round_report_problem_24)
                                .fitCenter()
                                .into(userAvatar);
                        Glide.with(postPicture.getContext())
                                .load(postImageUrl)
                                .error(R.drawable.round_report_problem_24)
                                .fitCenter()
                                .into(postPicture);
                    } else {
                        Log.d("RetrieveSinglePostInfo", "Post not found.");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void retrieveComments(String postId, RecyclerView recyclerView) {
        postreference.child(postId).child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                if(snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Comment comment = dataSnapshot.getValue(Comment.class);
                        if (comment != null) {
                            commentList.add(comment);
                        }
                    }
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}