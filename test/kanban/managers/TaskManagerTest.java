package kanban.managers;

import static kanban.tasks.TaskStatus.DONE;
import static kanban.tasks.TaskStatus.IN_PROGRESS;
import static kanban.tasks.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import kanban.tasks.Epic;
import kanban.tasks.SubTask;
import kanban.tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

abstract class TaskManagerTest<T extends TaskManager> {

    protected abstract T createTaskManager();

    protected T taskManager;

    Task taskA;
    Task taskB;
    Task taskC;
    SubTask subA;
    SubTask subB;
    SubTask subC;
    Epic epicA;
    Epic epicB;

    @BeforeEach
    void beforeEachTest() {
        taskManager = createTaskManager();
        LocalDateTime testStartTime = LocalDateTime.of(2025, 1, 1,
                10, 0, 0);

        taskA = new Task(0, "Task A", NEW, "Task A description",
                testStartTime, Duration.ofMinutes(10));
        taskB = new Task(0, "Task B", NEW, "Task B description",
                testStartTime.plusMinutes(10), Duration.ofMinutes(10));
        taskC = new Task(0, "Task C", NEW, "Task C description",
                testStartTime.plusMinutes(20), Duration.ofMinutes(10));

        epicA = new Epic("Epic A", "Epic A description");
        epicB = new Epic("Epic B", "Epic B description");

        subA = new SubTask(0, "Sub A", NEW, "SubTask A description", 0,
                testStartTime.plusMinutes(30), Duration.ofMinutes(10));
        subB = new SubTask(0, "Sub B", NEW, "SubTask B description", 0,
                testStartTime.plusMinutes(40), Duration.ofMinutes(10));
        subC = new SubTask(0, "Sub C", NEW, "SubTask C description", 0,
                testStartTime.plusMinutes(50), Duration.ofMinutes(10));
    }

    /**
     * Verifies that when a task with a default ID (e.g., 0) is added,
     * the TaskManager assigns a new, non-default ID to it,
     * and the task can be retrieved with this new ID.
     */
    @Test
    void addTask_whenNewTaskAdded_thenIdIsGeneratedAndTaskIsRetrievable() {
        int initialId = taskA.getId();

        System.out.println(initialId);

        taskManager.addTask(taskA);
        System.out.println(taskA.getId());
        assertNotEquals(initialId, taskA.getId(),
                "Task ID should be updated from its initial value by the manager.");
        assertTrue(taskA.getId() != 0,
                "Generated Task ID should not be the default 'unassigned' ID (e.g., 0).");

        Task retrievedTaskA = taskManager.getTaskById(taskA.getId()).orElseThrow(() ->
                new NoSuchElementException("TaskA not found in taskManager."));
        assertEquals(taskA.getTitle(), retrievedTaskA.getTitle(),
                "Retrieved task's title should match.");
        assertEquals(taskA.getDescription(), retrievedTaskA.getDescription(),
                "Retrieved task's description should match.");
    }

    /**
     * Verifies that when an epic with a default ID is added,
     * the TaskManager assigns a new, non-default ID,
     * and the epic can be retrieved with this new ID.
     * Also checks default status and time for a new empty epic.
     */
    @Test
    void addEpic_whenNewEpicAdded_thenIdIsGeneratedAndEpicIsRetrievableWithDefaultState() {
        int initialId = epicA.getId();
        taskManager.addEpic(epicA);

        assertNotEquals(initialId, epicA.getId(),
                "Epic ID should be updated.");
        assertTrue(epicA.getId() != 0,
                "Generated Epic ID should not be default.");

        Epic retrievedEpicA = taskManager.getEpicById(epicA.getId()).orElseThrow(() ->
                new NoSuchElementException("EpicA not found in taskManager."));
        assertEquals(epicA.getTitle(), retrievedEpicA.getTitle(),
                "Retrieved epic's title should match.");

        assertEquals(NEW, retrievedEpicA.getStatus(),
                "A new epic without subtasks should have NEW status.");
        assertEquals(LocalDateTime.MIN, retrievedEpicA.getStartTime(),
                "A new epic without subtasks should have LocalDateTime.MIN start time.");
        assertEquals(Duration.ZERO, retrievedEpicA.getDuration(),
                "A new epic without subtasks should have Duration.ZERO duration.");
    }

