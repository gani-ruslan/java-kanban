package kanban.managers;
import kanban.tasks.*;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private HashMap<Integer, Task> taskStorageMap;
    private HashMap<Integer, Epic> epicStorageMap;
    private HashMap<Integer, SubTask> subsStorageMap;

    // Constructor
    public TaskManager() {
        taskStorageMap = new HashMap<>();
        epicStorageMap = new HashMap<>();
        subsStorageMap = new HashMap<>();
    }


    // Get task list by type (task/epic/sub)
    public ArrayList<Task> getTaskList() {
        return new ArrayList<>(taskStorageMap.values());
    }

    public ArrayList<Epic> getEpicList() {
        return new ArrayList<>(epicStorageMap.values());
    }

    public ArrayList<SubTask> getSubTaskList() {
        return new ArrayList<>(subsStorageMap.values());
    }

    public ArrayList<SubTask> getEpicSubTaskList(Epic epicTask) {
        return epicTask.getSubTaskList();
    }


    // Remove all tasks by type (task/epic/sub)
    public void removeAllTask() {
        taskStorageMap.clear();
    }

    public void removeAllEpic() {
        epicStorageMap.clear();
        subsStorageMap.clear();
    }

    public void removeAllSubs() {
        for(SubTask subTask : subsStorageMap.values()) {
            subTask.getParentTask().removeSubTask(subTask);
            subTask.getParentTask().updateStatus();
        }
        subsStorageMap.clear();
    }


    // Get task by ID (task/epic/sub)
    public Task getTaskByID(Integer taskID) {
        return taskStorageMap.get(taskID);
    }

    public Epic getEpicByID(Integer epicID) {
        return epicStorageMap.get(epicID);
    }

    public SubTask getSubTaskByID(Integer subsID) {
        return subsStorageMap.get(subsID);
    }


    // Add task (by type task/epic/sub)
    public void addTask(Task newTask) {
        taskStorageMap.put(newTask.getID(), newTask);
    }

    public void addEpic(Epic newEpic) {
        epicStorageMap.put(newEpic.getID(), newEpic);
    }

    public void addSub(SubTask newSubs, Epic parentTask) {
        parentTask.addSubTask(newSubs);
        newSubs.setParentTask(parentTask);
        subsStorageMap.put(newSubs.getID(), newSubs);
    }


    // Update task by ID
    public void updateTask(Task updateTask) {
        taskStorageMap.put(updateTask.getID(), updateTask);
    }

    public void updateEpic(Epic updateEpic) {
        updateEpic.updateStatus();
        epicStorageMap.put(updateEpic.getID(), updateEpic);
    }

    public void updateSubs(SubTask updateSubs) {
        updateSubs.getParentTask().updateStatus();
        subsStorageMap.put(updateSubs.getID(), updateSubs);
    }


    // Remove task (task/epic/sub)
    public void removeTask(Task task) {
        taskStorageMap.remove(task.getID());
    }

    public void removeEpic(Epic epic) {
        for (SubTask subTask : epicStorageMap.get(epic.getID()).getSubTaskList()) subsStorageMap.remove(subTask.getID());
        epicStorageMap.remove(epic.getID());
    }

    public void removeSubs(SubTask subs) {
        subsStorageMap.get(subs.getID()).getParentTask().removeSubTask(subsStorageMap.get(subs.getID()));
        subsStorageMap.get(subs.getID()).getParentTask().updateStatus();
        subsStorageMap.remove(subs.getID());
    }
}
