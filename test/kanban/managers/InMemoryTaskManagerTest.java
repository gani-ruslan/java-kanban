package kanban.managers;

import static kanban.tasks.TaskStatus.DONE;
import static kanban.tasks.TaskStatus.IN_PROGRESS;
import static kanban.tasks.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import kanban.tasks.Epic;
import kanban.tasks.SubTask;
import kanban.tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link InMemoryTaskManager}, including status propagation,
 * overlapping time detection, and task prioritization.
 */
class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    protected InMemoryTaskManager taskManager;
    SubTask subD;
    LocalDateTime testStartTime;

    /**
     * Creates a new instance of InMemoryTaskManager.
     */
    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    /**
     * Initializes test environment before each test.
     */
    @BeforeEach
    void setUp() {
        super.beforeEachTest();
        taskManager = createTaskManager();
        testStartTime = LocalDateTime.of(2025, 1, 1, 10, 0, 0);
        subD = new SubTask(0, "Sub D", NEW, "SubTask D description", 0,
                testStartTime.plusMinutes(60), Duration.ofMinutes(10));
    }

    /**
     * Verifies epic status changes to NEW when all subtasks are NEW.
     */
    @Test
    void shouldSetEpicStatusToNewWhenAllSubtasksAreNew() {
        taskManager.addEpic(epicA);
        subA.setParentId(epicA.getId());
        subB.setParentId(epicA.getId());
        taskManager.addSub(subA);
        taskManager.addSub(subB);
        Epic updatedEpic = taskManager.getEpicById(epicA.getId()).orElseThrow();
        updatedEpic.addSubId(subA.getId());
        updatedEpic.addSubId(subB.getId());
        taskManager.updateEpic(updatedEpic);
        epicA = taskManager.getEpicById(epicA.getId()).orElseThrow();
        assertEquals(NEW, epicA.getStatus());
    }

    /**
     * Verifies epic status is IN_PROGRESS if any subtask is IN_PROGRESS.
     */
    @Test
    void shouldSetEpicStatusToInProgressIfAnySubtaskIsInProgress() {
        taskManager.addEpic(epicA);
        subA.setParentId(epicA.getId());
        subB.setParentId(epicA.getId());
        subC.setParentId(epicA.getId());
        subA.setStatus(NEW);
        subB.setStatus(IN_PROGRESS);
        subC.setStatus(NEW);
        taskManager.addSub(subA);
        taskManager.addSub(subB);
        taskManager.addSub(subC);
        Epic updatedEpic = taskManager.getEpicById(epicA.getId()).orElseThrow();
        updatedEpic.addSubId(subA.getId());
        updatedEpic.addSubId(subB.getId());
        updatedEpic.addSubId(subC.getId());
        taskManager.updateEpic(updatedEpic);
        Epic epic = taskManager.getEpicById(epicA.getId()).orElseThrow();
        assertEquals(IN_PROGRESS, epic.getStatus());
    }

    /**
     * Verifies epic status becomes DONE if all subtasks are DONE.
     */
    @Test
    void shouldSetEpicStatusToDoneWhenAllSubtasksAreDone() {
        taskManager.addEpic(epicA);
        subA.setParentId(epicA.getId());
        subB.setParentId(epicA.getId());
        subC.setParentId(epicA.getId());
        subA.setStatus(DONE);
        subB.setStatus(DONE);
        subC.setStatus(DONE);
        taskManager.addSub(subA);
        taskManager.addSub(subB);
        taskManager.addSub(subC);
        Epic updatedEpic = taskManager.getEpicById(epicA.getId()).orElseThrow();
        updatedEpic.addSubId(subA.getId());
        updatedEpic.addSubId(subB.getId());
        updatedEpic.addSubId(subC.getId());
        taskManager.updateEpic(updatedEpic);
        Epic epic = taskManager.getEpicById(epicA.getId()).orElseThrow();
        assertEquals(DONE, epic.getStatus());
    }

    /**
     * Verifies epic status is IN_PROGRESS if subtask statuses are mixed.
     */
    @Test
    void shouldSetEpicStatusToInProgressWhenSubtasksMixedStatuses() {
        taskManager.addEpic(epicA);
        subA.setParentId(epicA.getId());
        subB.setParentId(epicA.getId());
        subC.setParentId(epicA.getId());
        subA.setStatus(DONE);
        subB.setStatus(DONE);
        subC.setStatus(NEW);
        taskManager.addSub(subA);
        taskManager.addSub(subB);
        taskManager.addSub(subC);
        Epic updatedEpic = taskManager.getEpicById(epicA.getId()).orElseThrow();
        updatedEpic.addSubId(subA.getId());
        updatedEpic.addSubId(subB.getId());
        updatedEpic.addSubId(subC.getId());
        taskManager.updateEpic(updatedEpic);
        Epic epic = taskManager.getEpicById(epicA.getId()).orElseThrow();
        assertEquals(IN_PROGRESS, epic.getStatus());
    }

    /**
     * Verifies that removing a subtask resets the epic's status to NEW and clears the subtask list.
     */
    @Test
    void shouldResetEpicStatusAndSubtaskListWhenSubtaskRemoved() {
        taskManager.addEpic(epicA);
        subA.setParentId(epicA.getId());
        subA.setStatus(DONE);
        taskManager.addSub(subA);

        Epic updateEpicA = taskManager.getEpicById(epicA.getId()).orElseThrow();
        updateEpicA.addSubId(subA.getId());
        taskManager.updateEpic(updateEpicA);

        epicA = taskManager.getEpicById(epicA.getId()).orElseThrow();
        assertEquals(DONE, epicA.getStatus());

        List<SubTask> epicSubTaskList = taskManager.getEpicSubTaskList(epicA.getId()).orElseThrow();
        assertFalse(epicSubTaskList.isEmpty());

        taskManager.removeSubById(subA.getId());

        epicA = taskManager.getEpicById(epicA.getId()).orElseThrow();
        epicSubTaskList = taskManager.getEpicSubTaskList(epicA.getId()).orElseThrow();
        assertTrue(epicSubTaskList.isEmpty());
        assertEquals(NEW, epicA.getStatus());
    }

    /**
     * Verifies that adding a task which starts before the previous task ends results
     * in a time overlap exception.
     */
    @Test
    void shouldThrowWhenTaskStartsBeforeOtherEnds() {
        taskA.setStartTime(testStartTime);
        taskA.setDuration(Duration.ofMinutes(10));
        taskB.setStartTime(testStartTime.plusMinutes(9));
        taskB.setDuration(Duration.ofMinutes(10));
        assertDoesNotThrow(() -> taskManager.addTask(taskA));
        assertThrows(TaskTimeOverlapException.class, () -> taskManager.addTask(taskB));
    }

    /**
     * Verifies that updating a task to extend into another task's timeframe
     * causes an overlap exception.
     */
    @Test
    void shouldThrowWhenTaskUpdateExtendsIntoAnother() {
        taskA.setStartTime(testStartTime);
        taskA.setDuration(Duration.ofMinutes(10));
        taskB.setStartTime(testStartTime.plusMinutes(10));
        taskB.setDuration(Duration.ofMinutes(10));
        assertDoesNotThrow(() -> taskManager.addTask(taskA));
        assertDoesNotThrow(() -> taskManager.addTask(taskB));
        taskA.setDuration(Duration.ofMinutes(15));
        assertThrows(TaskTimeOverlapException.class, () -> taskManager.updateTask(taskA));
    }

    /**
     * Verifies that adding a task fully inside another task's timeframe
     * causes an overlap exception.
     */
    @Test
    void shouldThrowWhenSecondTaskFullyInsideFirst() {
        taskA.setStartTime(testStartTime);
        taskA.setDuration(Duration.ofMinutes(20));
        taskB.setStartTime(testStartTime.plusMinutes(5));
        taskB.setDuration(Duration.ofMinutes(10));
        assertDoesNotThrow(() -> taskManager.addTask(taskA));
        assertThrows(TaskTimeOverlapException.class, () -> taskManager.addTask(taskB));
    }

    /**
     * Verifies that adding overlapping subtasks causes a time overlap exception.
     */
    @Test
    void shouldThrowWhenSubtasksOverlapInTime() {
        subA.setStartTime(testStartTime);
        subA.setDuration(Duration.ofMinutes(10));
        subB.setStartTime(testStartTime.plusMinutes(9));
        subB.setDuration(Duration.ofMinutes(10));
        assertDoesNotThrow(() -> taskManager.addSub(subA));
        assertThrows(TaskTimeOverlapException.class, () -> taskManager.addSub(subB));
    }

    /**
     * Verifies that a subtask and a task overlapping in time results in an exception.
     */
    @Test
    void shouldThrowWhenTaskOverlapsWithSubtask() {
        subA.setStartTime(testStartTime);
        subA.setDuration(Duration.ofMinutes(10));
        taskA.setStartTime(testStartTime.plusMinutes(9));
        taskA.setDuration(Duration.ofMinutes(10));
        assertDoesNotThrow(() -> taskManager.addSub(subA));
        assertThrows(TaskTimeOverlapException.class, () -> taskManager.addTask(taskA));
    }

    /**
     * Verifies that getPrioritizedTasks returns only normal tasks sorted by start time.
     */
    @Test
    void shouldReturnCorrectPrioritizedTaskList() {
        taskA.setStartTime(testStartTime);
        taskA.setDuration(Duration.ofMinutes(10));
        taskB.setStartTime(testStartTime.plusMinutes(10));
        taskB.setDuration(Duration.ofMinutes(10));
        taskC.setStartTime(LocalDateTime.MIN);
        taskC.setDuration(Duration.ofMinutes(10));
        subA.setStartTime(testStartTime.plusMinutes(20));
        subA.setDuration(Duration.ZERO);
        subB.setStartTime(LocalDateTime.MIN);
        subB.setDuration(Duration.ZERO);

        taskManager.addTask(taskA);
        taskManager.addTask(taskB);
        taskManager.addTask(taskC);
        taskManager.addSub(subA);
        taskManager.addSub(subB);

        assertEquals(taskManager.getPrioritizedTasks(), List.of(taskA, taskB));
    }

    /**
     * Full scenario test verifying task, epic, subtask creation,
     * status updates, and history tracking.
     */
    @Test
    void shouldBehaveCorrectlyUnderScenarioA() {
        assertTrue(taskManager.getHistoryTask().isEmpty());
        assertTrue(taskManager.getTaskList().isEmpty());
        assertTrue(taskManager.getEpicList().isEmpty());
        assertTrue(taskManager.getSubList().isEmpty());

        taskManager.addTask(taskA);
        taskManager.addTask(taskB);
        taskManager.addEpic(epicA);
        taskManager.addEpic(epicB);
        subA.setParentId(epicA.getId());
        subB.setParentId(epicA.getId());
        subC.setParentId(epicB.getId());
        taskManager.addSub(subA);
        taskManager.addSub(subB);
        taskManager.addSub(subC);

        subA = taskManager.getSubTaskById(subA.getId()).orElseThrow();
        subB = taskManager.getSubTaskById(subB.getId()).orElseThrow();
        subC = taskManager.getSubTaskById(subC.getId()).orElseThrow();
        epicA = taskManager.getEpicById(epicA.getId()).orElseThrow();
        epicB = taskManager.getEpicById(epicB.getId()).orElseThrow();

        epicA.addSubId(subA.getId());
        epicA.addSubId(subB.getId());
        epicB.addSubId(subC.getId());
        taskManager.updateEpic(epicA);
        taskManager.updateEpic(epicB);

        assertFalse(taskManager.getHistoryTask().isEmpty());
        assertFalse(taskManager.getTaskList().isEmpty());
        assertEquals(2, taskManager.getTaskList().size());
        assertFalse(taskManager.getEpicList().isEmpty());
        assertEquals(2, taskManager.getEpicList().size());
        assertFalse(taskManager.getSubList().isEmpty());
        assertEquals(3, taskManager.getSubList().size());

        epicA = taskManager.getEpicById(epicA.getId()).orElseThrow();
        epicB = taskManager.getEpicById(epicB.getId()).orElseThrow();

        assertTrue(epicA.getSubIdList().contains(subA.getId()));
        assertTrue(epicA.getSubIdList().contains(subB.getId()));
        assertTrue(epicB.getSubIdList().contains(subC.getId()));

        subA = taskManager.getSubTaskById(subA.getId()).orElseThrow();
        subB = taskManager.getSubTaskById(subB.getId()).orElseThrow();
        subC = taskManager.getSubTaskById(subC.getId()).orElseThrow();

        assertEquals(subA.getParentId(), epicA.getId());
        assertEquals(subB.getParentId(), epicA.getId());
        assertEquals(subC.getParentId(), epicB.getId());

        taskA.setStatus(IN_PROGRESS);
        subA.setStatus(IN_PROGRESS);
        subC.setStatus(DONE);
        taskManager.updateTask(taskA);
        taskManager.updateSub(subA);
        taskManager.updateSub(subC);

        taskA = taskManager.getTaskById(taskA.getId()).orElseThrow();
        epicA = taskManager.getEpicById(epicA.getId()).orElseThrow();
        epicB = taskManager.getEpicById(epicB.getId()).orElseThrow();
        subA = taskManager.getSubTaskById(subA.getId()).orElseThrow();
        subC = taskManager.getSubTaskById(subC.getId()).orElseThrow();

        assertEquals(IN_PROGRESS, taskA.getStatus());
        assertEquals(IN_PROGRESS, epicA.getStatus());
        assertEquals(IN_PROGRESS, subA.getStatus());
        assertEquals(DONE, epicB.getStatus());
        assertEquals(DONE, subC.getStatus());

        taskManager.removeTaskById(taskB.getId());
        taskManager.removeSubById(subA.getId());
        taskManager.removeEpicById(epicB.getId());

        assertFalse(taskManager.getTaskById(taskB.getId()).isPresent());
        assertFalse(taskManager.getSubTaskById(epicB.getId()).isPresent());
        assertFalse(taskManager.getSubTaskById(subC.getId()).isPresent());
        assertFalse(taskManager.getEpicById(subA.getId()).isPresent());

        assertTrue(taskManager.getEpicById(epicA.getId()).isPresent());
        assertEquals(NEW, taskManager.getEpicById(epicA.getId()).get().getStatus());

        assertEquals(3, taskManager.getHistoryTask().size());
        List<Task> finalHistory = taskManager.getHistoryTask();
        assertTrue(finalHistory.contains(taskA));
        assertTrue(finalHistory.contains(subB));
        assertTrue(finalHistory.contains(epicA));
    }
}
