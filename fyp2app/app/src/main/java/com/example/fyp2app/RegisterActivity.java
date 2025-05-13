package com.example.fyp2app;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etPhoneNumber;
    private Spinner spinnerUserType;
    private Button btnRegister;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize UI elements
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        spinnerUserType = findViewById(R.id.spinnerUserType);
        btnRegister = findViewById(R.id.btnRegister);

        // Initialize Firebase Database reference with updated URL
        databaseReference = FirebaseDatabase.getInstance("https://fyp2app-aab47-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Users");

        // Set up the Spinner for user types
        setupUserTypeSpinner();

        // Handle Register Button Click
        btnRegister.setOnClickListener(v -> registerUser());
    }

    /**
     * Sets up the user type spinner with predefined options.
     */
    private void setupUserTypeSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserType.setAdapter(adapter);
    }

    /**
     * Validates user input and registers the user.
     */
    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().replace(".", ",").trim(); // Firebase-safe email
        String password = etPassword.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String userType = spinnerUserType.getSelectedItem().toString();

        // Input validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phoneNumber.isEmpty() || userType.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.replace(",", ".")).matches()) {
            Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save user to Firebase Realtime Database
        saveUserToDatabase(email, name, password, phoneNumber, userType);
    }

    /**
     * Saves user data to Firebase Realtime Database.
     *
     * @param email       Firebase-safe email
     * @param name        User's name
     * @param password    User's password
     * @param phoneNumber User's phone number
     * @param userType    User's type (e.g., Employee or Employer)
     */
    private void saveUserToDatabase(String email, String name, String password, String phoneNumber, String userType) {
        DatabaseReference userRef = databaseReference.child(email);

        userRef.child("name").setValue(name);
        userRef.child("password").setValue(password);
        userRef.child("phoneNumber").setValue(phoneNumber);
        userRef.child("userType").setValue(userType)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
