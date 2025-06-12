package kanban.api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import kanban.managers.TaskManager;

/**
 * HTTP handler responsible for exposing the history of accessed tasks.
 * Supports:
 * - GET /history → returns the history of accessed tasks
 * All other requests (e.g., POST, DELETE, /history/{id}) will result in 400 Bad Request.
 */
public class HistoryHandler extends CrudHandler {

    public HistoryHandler(TaskManager manager, Gson gson) {
        super(manager, gson, "history");
    }

    @Override
    protected void getAll(HttpExchange exchange) {
        try {
            String response = gson.toJson(manager.getHistoryTask());
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
     * Sends a 400 Bad Request without throwing or logging IOException internally.
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
