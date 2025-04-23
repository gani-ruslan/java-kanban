package kanban.tasks;

import static kanban.tasks.TaskStatus.IN_PROGRESS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


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
        TaskA.setId(1);
        TaskB.setId(2);
    }

    @Test
    void givenTwoTaskWithSameId_whenIdEquals_thenTaskEquals() {
        TaskA.setId(1);
        TaskB.setId(1);
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
        TaskA.setStatus(IN_PROGRESS);
        assertEquals(IN_PROGRESS, TaskA.getStatus());
    }
}