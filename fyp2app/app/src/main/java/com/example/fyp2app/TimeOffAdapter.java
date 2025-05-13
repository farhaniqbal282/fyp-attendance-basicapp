package com.example.fyp2app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TimeOffAdapter extends RecyclerView.Adapter<TimeOffAdapter.TimeOffViewHolder> {

    private final Context context;
    private final List<TimeOffRequest> timeOffRequests;

    public TimeOffAdapter(Context context, List<TimeOffRequest> timeOffRequests) {
        this.context = context;
        this.timeOffRequests = timeOffRequests;
    }

    @NonNull
    @Override
    public TimeOffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_time_off_request, parent, false);
        return new TimeOffViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeOffViewHolder holder, int position) {
        TimeOffRequest request = timeOffRequests.get(position);
        holder.tvEmployeeName.setText(request.getEmployeeName());
        holder.tvDateRange.setText(String.format("%s to %s", request.getStartDate(), request.getEndDate()));
        holder.tvStatus.setText(request.getStatus());
    }

    @Override
    public int getItemCount() {
        return timeOffRequests.size();
    }

    public static class TimeOffViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmployeeName, tvDateRange, tvStatus;

        public TimeOffViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmployeeName = itemView.findViewById(R.id.tvEmployeeName);
            tvDateRange = itemView.findViewById(R.id.tvDateRange);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
