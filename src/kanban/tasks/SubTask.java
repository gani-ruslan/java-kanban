package kanban.tasks;

/**
 * Represents a SubTask, which is a smaller task associated with an Epic.
 * Inherits from {@link Task} and adds a reference to its parent Epic task by ID.
 */
public class SubTask extends Task {

    private Integer parentId;

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
        super(subTask.getTitle(), subTask.getDescription(), subTask.getId(), subTask.getStatus());
        parentId = subTask.getParentId();
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
     * Returns a string representation of the subtask.
     *
     * @return a formatted string with subtask details and its parent Epic ID
     */
    @Override
    public String toString() {
        return "SB{"
                + "Name:" + this.getTitle() + " \\ "
                + this.getDescription()
                + "|Id:" + this.getId()
                + "|S:" + this.getStatus()
                + "|ET:" + parentId
                + "}";
    }
}
