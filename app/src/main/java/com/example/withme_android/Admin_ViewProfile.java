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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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

public class Admin_ViewProfile extends AppCompatActivity {

    private Button followProfileBtn, backProfileBtn, suspendUserBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference reference, currUserRef, visUserRef,suspendRef;
    private TextView userFullName, numberOfFollowers, numberOfPosts, numberOfFollowing,userBio,noPostsMessage;
    private ImageView homeIcon, searchIcon, addPostIcon, smallAvatar, bigAvatar;
    private List<Post> postList;
    private Admin_PostAdapter adminPostAdapter;

    private RecyclerView visitedPostRecView;
    private LinearLayoutManager layoutManager;
    private String currentUserId,visitedUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_view_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
        suspendUserBtn = findViewById(R.id.suspendUserBtn);
        reference = FirebaseDatabase.getInstance().getReference("users");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        currUserRef = reference.child(currentUserId);
        suspendRef = FirebaseDatabase.getInstance().getReference("suspendedUsers");

        visitedPostRecView = findViewById(R.id.visitedPostRecView);

        layoutManager = new LinearLayoutManager(this);
        visitedPostRecView.setLayoutManager(layoutManager);
        postList = new ArrayList<>();

        adminPostAdapter = new Admin_PostAdapter(this,postList);
        visitedPostRecView.setAdapter(adminPostAdapter);

        retrieveInfo(currentUserId);

        visitedUserId = getIntent().getStringExtra("visitedUserId");
        if(visitedUserId != null) {
            visUserRef = reference.child(visitedUserId);
            retrieveVisitedInfo(visitedUserId);
            checkFollowStatus();

            suspendRef.child(visitedUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        suspendUserBtn.setText("Unsuspend User");
                    } else {
                        suspendUserBtn.setText("Suspend User");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Admin_ViewProfile.this, "Error loading suspension status.", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(Admin_ViewProfile.this,"Error loading user profile.", Toast.LENGTH_SHORT).show();
        }

        suspendUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                suspendUser();
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
                Intent intent = new Intent(Admin_ViewProfile.this, Admin_HomePage.class);
                startActivity(intent);
                finish();
            }
        });

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin_ViewProfile.this, Admin_SearchPage.class);

                startActivity(intent);
                finish();
            }
        });

        addPostIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin_ViewProfile.this, Admin_AddPostPage.class);
                startActivity(intent);
                finish();
            }
        });

        smallAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin_ViewProfile.this, Admin_ProfilePage.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void suspendUser(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Admin_ViewProfile.this);
        View reportUser = getLayoutInflater().inflate(R.layout.suspend_user_dialog, null);
        Button noSuspendUserBtn, yesSuspendUserBtn;
        ImageView closeSuspendUserDialog;

        noSuspendUserBtn = reportUser.findViewById(R.id.noSuspendUserBtn);
        yesSuspendUserBtn = reportUser.findViewById(R.id.yesSuspendUserBtn);
        closeSuspendUserDialog = reportUser.findViewById(R.id.closeSuspendUserDialog);

        builder.setView(reportUser);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();

        closeSuspendUserDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        yesSuspendUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                suspendRef.child(visitedUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            suspendRef.child(visitedUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Admin_ViewProfile.this, "User unsuspended.", Toast.LENGTH_SHORT).show();
                                        suspendUserBtn.setEnabled(true);
                                        suspendUserBtn.setText("Suspend User");
                                    } else {
                                        Toast.makeText(Admin_ViewProfile.this, "Error unsuspending user.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Suspend suspendUser = new Suspend(visitedUserId, visitedUserId, currentUserId);
                            suspendRef.child(visitedUserId).setValue(suspendUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Admin_ViewProfile.this, "User suspended.", Toast.LENGTH_SHORT).show();
                                        suspendUserBtn.setEnabled(false);
                                        suspendUserBtn.setText("Unsuspend User");
                                    } else {
                                        Toast.makeText(Admin_ViewProfile.this, "Error suspending user.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Admin_ViewProfile.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.dismiss();
            }
        });

        noSuspendUserBtn.setOnClickListener(new View.OnClickListener() {
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
                    Toast.makeText(Admin_ViewProfile.this, "User data not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Admin_ViewProfile.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
            }
        });
        suspendRef.orderByChild("userId").equalTo(visitedUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot reportSnapshot : snapshot.getChildren()){
                    String reportingId= reportSnapshot.child("userReportingId").getValue(String.class);
                    if(reportingId != null && reportingId.equals(currentUserId)){
                        suspendUserBtn.setEnabled(false);
                        suspendUserBtn.setText("User suspended");
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
                postList.clear();
                if(snapshot.exists() && snapshot.hasChildren()){

                    for(DataSnapshot postSnapshot : snapshot.getChildren()){
                        Post post = postSnapshot.getValue(Post.class);
                        if(post != null){
                            postList.add(post);
                        }
                    }
                    if (!postList.isEmpty()) {
                        adminPostAdapter.notifyDataSetChanged();
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
                adminPostAdapter.notifyDataSetChanged();
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
                    Toast.makeText(Admin_ViewProfile.this, "User data not found.", Toast.LENGTH_SHORT).show();
                    Log.e("UserProfile", "User profile is null.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Admin_ViewProfile.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }


}