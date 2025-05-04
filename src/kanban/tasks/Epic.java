package kanban.tasks;

import static kanban.tasks.TaskType.EPIC;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an Epic task, which can contain multiple subtasks.
 * Inherits all fields from {@link Task} and adds a list of subtask IDs.
 */
public class Epic extends Task {

    private final List<Integer> subTaskIdList;

    /**
     * Constructor for creating a new empty Epic with status NEW and default ID.
     */
    public Epic() {
        super();
        subTaskIdList = new ArrayList<>();
    }

    /**
     * Constructor for creating a new Epic with an empty list of subtasks.
     *
     * @param title       the epic title
     * @param description the epic description
     */
    public Epic(String title,
                String description) {

        super(title, description);
        subTaskIdList = new ArrayList<>();
    }

    /**
     * Copy constructor for Epic.
     *
     * @param epic the epic to copy from
     */
    public Epic(Epic epic) {
        super(epic.getId(), epic.getTitle(), epic.getStatus(), epic.getDescription());
        subTaskIdList = epic.getSubIdList();
    }

    /**
     * Constructor for creating new SubTask with fromString method.
     *
     * @param id the subtask id
     * @param title the subtask title
     * @param status the subtask status
     * @param description the subtask description
     */
    public Epic(Integer id, String title, TaskStatus status, String description) {
        super(id, title, status, description);
        this.subTaskIdList = new ArrayList<>();
    }

    /**
     * Adds a subtask ID to the epic.
     * Subtask ID must not be null, must not match the epic's own ID,
     * and must not already be present in the list.
     *
     * @param subId the ID of the subtask to add
     * @throws IllegalArgumentException if subId is null or equal to the epic's own ID
     */
    public void addSubId(Integer subId) {
        if (subId == null) {
            throw new IllegalArgumentException("subId must not be null.");
        }
        if (subId.equals(this.getId())) {
            throw new IllegalArgumentException("subId cannot have the same Id as its epic.");
        }
        if (subTaskIdList.contains(subId)) {
            return;
        }
        subTaskIdList.add(subId);
    }

    /**
     * Removes a subtask ID from the epic.
     * Subtask ID must not be null and must not match the epic's own ID.
     *
     * @param subId the ID of the subtask to remove
     * @throws IllegalArgumentException if subId is null or equal to the epic's own ID
     */
    public void removeSubId(Integer subId) {
        if (subId == null) {
            throw new IllegalArgumentException("subId must not be null.");
        }
        if (subId.equals(this.getId())) {
            throw new IllegalArgumentException("subId cannot have the same Id as its epic.");
        }
        subTaskIdList.remove(subId);
    }

    /**
     * Returns the list of subtask IDs associated with this epic.
     *
     * @return a list of subtask IDs
     */
    public List<Integer> getSubIdList() {
        return subTaskIdList;
    }

    /**
     * Returns the task type.
     *
     * @return the type of the task
     */
    @Override
    public TaskType getType() {
        return EPIC;
    }

    /**
     * Returns a string representation of the epic task.
     *
     * @return a formatted string with epic details and its subtasks
     */
    @Override
    public String toString() {
        return "EPIC[ID:" + this.getId()
                + " T:" + this.getTitle()
                + " S:" + this.getStatus()
                + " D:" + this.getDescription()
                + " SB:" + this.subTaskIdList.toString()
                + "]";
    }
}