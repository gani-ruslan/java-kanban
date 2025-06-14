package kanban.http;

import static kanban.tasks.TaskStatus.DONE;
import static kanban.tasks.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import kanban.tasks.Task;
import org.junit.jupiter.api.Test;

public class HttpTaskHandlerTest extends HttpBaseTest {

    @Test
    public void shouldAddTaskAndReturn201() throws IOException, InterruptedException {
        Task task = new Task(0, "Task A", NEW, "Task A description",
                LocalDateTime.now(), Duration.ofMinutes(5));
        String taskEncoded = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskEncoded))
                .build();

        HttpResponse<String> response = taskClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(201, response.statusCode());

        List<Task> tasks = taskManager.getTaskList();
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertEquals("Task A", tasks.getFirst().getTitle());
    }

    @Test
    public void shouldReturn404ForNullTask() throws IOException, InterruptedException {
        String nullTaskJson = gson.toJson(null);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(nullTaskJson))
                .build();

        HttpResponse<String> response = taskClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(404, response.statusCode());
    }

    @Test
    public void shouldUpdateTaskCorrectly() throws IOException, InterruptedException {
        LocalDateTime start = LocalDateTime.of(2025, 2, 7, 10, 0);
        Duration duration = Duration.ofMinutes(30);
        Task task = new Task(0, "Task A", NEW, "Task A description", start, duration);
        taskManager.addTask(task);

        Task taskUpdate = new Task(task.getId(), "Task A updated", DONE,
                "Task A update description", start.plusHours(1), duration);
        String updateEncoded = gson.toJson(taskUpdate);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + task.getId()))
                .POST(HttpRequest.BodyPublishers.ofString(updateEncoded))
                .build();

        HttpResponse<String> response = taskClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(201, response.statusCode());

        Task fetched = taskManager.getTaskById(task.getId()).orElseThrow();
        assertEquals("Task A updated", fetched.getTitle());
        assertEquals(DONE, fetched.getStatus());
    }

    @Test
    public void shouldDeleteTaskById() throws IOException, InterruptedException {
        Task task = new Task(0, "Task A", NEW, "Task A description",
                LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + task.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = taskClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, response.statusCode());
        assertTrue(taskManager.getTaskById(task.getId()).isEmpty());
    }

    @Test
    public void shouldReturnTaskById() throws IOException, InterruptedException {
        Task task = new Task(0, "Task A", NEW, "Task A description",
                LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + task.getId()))
                .GET()
                .build();

        HttpResponse<String> response = taskClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, response.statusCode());

        Task received = gson.fromJson(response.body(), Task.class);

        assertEquals(task.getTitle(), received.getTitle());
        assertEquals(task.getId(), received.getId());
    }

    @Test
    public void shouldReturn400OnInvalidId() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/invalid-id");

        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();

        HttpResponse<String> response = taskClient.send(
                deleteRequest,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(400, response.statusCode());
        assertEquals("Bad Request", response.body());
    }
}
