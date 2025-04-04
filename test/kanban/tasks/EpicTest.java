package kanban.tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;


class EpicTest {
    private Epic epicA;
    private Epic epicB;
    private SubTask subA;
    private SubTask subB;

    @BeforeEach
    void beforeEachTest() {
        epicA = new Epic("Epic title A", "Epic description A");
        epicB = new Epic("Epic title B", "Epic description B");
        subA = new SubTask("Sub title A", "Sub description A");
        subB = new SubTask("Sub title B", "Sub description B");
        epicA.setID(1);
        epicB.setID(2);
        subA.setID(3);
        subB.setID(4);
        epicA.addSubID(subA.getID());
        epicA.addSubID(subB.getID());
        epicB.addSubID(subA.getID());
        epicB.addSubID(subB.getID());
        subA.setStatus(TaskStatus.NEW);
        subB.setStatus(TaskStatus.NEW);
    }

    @Test
    void givenEpicTask_whenSettingItAsItsOwnSubtask_thenThrowException() {
        assertThrows(IllegalArgumentException.class, () -> epicA.addSubID(epicA.getID()));
    }

    @Test
    void givenEpicTask_whenNeedAddSubTask_thenEpicSubTaskListNotNull() {
        System.out.println(epicA.getSubIDList());
        assertEquals(2, epicA.getSubIDList().size());
    }

    @Test
    void givenEpicTask_whenNeedAddSubTask_thenEpicsSubTaskHasSameID() {
        ArrayList<Integer> subTasksList = new ArrayList<>(epicA.getSubIDList());
        assertEquals(subA.getID(), subTasksList.get(0));
        assertEquals(subB.getID(), subTasksList.get(1));
    }

    @Test
    void givenTwoEpicWithSameID_whenIDEquals_thenEpicEquals() {
        epicA.setID(1);
        epicB.setID(1);
        assertEquals(epicA, epicB);
    }
}