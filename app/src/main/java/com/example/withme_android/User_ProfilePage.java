package com.example.withme_android;

import static android.content.ContentValues.TAG;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private LinearLayout followersLayout,followingLayout,notificationIcon;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(User_ProfilePage.this, "Notification permission granted.",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(User_ProfilePage.this, "Notification permission granted.",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile_page);

        editProfileBtn = findViewById(R.id.editProfileBtn);
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("users");
        userName = findViewById(R.id.userName);
        followersLayout = findViewById(R.id.followersLayout);
        followingLayout = findViewById(R.id.followingLayout);
        numberOfFollowers = findViewById(R.id.numberOfFollowers);
        numberOfPosts = findViewById(R.id.numberOfPosts);
        numberOfFollowing = findViewById(R.id.numberOfFollowing);
        notificationIcon = findViewById(R.id.notificationIcon);
        homeIcon = findViewById(R.id.homeIcon);
        noPostsMessage = findViewById(R.id.noPostsMessage);
        searchIcon = findViewById(R.id.searchIcon);
        addPostIcon = findViewById(R.id.addPostIcon);
        smallAvatar = findViewById(R.id.smallAvatar);
        bigAvatar = findViewById(R.id.bigAvatar);
        userBio = findViewById(R.id.userBio);
        personalPostRecView = findViewById(R.id.personalPostRecView);

        layoutManager = new LinearLayoutManager(this);
        personalPostRecView.setLayoutManager(layoutManager);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(this,postList);
        personalPostRecView.setAdapter(postAdapter);

        askNotificationPermission();

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                FirebaseUser user = mAuth.getCurrentUser();
                String userId = user.getUid();
                if (!task.isSuccessful()) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                    Toast.makeText(User_ProfilePage.this,"Token is missing.",Toast.LENGTH_SHORT).show();
                    return;
                }
                String token = task.getResult();
                FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("token").setValue(token);
                //String msg = getString(R.string.msg_token_fmt, token);
                //Log.d(TAG, msg);
                //Toast.makeText(User_ProfilePage.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        retrieveInfo();

        notificationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_ProfilePage.this, User_NotificationsPage.class);
                startActivity(intent);
                finish();
            }
        });

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
                        Map<String, Boolean> followers = userProfile.getFollowers();
                        Map<String, Boolean> following = userProfile.getFollowing();

                        String userAvatar = userProfile.getUserPhotoUrl();
                        String bio = userProfile.getUserBio();

                        userName.setText(name);
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

                        Map<String, Post> postsMap = userProfile.getPosts();
                        Log.d("UserProfile", "Posts Map: " + postsMap);

                        if (postsMap != null && !postsMap.isEmpty()) {
                            postList.clear();
                            postList.addAll(postsMap.values());
                            postAdapter.notifyDataSetChanged();

                            numberOfPosts.setText(String.valueOf(postList.size()));
                            noPostsMessage.setVisibility(View.GONE);
                            personalPostRecView.setVisibility(View.VISIBLE);
                        } else {
                            numberOfPosts.setText("0");
                            noPostsMessage.setVisibility(View.VISIBLE);
                            personalPostRecView.setVisibility(View.GONE);
                        }

                        followersLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Map<String, Boolean> followersMap = userProfile.getFollowers();
                                if(followersMap != null) {
                                    Intent intent = new Intent(User_ProfilePage.this, User_Followers.class);
                                    intent.putStringArrayListExtra("followersList", new ArrayList<>(userProfile.getFollowers().keySet()));
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(User_ProfilePage.this, "You have no followers.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        followingLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Map<String, Boolean> followingMap = userProfile.getFollowing();
                                if(followingMap != null) {
                                    Intent intent = new Intent(User_ProfilePage.this, User_Following.class);
                                    intent.putStringArrayListExtra("followingList", new ArrayList<>(userProfile.getFollowing().keySet()));
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(User_ProfilePage.this, "You don't follow anyone.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(User_ProfilePage.this, "User data not found.", Toast.LENGTH_SHORT).show();
                        Log.e("UserProfile", "User profile is null.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(User_ProfilePage.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                    Log.e("User_ProfilePage", "onCancelled: ", error.toException());
                }
            });
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            Log.e("User_ProfilePage", "User is null.");
        }
    }

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
                Toast.makeText(this, "Notifications are enabled.", Toast.LENGTH_SHORT).show();
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
}