    /**
     * Verifies that when a subtask (linked to an existing epic) with a default ID is added,
     * the TaskManager assigns a new, non-default ID, stores it,
     * and the subtask is correctly linked to its parent.
     */
    @Test
    void addSubTask_whenNewSubTaskAddedToExistingEpic_thenIdIsGeneratedAndParentIsUpdated() {
        taskManager.addEpic(epicA);
        assertNotEquals(0, epicA.getId(),
                "Parent epic should have a generated ID.");

        subA.setParentId(epicA.getId());
        int initialSubId = subA.getId();
        taskManager.addSub(subA);

        assertNotEquals(initialSubId, subA.getId(), "SubTask ID should be updated.");
        assertTrue(subA.getId() != 0, "Generated SubTask ID should not be default.");

        epicA.addSubId(subA.getId());
        taskManager.updateEpic(epicA);

        SubTask retrievedSubA = taskManager.getSubTaskById(subA.getId()).orElseThrow(() ->
                new NoSuchElementException("EpicA not found in taskManager."));
        assertEquals(subA.getTitle(), retrievedSubA.getTitle(),
                "Retrieved subtask's title should match.");

        assertEquals(epicA.getId(), retrievedSubA.getParentId(),
                "Retrieved subtask's parent ID should match epicA's ID.");

        epicA = taskManager.getEpicById(epicA.getId()).orElseThrow(() ->
                new NoSuchElementException("EpicA not found in taskManager."));
        assertTrue(epicA.getSubIdList().contains(subA.getId()),
                "Parent epic's subIdList should contain the new subtask's ID.");
    }

    /**
     * Verifies that getTaskList returns a non-empty list containing
     * all previously added tasks.
     */
    @Test
    void getTaskList_whenTasksAdded_shouldReturnListContainingThem() {
        taskManager.addTask(taskA);
        taskManager.addTask(taskB);

        List<Task> taskList = taskManager.getTaskList();
        assertEquals(2, taskList.size(), "Task list should contain 2 tasks.");
        assertTrue(taskList.contains(taskA), "Task list should contain taskA.");
        assertTrue(taskList.contains(taskB), "Task list should contain taskB.");
    }

    /**
     * Verifies that getEpicList returns a non-empty list containing
     * all previously added epics.
     */
    @Test
    void getEpicList_whenEpicsAdded_shouldReturnListContainingThem() {
        taskManager.addEpic(epicA);
        taskManager.addEpic(epicB);

        List<Epic> epicList = taskManager.getEpicList();
        assertEquals(2, epicList.size(), "Epic list should contain 2 epics.");
        assertTrue(epicList.contains(epicA), "Epic list should contain epicA.");
        assertTrue(epicList.contains(epicB), "Epic list should contain epicB.");
    }

    /**
     * Verifies that getSubList returns a non-empty list containing
     * all previously added subtasks.
     */
    @Test
    void getSubList_whenSubTasksAdded_shouldReturnListContainingThem() {
        taskManager.addEpic(epicA);
        subA.setParentId(epicA.getId());
        taskManager.addSub(subA);

        taskManager.addEpic(epicB);
        subB.setParentId(epicB.getId());
        taskManager.addSub(subB);

        List<SubTask> subTaskList = taskManager.getSubList();
        assertEquals(2, subTaskList.size(), "SubTask list should contain 2 subtasks.");
        assertTrue(subTaskList.contains(subA), "SubTask list should contain subA.");
        assertTrue(subTaskList.contains(subB), "SubTask list should contain subB.");
    }

    /**
     * Verifies that getEpicSubTaskList returns a list of subtasks
     * specifically associated with the given epic ID.
     */
    @Test
    void getEpicSubTaskList_forEpicWithSubtasks_shouldReturnItsSubtasks() {
        taskManager.addEpic(epicA);
        taskManager.addEpic(epicB);

        subA.setParentId(epicA.getId());
        subB.setParentId(epicA.getId());
        subC.setParentId(epicB.getId());

        taskManager.addSub(subA);
        taskManager.addSub(subB);
        taskManager.addSub(subC);

        epicA.addSubId(subA.getId());
        epicA.addSubId(subB.getId());
        epicB.addSubId(subC.getId());

        taskManager.updateEpic(epicA);
        taskManager.updateEpic(epicB);

        List<SubTask> subTasksOfEpicA;
        subTasksOfEpicA = taskManager.getEpicSubTaskList(epicA.getId()).orElseThrow(() ->
                new NoSuchElementException("Should find subtasks for epicA."));

        assertEquals(2, subTasksOfEpicA.size(), "EpicA should have 2 subtasks.");
        assertTrue(subTasksOfEpicA.contains(subA), "Subtasks of epicA should include subA.");
        assertTrue(subTasksOfEpicA.contains(subB), "Subtasks of epicA should include subB.");

        List<SubTask> subTasksOfEpicB;
        subTasksOfEpicB = taskManager.getEpicSubTaskList(epicB.getId()).orElseThrow(() ->
                new NoSuchElementException("Should find subtasks for epicB."));

        assertEquals(1, subTasksOfEpicB.size(), "EpicB should have 1 subtask.");
        assertTrue(subTasksOfEpicB.contains(subC), "Subtasks of epicB should include subC.");
    }

