package com.example.withme_android;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class User_HomePage extends AppCompatActivity {

    private RecyclerView rvPost;
    private List<String> posts;
    private PostRvAdapter postAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_page);

        rvPost = findViewById(R.id.rv_post_home);

        // create few dummy posts
        posts = new ArrayList<>();




        // Set the adapter and layout manager
        postAdapter = new PostRvAdapter(posts, this);
        rvPost.setAdapter(postAdapter);
        rvPost.setLayoutManager(new LinearLayoutManager(this));


    }
}
