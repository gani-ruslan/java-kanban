package kanban.tasks;

import static kanban.tasks.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EpicTest {
    private Epic epicA;
    private Epic epicB;
    private SubTask subA;
    private SubTask subB;

    @BeforeEach
    void beforeEachTest() {
        epicA = new Epic("Epic A", "Description A");
        epicB = new Epic("Epic B", "Description B");
        subA = new SubTask("Sub A", "Description C");
        subB = new SubTask("Sub B", "Description D");
        epicA.setId(1);
        epicB.setId(2);
        subA.setId(3);
        subB.setId(4);
        epicA.addSubId(subA.getId());
        epicA.addSubId(subB.getId());
        epicB.addSubId(subA.getId());
        epicB.addSubId(subB.getId());
        subA.setStatus(NEW);
        subB.setStatus(NEW);
    }

    @Test
    void givenEpicTask_whenSettingItAsItsOwnSubtask_thenThrowException() {
        assertThrows(IllegalArgumentException.class, () -> epicA.addSubId(epicA.getId()));
    }

    @Test
    void givenEpicTask_whenNeedAddSubTask_thenEpicSubTaskListNotNull() {
        assertEquals(2, epicA.getSubIdList().size());
    }

    @Test
    void givenEpicTask_whenNeedAddSubTask_thenEpicsSubTaskHasSameId() {
        ArrayList<Integer> subTasksList = new ArrayList<>(epicA.getSubIdList());
        assertEquals(subA.getId(), subTasksList.get(0));
        assertEquals(subB.getId(), subTasksList.get(1));
    }

    @Test
    void givenTwoEpicWithSameId_whenIdEquals_thenEpicEquals() {
        epicA.setId(1);
        epicB.setId(1);
        assertEquals(epicA, epicB);
    }
}