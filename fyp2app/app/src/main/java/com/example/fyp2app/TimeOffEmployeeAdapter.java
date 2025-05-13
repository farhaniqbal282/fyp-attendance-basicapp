package com.example.fyp2app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TimeOffEmployeeAdapter extends RecyclerView.Adapter<TimeOffEmployeeAdapter.ViewHolder> {

    private final Context context;
    private final List<TimeOffRequest> timeOffRequests;

    public TimeOffEmployeeAdapter(Context context, List<TimeOffRequest> timeOffRequests) {
        this.context = context;
        this.timeOffRequests = timeOffRequests;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_time_off_request_employee, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TimeOffRequest request = timeOffRequests.get(position);

        // Bind data to the views
        holder.tvType.setText("Type: " + request.getType());
        holder.tvDateRange.setText(String.format("From: %s To: %s", request.getStartDate(), request.getEndDate()));
        holder.tvReason.setText("Reason: " + request.getReason());
        holder.tvStatus.setText("Status: " + request.getStatus());

        // Dynamically change status color based on its value
        int statusColor;
        switch (request.getStatus().toLowerCase()) {
            case "approved":
                statusColor = android.R.color.holo_green_dark;
                break;
            case "rejected":
                statusColor = android.R.color.holo_red_dark;
                break;
            default: // Pending or other statuses
                statusColor = android.R.color.holo_orange_dark;
                break;
        }
        holder.tvStatus.setTextColor(context.getResources().getColor(statusColor));
    }

    @Override
    public int getItemCount() {
        return timeOffRequests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvDateRange, tvReason, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views from the item layout
            tvType = itemView.findViewById(R.id.tvType);
            tvDateRange = itemView.findViewById(R.id.tvDateRange);
            tvReason = itemView.findViewById(R.id.tvReason);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}


