package com.example.withme_android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class User_PostView extends AppCompatActivity {
    private ImageView homeIcon, searchIcon, addPostIcon, smallAvatar,userAvatar,postPicture;
    private TextView postOwnerName,locationName,yummysNumber,commentsNumber,postDate, postContent;
    private FirebaseAuth mAuth;
    private DatabaseReference reference,postreference;
    private String postId,ownerId;
    private Button backBtn, addCommentBtn;
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
        addCommentBtn = findViewById(R.id.addCommentBtn);
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
        postMenu = findViewById(R.id.postMenu);
        ownerId = getIntent().getStringExtra("userId");
        postId = getIntent().getStringExtra("postId");
        backBtn = findViewById(R.id.backBtn);
        postContent = findViewById(R.id.postContent);
        reference = FirebaseDatabase.getInstance().getReference("users");
        postreference = FirebaseDatabase.getInstance().getReference("users").child(ownerId).child("posts");


        retrieveSinglePostInfo(postId);
        retrieveComments(postId,commentPostRecView);
        retrieveInfo();

        addCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View commentView = LayoutInflater.from(User_PostView.this).inflate(R.layout.comment, null);
                AlertDialog dialog = new AlertDialog.Builder(User_PostView.this).setView(commentView).create();

                EditText addCommentText = commentView.findViewById(R.id.addComentText);
                Button saveCommentBtn = commentView.findViewById(R.id.saveCommentBtn);

                saveCommentBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String commentText = addCommentText.getText().toString().trim();
                        if (!commentText.isEmpty()) {
                            saveCommentToFirebase(commentText);
                            dialog.dismiss();
                        } else {
                            Toast.makeText(User_PostView.this, "Please enter a comment", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.show();
            }
        });

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

        postMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ownerId = getIntent().getStringExtra("userId");
                String currentUserId = mAuth.getUid();

                if (currentUserId != null && currentUserId.equals(ownerId)) {
                    View editView = LayoutInflater.from(User_PostView.this).inflate(R.layout.editpost_dialog, null);
                    AlertDialog dialog = new AlertDialog.Builder(User_PostView.this).setView(editView).create();

                    ImageView closeEditPostDialog = editView.findViewById(R.id.closeEditPostDialog);
                    ImageView deletePost = editView.findViewById(R.id.deletePost);
                    ImageView editPost = editView.findViewById(R.id.editPost);

                    closeEditPostDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    editPost.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(User_PostView.this, User_EditPost.class);
                            intent.putExtra("postId", postId);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    });

                    deletePost.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            postreference.child(postId).removeValue();
                            Toast.makeText(User_PostView.this, "Post deleted", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            finish();
                        }
                    });

                    dialog.show();
                } else {
                    View reportView = LayoutInflater.from(User_PostView.this).inflate(R.layout.reportpost_dialog, null);
                    AlertDialog dialog = new AlertDialog.Builder(User_PostView.this).setView(reportView).create();

                    ImageView closeReportPostDialog = reportView.findViewById(R.id.closeReportPostDialog);
                    ImageView reportPost = reportView.findViewById(R.id.reportPost);

                    closeReportPostDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    reportPost.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(User_PostView.this, "This function is not working yet.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
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
                        int yummysN = post.getYummysNumber();
                        String userPhotoUrl = post.getUserPhotoUrl();
                        String content = post.getContent();

                        postContent.setText(content);
                        postOwnerName.setText(name);
                        yummysNumber.setText(String.valueOf(yummysN));
                        locationName.setText(location);
                        postDate.setText(date);

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
                commentsNumber.setText(String.valueOf(commentList.size()));
                commentAdapter.notifyDataSetChanged();
                postreference.child(postId).child("commentsNumber").setValue(commentList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                        String userAvatar = userProfile.getUserPhotoUrl();

                        Glide.with(smallAvatar.getContext())
                                .load(userAvatar)
                                .error(R.drawable.round_report_problem_24)
                                .fitCenter()
                                .into(smallAvatar);


                    } else {
                        Toast.makeText(User_PostView.this, "User data not found.", Toast.LENGTH_SHORT).show();
                        Log.e("UserPostView", "User profile is null.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(User_PostView.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                    Log.e("User_PostView", "onCancelled: ", error.toException());
                }
            });
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            Log.e("User_PostView", "User is null.");
        }
    }

    private void saveCommentToFirebase(String commentText) {
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if (userProfile != null) {
                    String userName = userProfile.getName();
                    String date = getCurrentDate();

                    String commentId = postreference.child(postId).child("comments").push().getKey();
                    Comment comment = new Comment(userName, commentText, date, userId, postId, commentId);
                    postreference.child(postId).child("comments").child(commentId).setValue(comment);
                    postreference.child(postId).child("commentsNumber").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                int comNumber =  dataSnapshot.getValue(Integer.class);
                                comNumber = comNumber + 1;
                                commentsNumber.setText(String.valueOf(comNumber));
                                postreference.child(postId).child("commentsNumber").setValue(comNumber);
                                commentsNumber.setText(String.valueOf(comNumber));

                            } else {
                                commentsNumber.setText("0");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    Toast.makeText(User_PostView.this, "Comment added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(User_PostView.this, "User data not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(User_PostView.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }
}