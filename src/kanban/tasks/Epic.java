package kanban.tasks;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subTaskIDList;

    // Constructor
    public Epic(String title,
                String description) {

        super(title, description);
        subTaskIDList = new ArrayList<>();
    }

    public Epic(Epic epic) {
        super(epic.getTitle(), epic.getDescription(), epic.getID(), epic.getStatus());
        subTaskIDList = epic.getSubIDList();
    }

    public void addSubID(Integer subID) {
        if (subID == null) {
            throw new IllegalArgumentException("Subtask ID cannot be null.");
        }
        if (subID.equals(this.getID())) {
            throw new IllegalArgumentException("Invalid operation: " +
                    "a subtask cannot have the same ID as its epic. ID: " + subID);
        }
        if (subTaskIDList.contains(subID)) {
            return;
        }
        subTaskIDList.add(subID);
    }

    public void removeSubID(Integer subID) {
        if (subID == null) {
            throw new IllegalArgumentException("Subtask ID cannot be null.");
        }
        if (subID.equals(this.getID())) {
            throw new IllegalArgumentException("Invalid operation: " +
                    "a subtask cannot have the same ID as its epic. ID: " + subID);
        }
        subTaskIDList.remove(subID);
    }

    public ArrayList<Integer> getSubIDList() {
        return subTaskIDList;
    }

    @Override
    public String toString() {
        return "E{" +
                "Name:" + this.getTitle() + " \\ " +
                this.getDescription() +
                "|ID:" + this.getID() +
                "|S:" + this.getStatus() +
                "|ST:" + subTaskIDList +
                "}";
    }
}
