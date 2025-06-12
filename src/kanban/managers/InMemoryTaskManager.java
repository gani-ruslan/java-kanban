package kanban.managers;

import static kanban.tasks.TaskStatus.DONE;
import static kanban.tasks.TaskStatus.IN_PROGRESS;
import static kanban.tasks.TaskStatus.NEW;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import kanban.tasks.Epic;
import kanban.tasks.SubTask;
import kanban.tasks.Task;
import kanban.tasks.TaskStatus;
import kanban.utility.TimeSchedule;

/**
 * In-memory implementation of the TaskManager interface.
 * Stores and manages tasks, epics, and subtasks using hash maps.
 * Supports task history tracking, prioritization, and time validation.
 */
public class InMemoryTaskManager implements TaskManager {
    protected Integer globalIdCounter;
    protected final HistoryManager history;
    protected final Map<Integer, Task> taskStorageMap;
    protected final Map<Integer, Epic> epicStorageMap;
    protected final Map<Integer, SubTask> subStorageMap;
    protected TimeSchedule timeTable;
    protected final Set<Task> taskPriorityOrderList;

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
        timeTable = new TimeSchedule();
        taskPriorityOrderList = new TreeSet<>(Comparator.naturalOrder());
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
     * @return List of subtasks objects belonging to the specified epic
     * @throws IllegalArgumentException if epicId is null
     * @throws NoSuchElementException   if no epic with the specified ID exists
     */
    @Override
    public Optional<List<SubTask>> getEpicSubTaskList(Integer epicId) {
        return Optional.ofNullable(epicStorageMap.get(epicId))
                .flatMap(epic -> Optional.ofNullable(epic.getSubIdList()))
                .map(subIdList -> subIdList.stream()
                        .map(subStorageMap::get)
                        .toList()
                );
    }

    /**
     * Retrieves a task by its ID and adds it to the history.
     *
     * @param taskId the ID of the task
     * @return an Optional containing the task, or empty if not found
     * @throws IllegalArgumentException if taskId is null
     */
    @Override
    public Optional<Task> getTaskById(Integer taskId) {
        return Optional.ofNullable(getTaskByIdGeneric(taskStorageMap, taskId, history));
    }

    /**
     * Retrieves an epic by its ID and adds it to the history.
     *
     * @param epicId the ID of the epic
     * @return an Optional containing the epic, or empty if not found
     * @throws IllegalArgumentException if epicId is null
     */
    @Override
    public Optional<Epic> getEpicById(Integer epicId) {
        return Optional.ofNullable(getTaskByIdGeneric(epicStorageMap, epicId, history));
    }

    /**
     * Retrieves a subtask by its ID and adds it to the history.
     *
     * @param subId the ID of the subtask
     * @return an Optional containing the subtask, or empty if not found
     * @throws IllegalArgumentException if subId is null
     */
    @Override
    public Optional<SubTask> getSubTaskById(Integer subId) {
        return Optional.ofNullable(getTaskByIdGeneric(subStorageMap, subId, history));
    }

    /**
     * Retrieves the history of viewed tasks in the order they were accessed.
     *
     * @return list of tasks in the access history
     */
    @Override
    public ArrayList<Task> getHistoryTask() {
        return new ArrayList<>(history.getTasks());
    }

