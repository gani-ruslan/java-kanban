package kanban.managers;

import kanban.tasks.Task;
import java.util.ArrayList;

public interface HistoryManager {
    // Add and remove task methods
    void add(Task task);

    void remove(Integer taskID);

    // Get task list method
    ArrayList<Task> getTasks();
}
