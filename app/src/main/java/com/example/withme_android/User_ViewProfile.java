package com.example.withme_android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class User_ViewProfile extends AppCompatActivity {

    private Button followProfileBtn, backProfileBtn, reportUserBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference reference, currUserRef, visUserRef,reportRef;
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
        reportUserBtn = findViewById(R.id.reportUserBtn);
        reference = FirebaseDatabase.getInstance().getReference("users");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        currUserRef = reference.child(currentUserId);
        reportRef = FirebaseDatabase.getInstance().getReference("reportedUsers");

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

        reportUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportUser();
            }
        });
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

    private void reportUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(User_ViewProfile.this);
        View reportUser = getLayoutInflater().inflate(R.layout.report_user_dialog, null);
        Button noReportUserBtn, yesReportUserBtn;
        ImageView closeReportUserDialog;

        noReportUserBtn = reportUser.findViewById(R.id.noReportUserBtn);
        yesReportUserBtn = reportUser.findViewById(R.id.yesReportUserBtn);
        closeReportUserDialog = reportUser.findViewById(R.id.closeReportUserDialog);

        builder.setView(reportUser);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();

        closeReportUserDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        yesReportUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reportId = reportRef.push().getKey();
                if(reportId !=null){
                    Report reportUser = new Report(reportId,visitedUserId,currentUserId);
                    reportRef.child(reportId).setValue(reportUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(User_ViewProfile.this,"User reported.",Toast.LENGTH_SHORT).show();
                                reportUserBtn.setEnabled(false);
                                reportUserBtn.setText("User reported");
                            } else {
                                Toast.makeText(User_ViewProfile.this,"Error reporting user.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                dialog.dismiss();
            }
        });

        noReportUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
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
                    // i always need to check if i already reported this user or not when i visit profile


                } else {
                    Toast.makeText(User_ViewProfile.this, "User data not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(User_ViewProfile.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
            }
        });
        reportRef.orderByChild("userId").equalTo(visitedUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot reportSnapshot : snapshot.getChildren()){
                    String reportingId= reportSnapshot.child("userReportingId").getValue(String.class);
                    if(reportingId != null && reportingId.equals(currentUserId)){
                        reportUserBtn.setEnabled(false);
                        reportUserBtn.setText("User reported");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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