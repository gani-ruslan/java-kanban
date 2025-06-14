package kanban.http;

import static kanban.tasks.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import kanban.tasks.Epic;
import kanban.tasks.SubTask;
import kanban.tasks.Task;
import org.junit.jupiter.api.Test;

public class HttpPrioritizedHandlerTest extends HttpBaseTest {

    @Test
    public void shouldReturnPrioritizedTasksInCorrectOrder()
            throws IOException, InterruptedException {

        LocalDateTime start = LocalDateTime.of(2025, 2, 7, 10, 0);
        Duration duration = Duration.ofMinutes(30);

        Task taskA = new Task(0, "Task A", NEW, "Task A description",
                start, duration);
        Task taskB = new Task(0, "Task B", NEW, "Task B description",
                start.plusMinutes(30), duration);
        Task taskC = new Task(0, "Task C", NEW, "Task C description",
                start.plusMinutes(60), duration);
        Task taskD = new Task("Task D", "Task D description");
        taskManager.addTask(taskA);
        taskManager.addTask(taskB);
        taskManager.addTask(taskC);
        taskManager.addTask(taskD);

        Epic epicA = new Epic("Epic A", "Epic A description");
        Epic epicB = new Epic("Epic B", "Epic B description");
        taskManager.addEpic(epicA);
        taskManager.addEpic(epicB);

        SubTask subA = new SubTask(0, "Sub A", NEW, "Sub A description",
                epicA.getId(), start.plusMinutes(90), duration);
        SubTask subB = new SubTask(0, "Sub B", NEW, "Sub B description",
                epicA.getId(), start.plusMinutes(120), duration);
        SubTask subC = new SubTask(0, "Sub C", NEW, "Sub C description",
                epicB.getId(), start.plusMinutes(150), duration);
        taskManager.addSub(subA);
        taskManager.addSub(subB);
        taskManager.addSub(subC);

        List<Task> expected = taskManager.getPrioritizedTasks();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = taskClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, response.statusCode());

        Task[] returned = gson.fromJson(response.body(), Task[].class);
        assertEquals(expected.size(), returned.length);
        assertEquals(expected.getFirst(), returned[0]);
    }

    @Test
    public void shouldReturnEmptyListWhenNoTasksExist() throws IOException, InterruptedException {

        taskManager.removeAllTask();
        taskManager.removeAllEpic();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = taskClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, response.statusCode());

        Task[] returned = gson.fromJson(response.body(), Task[].class);
        assertEquals(0, returned.length);
    }

    @Test
    public void shouldReturn400OnInvalidPathOrMethod() throws IOException, InterruptedException {
        URI badPath = URI.create("http://localhost:8080/prioritized/invalid");
        HttpRequest requestA = HttpRequest.newBuilder()
                .uri(badPath)
                .GET()
                .build();
        HttpResponse<String> responseA = taskClient.send(
                requestA,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(400, responseA.statusCode());

        // Invalid method
        URI correctPath = URI.create("http://localhost:8080/prioritized");
        HttpRequest requestB = HttpRequest.newBuilder()
                .uri(correctPath)
                .DELETE()
                .build();
        HttpResponse<String> responseB = taskClient.send(
                requestB,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(400, responseB.statusCode());
    }
}
