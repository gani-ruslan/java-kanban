package kanban.tasks;

/**
 * Enumeration representing the type of task in the Kanban system.
 * There are three types of tasks:
 * - {@code TASK} – a regular standalone task.
 * - {@code SUB} – a subtask that belongs to an epic.
 * - {@code EPIC} – a large task that can contain multiple subtasks.
 */

public enum TaskType {
    TASK,
    SUB,
    EPIC
}
