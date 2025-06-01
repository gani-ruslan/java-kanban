package kanban.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import kanban.tasks.Task;
import kanban.utility.HistoryNode;

/**
 * In-memory implementation of the HistoryManager interface, which keeps track of tasks' history.
 * This class stores the history of tasks using a doubly linked list and a HashMap for quick access.
 */
public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, HistoryNode> historyMap;
    private HistoryNode firstHistoryNode;
    private HistoryNode lastHistoryNode;

    /**
     * Constructs an empty InMemoryHistoryManager with no tasks in history.
     */
    public InMemoryHistoryManager() {
        historyMap = new HashMap<>();
        firstHistoryNode = null;
        lastHistoryNode = null;
    }

    /**
     * Adds a task to the history. If the task already exists,
     * it is removed and re-added to the end of the history.
     *
     * @param task the task to be added to history
     * @throws IllegalArgumentException if the task is null
     */
    @Override
    public void add(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task must not be null.");
        }

        HistoryNode newHistoryNode = new HistoryNode(null, null, new Task(task));

        // Remove existing task if it exists in history
        if (historyMap.containsKey(task.getId())) {
            remove(task.getId());
        }

        // Add new task as the last node in the history
        if (historyMap.isEmpty()) {
            firstHistoryNode = newHistoryNode;
        }
        if (lastHistoryNode != null) {
            lastHistoryNode.setNext(newHistoryNode);
        }

        newHistoryNode.setPrevious(lastHistoryNode);
        lastHistoryNode = newHistoryNode;
        historyMap.put(task.getId(), newHistoryNode);
    }

    /**
     * Removes a task from the history based on its ID.
     *
     * @param id the ID of the task to be removed from history
     * @throws IllegalArgumentException if the task ID is null
     */
    @Override
    public void remove(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null.");
        }

        if (!historyMap.containsKey(id)) {
            return;
        }

        HistoryNode taskHistoryNode = historyMap.get(id);
        HistoryNode previousHistoryNode = taskHistoryNode.getPrevious();
        HistoryNode nextHistoryNode = taskHistoryNode.getNext();

        if (taskHistoryNode.equals(lastHistoryNode)) {
            if (previousHistoryNode != null) {
                previousHistoryNode.setNext(null);
                lastHistoryNode = previousHistoryNode;
            }
        } else if (taskHistoryNode.equals(firstHistoryNode)) {
            if (nextHistoryNode != null) {
                nextHistoryNode.setPrevious(null);
                firstHistoryNode = nextHistoryNode;
            }
        } else {
            previousHistoryNode.setNext(nextHistoryNode);
            nextHistoryNode.setPrevious(previousHistoryNode);
        }

        historyMap.remove(id);
    }

    /**
     * Retrieves the list of tasks in history, ordered from first to last.
     *
     * @return a list of tasks in the history
     */
    @Override
    public ArrayList<Task> getTasks() {
        ArrayList<Task> historyOrdered = new ArrayList<>();

        if (historyMap.isEmpty()) {
            return historyOrdered;
        }

        HistoryNode currentHistoryNode = firstHistoryNode;

        do {
            historyOrdered.add(currentHistoryNode.getNodeTask());
            currentHistoryNode = currentHistoryNode.getNext();
        } while (currentHistoryNode != null);

        return historyOrdered;
    }
}
