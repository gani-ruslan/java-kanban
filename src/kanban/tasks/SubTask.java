package kanban.tasks;

public class SubTask extends Task {

    private Integer parentTaskID;

    public SubTask(String title,
                   String description) {

        super(title, description);
        parentTaskID = 0;
    }

    public SubTask(SubTask subTask) {
        super(subTask.getTitle(), subTask.getDescription(), subTask.getID(), subTask.getStatus());
        parentTaskID = subTask.getParentTaskID();
    }

    // Setters and getters
    public Integer getParentTaskID() {
        return parentTaskID;
    }

    public void setParentTaskID(Integer parentTaskID) {
        this.parentTaskID = parentTaskID;
    }

    @Override
    public String toString() {
        return "SB{" +
                "Name:" + this.getTitle() + " \\ " +
                this.getDescription() +
                "|ID:" + this.getID() +
                "|S:" + this.getStatus() +
                "|ET:" + parentTaskID +
                "}";
    }
}
