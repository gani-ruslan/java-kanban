package kanban.managers;

import kanban.tasks.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

class InMemoryTaskManagerTest {

    private TaskManager manager;
    private Task taskA;
    private Task taskB;
    private Epic epicA;
    private Epic epicB;
    private SubTask subA;
    private SubTask subB;

    @BeforeEach
    void beforeEachTest() {
        manager = Managers.getDefault();
        taskA = new Task("Task A", "Description A");
        taskB = new Task("Task B", "Description B");
        epicA = new Epic("Epic A", "Description C");
        epicB = new Epic("Epic B", "Description D");
        subA = new SubTask("Sub A", "Description E");
        subB = new SubTask("Sub B", "Description F");
    }

    @Test
    void givenTasksOfVariousTypes_whenAdded_thenManagerStoresThemIndependently() {
        manager.addTask(taskA);
        manager.addEpic(epicA);
        manager.addSub(subA);
        taskA.setDescription("Description A modified");
        epicA.setDescription("Description C modified");
        subA.setDescription("Description E modified");
        assertNotEquals(taskA.getDescription(), manager.getTaskByID(taskA.getID()).getDescription());
        assertNotEquals(epicA.getDescription(), manager.getEpicByID(epicA.getID()).getDescription());
        assertNotEquals(subA.getDescription(), manager.getSubTaskByID(subA.getID()).getDescription());
    }

    @Test
    void givenInMemoryTaskManager_whenAddingTasksOfDifferentTypes_thenTasksAddedAndFoundById() {
        manager.addTask(taskA);
        manager.addEpic(epicA);
        manager.addSub(subA);
        assertEquals(taskA, manager.getTaskByID(taskA.getID()));
        assertEquals(epicA, manager.getEpicByID(epicA.getID()));
        assertEquals(subA, manager.getSubTaskByID(subA.getID()));
    }

    @Test
    void givenTaskManager_whenCreatingAndGeneratingIds_thenIdsDoNotCollide() {
        manager.addTask(taskA);
        manager.addTask(taskB);
        assertEquals(manager.getTaskByID(taskB.getID()), manager.getTaskByID(taskA.getID() + 1));
    }

    @Test
    void givenTaskManager_whenAddingNewTask_thenTaskRemainsUnchangedInManager() {
        manager.addTask(taskA);
        assertEquals(taskA.getTitle(), manager.getTaskByID(taskA.getID()).getTitle());
        assertEquals(taskA.getDescription(), manager.getTaskByID(taskA.getID()).getDescription());
        assertEquals(taskA.getStatus(), manager.getTaskByID(taskA.getID()).getStatus());
        assertEquals(taskA.getID(), manager.getTaskByID(taskA.getID()).getID());
    }

    @Test
    void givenHistoryManager_whenSavingTask_thenHistoryManagerStoresPreviousState() {
        ArrayList<Task> history;
        manager.addTask(taskA);
        Task updateTaskA = manager.getTaskByID(taskA.getID());
        updateTaskA.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(updateTaskA);
        updateTaskA = manager.getTaskByID(taskA.getID());
        updateTaskA.setStatus(TaskStatus.DONE);
        manager.updateTask(updateTaskA);
        history = manager.getHistoryTask();
        assertEquals(TaskStatus.IN_PROGRESS, history.getFirst().getStatus());
    }

