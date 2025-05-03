package kanban.managers;

import static kanban.tasks.TaskStatus.DONE;
import static kanban.tasks.TaskStatus.IN_PROGRESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import kanban.tasks.Epic;
import kanban.tasks.SubTask;
import kanban.tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class FileBackedTaskManagerTest {
    private FileBackedTaskManager taskManager;
    private FileBackedTaskManager taskManagerRestored;
    private File tempFile;

    @BeforeEach
    void beforeEachTest() {
        Task taskA = new Task("Task A", "Description A");
        taskA.setId(1);

        Task taskB = new Task("Task B", "Description B");
        taskB.setId(2);

        Task taskC = new Task("Task C", "Description C");
        taskC.setId(3);

        Task taskD = new Task("Task D", "Description D");
        taskD.setId(4);

        SubTask taskE = new SubTask("Subtask E", "Description E");
        taskE.setId(5);
        taskE.setParentId(12);

        try {
            tempFile = File.createTempFile("_tempCSV_", ".csv");
        } catch (IOException e) {
            throw new AssertionError("Can't create temporary file.", e);
        }
    }

    @Test
    void givenNewTaskManager_whenInitialized_thenTasksStructureEmpty() {
        taskManager = Managers.getFileBackedManager(tempFile);
        assertNotNull(taskManager);
        assertTrue(taskManager.getHistoryTask().isEmpty());
        assertTrue(taskManager.getTaskList().isEmpty());
        assertTrue(taskManager.getEpicList().isEmpty());
        assertTrue(taskManager.getSubList().isEmpty());
    }

    @Test
    void givenTaskManager_whenEmptyTaskSaved_thenEmptyTaskLoaded() {
        taskManager = Managers.getFileBackedManager(tempFile);
        assertTrue(taskManager.getHistoryTask().isEmpty());
        assertTrue(taskManager.getTaskList().isEmpty());
        assertTrue(taskManager.getEpicList().isEmpty());
        assertTrue(taskManager.getSubList().isEmpty());

        // Create different task type
        Task taskA = new Task("Task A", "Description A");

        // Adding task into manager
        taskManager.addTask(taskA);

        // Checking manager storage maps
        assertTrue(taskManager.getHistoryTask().isEmpty());
        assertFalse(taskManager.getTaskList().isEmpty());
        assertEquals(1, taskManager.getTaskList().size());

        // Delete all task
        taskManager.removeAllTask();

        // Checking manager storage maps
        assertTrue(taskManager.getHistoryTask().isEmpty());
        assertTrue(taskManager.getTaskList().isEmpty());

        // Load taskManager in new FileBackedTaskManager object
        taskManagerRestored = FileBackedTaskManager.loadFromFile(tempFile);
        tempFile.deleteOnExit();

        String restoredString = composeTaskString(taskManagerRestored);
        String originalString = composeTaskString(taskManager);
        assertEquals(originalString, restoredString);
    }

    @Test
    void givenTaskManager_whenTaskSaved_thenSameTaskLoadedInNewTaskManager() {
        taskManager = Managers.getFileBackedManager(tempFile);
        assertTrue(taskManager.getHistoryTask().isEmpty());
        assertTrue(taskManager.getTaskList().isEmpty());
        assertTrue(taskManager.getEpicList().isEmpty());
        assertTrue(taskManager.getSubList().isEmpty());

        // Create different task type
        Task taskA = new Task("Task A", "Description A");
        Task taskB = new Task("Task B", "Description B");
        Epic epicA = new Epic("Epic A", "Description C");
        Epic epicB = new Epic("Epic B", "Description D");
        SubTask subA = new SubTask("Subtask A", "Description E");
        SubTask subB = new SubTask("Subtask B", "Description F");
        SubTask subC = new SubTask("Subtask C", "Description G");

        // Adding task into manager
        taskManager.addTask(taskA);
        taskManager.addTask(taskB);
        taskManager.addEpic(epicA);
        taskManager.addEpic(epicB);
        taskManager.addSub(subA);
        taskManager.addSub(subB);
        taskManager.addSub(subC);

        // Checking manager storage maps
        assertTrue(taskManager.getHistoryTask().isEmpty());
        assertFalse(taskManager.getTaskList().isEmpty());
        assertEquals(2, taskManager.getTaskList().size());
        assertFalse(taskManager.getEpicList().isEmpty());
        assertEquals(2, taskManager.getEpicList().size());
        assertFalse(taskManager.getSubList().isEmpty());
        assertEquals(3, taskManager.getSubList().size());

        // Linking epic task and subtask then updating task
        epicA = taskManager.getEpicById(epicA.getId());
        epicB = taskManager.getEpicById(epicB.getId());
        subA = taskManager.getSubTaskById(subA.getId());
        subB = taskManager.getSubTaskById(subB.getId());
        subC = taskManager.getSubTaskById(subC.getId());
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

        // Checking epic<->subtask link
        assertTrue(taskManager.getEpicById(epicA.getId()).getSubIdList().contains(subA.getId()));
        assertTrue(taskManager.getEpicById(epicA.getId()).getSubIdList().contains(subB.getId()));
        assertTrue(taskManager.getEpicById(epicB.getId()).getSubIdList().contains(subC.getId()));
        assertEquals(taskManager.getSubTaskById(subA.getId()).getParentId(), epicA.getId());
        assertEquals(taskManager.getSubTaskById(subB.getId()).getParentId(), epicA.getId());
        assertEquals(taskManager.getSubTaskById(subC.getId()).getParentId(), epicB.getId());

        // Change task status then update task
        taskA = taskManager.getTaskById(taskA.getId());
        subA = taskManager.getSubTaskById(subA.getId());
        subC = taskManager.getSubTaskById(subC.getId());
        taskA.setStatus(IN_PROGRESS);
        subA.setStatus(IN_PROGRESS);
        subC.setStatus(DONE);
        taskManager.updateTask(taskA);
        taskManager.updateSub(subA);
        taskManager.updateSub(subC);

        // Load taskManager in new FileBackedTaskManager object
        taskManagerRestored = FileBackedTaskManager.loadFromFile(tempFile);
        tempFile.deleteOnExit();

        String originalString = composeTaskString(taskManager);
        String restoredString = composeTaskString(taskManagerRestored);
        assertEquals(originalString, restoredString);
    }

    private String composeTaskString(TaskManager manager) {
        StringBuilder string = new StringBuilder();

        for (Task task : manager.getTaskList()) {
            string.append(task.toString());
        }
        for (Epic epic : manager.getEpicList()) {
            string.append(epic.toString());
            if (!epic.getSubIdList().isEmpty()) {
                for (Integer subTaskId : epic.getSubIdList()) {
                    string.append(manager.getSubTaskById(subTaskId).toString());
                }
            }
        }
        return string.toString();
    }
}
