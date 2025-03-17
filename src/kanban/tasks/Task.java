package kanban.tasks;

import java.util.Objects;
import static kanban.tasks.TaskStatus.*;

public class Task {

    private Integer taskID;
    private String taskName;
    private String description;
    private TaskStatus taskStatus;

    // Constructor
    public Task(String taskName,
                String description) {
        this.taskName = taskName;
        this.description = description;
        taskID = 0;
        taskStatus = NEW;
    }

    // Setters and getters
    public String getName() {
        return taskName;
    }

    public void setName(String taskName) {
        this.taskName = taskName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getID() {
        return taskID;
    }

    public void setID(Integer taskID) {
        this.taskID = taskID;
    }

    public TaskStatus getStatus() {
        return taskStatus;
    }

    public void setStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    @Override
    public boolean equals(Object taskObject) {
        if (this == taskObject) return true;
        if (taskObject == null || getClass() != taskObject.getClass()) return false;
        Task task = (Task) taskObject;
        return taskID.equals(task.taskID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskID);
    }
}
