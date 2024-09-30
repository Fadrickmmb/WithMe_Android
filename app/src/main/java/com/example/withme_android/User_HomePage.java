package com.example.withme_android;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class User_HomePage extends AppCompatActivity {

    private RecyclerView rvPost;
    private List<Post> posts;
    private PostRvAdapter postAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_page);

        rvPost = findViewById(R.id.rv_post_home);

        // create few dummy posts
        posts = new ArrayList<>();

        posts.add(new Post("", "https://dynamic-media-cdn.tripadvisor.com/media/photo-o/12/ee/ab/bc/paneer-veggie-platter.jpg?w=600&h=400&s=1",
                100, 50, "Canada", "123",
                "Join me on a delicious journey as we explore local eateries, hidden gems, and mouthwatering recipes from around the world! Taste the adventure with every bite."));

        posts.add(new Post("", "https://www.expertbakeryconsultant.com/upload/category/1672754722fast-food-restaurants.jpg",
                50, 100, "Germany", "123",
                "Savor the flavor with us! From street food to gourmet dining, we’re on a mission to find the tastiest meals and the stories behind them. Come hungry!"));

        posts.add(new Post("", "https://d4t7t8y8xqo0t.cloudfront.net/resized/750X436/eazytrendz%2F2930%2Ftrend20200903104959.jpg",
                45, 500, "Switzerland", "123",
                "Get ready for amazing tantalize your taste buds! Join me as I cook, taste, and review a variety of dishes while exploring culinary traditions from every corner of the globe."));




        // Set the adapter and layout manager
        postAdapter = new PostRvAdapter(posts, this);
        rvPost.setAdapter(postAdapter);
        rvPost.setLayoutManager(new LinearLayoutManager(this));


    }
}
