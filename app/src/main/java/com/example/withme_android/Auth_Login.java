package com.example.withme_android;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Auth_Login extends AppCompatActivity {

    EditText email, password;
    Button login;
    TextView register, forgotPassword;

    private FirebaseAuth mAuth;
    DatabaseReference userDatabase, adminDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email = findViewById(R.id.loginScreen_emailInput);
        password = findViewById(R.id.loginScreen_passwordInput);

        login = findViewById(R.id.loginScreen_loginButton);

        register = findViewById(R.id.loginScreen_registerText);
        forgotPassword = findViewById(R.id.loginScreen_forgotText);

        mAuth = FirebaseAuth.getInstance();

        userDatabase = FirebaseDatabase.getInstance().getReference("users");
        adminDatabase = FirebaseDatabase.getInstance().getReference("admin");

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Auth_ForgotPassword.class);
                startActivity(intent);
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Auth_Register.class);
                startActivity(intent);
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

    }

    private void loginUser() {
        String emailInput = email.getText().toString().trim();
        String passwordInput = password.getText().toString().trim();

        if (TextUtils.isEmpty(emailInput)) {
            email.setError("Email is required.");
            return;
        }

        if (TextUtils.isEmpty(passwordInput)) {
            password.setError("Password is required.");
            return;
        }

        mAuth.signInWithEmailAndPassword(emailInput, passwordInput)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(Auth_Login.this, "Login Successful", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(Auth_Login.this, User_HomePage.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Auth_Login.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}