package kanban.managers;

/**
 * Utility class for providing default implementations of task and history managers.
 * This class serves as a factory for creating instances of:
 * - {@link TaskManager} - for managing tasks, epics, and subtasks</li>
 * - {@link HistoryManager} - for tracking task viewing history</li>
 * All methods are static, so no instantiation is required.
 */
public class Managers {
    /**
     * Returns a default implementation of {@link TaskManager}.
     * The current implementation is {@link InMemoryTaskManager}, which stores
     * all data in memory without persistence.
     *
     * @return a new instance of the default task manager
     */
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    /**
     * Returns a default implementation of {@link HistoryManager}.
     * The current implementation is {@link InMemoryHistoryManager}, which stores
     * the task viewing history in memory.
     *
     * @return a new instance of the default history manager
     */
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}