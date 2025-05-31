package kanban.tasks;

import static kanban.tasks.TaskType.SUB;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Represents a SubTask, which is a smaller task associated with an Epic.
 * Inherits from {@code Task} and adds a reference to its parent Epic task by ID.
 */
public class SubTask extends Task {

    private Integer parentId;

    /**
     * Creates an empty SubTask with default values.
     */
    public SubTask() {
        super();
        parentId = 0;
    }

    /**
     * Creates a SubTask with a title and description.
     *
     * @param title       the subtask title
     * @param description the subtask description
     */
    public SubTask(String title, String description) {
        super(title, description);
        parentId = 0;
    }

    /**
     * Copy constructor for SubTask.
     *
     * @param sub the subtask to copy from
     */
    public SubTask(SubTask sub) {
        super(sub.getId(), sub.getTitle(), sub.getStatus(), sub.getDescription(),
                sub.getStartTime(), sub.getDuration());
        parentId = sub.getParentId();
    }

    /**
     * Creates a SubTask with all fields specified.
     *
     * @param id          the subtask ID
     * @param title       the subtask title
     * @param status      the subtask status
     * @param description the subtask description
     * @param parentId    the parent Epic ID
     * @param startTime   the subtask start time
     * @param duration    the subtask duration
     */
    public SubTask(Integer id, String title, TaskStatus status,
                   String description, Integer parentId,
                   LocalDateTime startTime, Duration duration) {
        super(id, title, status, description, startTime, duration);
        this.parentId = parentId;
    }

    /**
     * Returns the ID of the parent Epic task.
     *
     * @return the parent Epic ID
     */
    public Integer getParentId() {
        return parentId;
    }

    /**
     * Sets the parent Epic ID for this SubTask.
     *
     * @param epicId the ID of the parent Epic
     * @throws IllegalArgumentException if epicId equals the SubTask's own ID
     */
    public void setParentId(Integer epicId) {
        if (epicId.equals(this.getId())) {
            throw new IllegalArgumentException("epicId cannot have the same Id as its subtask.");
        }
        if (epicId.equals(this.getParentId())) {
            return;
        }
        this.parentId = epicId;
    }

    /**
     * Returns the type of the task.
     *
     * @return {@code TaskType.SUB}
     */
    @Override
    public TaskType getType() {
        return SUB;
    }

    /**
     * Returns a string representation of the SubTask.
     *
     * @return formatted string with subtask details
     */
    @Override
    public String toString() {
        return "SUB[ID:" + this.getId()
                + " T:" + this.getTitle()
                + " S:" + this.getStatus()
                + " D:" + this.getDescription()
                + " ST:" + this.getStartTime()
                + " DR:" + this.getDuration()
                + " ET:" + this.getEndTime()
                + " EP:" + parentId
                + "]";
    }
}
