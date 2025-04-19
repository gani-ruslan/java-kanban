package kanban;

import kanban.managers.Managers;
import kanban.managers.TaskManager;
import kanban.tasks.Epic;
import kanban.tasks.SubTask;
import kanban.tasks.Task;
import kanban.tasks.TaskStatus;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    @Test
    void ScenarioA() {
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
        epicA = manager.getEpicByID(epicA.getID());
        epicB = manager.getEpicByID(epicB.getID());
        subA = manager.getSubTaskByID(subA.getID());
        subB = manager.getSubTaskByID(subB.getID());
        subC = manager.getSubTaskByID(subC.getID());
        epicA.addSubID(subA.getID());
        epicA.addSubID(subB.getID());
        epicB.addSubID(subC.getID());
        subA.setParentID(epicA.getID());
        subB.setParentID(epicA.getID());
        subC.setParentID(epicB.getID());
        manager.updateSub(subA);
        manager.updateSub(subB);
        manager.updateSub(subC);
        manager.updateEpic(epicA);
        manager.updateEpic(epicB);

        // Checking epic<->subtask link
        assertTrue(manager.getEpicByID(epicA.getID()).getSubIDList().contains(subA.getID()));
        assertTrue(manager.getEpicByID(epicA.getID()).getSubIDList().contains(subB.getID()));
        assertTrue(manager.getEpicByID(epicB.getID()).getSubIDList().contains(subC.getID()));
        assertEquals(manager.getSubTaskByID(subA.getID()).getParentID(), epicA.getID());
        assertEquals(manager.getSubTaskByID(subB.getID()).getParentID(), epicA.getID());
        assertEquals(manager.getSubTaskByID(subC.getID()).getParentID(), epicB.getID());

        // Change task status then update task
        taskA = manager.getTaskByID(taskA.getID());
        subA = manager.getSubTaskByID(subA.getID());
        subC = manager.getSubTaskByID(subC.getID());
        taskA.setStatus(TaskStatus.IN_PROGRESS);
        subA.setStatus(TaskStatus.IN_PROGRESS);
        subC.setStatus(TaskStatus.DONE);
        manager.updateTask(taskA);
        manager.updateSub(subA);
        manager.updateSub(subC);

        // Checking task status update
        assertEquals(TaskStatus.IN_PROGRESS, manager.getTaskByID(taskA.getID()).getStatus());
        assertEquals(TaskStatus.IN_PROGRESS, manager.getSubTaskByID(subA.getID()).getStatus());
        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicByID(epicA.getID()).getStatus());
        assertEquals(TaskStatus.DONE, manager.getSubTaskByID(subC.getID()).getStatus());
        assertEquals(TaskStatus.DONE, manager.getEpicByID(epicB.getID()).getStatus());

        // Remove some task
        manager.removeTaskByID(taskB.getID()); // ERROR
        manager.removeSubByID(subA.getID());
        manager.removeEpicByID(epicB.getID());

        // Checking task state and statuses
        Integer taskBID = taskB.getID();
        Integer subAID = subA.getID();
        Integer subCID = subC.getID();
        Integer epicBID = epicB.getID();

        assertThrows(NoSuchElementException.class, () -> manager.getTaskByID(taskBID));
        assertThrows(NoSuchElementException.class, () -> manager.getSubTaskByID(subAID));
        assertThrows(NoSuchElementException.class, () -> manager.getEpicByID(epicBID));
        assertThrows(NoSuchElementException.class, () -> manager.getSubTaskByID(subCID));
        assertEquals(TaskStatus.NEW, manager.getEpicByID(epicA.getID()).getStatus());

        // Checking final history state.
        assertEquals(3, manager.getHistoryTask().size());

        List<Task> finalHistory = manager.getHistoryTask();
        assertTrue(finalHistory.contains(taskA));
        assertTrue(finalHistory.contains(subB));
        assertTrue(finalHistory.contains(epicA));
    }

    @Test
    void ScenarioB() {
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
        epicA = manager.getEpicByID(epicA.getID());
        subA = manager.getSubTaskByID(subA.getID());
        subB = manager.getSubTaskByID(subB.getID());
        subC = manager.getSubTaskByID(subC.getID());
        epicA.addSubID(subA.getID());
        epicA.addSubID(subB.getID());
        epicA.addSubID(subC.getID());
        subA.setParentID(epicA.getID());
        subB.setParentID(epicA.getID());
        subC.setParentID(epicA.getID());
        manager.updateSub(subA);
        manager.updateSub(subB);
        manager.updateSub(subC);
        manager.updateEpic(epicA);

        // Checking epic<->subtask link
        assertTrue(manager.getEpicByID(epicA.getID()).getSubIDList().contains(subA.getID()));
        assertTrue(manager.getEpicByID(epicA.getID()).getSubIDList().contains(subB.getID()));
        assertTrue(manager.getEpicByID(epicA.getID()).getSubIDList().contains(subC.getID()));
        assertEquals(manager.getSubTaskByID(subA.getID()).getParentID(), epicA.getID());
        assertEquals(manager.getSubTaskByID(subB.getID()).getParentID(), epicA.getID());
        assertEquals(manager.getSubTaskByID(subC.getID()).getParentID(), epicA.getID());

        // Getting task and check history for duplicate entry
        manager.getEpicByID(epicA.getID());
        manager.getTaskByID(taskA.getID());
        manager.getSubTaskByID(subA.getID());
        manager.getEpicByID(epicB.getID());
        assertFalse(hasDuplicates(manager.getHistoryTask()));

        manager.getTaskByID(taskB.getID());
        manager.getSubTaskByID(subB.getID());
        manager.getTaskByID(taskA.getID());
        manager.getEpicByID(epicA.getID());
        assertFalse(hasDuplicates(manager.getHistoryTask()));

        manager.getSubTaskByID(subB.getID());
        manager.getSubTaskByID(subB.getID());
        manager.getTaskByID(taskB.getID());
        manager.getSubTaskByID(subC.getID());
        assertFalse(hasDuplicates(manager.getHistoryTask()));

        // Removing one task then check history
        manager.removeTaskByID(taskA.getID());
        assertFalse(hasDuplicates(manager.getHistoryTask()));
        assertFalse(manager.getHistoryTask().contains(taskA));

        // Removing epic with 3 subtask then check history
        manager.removeEpicByID(epicA.getID());
        assertFalse(hasDuplicates(manager.getHistoryTask()));
        assertFalse(manager.getHistoryTask().contains(epicA));
        assertFalse(manager.getHistoryTask().contains(subA));
        assertFalse(manager.getHistoryTask().contains(subB));
        assertFalse(manager.getHistoryTask().contains(subC));
    }

    // Utility method for search duplicates in ArrayList
    public <T> boolean hasDuplicates(ArrayList<T> list) {
        Set<T> set = new HashSet<>();
        for (T item : list) if (!set.add(item)) return true;
        return false;
    }
}
