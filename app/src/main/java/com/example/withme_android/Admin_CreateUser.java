package com.example.withme_android;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Admin_CreateUser extends AppCompatActivity {

    RadioGroup radioGroup;
    RadioButton modRadio, adminRadio;
    EditText username, email, password;
    Button createUser, toHome;

    FirebaseAuth mAuth;
    DatabaseReference adminDatabase, modDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_create_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        radioGroup = findViewById(R.id.admin_createUser_radioGroup);
        modRadio = findViewById(R.id.admin_createUser_modButton);
        adminRadio = findViewById(R.id.admin_createUser_adminButton);
        username = findViewById(R.id.admin_createUser_usernameInput);
        email = findViewById(R.id.admin_createUser_emailInput);
        password = findViewById(R.id.admin_createUser_passwordInput);
        createUser = findViewById(R.id.admin_createUser_createButton);
        toHome = findViewById(R.id.admin_createUser_homeButton);

        mAuth = FirebaseAuth.getInstance();
        adminDatabase = FirebaseDatabase.getInstance().getReference("admin");
        modDatabase = FirebaseDatabase.getInstance().getReference("mod");

        toHome.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Admin_HomePage.class);
            startActivity(intent);
            finish();
        });

        createUser.setOnClickListener(view -> {
            if (radioGroup.getCheckedRadioButtonId() == -1) {
                Toast.makeText(Admin_CreateUser.this, "Please choose Mod or Admin", Toast.LENGTH_SHORT).show();
            } else {
                createUser();
            }
        });
    }

    private void createUser() {
        String userNameInput = username.getText().toString().trim();
        String emailInput = email.getText().toString().trim();
        String passwordInput = password.getText().toString().trim();

        if (TextUtils.isEmpty(userNameInput)) {
            username.setError("Username is required");
            username.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(emailInput) || !Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            email.setError("Valid email is required");
            email.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(passwordInput) || passwordInput.length() < 6) {
            password.setError("Password must be at least 6 characters");
            password.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(emailInput, passwordInput)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(userNameInput)
                                    .build();

                            user.updateProfile(profileUpdates).addOnCompleteListener(updateTask -> {
                                if (updateTask.isSuccessful()) {
                                    String userId = user.getUid();
                                    User newUser = new User(userNameInput, emailInput, userId);

                                    if (modRadio.isChecked()) {
                                        modDatabase.child(userId).setValue(newUser)
                                                .addOnCompleteListener(databaseTask -> {
                                                    if (databaseTask.isSuccessful()) {
                                                        Toast.makeText(Admin_CreateUser.this, "Moderator created successfully!", Toast.LENGTH_SHORT).show();
                                                        navigateToHome();
                                                    } else {
                                                        Toast.makeText(Admin_CreateUser.this, "Failed to save Moderator data: " + databaseTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                    } else if (adminRadio.isChecked()) {
                                        adminDatabase.child(userId).setValue(newUser)
                                                .addOnCompleteListener(databaseTask -> {
                                                    if (databaseTask.isSuccessful()) {
                                                        Toast.makeText(Admin_CreateUser.this, "Admin created successfully!", Toast.LENGTH_SHORT).show();
                                                        navigateToHome();
                                                    } else {
                                                        Toast.makeText(Admin_CreateUser.this, "Failed to save Admin data: " + databaseTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                    }
                                }
                            });
                        }
                    } else {
                        Toast.makeText(Admin_CreateUser.this, "User creation failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void navigateToHome() {
        Intent intent = new Intent(getApplicationContext(), Admin_HomePage.class);
        startActivity(intent);
        finish();
    }
}
