package kanban.api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.NoSuchElementException;
import kanban.managers.ManagerSaveException;
import kanban.managers.TaskManager;
import kanban.managers.TaskTimeOverlapException;
import kanban.tasks.Task;

/**
 * HTTP handler responsible for processing REST-ful operations for Task entities.
 * Supported HTTP endpoints:
 * - GET     /tasks           → returns all tasks
 * - GET     /tasks/{id}      → returns a specific task by ID
 * - POST    /tasks           → creates a new task
 * - POST    /tasks/{id}      → updates an existing task
 * - DELETE  /tasks/{id}      → deletes a task by ID
 * Unsupported:
 * - GET     /tasks/{id}/xxx  → always returns 400 Bad Request
 */
public class TaskHandler extends CrudHandler {

    /**
     * Constructs a TaskHandler to handle requests related to tasks.
     *
     * @param manager the task manager instance
     * @param gson    the Gson instance for JSON serialization
     */
    public TaskHandler(TaskManager manager, Gson gson) {
        super(manager, gson, "tasks");
    }

    @Override
    protected void getById(HttpExchange exchange, int id) {
        try {
            Task task = manager.getTaskById(id)
                    .orElseThrow(() -> new NoSuchElementException("Task with ID "
                            + id + " not found."));
            String response = gson.toJson(task);
            sendText(exchange, response);
        } catch (NoSuchElementException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (IOException | ManagerSaveException e) {
            sendServerError(exchange);
        }
    }

    @Override
    protected void getAll(HttpExchange exchange) {
        try {
            String response = gson.toJson(manager.getTaskList());
            sendText(exchange, response);
        } catch (IOException e) {
            sendServerError(exchange);
        }
    }

    /**
     * This handler does not support any nested resources like /tasks/{id}/xyz,
     * so it always returns 400 Bad Request.
     */
    @Override
    protected void getAllLinked(HttpExchange exchange, int id, String linkedType) {
        try {
            sendBadRequest(exchange);
        } catch (IOException e) {
            sendServerError(exchange);
        }
    }

    @Override
    protected void create(HttpExchange exchange) {
        try {
            String body = readRequestBody(exchange);
            Task task = gson.fromJson(body, Task.class);
            manager.addTask(task);
            String response = "Task with ID " + task.getId() + " created successfully.";
            sendModified(exchange, response);
        } catch (IOException | ManagerSaveException e) {
            sendServerError(exchange);
        } catch (IllegalArgumentException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (TaskTimeOverlapException e) {
            sendHasTimeOverlapping(exchange, e.getMessage());
        }
    }

    @Override
    protected void update(HttpExchange exchange, int id) {
        try {
            String body = readRequestBody(exchange);
            Task task = gson.fromJson(body, Task.class);
            manager.updateTask(task);
            String response = "Task with ID " + id + " updated successfully.";
            sendModified(exchange, response);
        } catch (IOException | ManagerSaveException e) {
            sendServerError(exchange);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (TaskTimeOverlapException e) {
            sendHasTimeOverlapping(exchange, e.getMessage());
        }
    }

    @Override
    protected void delete(HttpExchange exchange, int id) {
        try {
            manager.removeTaskById(id);
            String response = "Task with ID " + id + " deleted.";
            sendText(exchange, response);
        } catch (IOException | ManagerSaveException e) {
            sendServerError(exchange);
        } catch (NoSuchElementException | IllegalArgumentException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }
}