    /**
     * Verifies that getEpicSubTaskList returns an Optional containing an empty list
     * for an epic that exists but has no subtasks.
     */
    @Test
    void getEpicSubTaskList_forEpicWithNoSubtasks_shouldReturnEmptyListInOptional() {
        taskManager.addEpic(epicA);
        List<SubTask> subTasksList = taskManager.getEpicSubTaskList(epicA.getId()).orElseThrow(() ->
                new NoSuchElementException("EpicA not found in taskManager."));
        assertTrue(subTasksList.isEmpty(), "Subtask list should be empty.");
    }

    /**
     * Verifies that getEpicSubTaskList returns an empty Optional
     * for a non-existent epic ID.
     */
    @Test
    void getEpicSubTaskList_forNonExistentEpicId_shouldReturnEmptyOptional() {
        Optional<List<SubTask>> subTasksOpt = taskManager.getEpicSubTaskList(999);
        assertFalse(subTasksOpt.isPresent(), "Optional should be empty for non-existent epic.");
    }

    /**
     * Verifies that getTaskById returns an Optional containing the correct task
     * when a task with the given ID exists.
     */
    @Test
    void getTaskById_whenTaskExists_shouldReturnOptionalWithTask() {
        taskManager.addTask(taskA);
        Task retrievedTaskA = taskManager.getTaskById(taskA.getId()).orElseThrow(() ->
                new NoSuchElementException("TaskA not found in taskManager."));
        assertEquals(taskA, retrievedTaskA, "Found task should be the one added.");
        assertEquals(taskA.getTitle(), retrievedTaskA.getTitle());
    }

    /**
     * Verifies that getTaskById returns an empty Optional
     * when no task with the given ID exists.
     */
    @Test
    void getTaskById_whenTaskDoesNotExist_shouldReturnEmptyOptional() {
        Optional<Task> optionalTask = taskManager.getTaskById(999); // Non-existent ID
        assertTrue(optionalTask.isEmpty(), "Optional should be empty for a non-existent task ID.");
    }

    /**
     * Verifies that getEpicById returns an Optional containing the correct epic
     * when an epic with the given ID exists.
     */
    @Test
    void getEpicById_whenEpicExists_shouldReturnOptionalWithEpic() {
        taskManager.addEpic(epicA);
        Epic retrievedEpicA = taskManager.getEpicById(epicA.getId()).orElseThrow(() ->
                new NoSuchElementException("EpicA not found in taskManager."));
        assertEquals(epicA, retrievedEpicA);
        assertEquals(epicA.getTitle(), retrievedEpicA.getTitle());
    }

    /**
     * Verifies that getEpicById returns an empty Optional
     * when no epic with the given ID exists.
     */
    @Test
    void getEpicById_whenEpicDoesNotExist_shouldReturnEmptyOptional() {
        Optional<Epic> optionalEpic = taskManager.getEpicById(999);
        assertTrue(optionalEpic.isEmpty());
    }

    /**
     * Verifies that getSubTaskById returns an Optional containing the correct subtask
     * when a subtask with the given ID exists.
     */
    @Test
    void getSubTaskById_whenSubTaskExists_shouldReturnOptionalWithSubTask() {
        taskManager.addEpic(epicA);
        subA.setParentId(epicA.getId());
        taskManager.addSub(subA);

        SubTask retrievedSubA = taskManager.getSubTaskById(subA.getId()).orElseThrow(() ->
                new NoSuchElementException("SubA not found in taskManager."));
        assertEquals(subA, retrievedSubA);
        assertEquals(subA.getTitle(), retrievedSubA.getTitle());
    }

    /**
     * Verifies that getSubTaskById returns an empty Optional
     * when no subtask with the given ID exists.
     */
    @Test
    void getSubTaskById_whenSubTaskDoesNotExist_shouldReturnEmptyOptional() {
        Optional<SubTask> optionalSubTask = taskManager.getSubTaskById(999);
        assertTrue(optionalSubTask.isEmpty());
    }

