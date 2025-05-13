package com.example.fyp2app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EmployerFeatureActivity extends AppCompatActivity {

    private RecyclerView recyclerViewAttendance;
    private AttendanceAdapter attendanceAdapter;
    private List<EmployeeAttendance> employeeAttendanceList;
    private DatabaseReference attendanceRef;
    private Button btnViewAttendance;
    private static final String TAG = "EmployerFeatureActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_feature);

        recyclerViewAttendance = findViewById(R.id.recyclerViewAttendance);
        recyclerViewAttendance.setLayoutManager(new LinearLayoutManager(this));

        btnViewAttendance = findViewById(R.id.btnViewAttendance);

        employeeAttendanceList = new ArrayList<>();
        attendanceAdapter = new AttendanceAdapter(this, employeeAttendanceList);
        recyclerViewAttendance.setAdapter(attendanceAdapter);

        // Firebase Reference
        attendanceRef = FirebaseDatabase.getInstance().getReference("AttendanceRecords");

        // Fetch Data
        fetchAttendanceData();

        // View Attendance Button
        btnViewAttendance.setOnClickListener(v -> {
            Intent intent = new Intent(EmployerFeatureActivity.this, ViewAttendanceActivity.class);
            startActivity(intent);
        });
    }

    private void fetchAttendanceData() {
        attendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                employeeAttendanceList.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot recordSnapshot : snapshot.getChildren()) {
                        AttendanceRecord record = recordSnapshot.getValue(AttendanceRecord.class);

                        if (record != null) {
                            employeeAttendanceList.add(new EmployeeAttendance(
                                    record.getName(),
                                    record.getDate() + " " + record.getTime(),
                                    true
                            ));
                            Log.d(TAG, "Record fetched: " + record.toString());
                        }
                    }
                } else {
                    Log.d(TAG, "No records found in AttendanceRecords node.");
                }

                attendanceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Failed to load attendance data: " + error.getMessage());
                Toast.makeText(EmployerFeatureActivity.this, "Failed to load attendance data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}






