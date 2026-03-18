package com.pao.laboratory03.bonus.model;

public class Task {
    private String id;
    private String title;
    private Status status;
    private Priority priority;
    private String assignee;

    public Task(String id, String title, Status status, Priority priority, String assignee) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.priority = priority;
        this.assignee = assignee;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Status getStatus() {
        return status;
    }

    public Priority getPriority() {
        return priority;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    @Override
    public String toString() {
        return "Task{id='" + id + "', title='" + title + "', priority=" + priority.name() + ", status=" + status.name() + "}";
    }
}
