package kanban.managers;

import kanban.tasks.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

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
        taskA = new Task("Title task A", "Task description A");
        taskB = new Task("Title task B", "Task description B");
        epicA = new Epic("Title epic A", "Epic description A");
        epicB = new Epic("Title epic B", "Epic description B");
        subA = new SubTask("Title sub A", "Sub description A");
        subB = new SubTask("Title sub B", "Sub description B");
    }

    @Test
    void givenEpic_whenSettingItAsItsOwnSubtask_thenThrowException() {
        manager.addEpic(epicA);
        assertThrows(IllegalArgumentException.class, () -> manager.addSubToEpic(epicA.getID(), epicA.getID()));
    }

    @Test
    void givenSubtask_whenSettingItAsItsOwnEpic_thenThrowException() {
        manager.addSub(subA);
        assertThrows(IllegalArgumentException.class, () -> manager.addSubToEpic(subA.getID(), subA.getID()));
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
        manager.addTask(taskA);
        manager.getTaskByID(taskA.getID()).setStatus(TaskStatus.IN_PROGRESS);
        manager.getTaskByID(taskA.getID()).setStatus(TaskStatus.DONE);
        ArrayList<Task> history = manager.getHistory();
        assertEquals(TaskStatus.IN_PROGRESS, history.get(1).getStatus());
    }

    @Test
    void givenEpicTask_whenChangedStatusOfSubTaskToInProgress_thenEpicChangeStatusToInProgress() {
        manager.addEpic(epicA);
        manager.addSub(subA);
        manager.addSub(subB);
        manager.addSubToEpic(subA.getID(), epicA.getID());
        manager.addSubToEpic(subB.getID(), epicA.getID());
        manager.getSubTaskByID(subB.getID()).setStatus(TaskStatus.IN_PROGRESS);
        manager.updateStatus(epicA.getID());
        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicByID(epicA.getID()).getStatus());
    }

    @Test
    void givenEpicTask_whenChangedStatusOfAllSubTaskToDone_thenEpicChangeStatusToDone() {
        manager.addEpic(epicA);
        manager.addSub(subA);
        manager.addSub(subB);
        manager.addSubToEpic(subA.getID(), epicA.getID());
        manager.addSubToEpic(subB.getID(), epicA.getID());
        manager.getSubTaskByID(subA.getID()).setStatus(TaskStatus.DONE);
        manager.getSubTaskByID(subB.getID()).setStatus(TaskStatus.DONE);
        manager.updateStatus(epicA.getID());
        assertEquals(TaskStatus.DONE, manager.getEpicByID(epicA.getID()).getStatus());
    }

    @Test
    void givenEpicTask_whenStatusOfSubTaskIsDifferent_thenEpicChangeStatusInProgress() {
        manager.addEpic(epicA);
        manager.addSub(subA);
        manager.addSub(subB);
        manager.addSubToEpic(subA.getID(), epicA.getID());
        manager.addSubToEpic(subB.getID(), epicA.getID());
        manager.getSubTaskByID(subA.getID()).setStatus(TaskStatus.IN_PROGRESS);
        manager.getSubTaskByID(subB.getID()).setStatus(TaskStatus.DONE);
        manager.updateStatus(epicA.getID());
        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicByID(epicA.getID()).getStatus());
    }

    @Test
    void givenEpicTask_whenRemoveSubTask_thenEpicTaskListEmptyAndStatusReset() {
        manager.addEpic(epicA);
        subA.setStatus(TaskStatus.DONE);
        manager.addSub(subA);
        manager.addSubToEpic(subA.getID(), epicA.getID());
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
        manager.addSubToEpic(subA.getID(), epicA.getID());
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
        assertEquals("Title task A", manager.getTaskByID(taskA.getID()).getTitle());
        assertEquals("Task description A", manager.getTaskByID(taskA.getID()).getDescription());
        taskB.setID(taskA.getID());
        taskB.setTitle("Updated title task A");
        taskB.setDescription("Updated description of task A");
        manager.updateTask(taskB);
        assertEquals("Updated title task A", manager.getTaskByID(taskA.getID()).getTitle());
        assertEquals("Updated description of task A", manager.getTaskByID(taskA.getID()).getDescription());
        taskB.setTitle("Updated title task B");
        taskB.setDescription("Updated description of task B");
        assertEquals("Updated title task A", manager.getTaskByID(taskA.getID()).getTitle());
        assertEquals("Updated description of task A", manager.getTaskByID(taskA.getID()).getDescription());
    }

    @Test
    void givenEpicTask_whenUpdateEpicTask_thenEpicTaskUpdated() {
        manager.addEpic(epicA);
        manager.addSub(subA);
        manager.getSubTaskByID(subA.getID()).setStatus(TaskStatus.IN_PROGRESS);
        manager.addSubToEpic(subA.getID(), epicA.getID());
        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicByID(epicA.getID()).getStatus());

        subB.setStatus(TaskStatus.DONE);
        manager.addSub(subB);
        epicB.setID(epicA.getID());
        epicB.addSubTaskID(subB.getID());
        manager.updateEpic(epicB);
        assertEquals(TaskStatus.DONE, manager.getEpicByID(epicA.getID()).getStatus());
    }

    @Test
    void givenSubTask_whenUpdateSubTask_thenSubTaskUpdated() {
        manager.addEpic(epicA);
        manager.addSub(subA);
        manager.getSubTaskByID(subA.getID()).setStatus(TaskStatus.IN_PROGRESS);
        manager.addSubToEpic(subA.getID(), epicA.getID());
        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicByID(epicA.getID()).getStatus());

        subB.setID(subA.getID());
        subB.setParentTaskID(epicA.getID());
        subB.setStatus(TaskStatus.DONE);
        manager.updateSub(subB);
        assertEquals(TaskStatus.DONE, manager.getEpicByID(epicA.getID()).getStatus());
    }
}