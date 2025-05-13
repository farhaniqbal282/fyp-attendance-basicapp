package com.example.fyp2app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {

    private final Context context;
    private final List<EmployeeAttendance> employeeAttendanceList;

    public AttendanceAdapter(Context context, List<EmployeeAttendance> employeeAttendanceList) {
        this.context = context;
        this.employeeAttendanceList = employeeAttendanceList;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_attendance, parent, false);
        return new AttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {
        EmployeeAttendance attendance = employeeAttendanceList.get(position);

        holder.tvName.setText(attendance.getName());
        holder.tvDate.setText(attendance.getDate());

        if (attendance.isRecorded()) {
            holder.tvStatus.setText("Recorded");
            holder.tvStatus.setBackgroundResource(R.drawable.rounded_green_background);
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        } else {
            holder.tvStatus.setText("No Record");
            holder.tvStatus.setBackgroundResource(R.drawable.rounded_red_background);
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return employeeAttendanceList.size();
    }

    public static class AttendanceViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDate, tvStatus;

        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}



