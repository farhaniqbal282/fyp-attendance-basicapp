package com.example.fyp2app;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvPhoneNumber, tvUserType;
    private Button btnBack;
    private DatabaseReference databaseReference;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize UI elements
        tvName = findViewById(R.id.tvName);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        tvUserType = findViewById(R.id.tvUserType);
        btnBack = findViewById(R.id.btnBack);

        // Handle Back Button
        btnBack.setOnClickListener(v -> finish());

        // Get email from Intent and format it for Firebase
        email = getIntent().getStringExtra("email");
        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "Invalid email provided!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String sanitizedEmail = email.replace(".", ",");

        // Initialize Firebase reference with updated URL
        databaseReference = FirebaseDatabase.getInstance("https://fyp2app-aab47-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Users").child(sanitizedEmail);

        // Fetch and display user details
        fetchUserDetails();
    }

    /**
     * Fetch user details from Firebase Realtime Database and update UI.
     */
    private void fetchUserDetails() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String phoneNumber = snapshot.child("phoneNumber").getValue(String.class);
                    String userType = snapshot.child("userType").getValue(String.class);

                    tvName.setText("Name: " + (name != null ? name : "Not Available"));
                    tvPhoneNumber.setText("Phone Number: " + (phoneNumber != null ? phoneNumber : "Not Available"));
                    tvUserType.setText("User Type: " + (userType != null ? userType : "Not Available"));
                } else {
                    displayNoDataFound();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                displayError(error.getMessage());
            }
        });
    }

    /**
     * Display a message and default values when no user data is found.
     */
    private void displayNoDataFound() {
        tvName.setText("Name: Not Available");
        tvPhoneNumber.setText("Phone Number: Not Available");
        tvUserType.setText("User Type: Not Available");
        Toast.makeText(this, "User data not found!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Display an error message and update UI with default error messages.
     *
     * @param errorMessage The error message to display.
     */
    private void displayError(String errorMessage) {
        tvName.setText("Error loading details");
        tvPhoneNumber.setText("Error loading details");
        tvUserType.setText("Error loading details");
        Toast.makeText(this, "Failed to load user data: " + errorMessage, Toast.LENGTH_SHORT).show();
    }
}
