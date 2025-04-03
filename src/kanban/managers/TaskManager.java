package kanban.managers;

import kanban.tasks.Epic;
import kanban.tasks.SubTask;
import kanban.tasks.Task;
import java.util.ArrayList;

public interface TaskManager {
    ArrayList<Task> getTaskList();
    ArrayList<Epic> getEpicList();
    ArrayList<SubTask> getSubList();
    ArrayList<SubTask> getEpicSubTaskList(Integer epicID);

    // Get task by ID (task/epic/sub)
    Task getTaskByID(Integer taskID);
    Epic getEpicByID(Integer epicID);
    SubTask getSubTaskByID(Integer subsID);
    ArrayList<Task> getHistory();

    // Add task (by type task/epic/sub)
    void addTask(Task newTask);
    void addEpic(Epic newEpic);
    void addSub(SubTask newSub);
    void addSubToEpic(Integer subID, Integer epicID);

    // Update task by ID
    void updateTask(Task updateTask);
    void updateEpic(Epic updateEpic);
    void updateSub(SubTask updateSubs);
    void updateStatus(Integer epicID);

    // Remove task (task/epic/sub)
    void removeTaskByID(Integer taskID);
    void removeEpicByID(Integer epicID);
    void removeSubByID(Integer subsID);

    // Remove all tasks by type (task/epic/sub)
    void removeAllTask();
    void removeAllEpic();
    void removeAllSub();
}
