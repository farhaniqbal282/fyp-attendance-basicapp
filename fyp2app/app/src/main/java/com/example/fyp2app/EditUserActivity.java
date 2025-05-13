package com.example.fyp2app;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditUserActivity extends AppCompatActivity {
    private EditText etNewPhoneNumber, etNewPassword;
    private Button btnSave;
    private DatabaseReference databaseReference;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        // Enable Back Button in the Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Edit User");
        }

        // Initialize UI elements
        etNewPhoneNumber = findViewById(R.id.etNewPhoneNumber);
        etNewPassword = findViewById(R.id.etNewPassword);
        btnSave = findViewById(R.id.btnSave);

        // Get the email from Intent
        email = getIntent().getStringExtra("email");

        // Validate the email
        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "Invalid user. Cannot edit.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firebase Realtime Database reference with the updated URL
        databaseReference = FirebaseDatabase.getInstance("https://fyp2app-aab47-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Users").child(email.replace(".", ",")); // Use Firebase-safe email format

        // Set up Save button click listener
        btnSave.setOnClickListener(v -> updateUserDetails());
    }

    /**
     * Updates user details in the Firebase Realtime Database.
     */
    private void updateUserDetails() {
        String newPhoneNumber = etNewPhoneNumber.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();

        // Validate if at least one field is filled
        if (newPhoneNumber.isEmpty() && newPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in at least one field to update.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Track if any updates were made
        boolean isUpdated = false;

        // Update phone number if provided
        if (!newPhoneNumber.isEmpty()) {
            databaseReference.child("phoneNumber").setValue(newPhoneNumber)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Phone number updated successfully.", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to update phone number: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            isUpdated = true;
        }

        // Update password if provided
        if (!newPassword.isEmpty()) {
            databaseReference.child("password").setValue(newPassword)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Password updated successfully.", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to update password: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            isUpdated = true;
        }

        // Finish activity if any updates were made
        if (isUpdated) {
            Toast.makeText(this, "User details updated successfully.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