    @Test
    void givenEpicTask_whenChangedStatusOfSubTaskToInProgress_thenEpicChangeStatusToInProgress() {
        manager.addEpic(epicA);
        manager.addSub(subA);
        manager.addSub(subB);

        SubTask updateSubA = manager.getSubTaskByID(subA.getID());
        SubTask updateSubB = manager.getSubTaskByID(subB.getID());
        updateSubA.setParentID(epicA.getID());
        updateSubB.setParentID(epicA.getID());
        manager.updateSub(updateSubA);
        manager.updateSub(updateSubB);

        Epic updateEpicA = manager.getEpicByID(epicA.getID());
        updateEpicA.addSubID(subA.getID());
        updateEpicA.addSubID(subB.getID());
        manager.updateEpic(updateEpicA);

        updateSubA = manager.getSubTaskByID(subA.getID());
        updateSubA.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSub(updateSubA);
        manager.updateEpic(manager.getEpicByID(updateSubA.getParentID()));

        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicByID(epicA.getID()).getStatus());
    }

    @Test
    void givenEpicTask_whenChangedStatusOfAllSubTaskToDone_thenEpicChangeStatusToDone() {
        manager.addEpic(epicA);
        manager.addSub(subA);
        manager.addSub(subB);

        SubTask updateSubA = manager.getSubTaskByID(subA.getID());
        SubTask updateSubB = manager.getSubTaskByID(subB.getID());
        updateSubA.setParentID(epicA.getID());
        updateSubB.setParentID(epicA.getID());
        manager.updateSub(updateSubA);
        manager.updateSub(updateSubB);

        Epic updateEpicA = manager.getEpicByID(epicA.getID());
        updateEpicA.addSubID(subA.getID());
        updateEpicA.addSubID(subB.getID());
        manager.updateEpic(updateEpicA);

        updateSubA = manager.getSubTaskByID(subA.getID());
        updateSubB = manager.getSubTaskByID(subB.getID());
        updateSubA.setStatus(TaskStatus.DONE);
        updateSubB.setStatus(TaskStatus.DONE);
        manager.updateSub(updateSubA);
        manager.updateSub(updateSubB);
        manager.updateEpic(manager.getEpicByID(updateSubA.getParentID()));

        assertEquals(TaskStatus.DONE, manager.getEpicByID(epicA.getID()).getStatus());
    }

    @Test
    void givenEpicWithSubtask_whenSubtaskRemoved_thenItIsDeletedFromEpicAndSubtaskMap() {
        manager.addEpic(epicA);
        manager.addSub(subA);
        manager.addSub(subB);

        SubTask updateSubA = manager.getSubTaskByID(subA.getID());
        SubTask updateSubB = manager.getSubTaskByID(subB.getID());
        updateSubA.setParentID(epicA.getID());
        updateSubB.setParentID(epicA.getID());
        manager.updateSub(updateSubA);
        manager.updateSub(updateSubB);

        Epic updateEpicA = manager.getEpicByID(epicA.getID());
        updateEpicA.addSubID(subA.getID());
        updateEpicA.addSubID(subB.getID());
        manager.updateEpic(updateEpicA);

        manager.removeSubByID(subB.getID());
        assertFalse(manager.getSubList().contains(subB));
        assertFalse(manager.getEpicSubTaskList(epicA.getID()).contains(subB));
        assertFalse(manager.getHistoryTask().contains(subB));
    }

    @Test
    void givenEpicTask_whenStatusOfSubTaskIsDifferent_thenEpicChangeStatusInProgress() {
        manager.addEpic(epicA);
        manager.addSub(subA);
        manager.addSub(subB);

        SubTask updateSubA = manager.getSubTaskByID(subA.getID());
        SubTask updateSubB = manager.getSubTaskByID(subB.getID());
        updateSubA.setParentID(epicA.getID());
        updateSubB.setParentID(epicA.getID());
        manager.updateSub(updateSubA);
        manager.updateSub(updateSubB);

        Epic updateEpicA = manager.getEpicByID(epicA.getID());
        updateEpicA.addSubID(subA.getID());
        updateEpicA.addSubID(subB.getID());
        manager.updateEpic(updateEpicA);

        updateSubA = manager.getSubTaskByID(subA.getID());
        updateSubB = manager.getSubTaskByID(subB.getID());
        updateSubA.setStatus(TaskStatus.IN_PROGRESS);
        updateSubB.setStatus(TaskStatus.DONE);
        manager.updateSub(updateSubA);
        manager.updateSub(updateSubB);
        manager.updateEpic(manager.getEpicByID(updateSubA.getParentID()));

        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicByID(epicA.getID()).getStatus());
    }

    @Test
    void givenEpicTask_whenRemoveSubTask_thenEpicTaskListEmptyAndStatusReset() {
        manager.addEpic(epicA);
        manager.addSub(subA);

        SubTask updateSubA = manager.getSubTaskByID(subA.getID());
        updateSubA.setStatus(TaskStatus.DONE);
        updateSubA.setParentID(epicA.getID());
        manager.updateSub(updateSubA);

        Epic updateEpicA = manager.getEpicByID(epicA.getID());
        updateEpicA.addSubID(subA.getID());
        manager.updateEpic(updateEpicA);

        assertEquals(TaskStatus.DONE, manager.getEpicByID(epicA.getID()).getStatus());
        assertFalse(manager.getEpicSubTaskList(epicA.getID()).isEmpty());

        manager.removeSubByID(subA.getID());

        assertTrue(manager.getEpicSubTaskList(epicA.getID()).isEmpty());
        assertEquals(TaskStatus.NEW, manager.getEpicByID(epicA.getID()).getStatus());
    }

    @Test
    void givenEpicTaskWithSub_whenRemovingEpicTask_thenEpicTaskWithSubRemoved() {
        manager.addEpic(epicA);
        manager.addSub(subA);

        SubTask updateSubA = manager.getSubTaskByID(subA.getID());
        updateSubA.setParentID(epicA.getID());
        manager.updateSub(updateSubA);

        Epic updateEpicA = manager.getEpicByID(epicA.getID());
        updateEpicA.addSubID(subA.getID());
        manager.updateEpic(updateEpicA);

        assertFalse(manager.getEpicList().isEmpty());
        assertFalse(manager.getSubList().isEmpty());

        manager.removeEpicByID(epicA.getID());

        assertTrue(manager.getEpicList().isEmpty());
        assertTrue(manager.getSubList().isEmpty());
    }

    @Test
    void givenTask_whenTaskRemoving_thenTaskListEmpty() {
        manager.addTask(taskA);
        assertFalse(manager.getTaskList().isEmpty());

        manager.removeTaskByID(taskA.getID());
        assertTrue(manager.getTaskList().isEmpty());
    }

    @Test
    void givenTaskManager_whenRemovingTaskByType_thenTaskManagerListsIsEmpty() {
        manager.addTask(taskA);
        manager.addEpic(epicA);
        manager.addSub(subA);
        assertFalse(manager.getTaskList().isEmpty());
        assertFalse(manager.getEpicList().isEmpty());
        assertFalse(manager.getSubList().isEmpty());

        manager.removeAllTask();
        manager.removeAllEpic();
        manager.removeAllSub();
        assertTrue(manager.getTaskList().isEmpty());
        assertTrue(manager.getEpicList().isEmpty());
        assertTrue(manager.getSubList().isEmpty());
    }

    @Test
    void givenTask_whenUpdateTask_thenTaskUpdated() {
        manager.addTask(taskA);
        assertEquals("Task A", manager.getTaskByID(taskA.getID()).getTitle());
        assertEquals("Description A", manager.getTaskByID(taskA.getID()).getDescription());

        taskB.setID(taskA.getID());
        taskB.setTitle("Updated task A");
        taskB.setDescription("Updated description A");
        manager.updateTask(taskB);
        assertEquals("Updated task A", manager.getTaskByID(taskA.getID()).getTitle());
        assertEquals("Updated description A", manager.getTaskByID(taskA.getID()).getDescription());

        taskB.setTitle("Updated task B");
        taskB.setDescription("Updated description B");
        assertEquals("Updated task A", manager.getTaskByID(taskA.getID()).getTitle());
        assertEquals("Updated description A", manager.getTaskByID(taskA.getID()).getDescription());
    }

    @Test
    void givenEpicTask_whenUpdateEpicTask_thenEpicTaskUpdated() {
        manager.addEpic(epicA);
        manager.addSub(subA);
        SubTask updateSubA = manager.getSubTaskByID(subA.getID());
        updateSubA.setParentID(epicA.getID());
        manager.updateSub(updateSubA);
        Epic updateEpicA = manager.getEpicByID(epicA.getID());
        updateEpicA.addSubID(subA.getID());
        manager.updateEpic(updateEpicA);
        updateSubA = manager.getSubTaskByID(subA.getID());
        updateSubA.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSub(updateSubA);
        manager.updateEpic(manager.getEpicByID(updateSubA.getParentID()));
        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicByID(epicA.getID()).getStatus());

        subB.setStatus(TaskStatus.DONE);
        manager.addSub(subB);
        epicB.setID(epicA.getID());
        epicB.addSubID(subB.getID());
        manager.updateEpic(epicB);
        assertEquals(TaskStatus.DONE, manager.getEpicByID(epicA.getID()).getStatus());
    }

    @Test
    void givenSubTask_whenUpdateSubTask_thenSubTaskUpdated() {
        manager.addEpic(epicA);
        manager.addSub(subA);
        SubTask updateSubA = manager.getSubTaskByID(subA.getID());
        updateSubA.setParentID(epicA.getID());
        manager.updateSub(updateSubA);
        Epic updateEpicA = manager.getEpicByID(epicA.getID());
        updateEpicA.addSubID(subA.getID());
        manager.updateEpic(updateEpicA);
        updateSubA = manager.getSubTaskByID(subA.getID());
        updateSubA.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSub(updateSubA);
        manager.updateEpic(manager.getEpicByID(updateSubA.getParentID()));
        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicByID(epicA.getID()).getStatus());

        subB.setID(subA.getID());
        subB.setParentID(epicA.getID());
        subB.setStatus(TaskStatus.DONE);
        manager.updateSub(subB);
        assertEquals(TaskStatus.DONE, manager.getEpicByID(epicA.getID()).getStatus());
    }
}