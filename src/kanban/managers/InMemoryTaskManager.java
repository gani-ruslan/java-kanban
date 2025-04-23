package kanban.managers;

import static kanban.tasks.TaskStatus.DONE;
import static kanban.tasks.TaskStatus.IN_PROGRESS;
import static kanban.tasks.TaskStatus.NEW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import kanban.tasks.Epic;
import kanban.tasks.SubTask;
import kanban.tasks.Task;

/**
 * InMemoryTaskManager is an in-memory implementation of the TaskManager interface.
 * It manages tasks, epics, and subtasks using separate maps.
 * Main responsibilities:
 * - Create, retrieve, update, and delete tasks of all types</li>
 * - Manage relationships between epics and subtasks</li>
 * - Automatically update the status of epics based on their subtasks</li>
 * - Maintain a history of accessed tasks using a HistoryManager</li>
 * This manager does not provide persistence and is intended for use in memory only.
 */
public class InMemoryTaskManager implements TaskManager {
    private Integer globalIdCounter;
    private final HistoryManager history;
    private final Map<Integer, Task> taskStorageMap;
    private final Map<Integer, Epic> epicStorageMap;
    private final Map<Integer, SubTask> subStorageMap;

    /**
     * Constructs a new InMemoryTaskManager with empty storages
     * for tasks, epics, and subtasks. Initializes the history manager
     * and sets the starting value for the ID generator.
     */
    public InMemoryTaskManager() {
        globalIdCounter = 1;
        history = Managers.getDefaultHistory();
        taskStorageMap = new HashMap<>();
        epicStorageMap = new HashMap<>();
        subStorageMap = new HashMap<>();
    }

    /**
     * Returns a list of all regular tasks.
     *
     * @return List of all tasks in the storage
     */
    @Override
    public List<Task> getTaskList() {
        return new ArrayList<>(taskStorageMap.values());
    }

    /**
     * Returns a list of all epics.
     *
     * @return List of all epics in the storage
     */
    @Override
    public List<Epic> getEpicList() {
        return new ArrayList<>(epicStorageMap.values());
    }

    /**
     * Returns a list of all subtasks.
     *
     * @return List of all subtasks in the storage
     */
    @Override
    public List<SubTask> getSubList() {
        return new ArrayList<>(subStorageMap.values());
    }

    /**
     * Returns a list of all subtasks belonging to a specific epic.
     *
     * @param epicId The ID of the epic whose subtasks are requested
     * @return List of subtasks belonging to the specified epic
     * @throws IllegalArgumentException if epicId is null
     * @throws NoSuchElementException if no epic with the specified ID exists
     */
    @Override
    public List<SubTask> getEpicSubTaskList(Integer epicId) {
        if (epicId == null) {
            throw new IllegalArgumentException("epicId must not be null.");
        }
        if (!epicStorageMap.containsKey(epicId)) {
            throw new NoSuchElementException("Epic with Id:" + epicId + " not found.");
        }

        List<SubTask> subTaskList = new ArrayList<>();
        for (Integer subTaskId : epicStorageMap.get(epicId).getSubIdList()) {
            subTaskList.add(subStorageMap.get(subTaskId));
        }
        return subTaskList;
    }

    /**
     * Retrieves a task by its ID and adds it to the history.
     *
     * @param taskId The ID of the task to retrieve
     * @return The requested task
     * @throws IllegalArgumentException if taskId is null
     * @throws NoSuchElementException if no task with the specified ID exists
     */
    @Override
    public Task getTaskById(Integer taskId) {
        return getTaskByIdGeneric(taskStorageMap, taskId, history);
    }

    /**
     * Retrieves an epic by its ID and adds it to the history.
     *
     * @param epicId The ID of the epic to retrieve
     * @return The requested epic
     * @throws IllegalArgumentException if epicId is null
     * @throws NoSuchElementException if no epic with the specified ID exists
     */
    @Override
    public Epic getEpicById(Integer epicId) {
        return getTaskByIdGeneric(epicStorageMap, epicId, history);
    }

    /**
     * Retrieves a subtask by its ID and adds it to the history.
     *
     * @param subId The ID of the subtask to retrieve
     * @return The requested subtask
     * @throws IllegalArgumentException if subId is null
     * @throws NoSuchElementException if no subtask with the specified ID exists
     */
    @Override
    public SubTask getSubTaskById(Integer subId) {
        return getTaskByIdGeneric(subStorageMap, subId, history);
    }

    /**
     * Returns the history of recently viewed tasks.
     *
     * @return List of tasks in the order they were accessed
     */
    @Override
    public ArrayList<Task> getHistoryTask() {
        return new ArrayList<>(history.getTasks());
    }

