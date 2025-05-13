package com.example.fyp2app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class ViewAttendanceActivity extends AppCompatActivity {

    private static final String TAG = "ViewAttendanceActivity";

    private Spinner spinnerEmployees;
    private RecyclerView recyclerViewAttendance;
    private AttendanceAdapter attendanceAdapter;
    private List<EmployeeAttendance> attendanceList;
    private List<String> employeeNames;
    private DatabaseReference attendanceRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance);

        spinnerEmployees = findViewById(R.id.spinnerEmployees);
        recyclerViewAttendance = findViewById(R.id.recyclerViewAttendance);
        Button btnBack = findViewById(R.id.btnBack);

        recyclerViewAttendance.setLayoutManager(new LinearLayoutManager(this));

        attendanceList = new ArrayList<>();
        employeeNames = new ArrayList<>();
        attendanceAdapter = new AttendanceAdapter(this, attendanceList);
        recyclerViewAttendance.setAdapter(attendanceAdapter);

        attendanceRef = FirebaseDatabase.getInstance()
                .getReference("AttendanceRecords");

        fetchEmployeeNames();

        spinnerEmployees.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {
                    String selectedEmployee = employeeNames.get(position);
                    fetchAttendanceRecords(selectedEmployee);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                attendanceList.clear();
                attendanceAdapter.notifyDataSetChanged();
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }

    /**
     * Fetches unique employee names from the AttendanceRecords node in Firebase.
     */
    private void fetchEmployeeNames() {
        attendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Set<String> uniqueNames = new HashSet<>();
                employeeNames.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot recordSnapshot : snapshot.getChildren()) {
                        String name = recordSnapshot.child("name").getValue(String.class);
                        if (name != null) {
                            uniqueNames.add(name);
                        }
                    }

                    employeeNames.addAll(uniqueNames);

                    if (!employeeNames.isEmpty()) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(ViewAttendanceActivity.this,
                                android.R.layout.simple_spinner_item, employeeNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerEmployees.setAdapter(adapter);
                        Log.d(TAG, "Employee names fetched: " + employeeNames);
                    } else {
                        Toast.makeText(ViewAttendanceActivity.this, "No employees found in AttendanceRecords.", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Employee list is empty.");
                    }
                } else {
                    Log.d(TAG, "AttendanceRecords node does not exist.");
                    Toast.makeText(ViewAttendanceActivity.this, "No attendance records found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Failed to load employee names: " + error.getMessage());
                Toast.makeText(ViewAttendanceActivity.this, "Error fetching employee names.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Fetches attendance records for the selected employee from the AttendanceRecords node.
     *
     * @param employeeName The name of the selected employee.
     */
    private void fetchAttendanceRecords(String employeeName) {
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
                                    Log.d(TAG, "Attendance record fetched: " + record.toString());
                                }
                            }
                        } else {
                            Toast.makeText(ViewAttendanceActivity.this, "No attendance records found for " + employeeName, Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "No attendance records found for " + employeeName);
                        }

                        attendanceAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e(TAG, "Failed to load attendance records: " + error.getMessage());
                        Toast.makeText(ViewAttendanceActivity.this, "Error fetching attendance records.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}








