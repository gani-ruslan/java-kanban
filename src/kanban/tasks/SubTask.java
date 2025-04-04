package kanban.tasks;

public class SubTask extends Task {

    private Integer parentID;

    public SubTask(String title,
                   String description) {

        super(title, description);
        parentID = null;
    }

    public SubTask(SubTask subTask) {
        super(subTask.getTitle(), subTask.getDescription(), subTask.getID(), subTask.getStatus());
        parentID = subTask.getParentID();
    }

    // Setters and getters
    public Integer getParentID() {
        return parentID;
    }

    public void setParentID(Integer parentID) {
        if (parentID.equals(this.getID())) {
            throw new IllegalArgumentException("Invalid operation: " +
                    "a parent(epic) cannot have the same ID as its subtask. ID: " + parentID);
        }
        if (parentID.equals(this.getParentID())) {
            return;
        }
        this.parentID = parentID;
    }

    @Override
    public String toString() {
        return "SB{" +
                "Name:" + this.getTitle() + " \\ " +
                this.getDescription() +
                "|ID:" + this.getID() +
                "|S:" + this.getStatus() +
                "|ET:" + parentID +
                "}";
    }
}
