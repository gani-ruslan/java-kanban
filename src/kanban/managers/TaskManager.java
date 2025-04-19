package kanban.managers;

import kanban.tasks.Epic;
import kanban.tasks.SubTask;
import kanban.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    List<Task> getTaskList();

    List<Epic> getEpicList();

    List<SubTask> getSubList();

    List<SubTask> getEpicSubTaskList(Integer epicID);

    // Get task by ID (task/epic/sub)
    Task getTaskByID(Integer taskID);

    Epic getEpicByID(Integer epicID);

    SubTask getSubTaskByID(Integer subsID);

    ArrayList<Task> getHistoryTask();

    // Add task (by type task/epic/sub)
    void addTask(Task newTask);

    void addEpic(Epic newEpic);

    void addSub(SubTask newSub);

    // Update task by ID
    void updateTask(Task updateTask);

    void updateEpic(Epic updateEpic);

    void updateSub(SubTask updateSubs);

    // Remove task (task/epic/sub)
    void removeTaskByID(Integer taskID);

    void removeEpicByID(Integer epicID);

    void removeSubByID(Integer subsID);

    // Remove all tasks by type (task/epic/sub)
    void removeAllTask();

    void removeAllEpic();

    void removeAllSub();
}
