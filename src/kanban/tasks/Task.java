package kanban.tasks;

import java.util.Objects;
import static kanban.tasks.TaskStatus.*;

public class Task {

    private Integer id;
    private String title;
    private String description;
    private TaskStatus status;

    public Task(String title,
                String description) {
        this.title = title;
        this.description = description;
        id = 0;
        status = NEW;
    }

    public Task(String title,
                String description,
                Integer id,
                TaskStatus status) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public Task(Task task) {
        title = task.getTitle();
        description = task.getDescription();
        id = task.getID();
        status = task.getStatus();
    }

    public void setID(Integer ID) {
        this.id = ID;
    }

    public Integer getID() {
        return id;
    }

    public void setTitle(String taskName) {
        this.title = taskName;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setStatus(TaskStatus taskStatus) {
        this.status = taskStatus;
    }

    public TaskStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object taskObject) {
        if (this == taskObject) return true;
        if (taskObject == null || getClass() != taskObject.getClass()) return false;
        Task task = (Task) taskObject;
        return id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "T{" +
                "Name:" + title + " \\ " +
                description +
                "|id:" + id +
                "|S:" + status +
                "}";
    }
}