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
import com.google.firebase.database.FirebaseDatabase;

public class Auth_ForgotPassword extends AppCompatActivity {

    EditText emailInput;
    Button resetPassword;
    TextView signIn;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailInput = findViewById(R.id.forgotPassword_emailInput);
        resetPassword = findViewById(R.id.forgotPassword_resetButton);
        signIn = findViewById(R.id.forgotPassword_signInText);

        mAuth = FirebaseAuth.getInstance();

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Auth_Login.class);
                startActivity(intent);
                finish();
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                String email = emailInput.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Auth_ForgotPassword.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Auth_ForgotPassword.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), Auth_Login.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Auth_ForgotPassword.this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }
}