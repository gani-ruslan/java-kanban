package kanban.managers;

import kanban.tasks.Task;
import kanban.history.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
//import java.util.NoSuchElementException;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> historyMap;
    private Node firstNode;
    private Node lastNode;

    // Constructor
    public InMemoryHistoryManager() {
        historyMap = new HashMap<>();
        firstNode = null;
        lastNode = null;
    }

    @Override
    public void add(Task task) {

        if (task == null)
            throw new IllegalArgumentException("Task must not be null.");

        Node newNode = new Node(null, null, new Task(task));

        if (historyMap.containsKey(task.getID())) {
            remove(task.getID());
        }
        if (historyMap.isEmpty()) {
            firstNode = newNode;
        }
        if (lastNode != null) {
            lastNode.setNext(newNode);
        }

        newNode.setPrevious(lastNode);
        lastNode = newNode;
        historyMap.put(task.getID(), newNode);
    }

    @Override
    public void remove(Integer taskID) {

        if (taskID == null)
            throw new IllegalArgumentException("taskID must not be null.");

        if (!historyMap.containsKey(taskID))
            return;
            // Commented cause: history.add not include in task.add methods
            //throw new NoSuchElementException("task with ID:" + taskID + " not found.");

        Node previousNode = historyMap.get(taskID).getPrevious();
        Node nextNode = historyMap.get(taskID).getNext();

        if (historyMap.get(taskID).equals(lastNode)) {
            if (previousNode != null) {
                previousNode.setNext(null);
                lastNode = previousNode;
            }

        } else if (historyMap.get(taskID).equals(firstNode)) {
            if (nextNode != null) {
                nextNode.setPrevious(null);
                firstNode = nextNode;
            }

        } else {
            previousNode.setNext(nextNode);
            nextNode.setPrevious(previousNode);

        }
        historyMap.remove(taskID);

    }

    @Override
    public ArrayList<Task> getTasks() {
        ArrayList<Task> historyOrdered = new ArrayList<>();

        if (historyMap.isEmpty()) return historyOrdered;

        Node currentNode = firstNode;

        do {
            historyOrdered.add(currentNode.getNodeTask());
            currentNode = currentNode.getNext();
        } while (currentNode != null);

        return historyOrdered;
    }
}
