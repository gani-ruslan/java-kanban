package kanban.tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Integer> subTaskIDList;

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
            throw new IllegalArgumentException("subID must not be null.");
        }
        if (subID.equals(this.getID())) {
            throw new IllegalArgumentException("subID cannot have the same ID as its epic.");
        }
        if (subTaskIDList.contains(subID)) {
            return;
        }
        subTaskIDList.add(subID);
    }

    public void removeSubID(Integer subID) {
        if (subID == null) {
            throw new IllegalArgumentException("subID must not be null.");
        }
        if (subID.equals(this.getID())) {
            throw new IllegalArgumentException("subID cannot have the same ID as its epic.");
        }
        subTaskIDList.remove(subID);
    }

    public List<Integer> getSubIDList() {
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
