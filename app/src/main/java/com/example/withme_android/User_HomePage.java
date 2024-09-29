package com.example.withme_android;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class User_HomePage extends AppCompatActivity {

    Button toProfile;
    private DatabaseReference userDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_home_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        mAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference("users");
        toProfile = findViewById(R.id.userHomePage_toProfileButton);


        fetchUserName();

        toProfile.setOnClickListener(view -> {

        });
    }

    private void fetchUserName() {

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();


            userDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        String userName = dataSnapshot.child("name").getValue(String.class);
                        if (userName != null) {
                            Toast.makeText(User_HomePage.this, "Welcome, " + userName, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(User_HomePage.this, "Welcome, user!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(User_HomePage.this, "User data not found", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(User_HomePage.this, "Failed to retrieve user data", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(User_HomePage.this, "No user logged in", Toast.LENGTH_LONG).show();
        }
    }
}
