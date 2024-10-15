package com.example.withme_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class User_SearchPage extends AppCompatActivity {

    EditText searchInput;
    ImageView searchButton;
    TextView result01, result02, result03;
    Button toHome;

    private DatabaseReference userDatabase;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_search_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        searchInput = findViewById(R.id.user_searchPage_input);
        searchButton = findViewById(R.id.user_searchPage_searchButton);
        result01 = findViewById(R.id.user_searchPage_result01);
        result02 = findViewById(R.id.user_searchPage_result02);
        result03 = findViewById(R.id.user_searchPage_result03);
        toHome = findViewById(R.id.user_searchPage_buttonToHome);

        mAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference("users");

        toHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), User_HomePage.class);
                startActivity(intent);
                finish();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userDatabase.limitToFirst(1).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        for (DataSnapshot userSnapshot : task.getResult().getChildren()) {
                            String userName = userSnapshot.child("name").getValue(String.class);
                            result01.setText(userName);
                        }
                    } else {
                        result01.setText("No users found.");
                    }
                });
            }
        });


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userDatabase.limitToFirst(1).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        for (DataSnapshot userSnapshot : task.getResult().getChildren()) {
                            String userName = userSnapshot.child("name").getValue(String.class);
                            result01.setText(userName);
                        }
                    } else {
                        result01.setText("No users found.");
                    }
                });
            }
        });



    }
}