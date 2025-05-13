package com.example.fyp2app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class TimeOffRequestAdapter extends RecyclerView.Adapter<TimeOffRequestAdapter.TimeOffViewHolder> {

    private final Context context;
    private final List<TimeOffRequest> timeOffRequestList;

    public TimeOffRequestAdapter(Context context, List<TimeOffRequest> timeOffRequestList) {
        this.context = context;
        this.timeOffRequestList = timeOffRequestList;
    }

    @NonNull
    @Override
    public TimeOffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_time_off_request, parent, false);
        return new TimeOffViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeOffViewHolder holder, int position) {
        TimeOffRequest request = timeOffRequestList.get(position);

        // Bind data to views
        holder.tvName.setText(request.getEmployeeName());
        holder.tvType.setText("Type: " + request.getType());
        holder.tvReason.setText("Reason: " + request.getReason());
        holder.tvDateRange.setText("From: " + request.getStartDate() + " To: " + request.getEndDate());
        holder.tvStatus.setText("Status: " + request.getStatus());

        // Setup Spinner for approval status
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context, R.array.approval_statuses, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerStatus.setAdapter(adapter);

        // Set initial value for Spinner
        int initialPosition = getStatusPosition(request.getStatus());
        holder.spinnerStatus.setSelection(initialPosition);

        // Handle Spinner item selection
        holder.spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int selectedPosition, long id) {
                String selectedStatus = parent.getItemAtPosition(selectedPosition).toString();

                // Only update if the status has changed
                if (!selectedStatus.equals(request.getStatus())) {
                    request.setStatus(selectedStatus);
                    holder.tvStatus.setText("Status: " + selectedStatus);

                    // Save the updated status to Firebase
                    saveStatusToFirebase(request);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Handle Delete Button
        holder.btnDelete.setOnClickListener(v -> deleteRequestFromFirebase(request, holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return timeOffRequestList.size();
    }

    /**
     * Gets the position of the status in the array.
     *
     * @param status The status to find.
     * @return The position in the array.
     */
    private int getStatusPosition(String status) {
        String[] statuses = context.getResources().getStringArray(R.array.approval_statuses);
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equalsIgnoreCase(status)) {
                return i;
            }
        }
        return 0; // Default to the first item (e.g., "Pending")
    }

    /**
     * Saves the updated status to Firebase.
     *
     * @param request The time-off request to update.
     */
    private void saveStatusToFirebase(TimeOffRequest request) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://fyp2app-aab47-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("TimeOffRequests").child(request.getId());
        databaseReference.child("status").setValue(request.getStatus())
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Status updated successfully.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to update status: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /**
     * Deletes a request from Firebase and updates the RecyclerView.
     *
     * @param request  The request to delete.
     * @param position The position in the list.
     */
    private void deleteRequestFromFirebase(TimeOffRequest request, int position) {
        if (request.getId() == null || request.getId().isEmpty()) {
            Toast.makeText(context, "Error: Request ID is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://fyp2app-aab47-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("TimeOffRequests").child(request.getId());
        databaseReference.removeValue()
                .addOnSuccessListener(aVoid -> {
                    timeOffRequestList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, timeOffRequestList.size());
                    Toast.makeText(context, "Request deleted successfully.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete request: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /**
     * ViewHolder class for the RecyclerView items.
     */
    public static class TimeOffViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvType, tvReason, tvDateRange, tvStatus;
        Spinner spinnerStatus;
        Button btnDelete;

        public TimeOffViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvEmployeeName);
            tvType = itemView.findViewById(R.id.tvType);
            tvReason = itemView.findViewById(R.id.tvReason);
            tvDateRange = itemView.findViewById(R.id.tvDateRange);
            spinnerStatus = itemView.findViewById(R.id.spinnerApprovalStatus);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}


