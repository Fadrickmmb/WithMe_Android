package com.example.withme_android;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import java.util.UUID;

public class Auth_Register extends AppCompatActivity {

    EditText username, email, password;
    Button register;
    TextView signInText;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        username = findViewById(R.id.registerScreen_usernameInput);
        email = findViewById(R.id.registerScreen_emailInput);
        password = findViewById(R.id.registerScreen_passwordInput);
        register = findViewById(R.id.registerScreen_registerButton);
        signInText = findViewById(R.id.registerScreen_signInText);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        signInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Auth_Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void registerUser() {

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

                                    UUID userId = UUID.randomUUID();
                                    User newUser = new User(userNameInput, emailInput, userId.toString());

                                    mDatabase.child(userId.toString()).setValue(newUser)
                                            .addOnCompleteListener(databaseTask -> {
                                                if (databaseTask.isSuccessful()) {
                                                    Toast.makeText(Auth_Register.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(Auth_Register.this, User_HomePage.class));
                                                    finish();
                                                } else {
                                                    Toast.makeText(Auth_Register.this, "Failed to save user data: " + databaseTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                }
                            });
                        }
                    } else {
                        Toast.makeText(Auth_Register.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
