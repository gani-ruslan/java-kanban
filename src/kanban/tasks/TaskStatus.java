package kanban.tasks;

/**
 * Represents the possible statuses for a task in the Kanban system.
 * A task can be in one of the following states:
 * - NEW: Task has just been created and is not yet started.
 * - IN_PROGRESS: Task is currently being worked on.
 * - DONE: Task has been completed.
 */
public enum TaskStatus {

    /**
     * Task is newly created and has not started yet.
     */
    NEW,

    /**
     * Task is in progress and is being worked on.
     */
    IN_PROGRESS,

    /**
     * Task is completed and finished.
     */
    DONE,
}
