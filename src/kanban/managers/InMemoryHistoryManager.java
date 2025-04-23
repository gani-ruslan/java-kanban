package kanban.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import kanban.history.Node;
import kanban.tasks.Task;

/**
 * In-memory implementation of the HistoryManager interface, which keeps track of tasks' history.
 * This class stores the history of tasks using a doubly linked list and a HashMap for quick access.
 */
public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> historyMap;
    private Node firstNode;
    private Node lastNode;

    /**
     * Constructs an empty InMemoryHistoryManager with no tasks in history.
     */
    public InMemoryHistoryManager() {
        historyMap = new HashMap<>();
        firstNode = null;
        lastNode = null;
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

        Node newNode = new Node(null, null, new Task(task));

        // Remove existing task if it exists in history
        if (historyMap.containsKey(task.getId())) {
            remove(task.getId());
        }

        // Add new task as the last node in the history
        if (historyMap.isEmpty()) {
            firstNode = newNode;
        }
        if (lastNode != null) {
            lastNode.setNext(newNode);
        }

        newNode.setPrevious(lastNode);
        lastNode = newNode;
        historyMap.put(task.getId(), newNode);
    }

    /**
     * Removes a task from the history based on its ID.
     *
     * @param taskId the ID of the task to be removed from history
     * @throws IllegalArgumentException if the taskId is null
     */
    @Override
    public void remove(Integer taskId) {

        if (taskId == null) {
            throw new IllegalArgumentException("taskId must not be null.");
        }

        if (!historyMap.containsKey(taskId)) {
            return;
        }

        Node taskNode = historyMap.get(taskId);
        Node previousNode = taskNode.getPrevious();
        Node nextNode = taskNode.getNext();

        if (taskNode.equals(lastNode)) {
            if (previousNode != null) {
                previousNode.setNext(null);
                lastNode = previousNode;
            }
        } else if (taskNode.equals(firstNode)) {
            if (nextNode != null) {
                nextNode.setPrevious(null);
                firstNode = nextNode;
            }
        } else {
            previousNode.setNext(nextNode);
            nextNode.setPrevious(previousNode);
        }

        historyMap.remove(taskId);
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

        Node currentNode = firstNode;

        do {
            historyOrdered.add(currentNode.getNodeTask());
            currentNode = currentNode.getNext();
        } while (currentNode != null);

        return historyOrdered;
    }
}
