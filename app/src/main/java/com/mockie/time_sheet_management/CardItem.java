package com.mockie.time_sheet_management;

public class CardItem {
    private String projectName;
    private String taskName;
    private String assignee;
    private String startDate;
    private String endDate;
    private String status;

    // Constructor
    public CardItem(String projectName, String taskName, String assignee, String startDate, String endDate, String status) {
        this.projectName = projectName;
        this.taskName = taskName;
        this.assignee = assignee;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    // Getters and setters (if needed)
    // ...

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
