package com.example.withme_android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class Admin_EditProfile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseUser firebaseUser;
    private StorageReference storageReference;

    private EditText etName;
    private EditText etBio;
    private Button btnUpdate;
    private Button btnLogout;
    private TextView profileName;
    private TextView tvChangeImage;
    private EditText etPassword;

    private Uri imageUri;
    private ImageView ivProfileImage;
    private DatabaseReference firebaseDatabase;

    private User user;
    private ImageView homeIcon, searchIcon, addPostIcon, smallAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_profile);

        etName = findViewById(R.id.et_name);
        etBio = findViewById(R.id.et_bio);
        btnUpdate = findViewById(R.id.btn_update_profile);
        ivProfileImage = findViewById(R.id.iv_profile);
        profileName = findViewById(R.id.tv_profile_name);
        tvChangeImage = findViewById(R.id.tv_profile_pic_update);
        btnLogout = findViewById(R.id.btn_logout);
        etPassword = findViewById(R.id.et_password);
        homeIcon = findViewById(R.id.homeIcon);
        searchIcon = findViewById(R.id.searchIcon);
        addPostIcon = findViewById(R.id.addPostIcon);
        smallAvatar = findViewById(R.id.smallAvatar);

        storageReference = FirebaseStorage.getInstance().getReference("admin_profile_pictures");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference("admin");


        tvChangeImage.setOnClickListener(v -> openFileChooser());

        btnUpdate.setOnClickListener(view1 -> validateAndUpdateProfile());


        homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin_EditProfile.this, User_HomePage.class);
                startActivity(intent);
                finish();
            }
        });

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin_EditProfile.this, User_SearchPage.class);
                startActivity(intent);
                finish();
            }
        });

        addPostIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin_EditProfile.this, User_AddPostPage.class);
                startActivity(intent);
                finish();
            }
        });

        smallAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin_EditProfile.this, User_ProfilePage.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogout.setOnClickListener(v -> {
            Toast.makeText(this, "Logout success", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(Admin_EditProfile.this, Auth_Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        fetchUserData();
    }

    private void fetchUserData() {

        String userId = firebaseUser.getUid();

        firebaseDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        profileName.setText(user.getName());
                        etName.setText(user.getName());
                        etBio.setText(user.getUserBio());

                        Glide.with(Admin_EditProfile.this)
                                .load(user.getUserPhotoUrl())
                                .error(R.drawable.baseline_person_24)
                                .fitCenter()
                                .into(ivProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Admin_EditProfile.this, "Failed to retrieve user data", Toast.LENGTH_LONG).show();
            }
        });
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

        if (user == null) {
            user = new User();
        }

        if (!etName.getText().toString().equals(user.getName())) {
            user.setName(etName.getText().toString());
        }

        if (!etBio.getText().toString().isEmpty()) {
            user.setUserBio(etBio.getText().toString());
        }

        if (!etPassword.getText().toString().isEmpty()) {
            firebaseUser.updatePassword(etPassword.getText().toString())
                    .addOnFailureListener(err -> {
                        Toast.makeText(this, "Error updating admin password", Toast.LENGTH_SHORT).show();
                        Log.e("wma", "validateAndUpdateProfile: ", err);
                    });
        }


        if (imageUri != null) {
            uploadImageToFirebaseStorage();
        } else {
            updateInFirebase();
        }
    }

    private void uploadImageToFirebaseStorage() {
        StorageReference fileReference = storageReference.child(firebaseUser.getUid() + ".jpg");

        fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                updateProfileImageUri(uri.toString());
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateProfileImageUri(String uri) {
        user.setUserPhotoUrl(Uri.parse(uri).toString());
        updateInFirebase();
    }

    private void updateInFirebase() {
        firebaseDatabase.child(firebaseUser.getUid().toString()).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(err -> {
            Toast.makeText(this, "Error updating admin profile", Toast.LENGTH_SHORT).show();
            Log.e("wmlogs", "updateProfileImageUri: ", err);
        });
    }
}