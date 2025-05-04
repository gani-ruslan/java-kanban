package kanban.managers;

import java.io.File;

/**
 * Utility class for providing default implementations of task and history managers.
 * This class serves as a factory for creating instances of:
 * - {@link TaskManager} – for managing tasks, epics, and subtasks.
 * - {@link HistoryManager} – for tracking the history of viewed tasks.
 * All methods are static, so instantiation of this class is not required.
 */
public class Managers {

    /**
     * Returns the default implementation of {@link TaskManager}.
     * Currently, this is an instance of {@link InMemoryTaskManager}, which stores
     * all tasks in memory without any form of persistence.
     *
     * @return a new instance of the default task manager
     */
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    /**
     * Returns a file-backed implementation of {@link TaskManager} using the specified file name.
     *
     * @param file is File class object contain save/load file
     * @return a new instance of {@link FileBackedTaskManager}
     */
    public static FileBackedTaskManager getFileBackedManager(File file) {
        return new FileBackedTaskManager(file);
    }

    /**
     * Returns the default implementation of {@link HistoryManager}.
     * Currently, this is an instance of {@link InMemoryHistoryManager}, which stores
     * the task view history in memory.
     *
     * @return a new instance of the default history manager
     */
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
