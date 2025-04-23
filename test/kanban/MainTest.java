package kanban;

import static kanban.tasks.TaskStatus.DONE;
import static kanban.tasks.TaskStatus.IN_PROGRESS;
import static kanban.tasks.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import kanban.managers.Managers;
import kanban.managers.TaskManager;
import kanban.tasks.Epic;
import kanban.tasks.SubTask;
import kanban.tasks.Task;
import org.junit.jupiter.api.Test;

public class MainTest {

    @Test
    void scenarioA() {
        // Manager initialization
        TaskManager manager = Managers.getDefault();

        // Check manager initialization
        assertTrue(manager.getHistoryTask().isEmpty());
        assertTrue(manager.getTaskList().isEmpty());
        assertTrue(manager.getEpicList().isEmpty());
        assertTrue(manager.getSubList().isEmpty());

        // Create different task type
        Task taskA = new Task("Task A", "Description A");
        Task taskB = new Task("Task B", "Description B");
        Epic epicA = new Epic("Epic A", "Description C");
        Epic epicB = new Epic("Epic B", "Description D");
        SubTask subA = new SubTask("Subtask A", "Description E");
        SubTask subB = new SubTask("Subtask B", "Description F");
        SubTask subC = new SubTask("Subtask C", "Description G");

        // Adding task into manager
        manager.addTask(taskA);
        manager.addTask(taskB);
        manager.addEpic(epicA);
        manager.addEpic(epicB);
        manager.addSub(subA);
        manager.addSub(subB);
        manager.addSub(subC);

        // Checking manager storage maps
        assertTrue(manager.getHistoryTask().isEmpty());
        assertFalse(manager.getTaskList().isEmpty());
        assertEquals(2, manager.getTaskList().size());
        assertFalse(manager.getEpicList().isEmpty());
        assertEquals(2, manager.getEpicList().size());
        assertFalse(manager.getSubList().isEmpty());
        assertEquals(3, manager.getSubList().size());

        // Linking epic task and subtask then updating task
        epicA = manager.getEpicById(epicA.getId());
        epicB = manager.getEpicById(epicB.getId());
        subA = manager.getSubTaskById(subA.getId());
        subB = manager.getSubTaskById(subB.getId());
        subC = manager.getSubTaskById(subC.getId());
        epicA.addSubId(subA.getId());
        epicA.addSubId(subB.getId());
        epicB.addSubId(subC.getId());
        subA.setParentId(epicA.getId());
        subB.setParentId(epicA.getId());
        subC.setParentId(epicB.getId());
        manager.updateSub(subA);
        manager.updateSub(subB);
        manager.updateSub(subC);
        manager.updateEpic(epicA);
        manager.updateEpic(epicB);

        // Checking epic<->subtask link
        assertTrue(manager.getEpicById(epicA.getId()).getSubIdList().contains(subA.getId()));
        assertTrue(manager.getEpicById(epicA.getId()).getSubIdList().contains(subB.getId()));
        assertTrue(manager.getEpicById(epicB.getId()).getSubIdList().contains(subC.getId()));
        assertEquals(manager.getSubTaskById(subA.getId()).getParentId(), epicA.getId());
        assertEquals(manager.getSubTaskById(subB.getId()).getParentId(), epicA.getId());
        assertEquals(manager.getSubTaskById(subC.getId()).getParentId(), epicB.getId());

        // Change task status then update task
        taskA = manager.getTaskById(taskA.getId());
        subA = manager.getSubTaskById(subA.getId());
        subC = manager.getSubTaskById(subC.getId());
        taskA.setStatus(IN_PROGRESS);
        subA.setStatus(IN_PROGRESS);
        subC.setStatus(DONE);
        manager.updateTask(taskA);
        manager.updateSub(subA);
        manager.updateSub(subC);

        // Checking task status update
        assertEquals(IN_PROGRESS, manager.getTaskById(taskA.getId()).getStatus());
        assertEquals(IN_PROGRESS, manager.getSubTaskById(subA.getId()).getStatus());
        assertEquals(IN_PROGRESS, manager.getEpicById(epicA.getId()).getStatus());
        assertEquals(DONE, manager.getSubTaskById(subC.getId()).getStatus());
        assertEquals(DONE, manager.getEpicById(epicB.getId()).getStatus());

        // Remove some task
        manager.removeTaskById(taskB.getId()); // ERROR
        manager.removeSubById(subA.getId());
        manager.removeEpicById(epicB.getId());

        // Checking task state and statuses
        Integer taskBid = taskB.getId();
        Integer epicBid = epicB.getId();
        Integer subAid = subA.getId();
        assertThrows(NoSuchElementException.class, () -> manager.getTaskById(taskBid));
        assertThrows(NoSuchElementException.class, () -> manager.getSubTaskById(subAid));
        assertThrows(NoSuchElementException.class, () -> manager.getEpicById(epicBid));
        Integer subCid = subC.getId();
        assertThrows(NoSuchElementException.class, () -> manager.getSubTaskById(subCid));
        assertEquals(NEW, manager.getEpicById(epicA.getId()).getStatus());

        // Checking final history state.
        assertEquals(3, manager.getHistoryTask().size());

        List<Task> finalHistory = manager.getHistoryTask();
        assertTrue(finalHistory.contains(taskA));
        assertTrue(finalHistory.contains(subB));
        assertTrue(finalHistory.contains(epicA));
    }

