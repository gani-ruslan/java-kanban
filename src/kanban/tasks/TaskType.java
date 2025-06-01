package kanban.tasks;

/**
 * Enumeration representing the type of task in the Kanban system.
 * There are three types of tasks:
 * - TASK: a regular standalone task
 * - SUB: a subtask that belongs to an epic
 * - EPIC: a large task that can contain multiple subtasks
 */
public enum TaskType {

    /**
     * A regular standalone task.
     */
    TASK,

    /**
     * A subtask that is part of an epic.
     */
    SUB,

    /**
     * A high-level task that groups multiple subtasks.
     */
    EPIC
}
