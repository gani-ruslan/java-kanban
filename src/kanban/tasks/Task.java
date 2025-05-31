package kanban.tasks;

import static kanban.tasks.TaskStatus.NEW;
import static kanban.tasks.TaskType.TASK;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a basic task in the task management system.
 * A task includes a unique ID, title, description, status, start time, and duration.
 */
public class Task implements Comparable<Task> {

    private Integer id;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDateTime startTime;
    private Duration duration;

    /**
     * Creates an empty task with default values.
     */
    public Task() {
        title = "";
        description = "";
        id = 0;
        status = NEW;
        startTime = LocalDateTime.MIN;
        duration = Duration.ZERO;
    }

    /**
     * Creates a task with the given title and description.
     *
     * @param title       the title of the task
     * @param description the description of the task
     */
    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        id = 0;
        status = NEW;
        startTime = LocalDateTime.MIN;
        duration = Duration.ZERO;
    }

    /**
     * Creates a task with a specific ID, title, and description.
     *
     * @param id          the ID of the task
     * @param title       the title of the task
     * @param description the description of the task
     */
    public Task(Integer id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        status = NEW;
        startTime = LocalDateTime.MIN;
        duration = Duration.ZERO;
    }

    /**
     * Creates a task with all fields specified.
     *
     * @param id          the ID of the task
     * @param title       the title of the task
     * @param status      the status of the task
     * @param description the description of the task
     * @param startTime   the start time of the task
     * @param duration    the duration of the task
     */
    public Task(Integer id, String title, TaskStatus status, String description,
                LocalDateTime startTime, Duration duration) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    /**
     * Copy constructor.
     *
     * @param task the task to copy
     */
    public Task(Task task) {
        title = task.getTitle();
        description = task.getDescription();
        id = task.getId();
        status = task.getStatus();
        startTime = task.getStartTime();
        duration = task.getDuration();
    }

    /**
     * Sets the task ID.
     *
     * @param id the new ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Returns the task ID.
     *
     * @return the task ID
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
     * Sets the start time of the task.
     *
     * @param startTime the new start time
     */
    public void setStartTime(LocalDateTime startTime) {
        if (startTime == null) {
            return;
        }
        this.startTime = startTime;
    }

    /**
     * Returns the start time of the task.
     *
     * @return the start time
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Sets the duration of the task.
     *
     * @param duration the new duration
     */
    public void setDuration(Duration duration) {
        if (duration == null) {
            return;
        }
        this.duration = duration;
    }

    /**
     * Returns the duration of the task.
     *
     * @return the duration
     */
    public Duration getDuration() {
        return duration;
    }

    /**
     * Calculates and returns the end time of the task.
     *
     * @return the end time
     */
    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    /**
     * Returns the task type.
     *
     * @return {@code TaskType.TASK}
     */
    public TaskType getType() {
        return TASK;
    }

    /**
     * Compares this task to another by start time.
     *
     * @param other the task to compare with
     * @return comparison result by start time
     */
    @Override
    public int compareTo(Task other) {
        return this.startTime.compareTo(other.startTime);
    }

    /**
     * Checks whether this task is equal to another object by ID.
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
     * Returns the hash code of the task based on its ID.
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
     * @return string with formatted task details
     */
    @Override
    public String toString() {
        return "TASK[ID:" + id
                + " T:" + title
                + " S:" + status
                + " D:" + description
                + " ST:" + startTime
                + " DR:" + duration
                + " ET:" + getEndTime()
                + "]";
    }
}
