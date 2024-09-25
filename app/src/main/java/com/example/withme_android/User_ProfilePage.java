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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;

public class User_ProfilePage extends AppCompatActivity {

    private Button editProfileBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference reference1,reference2;
    private TextView userName, numberOfFollowers, numberOfPosts, numberOfYummys;
    private ImageView homeIcon, searchIcon, addPostIcon, smallAvatar, bigAvatar;
    private RecyclerView personalPostRecView;
    //private List<Post> postList;
    //private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile_page);

        editProfileBtn= findViewById(R.id.editProfileBtn);
        mAuth = FirebaseAuth.getInstance();
        reference1 = FirebaseDatabase.getInstance().getReference("users");
        reference1 = FirebaseDatabase.getInstance().getReference("posts");
        userName = findViewById(R.id.userName);
        numberOfFollowers = findViewById(R.id.numberOfFollowers);
        numberOfPosts = findViewById(R.id.numberOfPosts);
        numberOfYummys = findViewById(R.id.numberOfYummys);
        homeIcon = findViewById(R.id.homeIcon);
        searchIcon = findViewById(R.id.searchIcon);
        addPostIcon = findViewById(R.id.addPostIcon);
        smallAvatar = findViewById(R.id.smallAvatar);
        bigAvatar = findViewById(R.id.bigAvatar);
        personalPostRecView = findViewById(R.id.personalPostRecView);

        retrieveInfo();
        showPosts();

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

        if(user != null) {
            reference1.child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if(user != null){
                        String name = user.getName();
                        userName.setText(name);
                        String numberOfFollowers = String.valueOf(0); //find on firebase
                        String numberOfPosts = String.valueOf(0); //find on firebase
                        String numberOfYummys = String.valueOf(0); //find on firebase
                        // retrieve user picture for small and big avatars
                        // retrieve user`s posts
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void showPosts() {
        reference2.child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                   // postList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                     //   Post post = dataSnapshot.getValue(Post.class);
                       // if (post != null) {
                         //   postList.add(post);
                        //}
                    }
//                    postAdapter.notifyDataSetChanged();
                } else {
                    Log.d("Postview on user profile page", "Datasnapshot does not exists or is empty");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(User_ProfilePage.this, "Failed to load posts.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}