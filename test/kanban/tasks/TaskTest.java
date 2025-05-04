package kanban.tasks;

import static kanban.tasks.TaskStatus.IN_PROGRESS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class TaskTest {

    private static Task taskA;
    private static Task taskB;

    @BeforeAll
    static void beforeAllTest() {
        taskA = new Task("Task A", "Description A");
        taskB = new Task("Task B", "Description B");
    }

    @BeforeEach
    void beforeEachTest() {
        taskA.setId(1);
        taskB.setId(2);
    }

    @Test
    void givenTwoTaskWithSameId_whenIdEquals_thenTaskEquals() {
        taskA.setId(1);
        taskB.setId(1);
        assertEquals(taskA, taskB);
    }

    @Test
    void givenName_whenSetNewName_thenChangeName() {
        taskA.setTitle("Task A modified");
        assertEquals("Task A modified", taskA.getTitle());
    }

    @Test
    void givenDescription_whenSetNewDescription_thenChangeDescription() {
        taskA.setDescription("Description A modified");
        assertEquals("Description A modified", taskA.getDescription());
    }

    @Test
    void givenStatus_whenSetNewStatus_thenChangeStatus() {
        taskA.setStatus(IN_PROGRESS);
        assertEquals(IN_PROGRESS, taskA.getStatus());
    }
}