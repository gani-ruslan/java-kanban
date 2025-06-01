package kanban.tasks;

import static kanban.tasks.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link Epic} class to verify subtask handling,
 * equality behavior, and constraints.
 */
public class EpicTest {

    private Epic epicA;
    private Epic epicB;
    private SubTask subA;
    private SubTask subB;

    @BeforeEach
    void setUp() {
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

    /**
     * Verifies that an epic cannot reference itself as a subtask.
     */
    @Test
    void shouldThrowWhenEpicAddedAsItsOwnSubtask() {
        assertThrows(IllegalArgumentException.class, () -> epicA.addSubId(epicA.getId()));
    }

    /**
     * Verifies that subtasks are added to the epic and stored correctly.
     */
    @Test
    void shouldContainSubtasksAfterAddingThem() {
        assertEquals(2, epicA.getSubIdList().size());
    }

    /**
     * Verifies that the stored subtask IDs match the added subtask IDs.
     */
    @Test
    void shouldStoreCorrectSubtaskIdsInOrder() {
        ArrayList<Integer> subTasksList = new ArrayList<>(epicA.getSubIdList());
        assertEquals(subA.getId(), subTasksList.get(0));
        assertEquals(subB.getId(), subTasksList.get(1));
    }

    /**
     * Verifies that two epics with the same ID are considered equal.
     */
    @Test
    void shouldConsiderEpicsEqualIfTheyHaveSameId() {
        epicA.setId(1);
        epicB.setId(1);
        assertEquals(epicA, epicB);
    }
}
