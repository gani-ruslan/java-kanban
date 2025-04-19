package kanban.managers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ManagersTest {

    @Test
    void givenManager_whenInitialized_thenReturnsInitializedInstance() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager);
        assertTrue(manager.getTaskList().isEmpty());
    }

    @Test
    void givenHistoryManager_whenInitialized_thenReturnsInitializedInstance() {
        HistoryManager manager = Managers.getDefaultHistory();
        assertNotNull(manager);
        assertTrue(manager.getTasks().isEmpty());
    }
}
