package Kanban.Tasks;

public class SubTask extends Task {

    // Kanban.Tasks.SubTask field: base (current edition)
    private Integer parentTaskID;

    public SubTask(Integer taskID,
                   Integer parentTaskID,
                   String taskName,
                   String description) {

        super(taskID, taskName, description);
        this.parentTaskID = parentTaskID;
    }

    public SubTask(Integer parentTaskID,
                   String taskName,
                   String description) {

        super(taskName, description);
        this.parentTaskID = parentTaskID;
    }

    public SubTask(String taskName,
                   String description) {

        super(taskName, description);
        parentTaskID = -1;
    }


    // Setters and getters
    public Integer getParentTaskID() {
        return parentTaskID;
    }

    public void setParentTaskID(Integer parentTaskID) {
        this.parentTaskID = parentTaskID;
    }
}
