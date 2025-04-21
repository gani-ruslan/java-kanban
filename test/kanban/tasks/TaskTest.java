package kanban.tasks;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    private static Task TaskA;
    private static Task TaskB;

    @BeforeAll
    static void beforeAllTest() {
        TaskA = new Task("Task A", "Description A");
        TaskB = new Task("Task B", "Description B");
    }

    @BeforeEach
    void beforeEachTest() {
        TaskA.setID(1);
        TaskB.setID(2);
    }

    @Test
    void givenTwoTaskWithSameID_whenIDEquals_thenTaskEquals() {
        TaskA.setID(1);
        TaskB.setID(1);
        assertEquals(TaskA, TaskB);
    }

    @Test
    void givenName_whenSetNewName_thenChangeName() {
        TaskA.setTitle("Task A modified");
        assertEquals("Task A modified", TaskA.getTitle());
    }

    @Test
    void givenDescription_whenSetNewDescription_thenChangeDescription() {
        TaskA.setDescription("Description A modified");
        assertEquals("Description A modified", TaskA.getDescription());
    }

    @Test
    void givenStatus_whenSetNewStatus_thenChangeStatus() {
        TaskA.setStatus(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, TaskA.getStatus());
    }
}