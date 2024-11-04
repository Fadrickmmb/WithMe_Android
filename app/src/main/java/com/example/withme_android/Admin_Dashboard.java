package com.example.withme_android;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Admin_Dashboard extends AppCompatActivity {

    private Spinner dropdownMenu;
    private ListView listView;
    private DatabaseReference database;
    private List<String> items;
    private ArrayAdapter<String> adapter;
    private String selectedOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dropdownMenu = findViewById(R.id.dropdownMenu);
        listView = findViewById(R.id.listView);

        database = FirebaseDatabase.getInstance().getReference();
        items = new ArrayList<>();

        String[] options = {"Reported Comments", "Reported Posts", "Reported Users", "Suspended Users"};
        ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        dropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownMenu.setAdapter(dropdownAdapter);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);


        dropdownMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedOption = options[position];
                fetchItems();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectedOption.equals("Reported Users") || selectedOption.equals("Suspended Users")) {
                    String userId = items.get(position);
                    Intent intent = new Intent(Admin_Dashboard.this, Admin_ViewProfile.class);
                    intent.putExtra("visitedUserId", userId);
                    startActivity(intent);
                }
            }
        });

    }


    private void fetchItems() {
        String tableName = "";

        switch (selectedOption) {
            case "Reported Posts":
                tableName = "reportedPosts";
                break;
            case "Reported Comments":
                tableName = "reportedComments";
                break;
            case "Reported Users":
                tableName = "reportedUsers";
                break;
            case "Suspended Users":
                tableName = "suspendedUsers";
                break;
        }

        database.child(tableName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        items.add(data.getKey());
                    }
                } else {
                    Toast.makeText(Admin_Dashboard.this, "No data found", Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Admin_Dashboard.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}