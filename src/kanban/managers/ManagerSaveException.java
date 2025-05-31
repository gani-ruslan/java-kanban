package kanban.managers;

/**
 * Exception thrown when a failure occurs during the saving or loading
 * of task manager data (e.g., to or from a file).
 * This is a custom unchecked exception used to signal issues related
 * to persistence in the task management system.
 */
public class ManagerSaveException extends RuntimeException {

    /**
     * Constructs a new ManagerSaveException with the specified detail message.
     *
     * @param message the detail message explaining the cause of the exception
     */
    public ManagerSaveException(String message) {
        super(message);
    }
}
