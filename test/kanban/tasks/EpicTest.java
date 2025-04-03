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
        epicA.addSubTaskID(subA.getID());
        epicA.addSubTaskID(subB.getID());
        epicB.addSubTaskID(subA.getID());
        epicB.addSubTaskID(subB.getID());
        subA.setStatus(TaskStatus.NEW);
        subB.setStatus(TaskStatus.NEW);
    }

    @Test
    void givenEpicTask_whenNeedAddSubTask_thenEpicSubTaskListNotNull() {
        System.out.println(epicA.getSubTaskIDList());
        assertEquals(2, epicA.getSubTaskIDList().size());
    }

    @Test
    void givenEpicTask_whenNeedAddSubTask_thenEpicsSubTaskHasSameID() {
        ArrayList<Integer> subTasksList = new ArrayList<>(epicA.getSubTaskIDList());
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