package kanban.managers;
import kanban.tasks.*;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private Integer globalTaskID;
    private HashMap<Integer, Task> taskStorageMap;
    private HashMap<Integer, Epic> epicStorageMap;
    private HashMap<Integer, SubTask> subsStorageMap;

    // Constructor
    public TaskManager() {
        globalTaskID = 0;
        taskStorageMap = new HashMap<>();
        epicStorageMap = new HashMap<>();
        subsStorageMap = new HashMap<>();
    }


    // Add task (by type task/epic/sub)
    public void addTask(Task newTask) {
        newTask.setID(createNewID());
        taskStorageMap.put(newTask.getID(), newTask);
    }

    public void addEpic(Epic newEpic) {
        newEpic.setID(createNewID());
        epicStorageMap.put(newEpic.getID(), newEpic);
    }

    public void addSub(SubTask newSubs, Epic parentTask) {
        newSubs.setID(createNewID());
        parentTask.addSubTask(newSubs);
        newSubs.setParentTask(parentTask);
        subsStorageMap.put(newSubs.getID(), newSubs);
    }


    // Get task by ID
    public Task getTaskByID(Integer taskID) {
        return taskStorageMap.get(taskID);
    }

    public Epic getEpicByID(Integer epicID) {
        return epicStorageMap.get(epicID);
    }

    public SubTask getSubTaskByID(Integer subsID) {
        return subsStorageMap.get(subsID);
    }


    // Get task list by type
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


    // Remove all tasks
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
            subTask.getParentTask().updateEpicStatus();
        }
        subsStorageMap.clear();
    }

    // Remove task by ID
    public void removeTaskByID(Task task) {
        taskStorageMap.remove(task.getID());
    }

    public void removeTaskByID(Integer taskID) {
        taskStorageMap.remove(taskID);
    }

    public void removeEpicByID(Epic epic) {
        for (SubTask subTask : epicStorageMap.get(epic.getID()).getSubTaskList()) subsStorageMap.remove(subTask.getID());
        epicStorageMap.remove(epic.getID());
    }

    public void removeEpicByID(Integer epicID) {
        for (SubTask subTask : epicStorageMap.get(epicID).getSubTaskList()) subsStorageMap.remove(subTask.getID());
        epicStorageMap.remove(epicID);
    }

    public void removeSubsByID(SubTask subs) {
        subsStorageMap.get(subs.getID()).getParentTask().removeSubTask(subsStorageMap.get(subs.getID()));
        subsStorageMap.get(subs.getID()).getParentTask().updateEpicStatus();
        subsStorageMap.remove(subs.getID());
    }


    public void removeSubsByID(Integer subsID) {
        subsStorageMap.get(subsID).getParentTask().removeSubTask(subsStorageMap.get(subsID));
        subsStorageMap.get(subsID).getParentTask().updateEpicStatus();
        subsStorageMap.remove(subsID);
    }


    // Update task by ID
    public void updateTask(Task updateTask) {
        taskStorageMap.put(updateTask.getID(), updateTask);
    }

    public void updateEpic(Epic updateEpic) {
        epicStorageMap.put(updateEpic.getID(), updateEpic);
    }

    public void updateSubs(SubTask updateSubs) {
        subsStorageMap.put(updateSubs.getID(), updateSubs);
    }

    // Set status for Task and SubTask;
    public void setTaskStatus(Task task, TaskStatus taskStatus) {
        task.setStatus(taskStatus);
    }

    public void setSubsStatus(SubTask subTask, TaskStatus taskStatus) {
        subTask.setStatus(taskStatus);
        subTask.getParentTask().updateEpicStatus();
    }

    // Setters and getters
    public Integer getGlobalID() {
        return globalTaskID;
    }

    public Integer createNewID() {
        return ++globalTaskID;
    }
}
