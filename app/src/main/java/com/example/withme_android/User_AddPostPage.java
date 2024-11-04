package com.example.withme_android;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class User_AddPostPage extends FragmentActivity implements OnMapReadyCallback {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2; // Updated constant
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private ImageView homeIcon, searchIcon, addPostIcon, smallAvatar, bigAvatar;
    private TextView userName, locationTv;
    private Button btnPost;
    private EditText etContent;

    private User userData;
    private ImageView ivPostImg;
    private Uri postImgUri;
    private StorageReference storageReference;

    private MapView mapView;
    private GoogleMap gMap;
    private FusedLocationProviderClient fusedLocationClient;

    private Double latitude;
    private Double longitude;
    private String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_add_post_page);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("users");
        homeIcon = findViewById(R.id.homeIcon);
        searchIcon = findViewById(R.id.searchIcon);
        addPostIcon = findViewById(R.id.addPostIcon);
        smallAvatar = findViewById(R.id.smallAvatar);
        bigAvatar = findViewById(R.id.iv_profile);
        userName = findViewById(R.id.tv_user_name);
        btnPost = findViewById(R.id.btn_post);
        locationTv = findViewById(R.id.mapLocation);

        etContent = findViewById(R.id.et_content);
        ivPostImg = findViewById(R.id.iv_add_photo);

        storageReference = FirebaseStorage.getInstance().getReference("posts");

        retrieveInfo();
        setupNavigation();
        setupListeners();


        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void setupNavigation() {
        homeIcon.setOnClickListener(view -> startActivity(new Intent(User_AddPostPage.this, User_HomePage.class)));
        searchIcon.setOnClickListener(view -> startActivity(new Intent(User_AddPostPage.this, User_SearchPage.class)));
        addPostIcon.setOnClickListener(view -> startActivity(new Intent(User_AddPostPage.this, User_AddPostPage.class)));
        smallAvatar.setOnClickListener(view -> startActivity(new Intent(User_AddPostPage.this, User_ProfilePage.class)));
    }

    private void setupListeners() {
        btnPost.setOnClickListener(v -> createPost());
        ivPostImg.setOnClickListener(view -> selectPhoto());
    }

    private void selectPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void createPost() {
        String content = etContent.getText().toString();

        String postId = java.util.UUID.randomUUID().toString();

        if (postImgUri == null || postImgUri.toString().isEmpty()) {
            Toast.makeText(this, "Please select a photo", Toast.LENGTH_SHORT).show();
            return;
        }

        if (location == null || latitude == null || longitude == null) {
            Toast.makeText(this, "Please select location", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference fileReference = storageReference.child(postId + ".jpg");

        fileReference.putFile(postImgUri).addOnSuccessListener(taskSnapshot -> {
            fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                Post post = new Post(postId, content, mAuth.getUid(), uri.toString(), new Date().toString(),
                        userData.getName(), location, new HashMap<>(), userData.getUserPhotoUrl(),
                        new HashMap<>(), latitude, longitude);

                Map<String, Post> posts = userData.getPosts();
                if (posts == null) {
                    posts = new HashMap<>();
                }
                posts.put(postId, post);
                userData.setPosts(posts);

                if (userData.getNumberPosts() == null) {
                    userData.setNumberPosts(1L);
                } else {
                    userData.setNumberPosts(userData.getNumberPosts() + 1);
                }

                reference.child(mAuth.getUid()).setValue(userData).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Post created successfully", Toast.LENGTH_SHORT).show();
                        clearFields();
                    } else {
                        Toast.makeText(this, "Failed to create post", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(err -> {
                    Log.e("User_AddPostPage", "createPost: ", err);
                    Toast.makeText(this, "Failed to create post", Toast.LENGTH_SHORT).show();
                });
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
        });
    }

    private void clearFields() {
        etContent.setText("");
        ivPostImg.setImageResource(R.drawable.baseline_person_24);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            postImgUri = data.getData();
            ivPostImg.setImageURI(postImgUri);
        }
    }

    private void retrieveInfo() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            reference.child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User userProfile = snapshot.getValue(User.class);
                    if (userProfile != null) {
                        userData = userProfile;

                        String name = userProfile.getName();
                        String userAvatar = userProfile.getUserPhotoUrl();

                        userName.setText(name);

                        Glide.with(bigAvatar.getContext())
                                .load(userAvatar)
                                .error(R.drawable.round_report_problem_24)
                                .fitCenter()
                                .into(bigAvatar);

                        Glide.with(smallAvatar.getContext())
                                .load(userAvatar)
                                .error(R.drawable.round_report_problem_24)
                                .fitCenter()
                                .into(smallAvatar);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(User_AddPostPage.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        gMap.getUiSettings().setZoomControlsEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        gMap.setMyLocationEnabled(true);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();

                        LatLng currentLocation = new LatLng(latitude, longitude);
                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(currentLocation)
                                .title("Drag me!")
                                .draggable(true);

                        gMap.addMarker(markerOptions);

                        updateLocationName(latitude, longitude);
                    }
                });

        gMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(@NonNull Marker marker) {
            }

            @Override
            public void onMarkerDrag(@NonNull Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(@NonNull Marker marker) {
                LatLng newPosition = marker.getPosition();
                latitude = newPosition.latitude; // Update latitude
                longitude = newPosition.longitude; // Update longitude
                gMap.moveCamera(CameraUpdateFactory.newLatLng(newPosition));

                updateLocationName(latitude, longitude);
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    gMap.setMyLocationEnabled(true);
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(this, location -> {
                                if (location != null) {
                                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                                    gMap.addMarker(new MarkerOptions().position(currentLocation).title("You are here"));
                                }
                            });
                }
            } else {
                Toast.makeText(this, "Location permission is required to access your location.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateLocationName(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                StringBuilder addressString = new StringBuilder();
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressString.append(address.getAddressLine(i)).append(", ");
                }
                String finalAddress = addressString.substring(0, addressString.length() - 2);
                location = finalAddress;
                locationTv.setText(location);

                Toast.makeText(getApplicationContext(), finalAddress, Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "Unable to get address for this location.", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Geocoder service is not available.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
