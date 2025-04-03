package kanban.managers;

import kanban.tasks.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private ArrayList<Task> history;
    private Integer historyCapacity;

    // Constructor
    public InMemoryHistoryManager() {
        history = new ArrayList<>();
        historyCapacity = 10;
    }

    @Override
    public void add(Task task) {
        if (history.size() >= historyCapacity) history.removeFirst();
        history.add(new Task(task));
    }

    @Override
    public ArrayList<Task> getHistory() {
        return history;
    }
}
