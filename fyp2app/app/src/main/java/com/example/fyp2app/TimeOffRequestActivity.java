package com.example.fyp2app;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TimeOffRequestActivity extends AppCompatActivity {

    private Spinner spinnerType, spinnerFullDay;
    private EditText etReason;
    private TextView tvStartDate, tvEndDate;
    private Button btnCreateTask, btnBack;
    private DatabaseReference databaseReference;
    private String email, employeeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_off_request);

        // Retrieve data from Intent
        email = getIntent().getStringExtra("email");
        employeeName = getIntent().getStringExtra("userName"); // Ensure correct key for employee name

        if (employeeName == null || employeeName.isEmpty()) {
            Toast.makeText(this, "Error: Employee name is missing.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance("https://fyp2app-aab47-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("TimeOffRequests");

        // Initialize UI elements
        spinnerType = findViewById(R.id.spinnerType);
        spinnerFullDay = findViewById(R.id.spinnerFullDay);
        etReason = findViewById(R.id.etReason);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        btnCreateTask = findViewById(R.id.btnCreateTask);
        btnBack = findViewById(R.id.btnBack);

        // Back Button Functionality
        btnBack.setOnClickListener(v -> onBackPressed());

        // Setup Spinners
        setupSpinner(spinnerType, R.array.leave_types);
        setupSpinner(spinnerFullDay, R.array.day_options);

        // Start Date Picker
        findViewById(R.id.btnStartDate).setOnClickListener(v -> showDatePickerDialog(tvStartDate));

        // End Date Picker
        findViewById(R.id.btnEndDate).setOnClickListener(v -> showDatePickerDialog(tvEndDate));

        // Handle Submit Button
        btnCreateTask.setOnClickListener(v -> handleCreateTask());
    }

    private void setupSpinner(Spinner spinner, int arrayResId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, arrayResId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void showDatePickerDialog(TextView textView) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (DatePicker view, int year, int month, int dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);
                    String formattedDate = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                            .format(selectedDate.getTime());
                    textView.setText(formattedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void handleCreateTask() {
        String type = spinnerType.getSelectedItem().toString();
        String fullDay = spinnerFullDay.getSelectedItem().toString();
        String reason = etReason.getText().toString().trim();
        String startDate = tvStartDate.getText().toString();
        String endDate = tvEndDate.getText().toString();

        // Validate fields
        if (reason.isEmpty() || startDate.equals("Select start date") || endDate.equals("Select end date")) {
            Toast.makeText(TimeOffRequestActivity.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = databaseReference.push().getKey();
        if (id == null) {
            Toast.makeText(TimeOffRequestActivity.this, "Error creating request. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        TimeOffRequest request = new TimeOffRequest(employeeName, id, startDate, endDate, reason, type, "Pending");

        // Push data to Firebase
        databaseReference.child(id).setValue(request)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(TimeOffRequestActivity.this, "Request Submitted Successfully.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("TimeOffRequestActivity", "Failed to submit request: " + e.getMessage());
                    Toast.makeText(TimeOffRequestActivity.this, "Failed to submit request.", Toast.LENGTH_SHORT).show();
                });
    }
}


