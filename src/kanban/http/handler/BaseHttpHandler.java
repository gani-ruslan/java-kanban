package kanban.http.handler;

import static kanban.http.endpoints.Endpoints.DELETE;
import static kanban.http.endpoints.Endpoints.GET;
import static kanban.http.endpoints.Endpoints.POST;
import static kanban.http.endpoints.Endpoints.UNKNOWN;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import kanban.http.endpoints.Endpoints;
import kanban.managers.TaskManager;

/**
 * BaseHttpHandler is the abstract foundation for all HTTP request handlers.
 * It provides utility methods for writing HTTP responses, handling errors,
 * determining the HTTP method, and configuring logging.
 */
public class BaseHttpHandler implements HttpHandler {

    /**
     * Default charset used for encoding HTTP responses.
     */
    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    /**
     * The task manager instance used for processing business logic.
     */
    protected final TaskManager manager;

    /**
     * Gson instance for JSON serialization and deserialization.
     */
    protected final Gson gson;

    /**
     * Logger instance for logging handler activity.
     */
    protected static final Logger logger = Logger.getLogger(BaseHttpHandler.class.getName());

    static {
        configureLogger();
    }

    /**
     * Constructs a new BaseHttpHandler with the given manager and gson.
     *
     * @param manager the task manager
     * @param gson the Gson instance
     */
    public BaseHttpHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    /**
     * Default implementation of handle does nothing.
     * Subclasses should override this method to handle HTTP requests.
     *
     * @param exchange the HTTP exchange object
     */
    @Override
    public void handle(HttpExchange exchange) {
        // To be overridden by subclasses
    }

    /**
     * Sends a 200 OK response with a JSON text body.
     *
     * @param h the HttpExchange
     * @param text the response body text
     * @throws IOException if an I/O error occurs
     */
    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] response = text.getBytes(DEFAULT_CHARSET);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        try (OutputStream os = h.getResponseBody()) {
            h.sendResponseHeaders(200, response.length);
            os.write(response);
        }
    }

    /**
     * Sends a 400 Bad Request response.
     *
     * @param h the HttpExchange
     * @throws IOException if an I/O error occurs
     */
    protected void sendBadRequest(HttpExchange h) throws IOException {
        byte[] response = "Bad Request".getBytes(DEFAULT_CHARSET);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        try (OutputStream os = h.getResponseBody()) {
            h.sendResponseHeaders(400, response.length);
            os.write(response);
        }
    }

    /**
     * Sends a 201 Created response with a JSON text body.
     *
     * @param h the HttpExchange
     * @param text the response body text
     * @throws IOException if an I/O error occurs
     */
    protected void sendModified(HttpExchange h, String text) throws IOException {
        byte[] response = text.getBytes(DEFAULT_CHARSET);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(201, response.length);
        try (OutputStream os = h.getResponseBody()) {
            os.write(response);
        }
    }

    /**
     * Sends a 404 Not Found response with a message.
     *
     * @param h the HttpExchange
     * @param text the error message
     */
    protected void sendNotFound(HttpExchange h, String text) {
        byte[] response = text.getBytes(DEFAULT_CHARSET);
        try (OutputStream os = h.getResponseBody()) {
            h.sendResponseHeaders(404, response.length);
            os.write(response);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to send response: 404 Not Found", e);
        }
    }

    /**
     * Sends a 400 Bad request response indicating overlapping time.
     *
     * @param h the HttpExchange
     * @param text the error message
     */
    protected void sendHasTimeOverlapping(HttpExchange h, String text) {
        byte[] response = text.getBytes(DEFAULT_CHARSET);
        try (OutputStream os = h.getResponseBody()) {
            h.sendResponseHeaders(400, response.length);
            os.write(response);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to send response: 400 Bad Request", e);
        }
    }

    /**
     * Sends a 409 Conflict response.
     *
     * @param h the HttpExchange
     * @param message the error message
     */
    protected void sendConflict(HttpExchange h, String message) {
        byte[] response = message.getBytes(DEFAULT_CHARSET);
        try (OutputStream os = h.getResponseBody()) {
            h.sendResponseHeaders(409, response.length);
            os.write(response);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to send response: 409 Conflict", e);
        }
    }

    /**
     * Sends a 500 Internal Server Error response.
     *
     * @param h the HttpExchange
     */
    protected void sendServerError(HttpExchange h) {
        byte[] response = "Server Error".getBytes(DEFAULT_CHARSET);
        try (OutputStream os = h.getResponseBody()) {
            h.sendResponseHeaders(500, response.length);
            os.write(response);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to send response: 500 Server Error", e);
        }
    }

    /**
     * Maps a request method string to an Endpoints enum value.
     *
     * @param requestMethod the HTTP method string (e.g. GET, POST)
     * @return the corresponding Endpoints enum value
     */
    protected Endpoints getEndpointMethod(String requestMethod) {
        return switch (requestMethod) {
            case "GET" -> GET;
            case "POST" -> POST;
            case "DELETE" -> DELETE;
            default -> UNKNOWN;
        };
    }

    /**
     * Configures the logger to write to the console with all log levels.
     */
    private static void configureLogger() {
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.ALL);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        handler.setFormatter(new SimpleFormatter());
        logger.addHandler(handler);
    }
}
