package kanban.api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import kanban.managers.TaskManager;

/**
 * HTTP handler responsible for exposing the prioritized list of all tasks.
 * Supports:
 * - GET /prioritized → returns the prioritized task list
 * All other requests (e.g., POST, DELETE, /prioritized/{id}) will result in 400 Bad Request.
 */
public class PrioritizedHandler extends CrudHandler {

    public PrioritizedHandler(TaskManager manager, Gson gson) {
        super(manager, gson, "prioritized");
    }

    @Override
    protected void getAll(HttpExchange exchange) {
        try {
            String response = gson.toJson(manager.getPrioritizedTasks());
            sendText(exchange, response);
        } catch (IOException e) {
            sendServerError(exchange);
        }
    }

    @Override
    protected void getById(HttpExchange exchange, int id) {
        sendBadRequestSilently(exchange);
    }

    @Override
    protected void create(HttpExchange exchange) {
        sendBadRequestSilently(exchange);
    }

    @Override
    protected void update(HttpExchange exchange, int id) {
        sendBadRequestSilently(exchange);
    }

    @Override
    protected void delete(HttpExchange exchange, int id) {
        sendBadRequestSilently(exchange);
    }

    @Override
    protected void getAllLinked(HttpExchange exchange, int id, String linkedType) {
        sendBadRequestSilently(exchange);
    }

    /**
     * Sends a 400 Bad Request without propagating IOException.
     *
     * @param exchange the HTTP exchange object
     */
    private void sendBadRequestSilently(HttpExchange exchange) {
        try {
            sendBadRequest(exchange);
        } catch (IOException e) {
            sendServerError(exchange);
        }
    }
}
