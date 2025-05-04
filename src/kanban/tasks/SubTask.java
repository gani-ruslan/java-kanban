package kanban.tasks;

import static kanban.tasks.TaskType.SUB;

/**
 * Represents a SubTask, which is a smaller task associated with an Epic.
 * Inherits from {@link Task} and adds a reference to its parent Epic task by ID.
 */
public class SubTask extends Task {

    private Integer parentId;

    /**
     * Constructor for creating a new empty SubTask with status NEW and default ID.
     */
    public SubTask() {
        super();
        parentId = null;
    }

    /**
     * Constructor for creating a new SubTask with a null parent ID.
     *
     * @param title       the subtask title
     * @param description the subtask description
     */
    public SubTask(String title,
                   String description) {

        super(title, description);
        parentId = null;
    }

    /**
     * Copy constructor for SubTask.
     *
     * @param subTask the subtask to copy from
     */
    public SubTask(SubTask subTask) {
        super(subTask.getId(), subTask.getTitle(), subTask.getStatus(), subTask.getDescription());
        parentId = subTask.getParentId();
    }

    /**
     * Constructor for creating new SubTask with fromString method.
     *
     * @param id the subtask id
     * @param title the subtask title
     * @param status the subtask status
     * @param description the subtask description
     * @param parentId the subtask parentId
     */
    public SubTask(Integer id, String title, TaskStatus status,
                   String description, Integer parentId) {
        super(id, title, status, description);
        this.parentId = parentId;
    }

    /**
     * Returns the ID of the parent Epic task.
     *
     * @return the parent epic task ID, or null if not set
     */
    public Integer getParentId() {
        return parentId;
    }

    /**
     * Sets the parent Epic task ID for this subtask.
     * The Epic ID must not be the same as the SubTask's own ID,
     * and the SubTask must not already have the same parent ID.
     *
     * @param epicId the ID of the parent Epic task
     * @throws IllegalArgumentException if the epicId is the
     *         same as the subtask's own ID or the same as the current parent ID
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
     * Returns the task type.
     *
     * @return the type of the task
     */
    @Override
    public TaskType getType() {
        return SUB;
    }

    /**
     * Returns a string representation of the subtask.
     *
     * @return a formatted string with subtask details and its parent Epic ID
     */
    @Override
    public String toString() {
        return "SUB[ID:" + this.getId()
                + " T:" + this.getTitle()
                + " S:" + this.getStatus()
                + " D:" + this.getDescription()
                + " EP:" + parentId
                + "]";
    }
}