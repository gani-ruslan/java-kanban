package kanban.managers;

import java.util.*;

import kanban.tasks.*;
import static kanban.tasks.TaskStatus.*;


public class InMemoryTaskManager implements TaskManager {

    private Integer globalIDCounter;
    private final HistoryManager history;

    private final Map<Integer, Task> taskStorageMap;
    private final Map<Integer, Epic> epicStorageMap;
    private final Map<Integer, SubTask> subStorageMap;

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
    public List<Task> getTaskList() {
        return new ArrayList<>(taskStorageMap.values());
    }

    @Override
    public List<Epic> getEpicList() {
        return new ArrayList<>(epicStorageMap.values());
    }

    @Override
    public List<SubTask> getSubList() {
        return new ArrayList<>(subStorageMap.values());
    }

    @Override
    public List<SubTask> getEpicSubTaskList(Integer epicID) {
        if (epicID == null)
            throw new IllegalArgumentException("epicID must not be null.");

        if (!epicStorageMap.containsKey(epicID))
            throw new NoSuchElementException("Epic with ID:" + epicID + " not found.");

        List<SubTask> subTaskList = new ArrayList<>();
        for (Integer subTaskID : epicStorageMap.get(epicID).getSubIDList()) {
            subTaskList.add(subStorageMap.get(subTaskID));
        }
        return subTaskList;
    }

    // Get task by ID (task/epic/sub)
    @Override
    public Task getTaskByID(Integer taskID) {
        if (taskID == null)
            throw new IllegalArgumentException("taskID must not be null.");

        if (!taskStorageMap.containsKey(taskID))
            throw new NoSuchElementException("Task with ID:" + taskID + " not found.");

        history.add(taskStorageMap.get(taskID));
        return new Task(taskStorageMap.get(taskID));
    }

    @Override
    public Epic getEpicByID(Integer epicID) {
        if (epicID == null)
            throw new IllegalArgumentException("epicID must not be null.");

        if (!epicStorageMap.containsKey(epicID))
            throw new NoSuchElementException("Epic with ID:" + epicID + " not found.");

        history.add(epicStorageMap.get(epicID));
        return new Epic(epicStorageMap.get(epicID));
    }

    @Override
    public SubTask getSubTaskByID(Integer subID) {
        if (subID == null)
            throw new IllegalArgumentException("subID must not be null.");

        if (!subStorageMap.containsKey(subID))
            throw new NoSuchElementException("Subtask with ID:" + subID + " not found.");

        history.add(subStorageMap.get(subID));
        return new SubTask(subStorageMap.get(subID));
    }

    @Override
    public ArrayList<Task> getHistoryTask() {
        return new ArrayList<>(history.getTasks());
    }


    // Add task (by type task/epic/sub)
    @Override
    public void addTask(Task newTask) {
        if (newTask == null)
            throw new IllegalArgumentException("New Task must not be null.");

        newTask.setID(generateID());
        taskStorageMap.put(newTask.getID(), new Task(newTask));
    }

    @Override
    public void addEpic(Epic newEpic) {
        if (newEpic == null)
            throw new IllegalArgumentException("New Epic must not be null.");

        newEpic.setID(generateID());
        epicStorageMap.put(newEpic.getID(), new Epic(newEpic));
    }

    @Override
    public void addSub(SubTask newSub) {
        if (newSub == null)
            throw new IllegalArgumentException("New Subtask must not be null.");

        newSub.setID(generateID());
        subStorageMap.put(newSub.getID(), new SubTask(newSub));
    }


    // Update different task type by ID
    @Override
    public void updateTask(Task updateTask) {
        if (updateTask == null)
            throw new IllegalArgumentException("Updated Task must not be null.");

        if (!taskStorageMap.containsKey(updateTask.getID()))
            throw new NoSuchElementException("Subtask with ID:" + updateTask.getID() + " not found.");

        taskStorageMap.put(updateTask.getID(), new Task(updateTask));
    }

    @Override
    public void updateEpic(Epic updateEpic) {
        if (updateEpic == null)
            throw new IllegalArgumentException("Updated Epic must not be null.");

        if (!epicStorageMap.containsKey(updateEpic.getID()))
            throw new NoSuchElementException("Epic with ID:" + updateEpic.getID() + " not found.");

        epicStorageMap.put(updateEpic.getID(), new Epic(updateEpic));
        updateStatus(updateEpic.getID());
    }

