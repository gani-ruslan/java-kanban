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
        for(SubTask subTask : subsStorageMap.values()) subTask.getParentTask().removeSubTask(subTask);
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
    public void removeTaskByID(Integer taskID) {
        taskStorageMap.remove(taskID);
    }

    public void removeEpicByID(Integer epicID) {
        for (SubTask subTask : epicStorageMap.get(epicID).getSubTaskList()) subsStorageMap.remove(subTask.getID());
        epicStorageMap.remove(epicID);
    }

    public void removeSubsByID(Integer subsID) {
        subsStorageMap.get(subsID).getParentTask().removeSubTask(subsStorageMap.get(subsID));
        subsStorageMap.remove(subsID);
    }
}
