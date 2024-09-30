package com.example.withme_android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class User_ProfilePage extends AppCompatActivity {

    private FirebaseUser user;

    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile_page);

        user = FirebaseAuth.getInstance().getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();

        if (user.getDisplayName() == null) {
            ((TextView) findViewById(R.id.profile_name)).setText(user.getEmail());
        } else {
            ((TextView) findViewById(R.id.profile_name)).setText(user.getDisplayName());
        }

        if (user.getPhotoUrl() != null) {
            Glide.with(this).load(user.getPhotoUrl())
                    .into((ImageView) findViewById(R.id.profile_image));
        }

        firebaseDatabase.getReference("users").child(user.getUid()).child("bio")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String bio = task.getResult().getValue(String.class);
                        if (bio != null) {
                            ((TextView) findViewById(R.id.tv_user_bio)).setText(bio);
                        }
                    }
                }).addOnFailureListener((err) -> {
                    Toast.makeText(this, "Failed to fetch bio", Toast.LENGTH_SHORT).show();
                });

        findViewById(R.id.logout_button).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();

            Toast.makeText(this, "Logout success", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, Auth_Login.class);
            startActivity(intent);
        });

        findViewById(R.id.edit_profile_button).setOnClickListener(v -> {
            Intent intent = new Intent(this, User_EditProfile.class);
            startActivity(intent);
        });
    }
}