package kanban.http.endpoints;

/**
 * Enum representing supported HTTP endpoints in the task manager API.
 * This enum is used to map incoming HTTP request methods
 * to internal endpoint types for further processing in request handlers.
 */
public enum Endpoints {

    /**
     * Represents an HTTP GET request.
     */
    GET,

    /**
     * Represents an HTTP POST request.
     */
    POST,

    /**
     * Represents an HTTP DELETE request.
     */
    DELETE,

    /**
     * Represents an unknown or unsupported HTTP method.
     */
    UNKNOWN
}