    /**
     * Adds a new task to the storage.
     *
     * @param newTask The task to be added
     * @throws IllegalArgumentException if newTask is null
     */
    @Override
    public void addTask(Task newTask) {
        if (newTask == null) {
            throw new IllegalArgumentException("New Task must not be null.");
        }

        newTask.setId(generateId());
        taskStorageMap.put(newTask.getId(), new Task(newTask));
    }

    /**
     * Adds a new epic to the storage.
     *
     * @param newEpic The epic to be added
     * @throws IllegalArgumentException if newEpic is null
     */
    @Override
    public void addEpic(Epic newEpic) {
        if (newEpic == null) {
            throw new IllegalArgumentException("New Epic must not be null.");
        }

        newEpic.setId(generateId());
        epicStorageMap.put(newEpic.getId(), new Epic(newEpic));
    }

    /**
     * Adds a new subtask to the storage.
     *
     * @param newSub The subtask to be added
     * @throws IllegalArgumentException if newSub is null
     */
    @Override
    public void addSub(SubTask newSub) {
        if (newSub == null) {
            throw new IllegalArgumentException("New Subtask must not be null.");
        }

        newSub.setId(generateId());
        subStorageMap.put(newSub.getId(), new SubTask(newSub));
    }

    /**
     * Updates an existing task with new data.
     *
     * @param updateTask The task with updated data
     * @throws IllegalArgumentException if updateTask is null
     * @throws NoSuchElementException if no task with the specified ID exists
     */
    @Override
    public void updateTask(Task updateTask) {
        if (updateTask == null) {
            throw new IllegalArgumentException("Updated Task must not be null.");
        }
        if (!taskStorageMap.containsKey(updateTask.getId())) {
            throw new NoSuchElementException("Subtask with Id:"
                    + updateTask.getId() + " not found.");
        }

        // Potential SECURITY BREACH
        taskStorageMap.put(updateTask.getId(), new Task(updateTask));
    }

    /**
     * Updates an existing epic with new data and recalculates its status.
     *
     * @param updateEpic The epic with updated data
     * @throws IllegalArgumentException if updateEpic is null
     * @throws NoSuchElementException if no epic with the specified ID exists
     */
    @Override
    public void updateEpic(Epic updateEpic) {
        if (updateEpic == null) {
            throw new IllegalArgumentException("Updated Epic must not be null.");
        }
        if (!epicStorageMap.containsKey(updateEpic.getId())) {
            throw new NoSuchElementException("Epic with Id:" + updateEpic.getId() + " not found.");
        }

        // Potential SECURITY BREACH
        epicStorageMap.put(updateEpic.getId(), new Epic(updateEpic));
        updateStatus(updateEpic.getId());
    }

    /**
     * Updates an existing subtask with new data and updates its parent epic's status.
     *
     * @param updateSub The subtask with updated data
     * @throws IllegalArgumentException if updateSub is null
     * @throws NoSuchElementException if no subtask with the specified ID exists
     */
    @Override
    public void updateSub(SubTask updateSub) {
        if (updateSub == null) {
            throw new IllegalArgumentException("Updated SubTask must not be null.");
        }
        if (!subStorageMap.containsKey(updateSub.getId())) {
            throw new NoSuchElementException("Subtask with Id:"
                    + updateSub.getId() + " not found.");
        }

        // Potential SECURITY BREACH
        subStorageMap.put(updateSub.getId(), new SubTask(updateSub));
        updateStatus(updateSub.getParentId());
    }

    /**
     * Removes a task by its ID.
     *
     * @param taskId The ID of the task to remove
     * @throws IllegalArgumentException if taskId is null
     * @throws NoSuchElementException if no task with the specified ID exists
     */
    @Override
    public void removeTaskById(Integer taskId) {
        if (taskId == null) {
            throw new IllegalArgumentException("Removing taskId must not be null.");
        }
        if (!taskStorageMap.containsKey(taskId)) {
            throw new NoSuchElementException("Task with Id:" + taskId + " not found.");
        }

        history.remove(taskId);
        taskStorageMap.remove(taskId);
    }

    /**
     * Removes an epic by its ID, including all its subtasks.
     *
     * @param epicId The ID of the epic to remove
     * @throws IllegalArgumentException if epicId is null
     * @throws NoSuchElementException if no epic with the specified ID exists
     */
    @Override
    public void removeEpicById(Integer epicId) {
        if (epicId == null) {
            throw new IllegalArgumentException("Removing epicId must not be null.");
        }
        if (!epicStorageMap.containsKey(epicId)) {
            throw new NoSuchElementException("Epic with Id:" + epicId + " not found.");
        }

        for (Integer subId : epicStorageMap.get(epicId).getSubIdList()) {
            history.remove(subId);
            subStorageMap.remove(subId);
        }

        history.remove(epicId);
        epicStorageMap.remove(epicId);
    }

