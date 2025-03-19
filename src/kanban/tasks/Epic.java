package kanban.tasks;

import java.util.ArrayList;
import static kanban.tasks.TaskStatus.*;

public class Epic extends Task {

    private ArrayList<SubTask> subTaskList;

    public Epic(String taskName,
                String description) {

        super(taskName, description);
        subTaskList = new ArrayList<>();
    }

    public void addSubTask(SubTask subTask) {
        subTaskList.add(subTask);
        updateStatus();
    }

    public void removeSubTask(SubTask subTask) {
        subTaskList.remove(subTask);
        updateStatus();
    }

    public void updateStatus() {
        int countDoneTask = 0;
        int countInProgressTask = 0;

        for (SubTask task : subTaskList) {
            if (task.getStatus() == IN_PROGRESS) countInProgressTask++;
            if (task.getStatus() == DONE) countDoneTask++;
        }

        if (countDoneTask > 0 && countDoneTask == subTaskList.size() ) {
            setStatus(DONE);
        } else if (countInProgressTask > 0 || countDoneTask > 0) {
            setStatus(IN_PROGRESS);
        } else {
            setStatus(NEW);
        }
    }

    public ArrayList<SubTask> getSubTaskList() {
        return subTaskList;
    }
}
