package com.example.fyp2app;

/**
 * Represents a time-off request made by an employee.
 */
public class TimeOffRequest {
    private String employeeName; // Name of the employee requesting time off
    private String id;           // Unique ID for the request
    private String startDate;    // Start date of the leave
    private String endDate;      // End date of the leave
    private String reason;       // Reason for the leave
    private String type;         // Type of leave (e.g., Annual leave, Sick leave)
    private String status;       // Status of the request (e.g., Pending, Approved, Rejected)
    private boolean selected;    // Indicates if the request is selected (for UI purposes)

    /**
     * Default no-argument constructor required for Firebase.
     */
    public TimeOffRequest() {
    }

    /**
     * Parameterized constructor to initialize a TimeOffRequest object.
     *
     * @param employeeName Name of the employee requesting time off
     * @param id           Unique ID for the request
     * @param startDate    Start date of the leave
     * @param endDate      End date of the leave
     * @param reason       Reason for the leave
     * @param type         Type of leave (e.g., Annual leave)
     * @param status       Status of the request (e.g., Pending, Approved, Rejected)
     */
    public TimeOffRequest(String employeeName, String id, String startDate, String endDate, String reason, String type, String status) {
        this.employeeName = employeeName;
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.type = type;
        this.status = status;
        this.selected = false; // Default value for UI selection
    }

    // Getters and Setters

    /**
     * Gets the employee name.
     *
     * @return The employee name.
     */
    public String getEmployeeName() {
        return employeeName;
    }

    /**
     * Sets the employee name.
     *
     * @param employeeName The employee name to set.
     */
    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    /**
     * Gets the unique request ID.
     *
     * @return The unique request ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique request ID.
     *
     * @param id The unique request ID to set.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the start date of the leave.
     *
     * @return The start date.
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date of the leave.
     *
     * @param startDate The start date to set.
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets the end date of the leave.
     *
     * @return The end date.
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * Sets the end date of the leave.
     *
     * @param endDate The end date to set.
     */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    /**
     * Gets the reason for the leave.
     *
     * @return The reason.
     */
    public String getReason() {
        return reason;
    }

    /**
     * Sets the reason for the leave.
     *
     * @param reason The reason to set.
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * Gets the type of leave.
     *
     * @return The leave type.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of leave.
     *
     * @param type The leave type to set.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the status of the request.
     *
     * @return The status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the request.
     *
     * @param status The status to set.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Checks if the request is selected (for UI purposes).
     *
     * @return True if selected; false otherwise.
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Sets the selected state of the request.
     *
     * @param selected The selected state to set.
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return "TimeOffRequest{" +
                "employeeName='" + employeeName + '\'' +
                ", id='" + id + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", reason='" + reason + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", selected=" + selected +
                '}';
    }
}
