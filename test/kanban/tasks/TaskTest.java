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
        TaskA = new Task("Task title A", "Task description A");
        TaskB = new Task("Task title B", "Task description B");
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
        TaskA.setTitle("Task title A modified");
        assertEquals("Task title A modified", TaskA.getTitle());
    }

    @Test
    void givenDescription_whenSetNewDescription_thenChangeDescription() {
        TaskA.setDescription("Task description A modified");
        assertEquals("Task description A modified", TaskA.getDescription());
    }

    @Test
    void givenStatus_whenSetNewStatus_thenChangeStatus() {
        TaskA.setStatus(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, TaskA.getStatus());
    }
}