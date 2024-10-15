package com.example.withme_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class User_SearchPage extends AppCompatActivity {

    EditText searchInput;
    ImageView searchButton;
    TextView result01, result02;
    Button toHome;

    private DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_search_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        searchInput = findViewById(R.id.user_searchPage_input);
        searchButton = findViewById(R.id.user_searchPage_searchButton);
        result01 = findViewById(R.id.user_searchPage_result01);
        result02 = findViewById(R.id.user_searchPage_result02);
        toHome = findViewById(R.id.user_searchPage_buttonToHome);

        userDatabase = FirebaseDatabase.getInstance().getReference("users");

        toHome.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), User_HomePage.class);
            startActivity(intent);
            finish();
        });

        searchButton.setOnClickListener(view -> {
            String searchText = searchInput.getText().toString().trim();

            if (searchText.length() < 5) {
                result01.setText("Please enter at least 5 characters.");
                return;
            }

            String searchPrefix = searchText.substring(0, 5).toLowerCase();

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    boolean userFound = false;

                    if (snapshot.exists()) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            String uid = userSnapshot.getKey();
                            String name = userSnapshot.child("name").getValue(String.class);

                            if (name != null && name.toLowerCase().startsWith(searchPrefix)) {
                                result01.setText("User: " + name);
                                result02.setText("UID: " + uid);
                                userFound = true;

                                result01.setOnClickListener(v -> {
                                    Intent intent = new Intent(User_SearchPage.this, User_ViewProfile.class);
                                    intent.putExtra("userUid", uid);
                                    startActivity(intent);
                                });

                                break;
                            }
                        }

                        if (!userFound) {
                            result01.setText("We haven't found a user that matches '" + searchPrefix + "'");
                            result02.setText("");
                        }
                    } else {
                        result01.setText("No user data available.");
                        result02.setText("");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    result01.setText("Database error: " + databaseError.getMessage());
                    result02.setText("");
                }
            });
        });
    }
}