    @Override
    public void updateSub(SubTask updateSub) {
        if (updateSub == null)
            throw new IllegalArgumentException("Updated SubTask must not be null.");

        if (!subStorageMap.containsKey(updateSub.getID()))
            throw new NoSuchElementException("Subtask with ID:" + updateSub.getID() + " not found.");

        subStorageMap.put(updateSub.getID(), new SubTask(updateSub));
        updateStatus(updateSub.getParentID());
    }


    // Remove task (task/epic/sub)
    @Override
    public void removeTaskByID(Integer taskID) {
        if (taskID == null)
            throw new IllegalArgumentException("Removing taskID must not be null.");

        if (!taskStorageMap.containsKey(taskID))
            throw new NoSuchElementException("Task with ID:" + taskID + " not found.");

        history.remove(taskID);
        taskStorageMap.remove(taskID);
    }

    @Override
    public void removeEpicByID(Integer epicID) {
        if (epicID == null)
            throw new IllegalArgumentException("Removing epicID must not be null.");

        if (!epicStorageMap.containsKey(epicID))
            throw new NoSuchElementException("Epic with ID:" + epicID + " not found.");

        for (Integer subID : epicStorageMap.get(epicID).getSubIDList()) {
            history.remove(subID);
            subStorageMap.remove(subID);
        }
        history.remove(epicID);
        epicStorageMap.remove(epicID);
    }

    @Override
    public void removeSubByID(Integer subID) {
        if (subID == null)
            throw new IllegalArgumentException("Removing subID must not be null.");

        if (!subStorageMap.containsKey(subID))
            throw new NoSuchElementException("Subtask with ID:" + subID + " not found.");

        epicStorageMap.get(subStorageMap.get(subID).getParentID()).removeSubID(subID);
        updateStatus(subStorageMap.get(subID).getParentID());
        history.remove(subID);
        subStorageMap.remove(subID);
    }


    // Remove all tasks by type (task/epic/sub)
    @Override
    public void removeAllTask() {
        if (taskStorageMap.isEmpty()) return;

        for (Integer taskID : taskStorageMap.keySet()) history.remove(taskID);
        taskStorageMap.clear();
    }

    @Override
    public void removeAllEpic() {
        if (!epicStorageMap.isEmpty()) {
            for (Integer epicID : epicStorageMap.keySet()) history.remove(epicID);
            epicStorageMap.clear();
        }
        if (!subStorageMap.isEmpty()) {
            for (Integer subID : subStorageMap.keySet()) history.remove(subID);
            subStorageMap.clear();
        }
    }

    @Override
    public void removeAllSub() {
        if (subStorageMap.isEmpty()) return;

        for (SubTask subTask : subStorageMap.values()) {
            epicStorageMap.get(subTask.getParentID()).removeSubID(subTask.getID());
        }
        for (Epic epicTask : epicStorageMap.values()) {
            updateStatus(epicTask.getID());
        }
        for (Integer subID : subStorageMap.keySet()) history.remove(subID);
        subStorageMap.clear();
    }


    // Private methods
    private Integer generateID() {
        return globalIDCounter++;
    }

    private void updateStatus(Integer epicID) {
        // ID not a epicTaskID / EpicID is null / EpicID not found.
        if (epicID == null)
            throw new IllegalArgumentException("epicID must not be null.");

        if (!epicStorageMap.containsKey(epicID))
            throw new NoSuchElementException("Epic with ID:" + epicID + " not found.");

        int countDoneTask = 0;
        int countInProgressTask = 0;

        // if epicTask without subTask
        if (epicStorageMap.get(epicID).getSubIDList().isEmpty()) {
            epicStorageMap.get(epicID).setStatus(NEW);
            return;
        }

        // if epicTask has some subTask
        for (Integer subID : epicStorageMap.get(epicID).getSubIDList()) {
            if (subStorageMap.containsKey(subID)) {
                if (subStorageMap.get(subID).getStatus() == IN_PROGRESS) countInProgressTask++;
                if (subStorageMap.get(subID).getStatus() == DONE) countDoneTask++;
            }
        }

        if (countDoneTask == epicStorageMap.get(epicID).getSubIDList().size()) {
            epicStorageMap.get(epicID).setStatus(DONE);
        } else if (countInProgressTask > 0) {
            epicStorageMap.get(epicID).setStatus(IN_PROGRESS);
        } else {
            epicStorageMap.get(epicID).setStatus(NEW);
        }
    }
}


