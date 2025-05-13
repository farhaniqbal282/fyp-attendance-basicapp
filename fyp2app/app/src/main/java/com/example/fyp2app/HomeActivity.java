package com.example.fyp2app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    private TextView tvWelcome, tvDay, tvDate, tvTime;
    private Button btnLogout, btnAttendance, btnAttendanceRecord, btnEditUser, btnProfile, btnTimeOffRequest, btnViewTimeOffRequests, btnTimeOffRequested;
    private DatabaseReference databaseReference;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private String email, userType, userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Retrieve extras from intent
        email = getIntent().getStringExtra("email");
        userType = getIntent().getStringExtra("userType");

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance("https://fyp2app-aab47-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Users").child(email.replace(".", ",")); // Firebase-safe email

        // Initialize UI elements
        initializeUI();

        // Initialize location provider
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Display current day, date, and time
        updateDateTime();

        // Fetch and display user data
        fetchUserData();

        // Set up button actions
        handleButtonActions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUIBasedOnUserType();
    }

    private void initializeUI() {
        tvWelcome = findViewById(R.id.tvWelcome);
        tvDay = findViewById(R.id.tvDay);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);

        btnLogout = findViewById(R.id.btnLogout);
        btnAttendance = findViewById(R.id.btnAttendance);
        btnAttendanceRecord = findViewById(R.id.btnAttendanceRecord);
        btnEditUser = findViewById(R.id.btnEditUser);
        btnProfile = findViewById(R.id.btnProfile);
        btnTimeOffRequest = findViewById(R.id.btnTimeOffRequest);
        btnTimeOffRequested = findViewById(R.id.btnTimeOffRequested); // Corrected button ID
        btnViewTimeOffRequests = findViewById(R.id.btnViewTimeOffRequests);
    }

    private void fetchUserData() {
        Log.d(TAG, "Fetching user data...");
        databaseReference.get()
                .addOnSuccessListener(snapshot -> {
                    userName = snapshot.child("name").getValue(String.class);
                    if (userName != null) {
                        tvWelcome.setText("Welcome, " + userName + "!");
                        Log.d(TAG, "User data loaded: " + userName);
                    } else {
                        tvWelcome.setText("Welcome, User!");
                        Log.w(TAG, "User name is null.");
                    }
                })
                .addOnFailureListener(e -> {
                    tvWelcome.setText("Welcome, User!");
                    Toast.makeText(this, "Error loading user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to fetch user data.", e);
                });
    }

    private void updateUIBasedOnUserType() {
        if ("Employer".equals(userType)) {
            btnTimeOffRequest.setVisibility(View.GONE); // Hide Time Off Request
            btnTimeOffRequested.setVisibility(View.GONE); // Hide Time Off Requested
            btnViewTimeOffRequests.setVisibility(View.VISIBLE); // Show View Time Off Requests
            btnAttendanceRecord.setVisibility(View.VISIBLE); // Show Attendance Records
        } else if ("Employee".equals(userType)) {
            btnTimeOffRequest.setVisibility(View.VISIBLE); // Show Time Off Request
            btnTimeOffRequested.setVisibility(View.VISIBLE); // Show Time Off Requested
            btnViewTimeOffRequests.setVisibility(View.GONE); // Hide View Time Off Requests
            btnAttendanceRecord.setVisibility(View.GONE); // Hide Attendance Records
        }
    }

    private void handleButtonActions() {
        btnEditUser.setOnClickListener(v -> navigateTo(EditUserActivity.class));
        btnProfile.setOnClickListener(v -> navigateTo(ProfileActivity.class));
        btnAttendanceRecord.setOnClickListener(v -> navigateTo(AttendanceRecordActivity.class));
        btnTimeOffRequest.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, TimeOffRequestActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("userType", userType);
            intent.putExtra("userName", userName); // Pass employee name to TimeOffRequestActivity
            startActivity(intent);
        });
        btnTimeOffRequested.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, TimeOffRequestedActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("userName", userName);
            startActivity(intent);
        });
        btnViewTimeOffRequests.setOnClickListener(v -> navigateTo(ViewTimeOffRequestsActivity.class));
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
        btnAttendance.setOnClickListener(v -> recordAttendance());
    }

    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(HomeActivity.this, activityClass);
        intent.putExtra("email", email);
        intent.putExtra("userType", userType);
        intent.putExtra("userName", userName);
        startActivity(intent);
    }

    private void updateDateTime() {
        String currentDay = new SimpleDateFormat("EEEE", Locale.getDefault()).format(new Date());
        String currentDate = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        tvDay.setText(currentDay);
        tvDate.setText(currentDate);
        tvTime.setText(currentTime);
    }

    private void recordAttendance() {
        Log.d(TAG, "Attempting to record attendance...");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

                        DatabaseReference attendanceRef = FirebaseDatabase.getInstance("https://fyp2app-aab47-default-rtdb.asia-southeast1.firebasedatabase.app")
                                .getReference("AttendanceRecords").push();

                        AttendanceRecord newRecord = new AttendanceRecord(
                                userName, currentDate, currentTime, latitude, longitude
                        );

                        attendanceRef.setValue(newRecord)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(HomeActivity.this, "Attendance Recorded!", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Attendance recorded successfully.");
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(HomeActivity.this, "Failed to record attendance.", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "Error writing attendance data: " + e.getMessage());
                                });
                    } else {
                        Toast.makeText(HomeActivity.this, "Failed to retrieve location. Try again.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Location retrieval failed. Location is null.");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(HomeActivity.this, "Error retrieving location.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to retrieve location: " + e.getMessage());
                });
    }
}



