package kanban.api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import kanban.managers.ManagerSaveException;
import kanban.managers.TaskManager;
import kanban.tasks.Epic;
import kanban.tasks.SubTask;

/**
 * HTTP handler responsible for processing REST-ful operations for Epic entities.
 * Supports the following HTTP methods:
 * - GET /epics            → returns all epics
 * - GET /epics/{id}       → returns a specific epic
 * - GET /epics/{id}/subtasks → returns all subtasks of a specific epic
 * - POST /epics           → creates a new epic
 * - POST /epics/{id}      → updates an existing epic
 * - DELETE /epics/{id}    → deletes an existing epic
 */
public class EpicTaskHandler extends CrudHandler {

    /**
     * Constructs an EpicTaskHandler for handling requests on the "/epics" path.
     *
     * @param manager the task manager instance
     * @param gson    the Gson instance for JSON serialization
     */
    public EpicTaskHandler(TaskManager manager, Gson gson) {
        super(manager, gson, "epics");
    }

    /**
     * Handles GET requests for a specific epic by ID. If the URI ends with "/subtasks",
     * returns the list of subtasks for that epic instead.
     *
     * @param exchange the HTTP exchange
     * @param id       the ID of the epic
     */
    @Override
    protected void getById(HttpExchange exchange, int id) {
        try {
            Epic epic = manager.getEpicById(id)
                    .orElseThrow(() -> new NoSuchElementException("Epic with ID "
                            + id + " not found."));
            String response = gson.toJson(epic);
            sendText(exchange, response);
        } catch (IOException | ManagerSaveException e) {
            sendServerError(exchange);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    /**
     * Handles GET requests to retrieve all epics.
     *
     * @param exchange the HTTP exchange
     */
    @Override
    protected void getAll(HttpExchange exchange) {
        try {
            String response = gson.toJson(manager.getEpicList());
            sendText(exchange, response);
        } catch (IOException e) {
            sendServerError(exchange);
        }
    }

    /**
     * Handles GET requests to retrieve a linked sub-resource of an epic by its ID.
     * Method supports:
     * - /epics/{id}/subtasks → returns all subtasks linked to the specified epic
     *
     * @param exchange   the HTTP exchange
     * @param id         the ID of the epic
     * @param linkedType the sub-resource name (e.g., "subtasks")
     */
    @Override
    protected void getAllLinked(HttpExchange exchange, int id, String linkedType) {
        if (linkedType.equals("subtasks")) {
            try {
                Epic epic = manager.getEpicById(id)
                        .orElseThrow(() -> new NoSuchElementException("Epic with ID "
                                + id + " not found."));
                List<SubTask> subtasks = manager.getEpicSubTaskList(epic.getId())
                        .orElse(List.of());
                String response = gson.toJson(subtasks);
                sendText(exchange, response);
            } catch (NoSuchElementException e) {
                sendNotFound(exchange, e.getMessage());
            } catch (IOException e) {
                sendServerError(exchange);
            }
        } else {
            try {
                sendBadRequest(exchange);
            } catch (IOException e) {
                sendServerError(exchange);
            }
        }
    }

    /**
     * Handles POST requests to create a new epic.
     *
     * @param exchange the HTTP exchange
     */
    @Override
    protected void create(HttpExchange exchange) {
        try {
            String body = readRequestBody(exchange);
            Epic epic = gson.fromJson(body, Epic.class);
            manager.addEpic(epic);
            String response = "Epic with ID " + epic.getId() + " created successfully.";
            sendModified(exchange, response);
        } catch (IOException | ManagerSaveException e) {
            sendServerError(exchange);
        } catch (IllegalArgumentException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    /**
     * Handles POST requests to update an existing epic.
     * Validates that the epic ID in the body matches the path parameter.
     *
     * @param exchange the HTTP exchange
     * @param id       the ID of the epic to update
     */
    @Override
    protected void update(HttpExchange exchange, int id) {
        try {
            String body = readRequestBody(exchange);
            Epic epic = gson.fromJson(body, Epic.class);
            if (epic.getId() != id) {
                throw new NoSuchElementException("ID in request ("
                        + id + ") does not match epic ID (" + epic.getId() + ").");
            }
            manager.updateEpic(epic);
            String response = "Epic with ID " + epic.getId() + " updated successfully.";
            sendModified(exchange, response);
        } catch (IOException | ManagerSaveException e) {
            sendServerError(exchange);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (IllegalStateException e) {
            sendConflict(exchange, e.getMessage());
        }
    }

    /**
     * Handles DELETE requests to remove an epic by ID.
     *
     * @param exchange the HTTP exchange
     * @param id       the ID of the epic to delete
     */
    @Override
    protected void delete(HttpExchange exchange, int id) {
        try {
            manager.removeEpicById(id);
            String response = "Epic with ID " + id + " deleted.";
            sendText(exchange, response);
        } catch (IOException | ManagerSaveException e) {
            sendServerError(exchange);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }
}
