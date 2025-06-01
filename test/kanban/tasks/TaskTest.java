package kanban.tasks;

import static kanban.tasks.TaskStatus.IN_PROGRESS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link Task} class verifying equality, title, description,
 * and status changes.
 */
public class TaskTest {

    private static Task taskA;
    private static Task taskB;

    @BeforeAll
    static void setUpOnce() {
        taskA = new Task("Task A", "Description A");
        taskB = new Task("Task B", "Description B");
    }

    @BeforeEach
    void setUp() {
        taskA.setId(1);
        taskB.setId(2);
    }

    /**
     * Verifies that two tasks with the same ID are considered equal.
     */
    @Test
    void shouldConsiderTasksEqualIfTheyHaveSameId() {
        taskA.setId(1);
        taskB.setId(1);
        assertEquals(taskA, taskB);
    }

    /**
     * Verifies that setting a new title updates the task title.
     */
    @Test
    void shouldUpdateTitleWhenSetNewTitle() {
        taskA.setTitle("Task A modified");
        assertEquals("Task A modified", taskA.getTitle());
    }

    /**
     * Verifies that setting a new description updates the task description.
     */
    @Test
    void shouldUpdateDescriptionWhenSetNewDescription() {
        taskA.setDescription("Description A modified");
        assertEquals("Description A modified", taskA.getDescription());
    }

    /**
     * Verifies that setting a new status updates the task status.
     */
    @Test
    void shouldUpdateStatusWhenSetNewStatus() {
        taskA.setStatus(IN_PROGRESS);
        assertEquals(IN_PROGRESS, taskA.getStatus());
    }
}
