package com.example.fyp2app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ViewTimeOffRequestsActivity extends AppCompatActivity {

    private static final String TAG = "ViewTimeOffRequests";

    private RecyclerView recyclerViewTimeOff;
    private TimeOffRequestAdapter timeOffRequestAdapter;
    private List<TimeOffRequest> timeOffRequestList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_time_off_requests);

        // Set up back button in the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("View Time Off Requests");
        }

        // Initialize RecyclerView
        recyclerViewTimeOff = findViewById(R.id.recyclerViewTimeOff);
        recyclerViewTimeOff.setLayoutManager(new LinearLayoutManager(this));

        // Initialize List and Adapter
        timeOffRequestList = new ArrayList<>();
        timeOffRequestAdapter = new TimeOffRequestAdapter(this, timeOffRequestList);
        recyclerViewTimeOff.setAdapter(timeOffRequestAdapter);

        // Initialize Firebase Reference
        databaseReference = FirebaseDatabase.getInstance("https://fyp2app-aab47-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("TimeOffRequests");

        // Fetch Time-Off Requests
        fetchTimeOffRequests();
    }

    /**
     * Fetches time-off requests from Firebase and updates the RecyclerView.
     */
    private void fetchTimeOffRequests() {
        Log.d(TAG, "Fetching time-off requests...");
        databaseReference.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                timeOffRequestList.clear();
                for (DataSnapshot requestSnapshot : snapshot.getChildren()) {
                    TimeOffRequest request = requestSnapshot.getValue(TimeOffRequest.class);
                    if (request != null) {
                        request.setId(requestSnapshot.getKey()); // Set unique ID for reference
                        timeOffRequestList.add(request);
                    }
                }
                timeOffRequestAdapter.notifyDataSetChanged();
                if (timeOffRequestList.isEmpty()) {
                    Toast.makeText(ViewTimeOffRequestsActivity.this, "No time-off requests available.", Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, "Time-off requests fetched successfully.");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Error fetching time-off requests: " + error.getMessage(), error.toException());
                Toast.makeText(ViewTimeOffRequestsActivity.this, "Failed to fetch requests.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close the current activity and return to the previous screen
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}




