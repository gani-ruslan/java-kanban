package kanban.api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import kanban.http.endpoints.Endpoints;
import kanban.http.handler.BaseHttpHandler;
import kanban.managers.TaskManager;

/**
 * Abstract base class for HTTP handlers that support CRUD operations.
 * This handler provides standard routing logic for GET, POST, and DELETE requests
 * based on URI patterns, and delegates specific behavior to subclasses.
 */
public abstract class CrudHandler extends BaseHttpHandler {

    private final Pattern pattern;

    /**
     * Constructs a new AbstractCrudHandler for a given entity type.
     *
     * @param manager     the task manager used to perform operations
     * @param gson        the Gson instance used for JSON serialization/deserialization
     * @param entityPath  the base URI path associated with the entity (e.g., "tasks")
     */
    public CrudHandler(TaskManager manager, Gson gson, String entityPath) {
        super(manager, gson);
        this.pattern = Pattern.compile("^/" + entityPath + "(?:/([^/]+))?(?:/([a-z]+))?$");
    }

    /**
     * Handles an incoming HTTP request by routing it based on method and URI pattern.
     *
     * @param exchange the HTTP exchange object
     */
    @Override
    public void handle(HttpExchange exchange) {
        try {
            String path = exchange.getRequestURI().getPath();
            Matcher matcher = pattern.matcher(path);
            Endpoints method = getEndpointMethod(exchange.getRequestMethod());

            if (!matcher.matches() || method == Endpoints.UNKNOWN) {
                sendBadRequest(exchange);
                return;
            }

            int id = 0;
            Optional<String> idOpt = Optional.ofNullable(matcher.group(1));
            Optional<String> allLinksOpt = Optional.ofNullable(matcher.group(2));

            if (idOpt.isPresent()) {
                try {
                    id = Integer.parseInt(idOpt.get());
                } catch (NumberFormatException e) {
                    sendBadRequest(exchange);
                    return;
                }
            }

            switch (method) {
                case GET -> {
                    if (idOpt.isPresent() && allLinksOpt.isPresent()) {
                        getAllLinked(exchange, id, allLinksOpt.get());
                    } else if (idOpt.isPresent()) {
                        getById(exchange, id);
                    } else {
                        getAll(exchange);
                    }
                }
                case POST -> {
                    if (idOpt.isPresent()) {
                        update(exchange, id);
                    } else {
                        create(exchange);
                    }
                }
                case DELETE -> {
                    if (idOpt.isPresent()) {
                        delete(exchange, id);
                    } else {
                        sendBadRequest(exchange);
                    }
                }
                default -> sendBadRequest(exchange);
            }

        } catch (Exception e) {
            sendServerError(exchange);
        }
    }

    /**
     * Reads the request body from the HTTP exchange.
     *
     * @param exchange the HTTP exchange object
     * @return the request body as a UTF-8 string
     * @throws IOException if an I/O error occurs
     */
    protected String readRequestBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        return new String(is.readAllBytes(), DEFAULT_CHARSET);
    }

    /**
     * Handles a GET request for a single entity by its ID.
     *
     * @param exchange the HTTP exchange object
     * @param id       the ID of the entity to retrieve
     */
    protected abstract void getById(HttpExchange exchange, int id);

    /**
     * Handles a GET request for all entities of this type.
     *
     * @param exchange the HTTP exchange object
     */
    protected abstract void getAll(HttpExchange exchange);


    /**
     * Handles a GET request for all linked entities of this type.
     *
     * @param exchange the HTTP exchange object
     */
    protected abstract void getAllLinked(HttpExchange exchange, int id, String linkedType);

    /**
     * Handles a POST request to create a new entity.
     *
     * @param exchange the HTTP exchange object
     */
    protected abstract void create(HttpExchange exchange);

    /**
     * Handles a POST request to update an existing entity by its ID.
     *
     * @param exchange the HTTP exchange object
     * @param id       the ID of the entity to update
     */
    protected abstract void update(HttpExchange exchange, int id);

    /**
     * Handles a DELETE request to remove an entity by its ID.
     *
     * @param exchange the HTTP exchange object
     * @param id       the ID of the entity to delete
     */
    protected abstract void delete(HttpExchange exchange, int id);
}
