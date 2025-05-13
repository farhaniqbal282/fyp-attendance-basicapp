package com.example.fyp2app;

public class EmployeeAttendance {
    private String name;
    private String date;
    private boolean recorded;

    public EmployeeAttendance() {}

    public EmployeeAttendance(String name, String date, boolean recorded) {
        this.name = name;
        this.date = date;
        this.recorded = recorded;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public boolean isRecorded() {
        return recorded;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setRecorded(boolean recorded) {
        this.recorded = recorded;
    }

    @Override
    public String toString() {
        return "EmployeeAttendance{" +
                "name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", recorded=" + recorded +
                '}';
    }
}