    /**
     * Adds a new task to the storage.
     *
     * @param task the task to be added
     * @throws IllegalArgumentException if the task is null
     * @throws TaskTimeOverlapException if the task time overlaps with another task
     */
    @Override
    public void addTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("New task must not be null.");
        }
        if (task.getId() == null || task.getId() == 0) {
            task.setId(generateId());
        }
        if (timeTable.isValidTimeValue(task.getStartTime(), task.getDuration())) {
            if (timeTable.isTimeOverlapped(task.getStartTime(), task.getDuration())) {
                throw new TaskTimeOverlapException("New task with id: "
                        + task.getId() + " time overlapped with other task.");
            }
            timeTable.addTimeInterval(task.getStartTime(), task.getDuration());
            taskPriorityOrderList.add(new Task(task));
        }
        taskStorageMap.put(task.getId(), new Task(task));
    }

    /**
     * Adds a new subtask to the storage.
     *
     * @param sub the subtask to be added
     * @throws IllegalArgumentException if the subtask is null
     * @throws TaskTimeOverlapException if the subtask time overlaps with another task
     */
    @Override
    public void addSub(SubTask sub) {
        if (sub == null) {
            throw new IllegalArgumentException("New Subtask must not be null.");
        }
        if (sub.getId() == null || sub.getId() == 0) {
            sub.setId(generateId());
        }
        if (timeTable.isValidTimeValue(sub.getStartTime(), sub.getDuration())) {
            if (timeTable.isTimeOverlapped(sub.getStartTime(), sub.getDuration())) {
                throw new TaskTimeOverlapException("New task time overlapped with other task.");
            }
            timeTable.addTimeInterval(sub.getStartTime(), sub.getDuration());
            taskPriorityOrderList.add(new SubTask(sub));
        }

        if (sub.getParentId() != 0) {
            Epic epic = epicStorageMap.get(sub.getParentId());
            if (epic != null) {
                epic.addSubId(sub.getId());
            } else {
                throw new NoSuchElementException("SubTask linked to epic with id: "
                        + sub.getParentId() + " not found.");
            }
        }

        subStorageMap.put(sub.getId(), new SubTask(sub));

    }

    /**
     * Adds a new epic to the storage.
     *
     * @param epic the epic to be added
     * @throws IllegalArgumentException if epic is null
     */
    @Override
    public void addEpic(Epic epic) {
        if (epic == null) {
            throw new IllegalArgumentException("New epic must not be null.");
        }
        if (epic.getId() == null || epic.getId() == 0) {
            epic.setId(generateId());
        }
        if (!epic.getSubIdList().isEmpty()) {
            epic.getSubIdList().forEach(subId -> {
                if (subStorageMap.get(subId) != null) {
                    if (subStorageMap.get(subId).getParentId() == 0
                            || subStorageMap.get(subId).getParentId().equals(epic.getId())) {
                        subStorageMap.get(subId).setParentId(epic.getId());
                    } else {
                        throw new IllegalStateException("Data inconsistency: "
                                + "Epic with id: " + epic.getId()
                                + " lists child sub id: " + subId
                                + ", but SubTask with this id, already linked "
                                + "to another Epic task.");
                    }
                }
            });
        }
        epicStorageMap.put(epic.getId(), new Epic(epic));
    }

    /**
     * Updates an existing task with new data.
     *
     * @param updateTask The task with updated data
     * @throws IllegalArgumentException if updateTask is null
     * @throws NoSuchElementException   if no task with the specified ID exists
     */
    @Override
    public void updateTask(Task updateTask) {
        if (updateTask == null) {
            throw new IllegalArgumentException("Updated Task must not be null.");
        }

        Task currentTask = taskStorageMap.get(updateTask.getId());

        if (currentTask == null) {
            throw new NoSuchElementException("Task to update with id: "
                    + updateTask.getId() + " not found.");
        }

        timeTable = updateTimeTable(currentTask, updateTask);
        updateTaskPriorityOrderList(currentTask, updateTask);

        taskStorageMap.put(updateTask.getId(), new Task(updateTask));
    }

    /**
     * Updates an existing subtask with new data and updates its parent epic.
     *
     * @param updateSub the subtask with updated data
     * @throws IllegalArgumentException if updateSub is null
     * @throws NoSuchElementException   if no subtask with the specified ID exists
     * @throws IllegalStateException    if the subtask/epic data is inconsistent
     */
    @Override
    public void updateSub(SubTask updateSub) {
        if (updateSub == null) {
            throw new IllegalArgumentException("Updated SubTask must not be null.");
        }

        SubTask currentSub = subStorageMap.get(updateSub.getId());

        if (currentSub == null) {
            throw new NoSuchElementException("Updated subtask with id: "
                    + updateSub.getId() + " not found.");
        }

        timeTable = updateTimeTable(currentSub, updateSub);
        updateTaskPriorityOrderList(currentSub, updateSub);

        subStorageMap.put(updateSub.getId(), new SubTask(updateSub));

        Epic epic = epicStorageMap.get(updateSub.getParentId());
        if (epic != null) {
            if (epic.getSubIdList().contains(updateSub.getId())) {
                Epic updateEpic = updateEpicTime(epic, timeTable);
                updateEpic = updateEpicStatus(updateEpic);
                epicStorageMap.put(updateEpic.getId(), updateEpic);
            } else {
                throw new IllegalStateException("Data inconsistency: "
                        + "Subtask with id: " + updateSub.getId()
                        + " lists parent epic id: " + epic.getId()
                        + ", but epic does not contain this subtask in its subIdList.");
            }
        }
    }

    /**
     * Updates an existing epic with new data and recalculates its status.
     *
     * @param updateEpic the epic with updated data
     * @throws IllegalArgumentException if updateEpic is null
     * @throws NoSuchElementException   if no epic with the specified ID exists
     */
    @Override
    public void updateEpic(Epic updateEpic) {
        if (updateEpic == null) {
            throw new IllegalArgumentException("Updated Epic must not be null.");
        }

        if (!epicStorageMap.containsKey(updateEpic.getId())) {
            throw new NoSuchElementException("Epic with id: " + updateEpic.getId() + " not found.");
        }

        Epic epic = updateEpicTime(updateEpic, timeTable);
        epic = updateEpicStatus(epic);
        epicStorageMap.put(epic.getId(), epic);
    }

    /**
     * Removes a task by its ID.
     *
     * @param id the ID of the task to remove
     * @throws IllegalArgumentException if id is null
     * @throws NoSuchElementException   if no task with the specified ID exists
     */
    @Override
    public void removeTaskById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Removing id must not be null.");
        }

        Task task = taskStorageMap.get(id);

        if (task == null) {
            throw new NoSuchElementException("Task with id: " + id + " not found.");
        }
        if (timeTable.isValidTimeValue(task.getStartTime(), task.getDuration())) {
            timeTable.removeTimeInterval(task.getStartTime(), task.getDuration());
            taskPriorityOrderList.remove(task);
        }

        history.remove(id);
        taskStorageMap.remove(id);
    }

    /**
     * Removes a subtask by its ID and updates its parent epic.
     *
     * @param id the ID of the subtask to remove
     * @throws IllegalArgumentException if id is null
     * @throws NoSuchElementException   if no subtask with the specified ID exists
     */
    @Override
    public void removeSubById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Removing id must not be null.");
        }

        SubTask sub = subStorageMap.get(id);

        if (sub == null) {
            throw new NoSuchElementException("Subtask with id: " + id + " not found.");
        }
        if (timeTable.isValidTimeValue(sub.getStartTime(), sub.getDuration())) {
            timeTable.removeTimeInterval(sub.getStartTime(), sub.getDuration());
            taskPriorityOrderList.remove(sub);
        }

        history.remove(id);
        subStorageMap.remove(id);

        Epic epic = epicStorageMap.get(sub.getParentId());
        if (epic != null) {
            if (epic.getSubIdList().contains(sub.getId())) {
                epic.removeSubId(id);
                Epic updateEpic = updateEpicTime(epic, timeTable);
                updateEpic = updateEpicStatus(updateEpic);
                epicStorageMap.put(updateEpic.getId(), updateEpic);
            } else {
                throw new IllegalStateException("Data inconsistency: "
                        + "Subtask with id: " + id
                        + " lists parent epic id: " + epic.getId()
                        + ", but epic does not contain this subtask in its subIdList.");
            }
        }
    }

    /**
     * Removes an epic by its ID, including all its subtasks.
     *
     * @param id the ID of the epic to remove
     * @throws IllegalArgumentException if id is null
     * @throws NoSuchElementException   if no epic with the specified ID exists
     */
    @Override
    public void removeEpicById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Removing id must not be null.");
        }

        Epic epic = epicStorageMap.get(id);

        if (epic == null) {
            throw new NoSuchElementException("Epic with id: " + id + " not found.");
        }

        if (!epic.getSubIdList().isEmpty()) {
            epic.getSubIdList()
                    .forEach(subId -> {
                        if (subId == null) {
                            return;
                        }
                        SubTask sub = subStorageMap.get(subId);
                        if (sub != null
                                && timeTable.isValidTimeValue(
                                        sub.getStartTime(), sub.getDuration()
                        )) {
                            timeTable.removeTimeInterval(sub.getStartTime(), sub.getDuration());
                            taskPriorityOrderList.remove(sub);
                        }
                        history.remove(subId);
                        subStorageMap.remove(subId);
                    });
        }
        history.remove(id);
        epicStorageMap.remove(id);
    }

    /**
     * Removes all tasks from the storage.
     */
    @Override
    public void removeAllTask() {
        if (taskStorageMap.isEmpty()) {
            return;
        }
        List<Integer> taskToRemove = new ArrayList<>(taskStorageMap.keySet());
        taskToRemove.forEach(this::removeTaskById);
        taskToRemove.forEach(id -> getTaskById(id).ifPresent(taskPriorityOrderList::remove));
    }

    /**
     * Removes all epics and their subtasks from the storage.
     */
    @Override
    public void removeAllEpic() {
        if (epicStorageMap.isEmpty()) {
            return;
        }
        List<Integer> taskToRemove = new ArrayList<>(epicStorageMap.keySet());
        taskToRemove.forEach(this::removeEpicById);
    }

    /**
     * Removes all subtasks and updates their parent epics.
     */
    @Override
    public void removeAllSub() {
        if (subStorageMap.isEmpty()) {
            return;
        }
        List<Integer> taskToRemove = new ArrayList<>(subStorageMap.keySet());
        taskToRemove.forEach(this::removeSubById);
        taskToRemove.forEach(id -> getTaskById(id).ifPresent(taskPriorityOrderList::remove));
    }

    /**
     * Generates a new unique ID for a task.
     *
     * @return next available unique ID
     */
    private Integer generateId() {
        return globalIdCounter++;
    }

    /**
     * Returns a list of tasks sorted by their start time.
     *
     * @return prioritized list of tasks
     */
    public List<Task> getPrioritizedTasks() {
        return taskPriorityOrderList.stream().toList();
    }

    /**
     * Updates the task priority list by replacing an old task with a new one.
     *
     * @param currentTask the existing task
     * @param updateTask  the updated task
     */
    private void updateTaskPriorityOrderList(Task currentTask, Task updateTask) {
        if (currentTask == null || updateTask == null) {
            return;
        }
        if (!taskPriorityOrderList.contains(currentTask)) {
            return;
        }
        taskPriorityOrderList.remove(currentTask);
        taskPriorityOrderList.add(updateTask);
    }

    /**
     * Generic method to retrieve a task by ID from any storage map.
     * Adds the task to the history if found.
     *
     * @param <T>        the type of task (Task, SubTask, Epic)
     * @param storageMap the storage map to search
     * @param taskId     the ID of the task to retrieve
     * @param history    the history manager to record the access
     * @return the task if found, otherwise null
     * @throws IllegalArgumentException if taskId is null
     */
    private <T extends Task> T getTaskByIdGeneric(Map<Integer, T> storageMap,
                                                  Integer taskId,
                                                  HistoryManager history) {
        if (taskId == null) {
            throw new IllegalArgumentException("taskId must not be null.");
        }
        T taskGeneric = storageMap.get(taskId);
        if (taskGeneric != null) {
            history.add(taskGeneric);
        }
        return taskGeneric;
    }

    /**
     * Recalculates the epic’s time interval based on its subtasks.
     *
     * @param epic      the epic to update
     * @param timeTable the current time schedule
     * @return updated epic with recalculated time
     */
    private Epic updateEpicTime(Epic epic, TimeSchedule timeTable) {

        TimeSchedule currentTimeTable = (timeTable == null) ? this.timeTable : timeTable;
        Epic updateEpic = new Epic(epic);

        if (updateEpic.getSubIdList().isEmpty()) {
            updateEpic.setStartTime(LocalDateTime.MIN);
            updateEpic.setEndTime(LocalDateTime.MIN);
            updateEpic.setDuration(Duration.ZERO);
            return updateEpic;
        }

        List<SubTask> subTaskList = updateEpic.getSubIdList()
                .stream()
                .map(subStorageMap::get)
                .filter(Objects::nonNull)
                .toList();

        Optional<Duration> epicDuration = subTaskList
                .stream()
                .map(Task::getDuration)
                .filter(Objects::nonNull)
                .filter(currentTimeTable::isValidDurationValue)
                .reduce(Duration::plus);

        Optional<LocalDateTime> epicStartTime = subTaskList
                .stream()
                .map(Task::getStartTime)
                .filter(Objects::nonNull)
                .filter(currentTimeTable::isValidStartTimeValue)
                .min(Comparator.naturalOrder());

        updateEpic.setStartTime(epicStartTime.orElse(LocalDateTime.MIN));
        updateEpic.setDuration(epicDuration.orElse(Duration.ZERO));
        updateEpic.setEndTime(updateEpic.getStartTime().plus(updateEpic.getDuration()));
        return updateEpic;
    }

    /**
     * Recalculates the epic's status based on the statuses of its subtasks.
     *
     * @param epic the epic to update
     * @return epic with updated status
     */
    private Epic updateEpicStatus(Epic epic) {

        Epic updateEpic = new Epic(epic);

        List<SubTask> subTaskList = updateEpic.getSubIdList()
                .stream()
                    .map(subStorageMap::get)
                    .filter(Objects::nonNull)
                    .toList();

        updateEpic.setStatus(calculateEpicTaskStatus(subTaskList));

        return updateEpic;
    }

    /**
     * Calculates the status of an epic based on its subtasks.
     *
     * @param subTaskList list of subtasks
     * @return calculated TaskStatus for the epic
     */
    private TaskStatus calculateEpicTaskStatus(List<SubTask> subTaskList) {

        if (subTaskList.isEmpty()) {
            return NEW;
        }

        boolean hasAnyInProgress = subTaskList.stream()
                .anyMatch(sub -> sub.getStatus() == IN_PROGRESS);

        if (hasAnyInProgress) {
            return IN_PROGRESS;
        }

        boolean hasAnyNew = subTaskList.stream()
                .anyMatch(sub -> sub.getStatus() == NEW);
        boolean hasAnyDone = subTaskList.stream()
                .anyMatch(sub -> sub.getStatus() == DONE);

        if (hasAnyNew && hasAnyDone) {
            return IN_PROGRESS;
        } else if (hasAnyNew) {
            return NEW;
        } else {
            return DONE;
        }
    }

    /**
     * Updates the timetable with a modified task.
     * Ensures no overlapping occurs in the updated schedule.
     *
     * @param currentTask the task before update
     * @param updateTask  the task after update
     * @return updated TimeSchedule
     * @throws TaskTimeOverlapException if time overlap occurs
     */
    private TimeSchedule updateTimeTable(Task currentTask, Task updateTask) {

        TimeSchedule updateTimeTable = new TimeSchedule(timeTable.getTimeSchedule());

        boolean currentTaskTimeValid = timeTable.isValidTimeValue(
                currentTask.getStartTime(), currentTask.getDuration()
        );
        boolean updateTaskTimeValid = timeTable.isValidTimeValue(
                updateTask.getStartTime(), updateTask.getDuration()
        );

        if (updateTaskTimeValid && currentTaskTimeValid) {

            if (!currentTask.getStartTime().equals(updateTask.getStartTime())
                    || !currentTask.getDuration().equals(updateTask.getDuration())) {

                updateTimeTable.removeTimeInterval(
                        currentTask.getStartTime(), currentTask.getDuration()
                );
                if (updateTimeTable.isTimeOverlapped(
                        updateTask.getStartTime(), updateTask.getDuration()
                )) {
                    throw new TaskTimeOverlapException("Update task with id: "
                            + updateTask.getId() + " time overlapped with other task.");
                }
                updateTimeTable.addTimeInterval(
                        updateTask.getStartTime(), updateTask.getDuration()
                );
            }

        } else if (!currentTaskTimeValid && updateTaskTimeValid) {
            if (updateTimeTable.isTimeOverlapped(
                    updateTask.getStartTime(), updateTask.getDuration()
            )) {
                throw new TaskTimeOverlapException("Update task with id: "
                        + updateTask.getId() + " time overlapped with other task.");
            }
            updateTimeTable.addTimeInterval(
                    updateTask.getStartTime(), updateTask.getDuration()
            );

        } else if (currentTaskTimeValid) {
            updateTimeTable.removeTimeInterval(
                    currentTask.getStartTime(), currentTask.getDuration()
            );
        }
        return updateTimeTable;
    }
}