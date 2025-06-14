package kanban.api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.NoSuchElementException;
import kanban.managers.ManagerSaveException;
import kanban.managers.TaskManager;
import kanban.managers.TaskTimeOverlapException;
import kanban.tasks.SubTask;

/**
 * HTTP handler responsible for processing REST-ful operations for SubTask entities.
 * Supported HTTP endpoints:
 * - GET     /subtasks           → returns all subtasks
 * - GET     /subtasks/{id}      → returns a specific subtask by ID
 * - POST    /subtasks           → creates a new subtask
 * - POST    /subtasks/{id}      → updates an existing subtask
 * - DELETE  /subtasks/{id}      → deletes a subtask by ID
 * Unsupported:
 * - GET     /subtasks/{id}/xxx  → always returns 400 Bad Request
 */
public class SubtaskHandler extends CrudHandler {

    /**
     * Constructs a SubtaskHandler for handling requests related to subtasks.
     *
     * @param manager the task manager instance
     * @param gson    the Gson instance for JSON serialization
     */
    public SubtaskHandler(TaskManager manager, Gson gson) {
        super(manager, gson, "subtasks");
    }

    @Override
    protected void getById(HttpExchange exchange, int id) {
        try {
            SubTask subtask = manager.getSubTaskById(id)
                    .orElseThrow(() -> new NoSuchElementException("Subtask with ID "
                            + id + " not found."));
            String response = gson.toJson(subtask);
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
            String response = gson.toJson(manager.getSubList());
            sendText(exchange, response);
        } catch (IOException e) {
            sendServerError(exchange);
        }
    }

    /**
     * This handler does not support any nested resources like /subtasks/{id}/xyz,
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
            SubTask sub = gson.fromJson(body, SubTask.class);
            manager.addSub(sub);
            String response = "Subtask with ID " + sub.getId() + " created successfully.";
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
    protected void update(HttpExchange exchange, int id) {
        try {
            String body = readRequestBody(exchange);
            SubTask sub = gson.fromJson(body, SubTask.class);
            if (sub.getId() != id) {
                throw new NoSuchElementException("ID in request ("
                        + id + ") does not match subtask ID (" + sub.getId() + ").");
            }
            manager.updateSub(sub);
            String response = "Subtask with ID " + sub.getId() + " updated successfully.";
            sendModified(exchange, response);
        } catch (IOException | ManagerSaveException e) {
            sendServerError(exchange);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (TaskTimeOverlapException e) {
            sendHasTimeOverlapping(exchange, e.getMessage());
        } catch (IllegalStateException e) {
            sendConflict(exchange, e.getMessage());
        }
    }

    @Override
    protected void delete(HttpExchange exchange, int id) {
        try {
            manager.removeSubById(id);
            String response = "Subtask with ID " + id + " deleted.";
            sendText(exchange, response);
        } catch (IOException | ManagerSaveException e) {
            sendServerError(exchange);
        } catch (NoSuchElementException | IllegalArgumentException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }
}
