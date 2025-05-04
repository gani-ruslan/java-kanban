package kanban.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    @BeforeEach
    void beforeEachTest() {
        subA.setId(1);
        subB.setId(2);
        epicA.setId(3);
    }

    @Test
    void givenSubtask_whenSettingItAsItsOwnEpic_thenThrowException() {
        assertThrows(IllegalArgumentException.class, () -> subA.setParentId(subA.getId()));
    }

    @Test
    void givenSubTask_whenSetSubParentTaskAsEpic_thenSubParentTaskEqualEpicId() {
        subA.setParentId(epicA.getId());
        assertNotNull(subA.getParentId());
        assertEquals(subA.getParentId(), epicA.getId());
    }

    @Test
    void givenTwoSubWithSameId_whenIdEquals_thenSubEquals() {
        subA.setId(1);
        subB.setId(1);
        assertEquals(subA, subB);
    }
}