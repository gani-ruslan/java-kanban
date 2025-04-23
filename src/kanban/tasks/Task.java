package kanban.tasks;

import static kanban.tasks.TaskStatus.NEW;

import java.util.Objects;

/**
 * Represents a basic task in the task management system.
 * A task includes a unique ID, title, description, and status.
 */
public class Task {

    private Integer id;
    private String title;
    private String description;
    private TaskStatus status;

    /**
     * Constructor for creating a new task with status NEW and default ID.
     *
     * @param title       the task title
     * @param description the task description
     */
    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        id = 0;
        status = NEW;
    }

    /**
     * Full constructor for creating a task with all fields specified.
     *
     * @param title       the task title
     * @param description the task description
     * @param id          the task ID
     * @param status      the task status
     */
    public Task(String title, String description, Integer id, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    /**
     * Copy constructor.
     *
     * @param task the task to copy from
     */
    public Task(Task task) {
        title = task.getTitle();
        description = task.getDescription();
        id = task.getId();
        status = task.getStatus();
    }

    /**
     * Sets the task ID.
     *
     * @param id the new task ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Returns the task ID.
     *
     * @return the ID of the task
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the task title.
     *
     * @param taskName the new title
     */
    public void setTitle(String taskName) {
        this.title = taskName;
    }

    /**
     * Returns the task title.
     *
     * @return the title of the task
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the task description.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the task description.
     *
     * @return the description of the task
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the task status.
     *
     * @param taskStatus the new status
     */
    public void setStatus(TaskStatus taskStatus) {
        this.status = taskStatus;
    }

    /**
     * Returns the task status.
     *
     * @return the status of the task
     */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * Compares this task to another based on ID.
     *
     * @param taskObject the object to compare with
     * @return true if IDs match, false otherwise
     */
    @Override
    public boolean equals(Object taskObject) {
        if (this == taskObject) {
            return true;
        }
        if (!(taskObject instanceof Task task)) {
            return false;
        }

        return id.equals(task.id);
    }

    /**
     * Returns a hash code based on the task ID.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Returns a string representation of the task.
     *
     * @return a formatted string with task details
     */
    @Override
    public String toString() {
        return "T{"
                + "Name:" + title + " \\ "
                + description
                + "|id:" + id
                + "|S:" + status
                + "}";
    }
}
