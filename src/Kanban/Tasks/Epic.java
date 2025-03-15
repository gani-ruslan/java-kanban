package Kanban.Tasks;

import java.util.ArrayList;

public class Epic extends Task {
    // Kanban.Tasks.Epic field: base (current edition)
    private ArrayList<Integer> subTaskListIDs;

    // Constructor
    public Epic(Integer taskID,
                String taskName,
                String description) {

        super(taskID, taskName, description);
        subTaskListIDs = new ArrayList<>();
    }

    public Epic(String taskName,
                String description) {

        super(taskName, description);
        subTaskListIDs = new ArrayList<>();
    }


    // Utils methods
    public void addSubTask(Integer taskID) {
        subTaskListIDs.add(taskID);
    }

    public void addSubTask(Task task) {
        subTaskListIDs.add(task.getTaskID());
    }

    public void removeSubTask(Object o) {
        if (o instanceof Integer) {
            Integer taskID = (Integer) o;
            subTaskListIDs.remove(taskID);
        } else if (o instanceof Task) {
            Task task = (Task) o;
            subTaskListIDs.remove(task.getTaskID());
        }
    }

    public boolean isSubTaskExists(Object o) {
        if (o instanceof Integer) {
            Integer taskID = (Integer) o;
            return subTaskListIDs.contains(taskID);
        } else if (o instanceof Task) {
            Task task = (Task) o;
            return subTaskListIDs.contains(task.getTaskID());
        } else {
            return false;
        }
    }

    public boolean isSubTaskListEmpty() {
        return subTaskListIDs.isEmpty();
    }

    // Setters and getters
    public ArrayList<Integer> getSubTaskListIDs() {
        return subTaskListIDs;
    }

    public void setSubTaskListIDs(ArrayList<Integer> subTaskListIDs) {
        this.subTaskListIDs = subTaskListIDs;
    }
}
