package com.example.withme_android;

import android.os.Bundle;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User_HomePage extends AppCompatActivity {

    private RecyclerView rvPost;
    private List<Post> posts;
    private PostAdapter postAdapter;

    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_page);

        rvPost = findViewById(R.id.rv_post_home);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();





        // create few dummy posts
        posts = new ArrayList<>();

        Post post1 = new Post("Hello there, this is my first post", firebaseUser.getUid(), "",
                new Date().toString(), );
        posts.add(post1);


        // Set the adapter and layout manager
        postAdapter = new PostAdapter(posts, this);
        rvPost.setAdapter(postAdapter);
        rvPost.setLayoutManager(new LinearLayoutManager(this));
    }
}
