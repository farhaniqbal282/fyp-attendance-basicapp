package com.example.fyp2app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private Button btnLogin, btnRegister;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Database reference with updated URL
        databaseReference = FirebaseDatabase.getInstance("https://fyp2app-aab47-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Users");

        // Initialize UI elements
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // Set up Login button click listener
        btnLogin.setOnClickListener(v -> navigateTo(LoginActivity.class));

        // Set up Register button click listener
        btnRegister.setOnClickListener(v -> navigateTo(RegisterActivity.class));
    }

    /**
     * Helper method to navigate to the specified activity.
     *
     * @param activityClass The target activity class.
     */
    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(MainActivity.this, activityClass);
        startActivity(intent);
    }
}
