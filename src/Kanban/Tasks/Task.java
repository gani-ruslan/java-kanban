package Kanban.Tasks;

import static Kanban.Tasks.TaskStatus.*;

public class Task {
    // Kanban.Tasks.Task fields: base (current edition)
    private Integer taskID;
    private String taskName;
    private String description;
    private TaskStatus taskStatus;

    // Constructor
    public Task(Integer taskID,
                String taskName,
                String description,
                TaskStatus taskStatus) {
        this.taskName = taskName;
        this.description = description;
        this.taskID = taskID;
        this.taskStatus = taskStatus;
    }

    public Task(Integer taskID,
                String taskName,
                String description) {
        this.taskName = taskName;
        this.description = description;
        this.taskID = taskID;
        taskStatus = NEW;
    }

    public Task(String taskName,
                String description) {
        this.taskName = taskName;
        this.description = description;
        taskID = -1;
        taskStatus = NEW;
    }


    // Setters and getters
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTaskID() {
        return taskID;
    }

    public void setTaskID(Integer taskID) {
        this.taskID = taskID;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }
}
