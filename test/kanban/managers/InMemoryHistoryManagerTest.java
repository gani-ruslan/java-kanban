package kanban.managers;

import kanban.tasks.Task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;
    private Task taskA;
    private Task taskB;
    private Task taskC;
    private Task taskD;

    @BeforeEach
    void beforeEachTest() {
        historyManager = Managers.getDefaultHistory();
        taskA = new Task("Task A", "Description A");
        taskB = new Task("Task B", "Description B");
        taskC = new Task("Task C", "Description C");
        taskD = new Task("Task D", "Description D");
        taskA.setID(1);
        taskB.setID(2);
        taskC.setID(3);
        taskD.setID(4);
    }

    @Test
    void givenNewHistoryManager_whenInitialized_thenHistoryIsEmpty(){
        assertTrue(historyManager.getTasks().isEmpty());
    }

    @Test
    void givenTask_whenAddedToHistory_thenHistoryNotEmpty() {
        historyManager.add(taskA);
        historyManager.add(taskB);
        historyManager.add(taskC);
        assertFalse(historyManager.getTasks().isEmpty());
        assertEquals(3, historyManager.getTasks().size());
    }

    @Test
    void givenTask_whenModifiedAfterAddingToHistory_thenStoredTaskIsUnaffected() {
        historyManager.add(taskA);
        taskA.setDescription("Description B modified");
        assertNotEquals(historyManager.getTasks().getFirst().getDescription(), taskA.getDescription());
    }

    @Test
    void givenThreeTasks_whenAddingAgainFirstTask_thenItBecomesLastInHistory() {

        List<Task> expectedOrder = Arrays.asList(taskB, taskC, taskA);

        historyManager.add(taskA);
        historyManager.add(taskB);
        historyManager.add(taskC);
        historyManager.add(taskA);
        assertEquals(expectedOrder, historyManager.getTasks());
    }

    @Test
    void givenThreeTasks_whenAllAgainAdded_thenHistoryHasNoDuplicatesAndCorrectOrder() {

        List<Task> expectedOrder = Arrays.asList(taskC, taskB, taskA);

        historyManager.add(taskA);
        historyManager.add(taskB);
        historyManager.add(taskC);
        historyManager.add(taskC);
        historyManager.add(taskB);
        historyManager.add(taskA);
        assertFalse(hasDuplicates(historyManager.getTasks()));
        assertEquals(expectedOrder, historyManager.getTasks());
    }

    @Test
    void givenTasksInHistory_whenSomeRemoved_thenRemainingAreInCorrectOrderWithoutDuplicates() {

        List<Task> expectedOrder = Arrays.asList(taskB, taskD);

        historyManager.add(taskA);
        historyManager.add(taskB);
        historyManager.add(taskC);
        historyManager.remove(taskA.getID());
        historyManager.add(taskD);
        historyManager.remove(taskC.getID());
        assertFalse(hasDuplicates(historyManager.getTasks()));
        assertEquals(expectedOrder, historyManager.getTasks());
    }

    // Utility method for search duplicates in ArrayList
    public <T> boolean hasDuplicates(ArrayList<T> list) {
        Set<T> set = new HashSet<>();
        for (T item : list) if (!set.add(item)) return true;
        return false;
    }

}
