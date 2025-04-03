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
        subTaskIDList = epic.getSubTaskIDList();
    }

    public void addSubTaskID(Integer subTaskID) {
        subTaskIDList.add(subTaskID);
    }

    public void removeSubTaskID(Integer subTaskID) {
        subTaskIDList.remove(subTaskID);
    }

    public ArrayList<Integer> getSubTaskIDList() {
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
