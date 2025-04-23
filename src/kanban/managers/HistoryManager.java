package kanban.managers;

import java.util.ArrayList;
import kanban.tasks.Task;

/**
 * Interface for managing the history of tasks in the Kanban system.
 * Provides methods to add, remove, and retrieve tasks in the history.
 */
public interface HistoryManager {

    /**
     * Adds a task to the history.
     *
     * @param task the task to be added to the history
     */
    void add(Task task);

    /**
     * Removes a task from the history based on its ID.
     *
     * @param taskId the ID of the task to be removed
     */
    void remove(Integer taskId);

    /**
     * Retrieves all tasks currently in the history.
     *
     * @return a list of tasks in the history
     */
    ArrayList<Task> getTasks();
}
