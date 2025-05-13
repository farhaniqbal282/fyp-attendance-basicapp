package com.example.fyp2app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AttendanceRecordActivity extends AppCompatActivity {

    private static final String TAG = "AttendanceRecordActivity";

    private Spinner spinnerEmployees;
    private RecyclerView recyclerViewAttendance;
    private AttendanceAdapter attendanceAdapter;
    private List<EmployeeAttendance> attendanceList;
    private List<String> employeeNames;
    private DatabaseReference attendanceRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_record);

        // Enable back button in the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Attendance Records");
        }

        // Initialize UI components
        spinnerEmployees = findViewById(R.id.spinnerAttendanceEmployees);
        recyclerViewAttendance = findViewById(R.id.recyclerViewAttendanceRecords);

        // Setup RecyclerView
        recyclerViewAttendance.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Lists
        attendanceList = new ArrayList<>();
        employeeNames = new ArrayList<>();

        // Initialize Adapter
        attendanceAdapter = new AttendanceAdapter(this, attendanceList);
        recyclerViewAttendance.setAdapter(attendanceAdapter);

        // Initialize Firebase Reference
        attendanceRef = FirebaseDatabase.getInstance("https://fyp2app-aab47-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("AttendanceRecords");

        // Fetch Employee Names
        fetchEmployeeNames();

        // Handle Spinner Item Selection
        spinnerEmployees.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {
                    String selectedEmployee = employeeNames.get(position);
                    Log.d(TAG, "Selected employee: " + selectedEmployee);
                    fetchAttendanceRecords(selectedEmployee);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                attendanceList.clear();
                attendanceAdapter.notifyDataSetChanged();
                Log.d(TAG, "No employee selected.");
            }
        });
    }

    private void fetchEmployeeNames() {
        Log.d(TAG, "Fetching employee names...");
        attendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Set<String> uniqueNames = new HashSet<>();
                employeeNames.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot recordSnapshot : snapshot.getChildren()) {
                        String name = recordSnapshot.child("name").getValue(String.class);
                        if (name != null && !name.trim().isEmpty()) {
                            uniqueNames.add(name);
                        }
                    }

                    employeeNames.addAll(uniqueNames);

                    if (!employeeNames.isEmpty()) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(AttendanceRecordActivity.this,
                                android.R.layout.simple_spinner_item, employeeNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerEmployees.setAdapter(adapter);
                        Log.d(TAG, "Employee names fetched successfully: " + employeeNames);
                    } else {
                        Toast.makeText(AttendanceRecordActivity.this, "No employees found.", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "No employee names available.");
                    }
                } else {
                    Toast.makeText(AttendanceRecordActivity.this, "No attendance records available.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "No attendance records exist in Firebase.");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Failed to fetch employee names: " + error.getMessage(), error.toException());
                Toast.makeText(AttendanceRecordActivity.this, "Error fetching employee names.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAttendanceRecords(String employeeName) {
        Log.d(TAG, "Fetching attendance records for: " + employeeName);
        attendanceRef.orderByChild("name").equalTo(employeeName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        attendanceList.clear();

                        if (snapshot.exists()) {
                            for (DataSnapshot recordSnapshot : snapshot.getChildren()) {
                                AttendanceRecord record = recordSnapshot.getValue(AttendanceRecord.class);

                                if (record != null) {
                                    attendanceList.add(new EmployeeAttendance(
                                            record.getName(),
                                            record.getDate() + " " + record.getTime(),
                                            true
                                    ));
                                    Log.d(TAG, "Record added: " + record);
                                }
                            }

                            if (attendanceList.isEmpty()) {
                                Toast.makeText(AttendanceRecordActivity.this, "No attendance records for " + employeeName, Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "No records found for: " + employeeName);
                            }
                        } else {
                            Toast.makeText(AttendanceRecordActivity.this, "No attendance records found.", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "No attendance records in Firebase for: " + employeeName);
                        }

                        attendanceAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e(TAG, "Error fetching attendance records: " + error.getMessage(), error.toException());
                        Toast.makeText(AttendanceRecordActivity.this, "Error fetching attendance records.", Toast.LENGTH_SHORT).show();
                    }
                });
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






