package kanban.tasks;

public class SubTask extends Task {

    // Kanban.Tasks.SubTask field: base (current edition)
    private Epic parentTask;

    public SubTask(String taskName,
                   String description) {

        super(taskName, description);
        this.parentTask = null;
    }

    // Setters and getters
    public Epic getParentTask() {
        return parentTask;
    }

    public void setParentTask(Epic parentTask) {
        this.parentTask = parentTask;
    }
}
