package kanban.tasks;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    private static SubTask subA;
    private static SubTask subB;
    private static Epic epicA;

    @BeforeAll
    static void beforeAllTests() {
        subA = new SubTask("Sub A", "Description A");
        subB = new SubTask("Sub B", "Description B");
        epicA = new Epic("Epic A", "Description C");
    }

    @Test
    void givenSubtask_whenSettingItAsItsOwnEpic_thenThrowException() {
        assertThrows(IllegalArgumentException.class, () -> subA.setParentID(subA.getID()));
    }

    @Test
    void givenSubTask_whenSetSubParentTaskAsEpic_thenSubParentTaskNotNullandEqualEpicID() {
        subA.setParentID(epicA.getID());
        assertNotNull(subA.getParentID());
        assertEquals(subA.getParentID(), epicA.getID());
    }

    @Test
    void givenTwoSubWithSameID_whenIDEquals_thenSubEquals() {
        subA.setID(1);
        subB.setID(1);
        assertEquals(subA, subB);
    }
}