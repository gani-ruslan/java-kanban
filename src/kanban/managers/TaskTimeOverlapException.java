package kanban.managers;

/**
 * Exception thrown when a task or subtask has a time interval
 * that overlaps with an existing task in the schedule.
 * Used to enforce time integrity in the task manager.
 */
public class TaskTimeOverlapException extends RuntimeException {

    /**
     * Constructs a new TaskTimeOverlapException with a specified detail message.
     *
     * @param message the detail message explaining the cause of the overlap
     */
    public TaskTimeOverlapException(String message) {
        super(message);
    }
}
