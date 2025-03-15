package Kanban.Tasks;

public enum TaskType {
    TASK,
    EPIC,
    SUB,
    UNKNOWN;
    public static TaskType fromObject(Object o) {
        if (o instanceof Epic) {
            return EPIC;
        }
        if (o instanceof SubTask) {
            return SUB;
        }
        if (o instanceof Task) {
            return TASK;
        }
        return UNKNOWN;
    }
}
