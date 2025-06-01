package kanban.managers;

import java.io.File;

/**
 * Utility class for providing default implementations of task and history managers.
 * This class serves as a factory for creating instances of:
 * - TaskManager – for managing tasks, epics, and subtasks.
 * - HistoryManager – for tracking the history of viewed tasks.
 * All methods are static, so instantiation of this class is not required.
 */
public class Managers {

    /**
     * Returns the default implementation of {@code TaskManager}.
     * Currently, this is an instance of {@code InMemoryTaskManager}, which stores
     * all tasks in memory without persistence.
     *
     * @return a new instance of the default task manager
     */
    public static InMemoryTaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    /**
     * Returns a file-backed implementation of {@code TaskManager} using the specified file.
     * This allows tasks to be saved and loaded from persistent storage.
     *
     * @param file the file to use for saving and loading tasks
     * @return a new instance of {@code FileBackedTaskManager}
     */
    public static FileBackedTaskManager getFileBackedManager(File file) {
        return new FileBackedTaskManager(file);
    }

    /**
     * Returns the default implementation of {@code HistoryManager}.
     * Currently, this is an instance of {@code InMemoryHistoryManager}, which stores
     * the task view history in memory.
     *
     * @return a new instance of the default history manager
     */
    public static InMemoryHistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
