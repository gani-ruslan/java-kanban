package kanban.managers;

import kanban.tasks.*;
import java.util.ArrayList;
import java.util.HashMap;

import static kanban.tasks.TaskStatus.*;


public class InMemoryTaskManager implements TaskManager {

    private Integer globalIDCounter;
    private final HistoryManager history;

    private final HashMap<Integer, Task> taskStorageMap;
    private final HashMap<Integer, Epic> epicStorageMap;
    private final HashMap<Integer, SubTask> subStorageMap;

    // Constructor
    public InMemoryTaskManager() {
        globalIDCounter = 1;
        history = Managers.getDefaultHistory();
        taskStorageMap = new HashMap<>();
        epicStorageMap = new HashMap<>();
        subStorageMap = new HashMap<>();
    }

    // Get task list by type (task/epic/sub)
    @Override
    public ArrayList<Task> getTaskList() {
        return new ArrayList<>(taskStorageMap.values());
    }

    @Override
    public ArrayList<Epic> getEpicList() {
        return new ArrayList<>(epicStorageMap.values());
    }

    @Override
    public ArrayList<SubTask> getSubList() {
        return new ArrayList<>(subStorageMap.values());
    }

    @Override
    public ArrayList<SubTask> getEpicSubTaskList(Integer epicID) {
        ArrayList<SubTask> subTaskList = new ArrayList<>();
        for (Integer subTaskID : epicStorageMap.get(epicID).getSubTaskIDList()) {
            subTaskList.add(subStorageMap.get(subTaskID));
        }
        return subTaskList;
    }

    // Get task by ID (task/epic/sub)
    @Override
    public Task getTaskByID(Integer taskID) {
        history.add(taskStorageMap.get(taskID));
        return taskStorageMap.get(taskID);
    }

    @Override
    public Epic getEpicByID(Integer epicID) {
        history.add(epicStorageMap.get(epicID));
        return epicStorageMap.get(epicID);
    }

    @Override
    public SubTask getSubTaskByID(Integer subID) {
        history.add(subStorageMap.get(subID));
        return subStorageMap.get(subID);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return history.getHistory();
    }


    // Add task (by type task/epic/sub)
    @Override
    public void addTask(Task newTask) {
        newTask.setID(generateID());
        taskStorageMap.put(newTask.getID(), new Task(newTask));
    }

    @Override
    public void addEpic(Epic newEpic) {
        newEpic.setID(generateID());
        epicStorageMap.put(newEpic.getID(), new Epic(newEpic));
    }

    @Override
    public void addSub(SubTask newSub) {
        newSub.setID(generateID());
        subStorageMap.put(newSub.getID(), new SubTask(newSub));
    }

    @Override
    public void addSubToEpic(Integer subID, Integer epicID) {
        if (subID == null) {
            throw new IllegalArgumentException("Subtask ID cannot be null.");
        }
        if (epicID == null) {
            throw new IllegalArgumentException("Epic ID cannot be null.");
        }
        if (subID.equals(epicID)) {
            throw new IllegalArgumentException("Invalid operation: a subtask cannot have the same ID as its epic. ID: " + subID);
        }
        if (!subStorageMap.containsKey(subID)) {
            throw new IllegalArgumentException("Invalid subtask ID: " + subID + ". No such subtask found.");
        }
        if (!epicStorageMap.containsKey(epicID)) {
            throw new IllegalArgumentException("Invalid epic ID: " + epicID + ". No such epic found.");
        }
        subStorageMap.get(subID).setParentTaskID(epicID);
        epicStorageMap.get(epicID).addSubTaskID(subID);
        updateStatus(epicID);
    }

    private Integer generateID() {
        return globalIDCounter++;
    }


    // Update different task type by ID
    @Override
    public void updateTask(Task updateTask) {
        taskStorageMap.put(updateTask.getID(), new Task(updateTask));
    }

    @Override
    public void updateEpic(Epic updateEpic) {
        epicStorageMap.put(updateEpic.getID(), new Epic(updateEpic));
        updateStatus(updateEpic.getID());
    }

    @Override
    public void updateSub(SubTask updateSub) {
        subStorageMap.put(updateSub.getID(), new SubTask(updateSub));
        updateStatus(updateSub.getParentTaskID());
    }

    @Override
    public void updateStatus(Integer epicID) {
        int countDoneTask = 0;
        int countInProgressTask = 0;

        // ID not a epicTaskID / EpicID is null / EpicID not found.
        if (epicID == null) {
            throw new IllegalArgumentException("EpicID cannot be null.");
        }

        if (!epicStorageMap.containsKey(epicID)) {
            throw new IllegalArgumentException("Invalid argument: the provided ID (" + epicID + ") is not associated with an Epic task.");
        }

        // if epicTask without subTask
        if (epicStorageMap.get(epicID).getSubTaskIDList().isEmpty()) {
            epicStorageMap.get(epicID).setStatus(NEW);
            return;
        }

        // if epicTask has some subTask
        for (Integer subID : epicStorageMap.get(epicID).getSubTaskIDList()) {
            if (subStorageMap.containsKey(subID)) {
                if (subStorageMap.get(subID).getStatus() == IN_PROGRESS) countInProgressTask++;
                if (subStorageMap.get(subID).getStatus() == DONE) countDoneTask++;
            }
        }

        if (countDoneTask == epicStorageMap.get(epicID).getSubTaskIDList().size() ) {
            epicStorageMap.get(epicID).setStatus(DONE);
        } else if (countInProgressTask > 0) {
            epicStorageMap.get(epicID).setStatus(IN_PROGRESS);
        } else {
            epicStorageMap.get(epicID).setStatus(NEW);
        }
    }


    // Remove task (task/epic/sub)
    @Override
    public void removeTaskByID(Integer taskID) {
        taskStorageMap.remove(taskID);
    }

    @Override
    public void removeEpicByID(Integer epicID) {
        for (Integer subID : epicStorageMap.get(epicID).getSubTaskIDList()) subStorageMap.remove(subID);
        epicStorageMap.remove(epicID);
    }

    @Override
    public void removeSubByID(Integer subID) {
        epicStorageMap.get(subStorageMap.get(subID).getParentTaskID()).removeSubTaskID(subID);
        updateStatus(subStorageMap.get(subID).getParentTaskID());
        subStorageMap.remove(subID);
    }


    // Remove all tasks by type (task/epic/sub)
    @Override
    public void removeAllTask() {
        taskStorageMap.clear();
    }

    @Override
    public void removeAllEpic() {
        epicStorageMap.clear();
        subStorageMap.clear();
    }

    @Override
    public void removeAllSub() {
        for(SubTask subTask : subStorageMap.values()) {
            epicStorageMap.get(subTask.getParentTaskID()).removeSubTaskID(subTask.getID());
        }
        for (Epic epicTask : epicStorageMap.values()) {
            updateStatus(epicTask.getID());
        }
        subStorageMap.clear();
    }
}


