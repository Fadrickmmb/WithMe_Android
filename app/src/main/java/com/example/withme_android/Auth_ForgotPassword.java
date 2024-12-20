package com.example.withme_android;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Auth_ForgotPassword extends AppCompatActivity {

    EditText emailInput;
    Button resetPassword;
    TextView signIn;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_forgot_password);


        emailInput = findViewById(R.id.et_email);
        resetPassword = findViewById(R.id.btn_send_link);
        signIn = findViewById(R.id.tv_sign_in);

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