    @Test
    void scenarioB() {
        // Manager initialization
        TaskManager manager = Managers.getDefault();

        // Check manager initialization
        assertTrue(manager.getHistoryTask().isEmpty());
        assertTrue(manager.getTaskList().isEmpty());
        assertTrue(manager.getEpicList().isEmpty());
        assertTrue(manager.getSubList().isEmpty());

        // Create different task type
        Task taskA = new Task("Task A", "Description A");
        Task taskB = new Task("Task B", "Description B");
        Epic epicA = new Epic("Epic A", "Description C");
        Epic epicB = new Epic("Epic B", "Description D");
        SubTask subA = new SubTask("Subtask A", "Description E");
        SubTask subB = new SubTask("Subtask B", "Description F");
        SubTask subC = new SubTask("Subtask C", "Description G");

        // Adding task into manager
        manager.addTask(taskA);
        manager.addTask(taskB);
        manager.addEpic(epicA);
        manager.addEpic(epicB);
        manager.addSub(subA);
        manager.addSub(subB);
        manager.addSub(subC);

        // Checking manager storage maps
        assertTrue(manager.getHistoryTask().isEmpty());
        assertFalse(manager.getTaskList().isEmpty());
        assertEquals(2, manager.getTaskList().size());
        assertFalse(manager.getEpicList().isEmpty());
        assertEquals(2, manager.getEpicList().size());
        assertFalse(manager.getSubList().isEmpty());
        assertEquals(3, manager.getSubList().size());

        // Linking epic task and subtask then updating task
        epicA = manager.getEpicById(epicA.getId());
        subA = manager.getSubTaskById(subA.getId());
        subB = manager.getSubTaskById(subB.getId());
        subC = manager.getSubTaskById(subC.getId());
        epicA.addSubId(subA.getId());
        epicA.addSubId(subB.getId());
        epicA.addSubId(subC.getId());
        subA.setParentId(epicA.getId());
        subB.setParentId(epicA.getId());
        subC.setParentId(epicA.getId());
        manager.updateSub(subA);
        manager.updateSub(subB);
        manager.updateSub(subC);
        manager.updateEpic(epicA);

        // Checking epic<->subtask link
        assertTrue(manager.getEpicById(epicA.getId()).getSubIdList().contains(subA.getId()));
        assertTrue(manager.getEpicById(epicA.getId()).getSubIdList().contains(subB.getId()));
        assertTrue(manager.getEpicById(epicA.getId()).getSubIdList().contains(subC.getId()));
        assertEquals(manager.getSubTaskById(subA.getId()).getParentId(), epicA.getId());
        assertEquals(manager.getSubTaskById(subB.getId()).getParentId(), epicA.getId());
        assertEquals(manager.getSubTaskById(subC.getId()).getParentId(), epicA.getId());

        // Getting task and check history for duplicate entry
        manager.getEpicById(epicA.getId());
        manager.getTaskById(taskA.getId());
        manager.getSubTaskById(subA.getId());
        manager.getEpicById(epicB.getId());
        assertFalse(hasDuplicates(manager.getHistoryTask()));

        manager.getTaskById(taskB.getId());
        manager.getSubTaskById(subB.getId());
        manager.getTaskById(taskA.getId());
        manager.getEpicById(epicA.getId());
        assertFalse(hasDuplicates(manager.getHistoryTask()));

        manager.getSubTaskById(subB.getId());
        manager.getSubTaskById(subB.getId());
        manager.getTaskById(taskB.getId());
        manager.getSubTaskById(subC.getId());
        assertFalse(hasDuplicates(manager.getHistoryTask()));

        // Removing one task then check history
        manager.removeTaskById(taskA.getId());
        assertFalse(hasDuplicates(manager.getHistoryTask()));
        assertFalse(manager.getHistoryTask().contains(taskA));

        // Removing epic with 3 subtask then check history
        manager.removeEpicById(epicA.getId());
        assertFalse(hasDuplicates(manager.getHistoryTask()));
        assertFalse(manager.getHistoryTask().contains(epicA));
        assertFalse(manager.getHistoryTask().contains(subA));
        assertFalse(manager.getHistoryTask().contains(subB));
        assertFalse(manager.getHistoryTask().contains(subC));
    }

    // Utility method for search duplicates in ArrayList
    public <T> boolean hasDuplicates(ArrayList<T> list) {
        Set<T> set = new HashSet<>();
        for (T item : list) {
            if (!set.add(item)) {
                return true;
            }
        }
        return false;
    }
}
