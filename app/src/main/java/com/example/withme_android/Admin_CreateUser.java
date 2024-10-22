package com.example.withme_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
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

        toHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Admin_HomePage.class);
                startActivity(intent);
                finish();
            }
        });




    }
}