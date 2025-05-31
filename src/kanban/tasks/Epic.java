package kanban.tasks;

import static kanban.tasks.TaskType.EPIC;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an Epic task, which can contain multiple subtasks.
 * Inherits from {@code Task} and adds a list of associated subtask IDs.
 */
public class Epic extends Task {

    private final List<Integer> subTaskIdList;
    private LocalDateTime endTime;

    /**
     * Creates an empty epic with default values.
     */
    public Epic() {
        super();
        subTaskIdList = new ArrayList<>();
        endTime = LocalDateTime.MIN;
    }

    /**
     * Creates an epic with title and description.
     *
     * @param title       the epic title
     * @param description the epic description
     */
    public Epic(String title, String description) {
        super(title, description);
        subTaskIdList = new ArrayList<>();
        endTime = LocalDateTime.MIN;
    }

    /**
     * Creates an epic with ID, title and description.
     *
     * @param id          the epic ID
     * @param title       the epic title
     * @param description the epic description
     */
    public Epic(Integer id, String title, String description) {
        super(id, title, description);
        subTaskIdList = new ArrayList<>();
        endTime = LocalDateTime.MIN;
    }

    /**
     * Full constructor for Epic.
     *
     * @param id          the epic ID
     * @param title       the epic title
     * @param status      the status of the epic
     * @param description the epic description
     * @param startTime   the start time
     * @param duration    the duration
     */
    public Epic(Integer id, String title, TaskStatus status, String description,
                LocalDateTime startTime, Duration duration) {
        super(id, title, status, description, startTime, duration);
        this.subTaskIdList = new ArrayList<>();
        this.endTime = LocalDateTime.MIN;
    }

    /**
     * Copy constructor.
     *
     * @param epic the epic to copy
     */
    public Epic(Epic epic) {
        super(epic.getId(), epic.getTitle(), epic.getStatus(), epic.getDescription(),
                epic.getStartTime(), epic.getDuration());
        this.subTaskIdList = epic.getSubIdList();
        this.endTime = epic.getEndTime();
    }

    /**
     * Sets the end time of the epic.
     *
     * @param endTime the new end time
     */
    public void setEndTime(LocalDateTime endTime) {
        if (endTime != null) {
            this.endTime = endTime;
        }
    }

    /**
     * Returns the end time of the epic.
     *
     * @return the end time
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * Adds a subtask ID to this epic.
     *
     * @param subId the subtask ID
     * @throws IllegalArgumentException if subId is null or equals the epic's ID
     */
    public void addSubId(Integer subId) {
        if (subId == null) {
            throw new IllegalArgumentException("subId must not be null.");
        }
        if (subId.equals(this.getId())) {
            throw new IllegalArgumentException("subId cannot have the same Id as its epic.");
        }
        if (!subTaskIdList.contains(subId)) {
            subTaskIdList.add(subId);
        }
    }

    /**
     * Removes a subtask ID from this epic.
     *
     * @param subId the subtask ID to remove
     * @throws IllegalArgumentException if subId is null or equals the epic's ID
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
     * Returns the list of associated subtask IDs.
     *
     * @return list of subtask IDs
     */
    public List<Integer> getSubIdList() {
        return subTaskIdList;
    }

    /**
     * Returns the type of this task.
     *
     * @return {@code TaskType.EPIC}
     */
    @Override
    public TaskType getType() {
        return EPIC;
    }

    /**
     * Returns a string representation of the epic.
     *
     * @return formatted string with epic details and subtasks
     */
    @Override
    public String toString() {
        return "EPIC[ID:" + this.getId()
                + " T:" + this.getTitle()
                + " S:" + this.getStatus()
                + " D:" + this.getDescription()
                + " ST:" + this.getStartTime()
                + " DR:" + this.getDuration()
                + " ET:" + getEndTime()
                + " SB:" + subTaskIdList
                + "]";
    }
}