    /**
     * Removes a subtask by its ID and updates its parent epic.
     *
     * @param subId The ID of the subtask to remove
     * @throws IllegalArgumentException if subId is null
     * @throws NoSuchElementException if no subtask with the specified ID exists
     */
    @Override
    public void removeSubById(Integer subId) {
        if (subId == null) {
            throw new IllegalArgumentException("Removing subId must not be null.");
        }
        if (!subStorageMap.containsKey(subId)) {
            throw new NoSuchElementException("Subtask with Id:" + subId + " not found.");
        }

        epicStorageMap.get(subStorageMap.get(subId).getParentId()).removeSubId(subId);
        updateStatus(subStorageMap.get(subId).getParentId());
        history.remove(subId);
        subStorageMap.remove(subId);
    }

    /**
     * Removes all tasks from the storage.
     */
    @Override
    public void removeAllTask() {
        if (taskStorageMap.isEmpty()) {
            return;
        }

        for (Integer taskId : taskStorageMap.keySet()) {
            history.remove(taskId);
        }

        taskStorageMap.clear();
    }

    /**
     * Removes all epics and their subtasks from the storage.
     */
    @Override
    public void removeAllEpic() {
        if (!epicStorageMap.isEmpty()) {
            for (Integer epicId : epicStorageMap.keySet()) {
                history.remove(epicId);
            }
            epicStorageMap.clear();
        }
        if (!subStorageMap.isEmpty()) {
            for (Integer subId : subStorageMap.keySet()) {
                history.remove(subId);
            }
            subStorageMap.clear();
        }
    }

    /**
     * Removes all subtasks from the storage and updates their parent epics.
     */
    @Override
    public void removeAllSub() {
        if (subStorageMap.isEmpty()) {
            return;
        }

        for (SubTask subTask : subStorageMap.values()) {
            epicStorageMap.get(subTask.getParentId()).removeSubId(subTask.getId());
        }
        for (Epic epicTask : epicStorageMap.values()) {
            updateStatus(epicTask.getId());
        }
        for (Integer subId : subStorageMap.keySet()) {
            history.remove(subId);
        }

        subStorageMap.clear();
    }

    /**
     * Generates a new unique ID for tasks.
     *
     * @return The next available ID
     */
    private Integer generateId() {
        return globalIdCounter++;
    }

    /**
     * Generic method to retrieve a task by ID from any storage map.
     *
     * @param <T> The type of task (Task, Epic, or SubTask)
     * @param storageMap The map to search for the task
     * @param taskId The ID of the task to retrieve
     * @param history The history manager to record the access
     * @return The requested task
     * @throws IllegalArgumentException if taskId is null
     * @throws NoSuchElementException if no task with the specified ID exists
     */
    private <T extends Task> T getTaskByIdGeneric(Map<Integer, T> storageMap,
                                                  Integer taskId,
                                                  HistoryManager history) {
        if (taskId == null) {
            throw new IllegalArgumentException("taskId must not be null.");
        }
        T taskGeneric = storageMap.get(taskId);
        if (taskGeneric == null) {
            throw new NoSuchElementException("Task with Id:" + taskId + " not found.");
        }

        history.add(taskGeneric);
        return taskGeneric;
    }

    /**
     * Updates the status of an epic based on the statuses of its subtasks.
     *
     * @param epicId The ID of the epic to update
     * @throws IllegalArgumentException if epicId is null
     * @throws NoSuchElementException if no epic with the specified ID exists
     */
    private void updateStatus(Integer epicId) {
        if (epicId == null) {
            throw new IllegalArgumentException("epicId must not be null.");
        }
        if (!epicStorageMap.containsKey(epicId)) {
            throw new NoSuchElementException("Epic with Id:" + epicId + " not found.");
        }

        Epic epicTask = epicStorageMap.get(epicId);
        List<Integer> subTaskList = epicTask.getSubIdList();

        if (subTaskList.isEmpty()) {
            epicTask.setStatus(NEW);
            return;
        }

        int countDoneTask = 0;
        int countInProgressTask = 0;

        for (Integer subId : subTaskList) {
            SubTask subTask = subStorageMap.get(subId);
            if (subTask == null) {
                continue;
            }
            switch (subTask.getStatus()) {
                case DONE -> countDoneTask++;
                case IN_PROGRESS -> countInProgressTask++;
                default -> {}
            }
        }

        if (countDoneTask == subTaskList.size()) {
            epicTask.setStatus(DONE);
        } else if (countInProgressTask > 0) {
            epicTask.setStatus(IN_PROGRESS);
        } else {
            epicTask.setStatus(NEW);
        }
    }
}