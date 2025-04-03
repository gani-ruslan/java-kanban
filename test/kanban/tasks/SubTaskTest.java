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
        subA = new SubTask("Task title A", "Task Description A");
        subB = new SubTask("Task title B", "Task Description B");
        epicA = new Epic("Epic task title", "Epic task description");
    }

    @Test
    void givenSubTask_whenSetSubParentTaskAsEpic_thenSubParentTaskNotNullandEqualEpicID() {
        subA.setParentTaskID(epicA.getID());
        assertNotNull(subA.getParentTaskID());
        assertEquals(subA.getParentTaskID(), epicA.getID());
    }

    @Test
    void givenTwoSubWithSameID_whenIDEquals_thenSubEquals() {
        subA.setID(1);
        subB.setID(1);
        assertEquals(subA, subB);
    }
}