    /**
     * Verifies that updating an existing task changes its properties.
     */
    @Test
    void updateTask_whenExistingTaskIsUpdated_thenPropertiesAreChanged() {
        taskManager.addTask(taskA);
        Task taskWithUpdates = new Task(taskA.getId(), "Task A Updated Title", DONE,
                "Updated Description", taskA.getStartTime().plusDays(1), Duration.ofHours(5));
        taskManager.updateTask(taskWithUpdates);

        Task retrievedTaskA = taskManager.getTaskById(taskA.getId()).orElseThrow(() ->
                new NoSuchElementException("TaskA not found in taskManager."));

        assertEquals("Task A Updated Title", retrievedTaskA.getTitle(),
                "Title should be updated.");
        assertEquals(DONE, retrievedTaskA.getStatus(),
                "Status should be updated.");
        assertEquals("Updated Description", retrievedTaskA.getDescription(),
                "Description should be updated.");
        assertEquals(taskA.getStartTime().plusDays(1), retrievedTaskA.getStartTime(),
                "Start time should be updated.");
        assertEquals(Duration.ofHours(5), retrievedTaskA.getDuration(),
                "Duration should be updated.");
    }

    /**
     * Verifies that updating an epic (e.g., its title or description) persists the changes.
     * Note: Epic's status and time are derived, so this test focuses on direct fields.
     */
    @Test
    void updateEpic_whenExistingEpicIsUpdated_thenDirectPropertiesAreChanged() {
        taskManager.addEpic(epicA);
        Epic epicToUpdate = new Epic(epicA.getId(), "Epic A Updated Title", "Updated Epic Desc");

        taskManager.updateEpic(epicToUpdate);

        Epic retrievedEpicA = taskManager.getEpicById(epicA.getId()).orElseThrow(() ->
                new NoSuchElementException("EpicA not found in taskManager."));
        assertEquals("Epic A Updated Title", retrievedEpicA.getTitle());
        assertEquals("Updated Epic Desc", retrievedEpicA.getDescription());
    }

    /**
     * Verifies that updating an existing subtask changes its properties
     * and potentially updates its parent epic's status/time.
     */
    @Test
    void updateSubTask_whenExistingSubTaskIsUpdated_thenPropertiesAreChanged() {
        taskManager.addEpic(epicA);
        subA.setParentId(epicA.getId());
        taskManager.addSub(subA);
        epicA.addSubId(subA.getId());
        taskManager.updateEpic(epicA);

        SubTask subTaskToUpdate = new SubTask(subA.getId(), "Sub A Updated Title",
                IN_PROGRESS, "Updated Sub Desc",
                epicA.getId(), subA.getStartTime().plusHours(1), Duration.ofMinutes(30));

        taskManager.updateSub(subTaskToUpdate);

        SubTask retrievedSubA = taskManager.getSubTaskById(subA.getId()).orElseThrow(() ->
                new NoSuchElementException("SubA not found in taskManager."));
        assertEquals("Sub A Updated Title", retrievedSubA.getTitle());
        assertEquals(IN_PROGRESS, retrievedSubA.getStatus());

        Epic updatedEpicA = taskManager.getEpicById(epicA.getId()).orElseThrow(() ->
                new NoSuchElementException("EpicA not found in taskManager."));
        assertEquals(IN_PROGRESS, updatedEpicA.getStatus(),
                "Epic status should be IN_PROGRESS after subtask update.");

        LocalDateTime originalEpicStartTime = epicA.getStartTime();
        assertNotEquals(originalEpicStartTime, updatedEpicA.getStartTime(),
                "Epic start time should have been recalculated.");
    }

    /**
     * Verifies that removing an existing task by its ID makes it no longer retrievable
     * and removes it from the overall task list.
     */
    @Test
    void removeTaskById_whenExistingTaskIsRemoved_thenItIsNoLongerAvailable() {
        taskManager.addTask(taskA);
        int taskId = taskA.getId();
        assertFalse(taskManager.getTaskList().isEmpty(),
                "Task list should not be empty before removal.");

        taskManager.removeTaskById(taskId);

        assertTrue(taskManager.getTaskList().isEmpty(),
                "Task list should be empty after removal.");
        assertFalse(taskManager.getTaskById(taskId).isPresent(),
                "Removed task should not be retrievable by ID.");

        assertThrows(NoSuchElementException.class, () -> taskManager.removeTaskById(taskId),
                "Attempting to remove a non-existent task should throw NoSuchElementException.");
    }

