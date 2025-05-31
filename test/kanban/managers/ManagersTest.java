package kanban.managers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link Managers} utility class,
 * verifying that it returns initialized instances of managers.
 */
public class ManagersTest {

    /**
     * Verifies that the default TaskManager instance is properly initialized
     * and returns empty task list.
     */
    @Test
    void shouldReturnInitializedTaskManagerWithEmptyTaskList() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager);
        assertTrue(manager.getTaskList().isEmpty());
    }

    /**
     * Verifies that the default HistoryManager instance is properly initialized
     * and returns empty history list.
     */
    @Test
    void shouldReturnInitializedHistoryManagerWithEmptyTaskHistory() {
        HistoryManager manager = Managers.getDefaultHistory();
        assertNotNull(manager);
        assertTrue(manager.getTasks().isEmpty());
    }
}
