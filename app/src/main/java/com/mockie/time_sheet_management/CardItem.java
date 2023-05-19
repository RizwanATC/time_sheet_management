package com.mockie.time_sheet_management;

public class CardItem {
    private String project;
    private String task;
    private String assignedTo;
    private String from;
    private String to;
    private String status;

    public CardItem(String project, String task, String assignedTo, String from, String to, String status) {
        this.project = project;
        this.task = task;
        this.assignedTo = assignedTo;
        this.from = from;
        this.to = to;
        this.status = status;
    }

    public String getProject() {
        return project;
    }

    public String getTask() {
        return task;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getStatus() {
        return status;
    }
}