    /**
     * Verifies that removing an existing subtask by its ID makes it no longer retrievable,
     * removes it from the subtask list, and updates the parent epic.
     */
    @Test
    void removeSubById_whenExistingSubTaskIsRemoved_thenItIsNoLongerAvailableAndEpicIsUpdated() {
        taskManager.addEpic(epicA);
        subA.setParentId(epicA.getId());
        taskManager.addSub(subA);

        epicA.addSubId(subA.getId());
        taskManager.updateEpic(epicA);
        taskManager.removeSubById(subA.getId());

        assertFalse(taskManager.getSubTaskById(subA.getId()).isPresent(),
                "Removed subtask should not be retrievable.");

        Epic retrievedEpicA = taskManager.getEpicById(epicA.getId()).orElseThrow(() ->
                new NoSuchElementException("EpicA not found in taskManager."));
        assertFalse(retrievedEpicA.getSubIdList().contains(subA.getId()),
                "Parent epic should no longer list the removed subtask.");
        assertEquals(NEW, retrievedEpicA.getStatus(),
                "Epic status should become NEW if all subtasks removed.");
    }

    /**
     * Verifies that removing an existing epic by its ID also removes all its subtasks.
     */
    @Test
    void removeEpicById_whenExistingEpicIsRemoved_thenItAndItsSubtasksAreNoLongerAvailable() {
        taskManager.addEpic(epicA);
        subA.setParentId(epicA.getId());
        subB.setParentId(epicA.getId());
        taskManager.addSub(subA);
        taskManager.addSub(subB);

        epicA.addSubId(subA.getId());
        epicA.addSubId(subB.getId());
        taskManager.updateEpic(epicA);

        Integer epicId = epicA.getId();
        taskManager.removeEpicById(epicId);
        assertFalse(taskManager.getEpicById(epicId).isPresent(),
                "Removed epic should not be retrievable.");

        Integer subIdA = subA.getId();
        Integer subIdB = subB.getId();
        assertFalse(taskManager.getSubTaskById(subIdA).isPresent(),
                "Subtask A of removed epic should also be removed.");
        assertFalse(taskManager.getSubTaskById(subIdB).isPresent(),
                "Subtask B of removed epic should also be removed.");
        List<SubTask> remainingSubTasks = taskManager.getSubList();
        assertFalse(remainingSubTasks.contains(subA), "getSubList should not contain subA.");
        assertFalse(remainingSubTasks.contains(subB), "getSubList should not contain subB.");
    }

    /**
     * Verifies that removeAllTask removes all standard tasks from the manager.
     */
    @Test
    void removeAllTasks_whenTasksExist_shouldLeaveTaskListEmpty() {
        taskManager.addTask(taskA);
        taskManager.addTask(taskB);
        taskManager.removeAllTask();
        assertTrue(taskManager.getTaskList().isEmpty(),
                "Task list should be empty after removeAllTasks.");
    }

    /**
     * Verifies that removeAllEpic removes all epics and their associated subtasks.
     */
    @Test
    void removeAllEpics_whenEpicsAndSubtasksExist_shouldLeaveEpicAndSubTaskListEmpty() {
        taskManager.addEpic(epicA);
        subA.setParentId(epicA.getId());
        taskManager.addSub(subA);
        taskManager.addEpic(epicB);

        epicA.addSubId(subA.getId());
        taskManager.updateEpic(epicA);

        taskManager.removeAllEpic();

        assertTrue(taskManager.getEpicList().isEmpty(),
                "Epic list should be empty.");
        assertTrue(taskManager.getSubList().isEmpty(),
                "Subtask list should also be empty as their epics are removed.");
    }

    /**
     * Verifies that removeAllSub removes all subtasks,
     * and that parent epics are updated accordingly (e.g., status becomes NEW).
     */
    @Test
    void removeAllSubTasks_whenSubTasksExist_shouldLeaveSubTaskListEmptyAndEpicsUpdated() {
        taskManager.addEpic(epicA);
        subA.setParentId(epicA.getId());
        subB.setParentId(epicA.getId());
        taskManager.addSub(subA);
        taskManager.addSub(subB);
        epicA.addSubId(subA.getId());
        epicA.addSubId(subB.getId());
        taskManager.updateEpic(epicA);

        taskManager.removeAllSub();

        assertTrue(taskManager.getSubList().isEmpty(), "Subtask list should be empty.");
        Epic retrievedEpicA = taskManager.getEpicById(epicA.getId()).orElseThrow(() ->
                new NoSuchElementException("EpicA not found in taskManager."));
        assertEquals(NEW, retrievedEpicA.getStatus(),
                "Parent epic's status should be updated to NEW "
                        + "after all its subtasks are removed.");
        assertTrue(retrievedEpicA.getSubIdList().isEmpty(),
                "Parent epic's subIdList should be empty.");
    }
}