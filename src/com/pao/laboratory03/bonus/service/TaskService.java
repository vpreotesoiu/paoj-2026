package com.pao.laboratory03.bonus.service;

import com.pao.laboratory03.bonus.exception.InvalidTransitionException;
import com.pao.laboratory03.bonus.exception.TaskNotFoundException;
import com.pao.laboratory03.bonus.model.Priority;
import com.pao.laboratory03.bonus.model.Status;
import com.pao.laboratory03.bonus.model.Task;
import com.pao.laboratory03.exercise.model.Student;
import com.pao.laboratory03.exercise.service.StudentService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskService {
    private Integer crtId;
    private Map<String, Task> tasksById;
    private Map<Priority, List<Task>> tasksByPriority;
    private List<String> auditLog;

    private TaskService() {
        crtId = 1;
        tasksById = new HashMap<String, Task>();
        tasksByPriority = new HashMap<Priority, List<Task>>();
        auditLog = new ArrayList<String>();
    }

    private static class Holder {
        private static final TaskService INSTANCE = new TaskService();
    }

    public static TaskService getInstance() {
        return TaskService.Holder.INSTANCE;
    }

    public Task addTask(String title, Priority priority) {
        String idStr = crtId.toString();
        while (idStr.length() < 4) {
            idStr = "0" + idStr;
        }
        idStr = "T" + idStr;
        Task t = new Task(idStr, title, Status.TODO, priority, null);
        tasksById.put(idStr, t);
        if (!tasksByPriority.containsKey(priority)) {
            tasksByPriority.put(priority, new ArrayList<Task>());
        }
        tasksByPriority.get(priority).add(t);
        ++crtId;
        auditLog.add("[ADD] " + idStr + ": '" + title + "' (" + priority + ")");
        System.out.println("Adaugat: " + t);
        return t;
    }

    public void assignTask(String taskId, String assignee) {
        if (tasksById.containsKey(taskId)) {
            Task t = tasksById.get(taskId);
            t.setAssignee(assignee);
            tasksById.put(taskId, t);
            auditLog.add("[ASSIGN] " + taskId + " -> " + assignee);
            System.out.println(taskId + " -> " + assignee);
            return;
        }
        throw new TaskNotFoundException("Id-ul task-ului '" + taskId + "' nu exista!");
    }

    public void changeStatus(String taskId, Status newStatus) {
        if (tasksById.containsKey(taskId)) {
            Task t = tasksById.get(taskId);
            if (!t.getStatus().canTransitionTo(newStatus)) {
                InvalidTransitionException te = new InvalidTransitionException(t.getStatus(), newStatus);
                System.out.println(t.getStatus() + " -> " + newStatus + " " + te.getMessage());
                throw te;
            }
            Status oldStatus = t.getStatus();
            t.setStatus(newStatus);
            auditLog.add("[STATUS] " + taskId + ": " + oldStatus.name() + " -> " + newStatus.name());
            System.out.println(taskId + ": " + oldStatus.name() + " -> " + newStatus.name() + " ✓");
            return;
        }
        throw new TaskNotFoundException("Id-ul task-ului '" + taskId + "' nu exista!");
    }

    public List<Task> getTasksByPriority(Priority priority) {
        if (!tasksByPriority.containsKey(priority)) {
            tasksByPriority.put(priority, new ArrayList<Task>());
        }
        return tasksByPriority.get(priority);
    }

    public Map<Status, Long> getStatusSummary() {
        Map<Status, Long> taskCount = new HashMap<Status, Long>();
        for (Map.Entry<String, Task> elem: tasksById.entrySet()) {
            Task t = elem.getValue();
            Status taskStatus = t.getStatus();
            if (!taskCount.containsKey(taskStatus)) {
                taskCount.put(taskStatus, (long) 0);
            }
            taskCount.put(taskStatus, taskCount.get(taskStatus) + 1);
        }
        return taskCount;
    }

    public List<Task> getUnassignedTasks() {
        List<Task> tasks = new ArrayList<Task>();
        for (Map.Entry<String, Task> elem: tasksById.entrySet()) {
            Task t = elem.getValue();
            if (t.getAssignee() == null) {
                tasks.add(t);
            }
        }
        return tasks;
    }

    public void printAuditLog() {
        for (String s : auditLog) {
            System.out.println(s);
        }
    }

    public double getTotalUrgencyScore(int baseDays) {
        double sum = 0;
        for (Map.Entry<String, Task> elem: tasksById.entrySet()) {
            Task t = elem.getValue();
            Status taskStatus = t.getStatus();
            if (taskStatus == Status.CANCELLED || taskStatus == Status.DONE) {
                continue;
            }
            Priority p = t.getPriority();
            sum += p.calculateScore(baseDays);
        }
        return sum;
    }
}
