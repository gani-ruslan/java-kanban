package kanban.managers;

import static kanban.tasks.TaskStatus.DONE;
import static kanban.tasks.TaskStatus.IN_PROGRESS;
import static kanban.tasks.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import kanban.tasks.Epic;
import kanban.tasks.SubTask;
import kanban.tasks.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link FileBackedTaskManager}.
 * Verifies that task persistence and restoration from file works correctly.
 */
class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    protected FileBackedTaskManager taskManager;
    protected FileBackedTaskManager taskManagerRestored;
    protected static File tempFile;
    Task taskD;

    /**
     * Creates a new instance of FileBackedTaskManager for testing.
     *
     * @return a new FileBackedTaskManager instance
     */
    @Override
    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(tempFile);
    }

    /**
     * Initializes test environment by creating a temporary CSV file and sample tasks.
     */
    @BeforeEach
    void setUp() {
        try {
            tempFile = File.createTempFile("_tempCSV_", ".csv");
        } catch (IOException e) {
            throw new AssertionError("Can't create temporary file.", e);
        }
        taskManager = createTaskManager();
        super.beforeEachTest();

        LocalDateTime testStartTime = LocalDateTime.of(2025, 1, 1, 10, 0, 0);

        taskD = new Task(0, "Task D", NEW, "Task D description",
                testStartTime.plusMinutes(30), Duration.ofMinutes(10));

        subA = new SubTask(0, "Sub A", NEW, "SubTask A description", 0,
                testStartTime.plusMinutes(40), Duration.ofMinutes(10));
        subB = new SubTask(0, "Sub B", NEW, "SubTask B description", 0,
                testStartTime.plusMinutes(50), Duration.ofMinutes(10));
        subC = new SubTask(0, "Sub C", NEW, "SubTask C description", 0,
                testStartTime.plusMinutes(60), Duration.ofMinutes(10));
    }

    /**
     * Ensures the temporary file is removed after each test.
     */
    @AfterEach
    void tearDown() {
        tempFile.deleteOnExit();
    }

    /**
     * Verifies that a newly created FileBackedTaskManager is initially empty.
     */
    @Test
    void shouldInitializeEmptyManager() {
        taskManager = Managers.getFileBackedManager(tempFile);
        assertNotNull(taskManager);
        assertTrue(taskManager.getHistoryTask().isEmpty());
        assertTrue(taskManager.getTaskList().isEmpty());
        assertTrue(taskManager.getEpicList().isEmpty());
        assertTrue(taskManager.getSubList().isEmpty());
    }

    /**
     * Verifies that saving and loading an empty task taskManager works as expected.
     */
    @Test
    void shouldSaveAndLoadEmptyTaskManagerCorrectly() {
        taskManager = Managers.getFileBackedManager(tempFile);
        assertTrue(taskManager.getTaskList().isEmpty());

        taskManager.addTask(taskA);
        assertEquals(1, taskManager.getTaskList().size());

        taskManager.removeAllTask();
        assertTrue(taskManager.getTaskList().isEmpty());

        taskManagerRestored = FileBackedTaskManager.loadFromFile(tempFile);
        String restored = composeTaskString(taskManagerRestored);
        String original = composeTaskString(taskManager);
        assertEquals(original, restored);
    }

    /**
     * Verifies that tasks and their states are correctly persisted and restored from file.
     */
    @Test
    void shouldPersistAndRestoreTasksCorrectly() {
        taskManager = Managers.getFileBackedManager(tempFile);

        taskManager.addTask(taskA);
        taskManager.addTask(taskB);
        taskManager.addEpic(epicA);
        taskManager.addEpic(epicB);
        taskManager.addSub(subA);
        taskManager.addSub(subB);
        taskManager.addSub(subC);

        // Link epics and subtasks
        epicA = taskManager.getEpicById(epicA.getId()).orElseThrow();
        epicB = taskManager.getEpicById(epicB.getId()).orElseThrow();
        subA = taskManager.getSubTaskById(subA.getId()).orElseThrow();
        subB = taskManager.getSubTaskById(subB.getId()).orElseThrow();
        subC = taskManager.getSubTaskById(subC.getId()).orElseThrow();

        epicA.addSubId(subA.getId());
        epicA.addSubId(subB.getId());
        epicB.addSubId(subC.getId());

        subA.setParentId(epicA.getId());
        subB.setParentId(epicA.getId());
        subC.setParentId(epicB.getId());

        taskManager.updateSub(subA);
        taskManager.updateSub(subB);
        taskManager.updateSub(subC);
        taskManager.updateEpic(epicA);
        taskManager.updateEpic(epicB);

        // Modify task statuses
        taskA = taskManager.getTaskById(taskA.getId()).orElseThrow();
        taskA.setStatus(IN_PROGRESS);
        subA.setStatus(IN_PROGRESS);
        subC.setStatus(DONE);

        taskManager.updateTask(taskA);
        taskManager.updateSub(subA);
        taskManager.updateSub(subC);

        taskManagerRestored = FileBackedTaskManager.loadFromFile(tempFile);
        String original = composeTaskString(taskManager);
        String restored = composeTaskString(taskManagerRestored);
        assertEquals(original, restored);
    }

    /**
     * Converts all tasks, epics, and subtasks from a task taskManager to a string.
     *
     * @param manager the task taskManager
     * @return string representation of all tasks
     */
    private String composeTaskString(TaskManager manager) {
        StringBuilder string = new StringBuilder();
        for (Task task : manager.getTaskList()) {
            string.append(task.toString());
        }
        for (Epic epic : manager.getEpicList()) {
            string.append(epic.toString());
            for (Integer subTaskId : epic.getSubIdList()) {
                SubTask subTask = manager.getSubTaskById(subTaskId).orElseThrow(() ->
                        new NoSuchElementException("SubTask with id: "
                                + subTaskId + " not found in taskManager."));
                string.append(subTask.toString());
            }
        }
        return string.toString();
    }
}
