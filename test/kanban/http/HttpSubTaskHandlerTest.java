package kanban.http;

import static kanban.tasks.TaskStatus.DONE;
import static kanban.tasks.TaskStatus.IN_PROGRESS;
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
import kanban.tasks.Epic;
import kanban.tasks.SubTask;
import org.junit.jupiter.api.Test;

public class HttpSubTaskHandlerTest extends HttpBaseTest {

    @Test
    public void shouldCreateSubtaskAndHandleConflicts() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic A", "Epic A description");
        taskManager.addEpic(epic);

        SubTask subtask = new SubTask(0, "Sub A", NEW, "Sub A description",
                epic.getId(), LocalDateTime.now(), Duration.ofMinutes(5));
        String subtaskJson = gson.toJson(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = taskClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(201, response.statusCode());

        List<SubTask> subtasks = taskManager.getSubList();
        assertNotNull(subtasks);
        assertEquals(1, subtasks.size());
        assertEquals("Sub A", subtasks.getFirst().getTitle());

        SubTask conflictSubtask = new SubTask(0, "Sub B", DONE, "Sub B description",
                epic.getId(), subtask.getStartTime(), Duration.ofMinutes(10));
        HttpRequest conflictRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(conflictSubtask)))
                .build();

        HttpResponse<String> conflictResponse = taskClient.send(
                conflictRequest,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(400, conflictResponse.statusCode());
    }

    @Test
    public void shouldReturn404ForInvalidEpicId() throws IOException, InterruptedException {
        SubTask subtask = new SubTask(0, "Sub A", NEW, "Sub A description",
                999, LocalDateTime.now(), Duration.ofMinutes(5));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                .build();

        HttpResponse<String> response = taskClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(404, response.statusCode());
    }

    @Test
    public void shouldUpdateSubtaskCorrectly() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic A", "Epic A description");
        taskManager.addEpic(epic);

        SubTask sub = new SubTask(0, "Sub A", NEW, "Sub A description",
                epic.getId(), LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.addSub(sub);

        SubTask update = new SubTask(sub.getId(), "Sub A update", IN_PROGRESS,
                "Sub A description update", epic.getId(),
                sub.getStartTime().plusHours(1), Duration.ofMinutes(30));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + sub.getId()))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(update)))
                .build();

        HttpResponse<String> response = taskClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(201, response.statusCode());

        SubTask actual = taskManager.getSubTaskById(sub.getId()).orElseThrow();
        assertEquals("Sub A update", actual.getTitle());
        assertEquals(IN_PROGRESS, actual.getStatus());
    }

    @Test
    public void shouldDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic A", "Epic A description");
        taskManager.addEpic(epic);

        SubTask sub = new SubTask(0, "Sub A", NEW, "Sub A description",
                epic.getId(), LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.addSub(sub);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + sub.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = taskClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, response.statusCode());
        assertTrue(taskManager.getSubTaskById(sub.getId()).isEmpty());
    }

    @Test
    public void shouldReturnSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic A", "Epic A description");
        taskManager.addEpic(epic);

        SubTask sub = new SubTask(0, "Sub A", NEW, "Sub A description",
                epic.getId(), LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.addSub(sub);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + sub.getId()))
                .GET()
                .build();

        HttpResponse<String> response = taskClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, response.statusCode());

        SubTask fetched = gson.fromJson(response.body(), SubTask.class);
        assertEquals(sub.getTitle(), fetched.getTitle());
        assertEquals(sub.getId(), fetched.getId());
    }

    @Test
    public void shouldReturn400OnInvalidId() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/subtasks/invalid-id");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();

        HttpResponse<String> response = taskClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(400, response.statusCode());
        assertEquals("Bad Request", response.body());
    }

    @Test
    public void shouldReturn404ForNonexistentSubtask() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/subtasks/999");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = taskClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(404, response.statusCode());
    }
}
