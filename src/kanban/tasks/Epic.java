package kanban.tasks;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<SubTask> subTaskList;

    public Epic(String taskName,
                String description) {

        super(taskName, description);
        subTaskList = new ArrayList<>();
    }

    public void addSubTask(SubTask subTask) {
        subTaskList.add(subTask);
    }

    public void removeSubTask(SubTask subTask) {
        subTaskList.remove(subTask);
    }

    public void updateEpicStatus() {
        int countDoneTask = 0;
        int countInProgressTask = 0;

        for (SubTask task : subTaskList) {
            if (task.getStatus() == TaskStatus.IN_PROGRESS) countInProgressTask++;
            if (task.getStatus() == TaskStatus.DONE) countDoneTask++;
        }

        if (countDoneTask > 0 && countDoneTask == subTaskList.size() ) {
            setStatus(TaskStatus.DONE);
        } else if (countInProgressTask > 0) {
            setStatus(TaskStatus.IN_PROGRESS);
        } else {
            setStatus(TaskStatus.NEW);
        }
    }

    public ArrayList<SubTask> getSubTaskList() {
        return subTaskList;
    }
}
