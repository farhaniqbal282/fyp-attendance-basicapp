package com.example.fyp2app;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TimeOffRequestedActivity extends AppCompatActivity {

    private static final String TAG = "TimeOffRequestedActivity";

    private RecyclerView recyclerViewTimeOffRequests;
    private TimeOffEmployeeAdapter timeOffEmployeeAdapter;
    private List<TimeOffRequest> timeOffRequestList;
    private DatabaseReference databaseReference;

    private String employeeName; // Passed from the previous activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_off_requested);

        // Set up the back button in the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Time Off Requested");
        }

        // Retrieve employee name from Intent
        employeeName = getIntent().getStringExtra("userName");

        // Validate the employee name
        if (employeeName == null || employeeName.isEmpty()) {
            Toast.makeText(this, "Error: Employee name is missing.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize RecyclerView
        recyclerViewTimeOffRequests = findViewById(R.id.recyclerViewTimeOffRequests);
        recyclerViewTimeOffRequests.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Adapter and List
        timeOffRequestList = new ArrayList<>();
        timeOffEmployeeAdapter = new TimeOffEmployeeAdapter(this, timeOffRequestList);
        recyclerViewTimeOffRequests.setAdapter(timeOffEmployeeAdapter);

        // Initialize Firebase Database Reference
        databaseReference = FirebaseDatabase.getInstance("https://fyp2app-aab47-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("TimeOffRequests");

        // Fetch time-off requests for the employee
        fetchTimeOffRequests();
    }

    private void fetchTimeOffRequests() {
        Log.d(TAG, "Fetching time-off requests for employee: " + employeeName);

        // Query Firebase to get time-off requests for the specific employee
        databaseReference.orderByChild("employeeName").equalTo(employeeName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        timeOffRequestList.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot requestSnapshot : snapshot.getChildren()) {
                                TimeOffRequest request = requestSnapshot.getValue(TimeOffRequest.class);
                                if (request != null) {
                                    timeOffRequestList.add(request);
                                }
                            }
                            if (timeOffRequestList.isEmpty()) {
                                Toast.makeText(TimeOffRequestedActivity.this, "No time-off requests found.", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Time-off request list is empty.");
                            }
                        } else {
                            Toast.makeText(TimeOffRequestedActivity.this, "No time-off requests found.", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "No records exist for the provided employee name.");
                        }
                        timeOffEmployeeAdapter.notifyDataSetChanged();
                        Log.d(TAG, "Time-off requests loaded successfully.");
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e(TAG, "Error fetching time-off requests: " + error.getMessage(), error.toException());
                        Toast.makeText(TimeOffRequestedActivity.this, "Error fetching data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close the current activity and return to the previous screen
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}




