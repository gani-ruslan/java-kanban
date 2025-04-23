package kanban.managers;

import java.util.ArrayList;
import java.util.List;
import kanban.tasks.Epic;
import kanban.tasks.SubTask;
import kanban.tasks.Task;

/**
 * Interface for managing tasks, epics, and subtasks in the Kanban system.
 * Provides methods to add, update, remove, and retrieve tasks, epics, and subtasks.
 */
public interface TaskManager {

    /**
     * Retrieves a list of all tasks.
     *
     * @return a list of all tasks
     */
    List<Task> getTaskList();

    /**
     * Retrieves a list of all epics.
     *
     * @return a list of all epics
     */
    List<Epic> getEpicList();

    /**
     * Retrieves a list of all subtasks.
     *
     * @return a list of all subtasks
     */
    List<SubTask> getSubList();

    /**
     * Retrieves a list of subtasks associated with a specific epic.
     *
     * @param epicId the ID of the epic whose subtasks are to be retrieved
     * @return a list of subtasks associated with the specified epic
     */
    List<SubTask> getEpicSubTaskList(Integer epicId);

    /**
     * Retrieves a task by its ID.
     *
     * @param taskId the ID of the task to retrieve
     * @return the task with the specified ID
     */
    Task getTaskById(Integer taskId);

    /**
     * Retrieves an epic by its ID.
     *
     * @param epicId the ID of the epic to retrieve
     * @return the epic with the specified ID
     */
    Epic getEpicById(Integer epicId);

    /**
     * Retrieves a subtask by its ID.
     *
     * @param subId the ID of the subtask to retrieve
     * @return the subtask with the specified ID
     */
    SubTask getSubTaskById(Integer subId);

    /**
     * Retrieves the task history.
     *
     * @return a list of tasks that are part of the history
     */
    ArrayList<Task> getHistoryTask();

    /**
     * Adds a new task to the system.
     *
     * @param newTask the task to be added
     */
    void addTask(Task newTask);

    /**
     * Adds a new epic to the system.
     *
     * @param newEpic the epic to be added
     */
    void addEpic(Epic newEpic);

    /**
     * Adds a new subtask to the system.
     *
     * @param newSub the subtask to be added
     */
    void addSub(SubTask newSub);

    /**
     * Updates an existing task.
     *
     * @param updateTask the task to be updated
     */
    void updateTask(Task updateTask);

    /**
     * Updates an existing epic.
     *
     * @param updateEpic the epic to be updated
     */
    void updateEpic(Epic updateEpic);

    /**
     * Updates an existing subtask.
     *
     * @param updateSub the subtask to be updated
     */
    void updateSub(SubTask updateSub);

    /**
     * Removes a task by its ID.
     *
     * @param taskId the ID of the task to be removed
     */
    void removeTaskById(Integer taskId);

    /**
     * Removes an epic by its ID.
     *
     * @param epicId the ID of the epic to be removed
     */
    void removeEpicById(Integer epicId);

    /**
     * Removes a subtask by its ID.
     *
     * @param subId the ID of the subtask to be removed
     */
    void removeSubById(Integer subId);

    /**
     * Removes all tasks from the system.
     */
    void removeAllTask();

    /**
     * Removes all epics from the system.
     */
    void removeAllEpic();

    /**
     * Removes all subtasks from the system.
     */
    void removeAllSub();
}
