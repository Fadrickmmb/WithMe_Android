package com.example.withme_android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class User_EditProfile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseUser user;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private EditText etName;
    private EditText etBio;
    private Button btnUpdate;
    private CircleImageView ivProfileImage;
    private TextView profileName;
    private TextView tvChangeImage;

    private Uri imageUri;

    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit_profile);

        etName = findViewById(R.id.et_name_display);
        etBio = findViewById(R.id.et_bio_display);
        btnUpdate = findViewById(R.id.btn_update);
        ivProfileImage = findViewById(R.id.profile_image);
        profileName = findViewById(R.id.profile_name);
        tvChangeImage = findViewById(R.id.change_profile_picture);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("profile_pictures");
        user = FirebaseAuth.getInstance().getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();

        if (user != null && user.getDisplayName() != null) {
            etName.setText(user.getDisplayName());
            profileName.setText(user.getDisplayName());
        } else {
            profileName.setText(Objects.requireNonNull(user).getEmail());
        }

        if (user.getPhotoUrl() != null) {
            Glide.with(this).load(user.getPhotoUrl()).into(ivProfileImage);
        }

        firebaseDatabase.getReference("users").child(user.getUid()).child("bio")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String bio = task.getResult().getValue(String.class);
                        if (bio != null) {
                            etBio.setText(bio);
                        }
                    }
                }).addOnFailureListener((err) -> {
                    Toast.makeText(this, "Failed to fetch bio", Toast.LENGTH_SHORT).show();
                });

        tvChangeImage.setOnClickListener(v -> openFileChooser());

        btnUpdate.setOnClickListener(view1 -> validateAndUpdateProfile());

    }
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            imageUri = data.getData();
            ivProfileImage.setImageURI(imageUri);
        }
    }

    private void validateAndUpdateProfile() {
        if (!etName.getText().toString().equals(user.getDisplayName())) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(etName.getText().toString())
                    .build();
            user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    profileName.setText(user.getDisplayName()); // Refresh name
                }
            });
        }

        if (!etBio.getText().toString().isEmpty()) {
            firebaseDatabase.getReference("users").child(user.getUid()).child("bio")
                    .setValue(etBio.getText());
        }

        if (imageUri != null) {
            uploadImageToFirebaseStorage();
        } else {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageToFirebaseStorage() {
        StorageReference fileReference = storageReference.child(user.getUid() + ".jpg");

        fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                updateProfileImageUri(uri.toString());
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateProfileImageUri(String uri) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(uri))
                .build();

        user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to update profile image", Toast.LENGTH_SHORT).show();
            }
        });
    }
}