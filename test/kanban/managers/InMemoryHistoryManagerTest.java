package kanban.managers;

import static kanban.tasks.TaskStatus.DONE;
import static kanban.tasks.TaskStatus.IN_PROGRESS;
import static kanban.tasks.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import kanban.tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link InMemoryHistoryManager} to validate task history tracking.
 */
class InMemoryHistoryManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    protected InMemoryTaskManager taskManager;
    protected HistoryManager historyManager;
    private Task taskD;
    private Task taskE;

    /**
     * Creates a new in-memory task taskManager instance.
     */
    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    /**
     * Initializes the test environment before each test run.
     */
    @BeforeEach
    void setUp() {
        super.beforeEachTest();
        taskManager = createTaskManager();
        historyManager = Managers.getDefaultHistory();

        LocalDateTime testStartTime = LocalDateTime.of(2025, 1, 1, 10, 0, 0);

        taskB.setId(2);
        taskC.setId(3);

        taskD = new Task(4, "Task D", NEW, "Task D description",
                testStartTime.plusMinutes(60), Duration.ofMinutes(10));
        taskE = new Task(5, "Task E", NEW, "Task E description",
                testStartTime.plusMinutes(70), Duration.ofMinutes(10));
    }

    /**
     * Should preserve previous task state in history after multiple updates.
     */
    @Test
    void shouldTrackPreviousStateWhenTaskIsUpdated() {
        taskManager.addTask(taskE);
        taskE = taskManager.getTaskById(taskE.getId()).orElseThrow();
        taskE.setStatus(IN_PROGRESS);
        taskManager.updateTask(taskE);

        taskE = taskManager.getTaskById(taskE.getId()).orElseThrow();
        taskE.setStatus(DONE);
        taskManager.updateTask(taskE);

        ArrayList<Task> history = taskManager.getHistoryTask();
        assertEquals(IN_PROGRESS, history.getFirst().getStatus());
    }

    /**
     * Should return empty history on new history taskManager.
     */
    @Test
    void shouldBeEmptyWhenInitialized() {
        assertTrue(historyManager.getTasks().isEmpty());
    }

    /**
     * Should add tasks to history and maintain correct size.
     */
    @Test
    void shouldAddTasksToHistoryCorrectly() {
        historyManager.add(taskA);
        historyManager.add(taskB);
        historyManager.add(taskC);
        assertFalse(historyManager.getTasks().isEmpty());
        assertEquals(3, historyManager.getTasks().size());
    }

    /**
     * Should not modify stored task if original task changes after being added to history.
     */
    @Test
    void shouldKeepHistoryTaskImmutableAfterOriginalChanges() {
        historyManager.add(taskA);
        taskA.setDescription("Description B modified");
        assertNotEquals(historyManager.getTasks().getFirst().getDescription(),
                taskA.getDescription());
    }

    /**
     * Should reorder task to end of history when re-added.
     */
    @Test
    void shouldMoveTaskToEndWhenReadded() {
        historyManager.add(taskA);
        historyManager.add(taskB);
        historyManager.add(taskC);
        historyManager.add(taskA);
        List<Task> expectedOrder = List.of(taskB, taskC, taskA);
        assertEquals(expectedOrder, historyManager.getTasks());
    }

    /**
     * Should remove duplicates and maintain correct order when re-adding tasks.
     */
    @Test
    void shouldNotContainDuplicatesWhenReaddingTasks() {
        historyManager.add(taskA);
        historyManager.add(taskB);
        historyManager.add(taskC);
        historyManager.add(taskC);
        historyManager.add(taskB);
        historyManager.add(taskA);
        assertFalse(hasDuplicates(historyManager.getTasks()));
        List<Task> expectedOrder = List.of(taskC, taskB, taskA);
        assertEquals(expectedOrder, historyManager.getTasks());
    }

    /**
     * Should correctly maintain order and uniqueness after removing items from history.
     */
    @Test
    void shouldMaintainCorrectOrderAfterRemovals() {
        historyManager.add(taskA);
        historyManager.add(taskB);
        historyManager.add(taskC);
        historyManager.remove(taskA.getId());
        historyManager.add(taskD);
        historyManager.remove(taskC.getId());
        assertFalse(hasDuplicates(historyManager.getTasks()));
        List<Task> expectedOrder = List.of(taskB, taskD);
        assertEquals(expectedOrder, historyManager.getTasks());
    }

    /**
     * Custom scenario from Sprint-04: add and remove tasks and verify final history.
     */
    @Test
    void shouldHandleSprint04ScenarioCorrectly() {
        assertTrue(taskManager.getHistoryTask().isEmpty());
        assertTrue(taskManager.getTaskList().isEmpty());
        assertTrue(taskManager.getEpicList().isEmpty());
        assertTrue(taskManager.getSubList().isEmpty());

        taskManager.addTask(taskA);
        taskManager.addTask(taskB);
        taskManager.addEpic(epicA);
        taskManager.addEpic(epicB);
        taskManager.addSub(subA);
        taskManager.addSub(subB);
        taskManager.addSub(subC);

        assertEquals(2, taskManager.getTaskList().size());
        assertEquals(2, taskManager.getEpicList().size());
        assertEquals(3, taskManager.getSubList().size());

        epicA = taskManager.getEpicById(epicA.getId()).orElseThrow();
        subA = taskManager.getSubTaskById(subA.getId()).orElseThrow();
        subB = taskManager.getSubTaskById(subB.getId()).orElseThrow();
        subC = taskManager.getSubTaskById(subC.getId()).orElseThrow();

        epicA.addSubId(subA.getId());
        epicA.addSubId(subB.getId());
        epicA.addSubId(subC.getId());
        subA.setParentId(epicA.getId());
        subB.setParentId(epicA.getId());
        subC.setParentId(epicA.getId());

        taskManager.updateSub(subA);
        taskManager.updateSub(subB);
        taskManager.updateSub(subC);
        taskManager.updateEpic(epicA);

        assertTrue(epicA.getSubIdList().containsAll(
                List.of(subA.getId(), subB.getId(), subC.getId())
        ));
        assertEquals(epicA.getId(), subA.getParentId());
        assertEquals(epicA.getId(), subB.getParentId());
        assertEquals(epicA.getId(), subC.getParentId());

        taskManager.getEpicById(epicA.getId());
        taskManager.getTaskById(taskA.getId());
        taskManager.getSubTaskById(subA.getId());
        taskManager.getEpicById(epicB.getId());
        taskManager.getTaskById(taskB.getId());
        taskManager.getSubTaskById(subB.getId());
        taskManager.getTaskById(taskA.getId());
        taskManager.getEpicById(epicA.getId());
        taskManager.getSubTaskById(subB.getId());
        taskManager.getSubTaskById(subB.getId());
        taskManager.getTaskById(taskB.getId());
        taskManager.getSubTaskById(subC.getId());

        assertFalse(hasDuplicates(taskManager.getHistoryTask()));

        taskManager.removeTaskById(taskA.getId());
        assertFalse(taskManager.getHistoryTask().contains(taskA));

        taskManager.removeEpicById(epicA.getId());
        assertFalse(taskManager.getHistoryTask().contains(epicA));
        assertFalse(taskManager.getHistoryTask().contains(subA));
        assertFalse(taskManager.getHistoryTask().contains(subB));
        assertFalse(taskManager.getHistoryTask().contains(subC));
    }

    /**
     * Utility method to check for duplicates in a list.
     *
     * @param list list to be checked
     * @param <T>  type of items in the list
     * @return true if duplicates exist, false otherwise
     */
    private <T> boolean hasDuplicates(List<T> list) {
        Set<T> set = new HashSet<>();
        for (T item : list) {
            if (!set.add(item)) {
                return true;
            }
        }
        return false;
    }
}
