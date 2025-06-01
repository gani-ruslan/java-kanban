package kanban.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link SubTask} class to verify parent ID handling
 * and equality behavior.
 */
public class SubTaskTest {

    private static SubTask subA;
    private static SubTask subB;
    private static Epic epicA;

    @BeforeAll
    static void setUpOnce() {
        subA = new SubTask("Sub A", "Description A");
        subB = new SubTask("Sub B", "Description B");
        epicA = new Epic("Epic A", "Description C");
    }

    @BeforeEach
    void setUp() {
        subA.setId(1);
        subB.setId(2);
        epicA.setId(3);
    }

    /**
     * Verifies that a subtask cannot reference itself as its own parent epic.
     */
    @Test
    void shouldThrowWhenSubtaskParentIsItself() {
        assertThrows(IllegalArgumentException.class, () -> subA.setParentId(subA.getId()));
    }

    /**
     * Verifies that setting an epic as a subtask's parent stores the correct parent ID.
     */
    @Test
    void shouldStoreParentEpicIdInSubtask() {
        subA.setParentId(epicA.getId());
        assertNotNull(subA.getParentId());
        assertEquals(epicA.getId(), subA.getParentId());
    }

    /**
     * Verifies that two subtasks with the same ID are considered equal.
     */
    @Test
    void shouldConsiderSubtasksEqualIfTheyHaveSameId() {
        subA.setId(1);
        subB.setId(1);
        assertEquals(subA, subB);
    }
}
