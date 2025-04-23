package kanban.managers;

import static kanban.tasks.TaskStatus.DONE;
import static kanban.tasks.TaskStatus.IN_PROGRESS;
import static kanban.tasks.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import kanban.tasks.Epic;
import kanban.tasks.SubTask;
import kanban.tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        assertNotEquals(taskA.getDescription(),
                manager.getTaskById(taskA.getId()).getDescription());
        assertNotEquals(epicA.getDescription(),
                manager.getEpicById(epicA.getId()).getDescription());
        assertNotEquals(subA.getDescription(),
                manager.getSubTaskById(subA.getId()).getDescription());
    }

    @Test
    void givenInMemoryTaskManager_whenAddingTasksOfDifferentTypes_thenTasksAddedAndFoundById() {
        manager.addTask(taskA);
        manager.addEpic(epicA);
        manager.addSub(subA);
        assertEquals(taskA, manager.getTaskById(taskA.getId()));
        assertEquals(epicA, manager.getEpicById(epicA.getId()));
        assertEquals(subA, manager.getSubTaskById(subA.getId()));
    }

    @Test
    void givenTaskManager_whenCreatingAndGeneratingIds_thenIdsDoNotCollide() {
        manager.addTask(taskA);
        manager.addTask(taskB);
        assertEquals(manager.getTaskById(taskB.getId()), manager.getTaskById(taskA.getId() + 1));
    }

    @Test
    void givenTaskManager_whenAddingNewTask_thenTaskRemainsUnchangedInManager() {
        manager.addTask(taskA);
        assertEquals(taskA.getTitle(), manager.getTaskById(taskA.getId()).getTitle());
        assertEquals(taskA.getDescription(), manager.getTaskById(taskA.getId()).getDescription());
        assertEquals(taskA.getStatus(), manager.getTaskById(taskA.getId()).getStatus());
        assertEquals(taskA.getId(), manager.getTaskById(taskA.getId()).getId());
    }

    @Test
    void givenHistoryManager_whenSavingTask_thenHistoryManagerStoresPreviousState() {
        manager.addTask(taskA);
        Task updateTaskA = manager.getTaskById(taskA.getId());
        updateTaskA.setStatus(IN_PROGRESS);
        manager.updateTask(updateTaskA);
        updateTaskA = manager.getTaskById(taskA.getId());
        updateTaskA.setStatus(DONE);
        manager.updateTask(updateTaskA);
        ArrayList<Task> history;
        history = manager.getHistoryTask();
        assertEquals(IN_PROGRESS, history.getFirst().getStatus());
    }

    @Test
    void givenEpicTask_whenChangedStatusOfSubTaskToInProgress_thenEpicChangeStatusToInProgress() {
        manager.addEpic(epicA);
        manager.addSub(subA);
        manager.addSub(subB);

        SubTask updateSubA = manager.getSubTaskById(subA.getId());
        SubTask updateSubB = manager.getSubTaskById(subB.getId());
        updateSubA.setParentId(epicA.getId());
        updateSubB.setParentId(epicA.getId());
        manager.updateSub(updateSubA);
        manager.updateSub(updateSubB);

        Epic updateEpicA = manager.getEpicById(epicA.getId());
        updateEpicA.addSubId(subA.getId());
        updateEpicA.addSubId(subB.getId());
        manager.updateEpic(updateEpicA);

        updateSubA = manager.getSubTaskById(subA.getId());
        updateSubA.setStatus(IN_PROGRESS);
        manager.updateSub(updateSubA);
        manager.updateEpic(manager.getEpicById(updateSubA.getParentId()));

        assertEquals(IN_PROGRESS, manager.getEpicById(epicA.getId()).getStatus());
    }

    @Test
    void givenEpicTask_whenChangedStatusOfAllSubTaskToDone_thenEpicChangeStatusToDone() {
        manager.addEpic(epicA);
        manager.addSub(subA);
        manager.addSub(subB);

        SubTask updateSubA = manager.getSubTaskById(subA.getId());
        SubTask updateSubB = manager.getSubTaskById(subB.getId());
        updateSubA.setParentId(epicA.getId());
        updateSubB.setParentId(epicA.getId());
        manager.updateSub(updateSubA);
        manager.updateSub(updateSubB);

        Epic updateEpicA = manager.getEpicById(epicA.getId());
        updateEpicA.addSubId(subA.getId());
        updateEpicA.addSubId(subB.getId());
        manager.updateEpic(updateEpicA);

        updateSubA = manager.getSubTaskById(subA.getId());
        updateSubB = manager.getSubTaskById(subB.getId());
        updateSubA.setStatus(DONE);
        updateSubB.setStatus(DONE);
        manager.updateSub(updateSubA);
        manager.updateSub(updateSubB);
        manager.updateEpic(manager.getEpicById(updateSubA.getParentId()));

        assertEquals(DONE, manager.getEpicById(epicA.getId()).getStatus());
    }

    @Test
    void givenEpicWithSubtask_whenSubtaskRemoved_thenItIsDeletedFromEpicAndSubtaskMap() {
        manager.addEpic(epicA);
        manager.addSub(subA);
        manager.addSub(subB);

        SubTask updateSubA = manager.getSubTaskById(subA.getId());
        SubTask updateSubB = manager.getSubTaskById(subB.getId());
        updateSubA.setParentId(epicA.getId());
        updateSubB.setParentId(epicA.getId());
        manager.updateSub(updateSubA);
        manager.updateSub(updateSubB);

        Epic updateEpicA = manager.getEpicById(epicA.getId());
        updateEpicA.addSubId(subA.getId());
        updateEpicA.addSubId(subB.getId());
        manager.updateEpic(updateEpicA);

        manager.removeSubById(subB.getId());
        assertFalse(manager.getSubList().contains(subB));
        assertFalse(manager.getEpicSubTaskList(epicA.getId()).contains(subB));
        assertFalse(manager.getHistoryTask().contains(subB));
    }

    @Test
    void givenEpicTask_whenStatusOfSubTaskIsDifferent_thenEpicChangeStatusInProgress() {
        manager.addEpic(epicA);
        manager.addSub(subA);
        manager.addSub(subB);

        SubTask updateSubA = manager.getSubTaskById(subA.getId());
        SubTask updateSubB = manager.getSubTaskById(subB.getId());
        updateSubA.setParentId(epicA.getId());
        updateSubB.setParentId(epicA.getId());
        manager.updateSub(updateSubA);
        manager.updateSub(updateSubB);

        Epic updateEpicA = manager.getEpicById(epicA.getId());
        updateEpicA.addSubId(subA.getId());
        updateEpicA.addSubId(subB.getId());
        manager.updateEpic(updateEpicA);

        updateSubA = manager.getSubTaskById(subA.getId());
        updateSubB = manager.getSubTaskById(subB.getId());
        updateSubA.setStatus(IN_PROGRESS);
        updateSubB.setStatus(DONE);
        manager.updateSub(updateSubA);
        manager.updateSub(updateSubB);
        manager.updateEpic(manager.getEpicById(updateSubA.getParentId()));

        assertEquals(IN_PROGRESS, manager.getEpicById(epicA.getId()).getStatus());
    }

    @Test
    void givenEpicTask_whenRemoveSubTask_thenEpicTaskListEmptyAndStatusReset() {
        manager.addEpic(epicA);
        manager.addSub(subA);

        SubTask updateSubA = manager.getSubTaskById(subA.getId());
        updateSubA.setStatus(DONE);
        updateSubA.setParentId(epicA.getId());
        manager.updateSub(updateSubA);

        Epic updateEpicA = manager.getEpicById(epicA.getId());
        updateEpicA.addSubId(subA.getId());
        manager.updateEpic(updateEpicA);

        assertEquals(DONE, manager.getEpicById(epicA.getId()).getStatus());
        assertFalse(manager.getEpicSubTaskList(epicA.getId()).isEmpty());

        manager.removeSubById(subA.getId());

        assertTrue(manager.getEpicSubTaskList(epicA.getId()).isEmpty());
        assertEquals(NEW, manager.getEpicById(epicA.getId()).getStatus());
    }

    @Test
    void givenEpicTaskWithSub_whenRemovingEpicTask_thenEpicTaskWithSubRemoved() {
        manager.addEpic(epicA);
        manager.addSub(subA);

        SubTask updateSubA = manager.getSubTaskById(subA.getId());
        updateSubA.setParentId(epicA.getId());
        manager.updateSub(updateSubA);

        Epic updateEpicA = manager.getEpicById(epicA.getId());
        updateEpicA.addSubId(subA.getId());
        manager.updateEpic(updateEpicA);

        assertFalse(manager.getEpicList().isEmpty());
        assertFalse(manager.getSubList().isEmpty());

        manager.removeEpicById(epicA.getId());

        assertTrue(manager.getEpicList().isEmpty());
        assertTrue(manager.getSubList().isEmpty());
    }

    @Test
    void givenTask_whenTaskRemoving_thenTaskListEmpty() {
        manager.addTask(taskA);
        assertFalse(manager.getTaskList().isEmpty());

        manager.removeTaskById(taskA.getId());
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
        assertEquals("Task A", manager.getTaskById(taskA.getId()).getTitle());
        assertEquals("Description A", manager.getTaskById(taskA.getId()).getDescription());

        taskB.setId(taskA.getId());
        taskB.setTitle("Updated task A");
        taskB.setDescription("Updated description A");
        manager.updateTask(taskB);
        assertEquals("Updated task A", manager.getTaskById(taskA.getId()).getTitle());
        assertEquals("Updated description A", manager.getTaskById(taskA.getId()).getDescription());

        taskB.setTitle("Updated task B");
        taskB.setDescription("Updated description B");
        assertEquals("Updated task A", manager.getTaskById(taskA.getId()).getTitle());
        assertEquals("Updated description A", manager.getTaskById(taskA.getId()).getDescription());
    }

    @Test
    void givenEpicTask_whenUpdateEpicTask_thenEpicTaskUpdated() {
        manager.addEpic(epicA);
        manager.addSub(subA);
        SubTask updateSubA = manager.getSubTaskById(subA.getId());
        updateSubA.setParentId(epicA.getId());
        manager.updateSub(updateSubA);
        Epic updateEpicA = manager.getEpicById(epicA.getId());
        updateEpicA.addSubId(subA.getId());
        manager.updateEpic(updateEpicA);
        updateSubA = manager.getSubTaskById(subA.getId());
        updateSubA.setStatus(IN_PROGRESS);
        manager.updateSub(updateSubA);
        manager.updateEpic(manager.getEpicById(updateSubA.getParentId()));
        assertEquals(IN_PROGRESS, manager.getEpicById(epicA.getId()).getStatus());

        subB.setStatus(DONE);
        manager.addSub(subB);
        epicB.setId(epicA.getId());
        epicB.addSubId(subB.getId());
        manager.updateEpic(epicB);
        assertEquals(DONE, manager.getEpicById(epicA.getId()).getStatus());
    }

    @Test
    void givenSubTask_whenUpdateSubTask_thenSubTaskUpdated() {
        manager.addEpic(epicA);
        manager.addSub(subA);
        SubTask updateSubA = manager.getSubTaskById(subA.getId());
        updateSubA.setParentId(epicA.getId());
        manager.updateSub(updateSubA);
        Epic updateEpicA = manager.getEpicById(epicA.getId());
        updateEpicA.addSubId(subA.getId());
        manager.updateEpic(updateEpicA);
        updateSubA = manager.getSubTaskById(subA.getId());
        updateSubA.setStatus(IN_PROGRESS);
        manager.updateSub(updateSubA);
        manager.updateEpic(manager.getEpicById(updateSubA.getParentId()));
        assertEquals(IN_PROGRESS, manager.getEpicById(epicA.getId()).getStatus());

        subB.setId(subA.getId());
        subB.setParentId(epicA.getId());
        subB.setStatus(DONE);
        manager.updateSub(subB);
        assertEquals(DONE, manager.getEpicById(epicA.getId()).getStatus());
    }
}