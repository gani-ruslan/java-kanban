package kanban.http.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import kanban.api.adapters.GsonFactory;
import kanban.api.handlers.EpicTaskHandler;
import kanban.api.handlers.HistoryHandler;
import kanban.api.handlers.PrioritizedHandler;
import kanban.api.handlers.SubtaskHandler;
import kanban.api.handlers.TaskHandler;
import kanban.managers.FileBackedTaskManager;
import kanban.managers.InMemoryTaskManager;
import kanban.managers.ManagerSaveException;
import kanban.managers.TaskManager;

/**
 * HttpTaskServer sets up and runs an HTTP server that handles
 * incoming REST API requests related to tasks, subtasks, epics,
 * history, and prioritized tasks.
 * The server registers endpoint contexts and routes requests to
 * the appropriate handlers. It also provides basic logging and
 * JSON serialization configuration.
 */
public class TasksServer {

    /**
     * The port number on which the HTTP server will run.
     */
    private static final int PORT = 8080;

    /**
     * The HTTP server instance.
     */
    private static HttpServer httpServer;

    /**
     * Gson instance used for JSON serialization and deserialization.
     */
    private static final Gson gson = getGson();

    /**
     * Manager field for test purpose only.
     */
    static TaskManager manager;

    /**
     * Logger used for server runtime logging.
     */
    private static final Logger logger = Logger.getLogger(TasksServer.class.getName());

    static {
        configureLogger();
    }

    /**
     * Creates and initializes the HTTP server using the appropriate TaskManager implementation.
     * Defaults to InMemoryTaskManager for test purpose only.
     */
    public TasksServer(TaskManager manager) {
        TasksServer.manager = manager;
        try {
            httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
            httpServer.createContext("/tasks", new TaskHandler(manager, gson));
            httpServer.createContext("/subtasks", new SubtaskHandler(manager, gson));
            httpServer.createContext("/epics", new EpicTaskHandler(manager, gson));
            httpServer.createContext("/history", new HistoryHandler(manager, gson));
            httpServer.createContext("/prioritized", new PrioritizedHandler(manager, gson));
            logger.info("HTTP server successfully initialized on port " + PORT);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to initialize HTTP server on port " + PORT, e);
        }
    }

    /**
     * Creates and initializes the HTTP server using the appropriate TaskManager implementation.
     * If a non-empty file is provided, it uses FileBackedTaskManager; otherwise,
     * it defaults to InMemoryTaskManager.
     *
     * @param file the file used for persistent task storage
     */
    public TasksServer(File file) {
        try {
            TaskManager manager;
            if (file != null && file.exists() && file.length() > 0) {
                manager = FileBackedTaskManager.loadFromFile(file);
                logger.info("FileBackedTaskManager loaded from file: " + file.getAbsolutePath());
            } else {
                manager = new InMemoryTaskManager();
                logger.info("InMemoryTaskManager initialized (file is null,"
                        + " does not exist, or is empty).");
            }

            httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
            httpServer.createContext("/tasks", new TaskHandler(manager, gson));
            httpServer.createContext("/subtasks", new SubtaskHandler(manager, gson));
            httpServer.createContext("/epics", new EpicTaskHandler(manager, gson));
            httpServer.createContext("/history", new HistoryHandler(manager, gson));
            httpServer.createContext("/prioritized", new PrioritizedHandler(manager, gson));
            logger.info("HTTP server successfully initialized on port " + PORT);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to initialize HTTP server on port " + PORT, e);
            throw new RuntimeException("Unable to start HTTP server", e);
        } catch (ManagerSaveException e) {
            logger.log(Level.SEVERE, "Failed to load task manager from file", e);
            throw new RuntimeException("Failed to load task manager from file", e);
        }
    }

    /**
     * Starts the HTTP server and begins handling requests.
     */
    public static void start() {
        if (httpServer != null) {
            httpServer.start();
            logger.info("HTTP server started. Listening on port " + PORT);
        } else {
            logger.warning("Attempted to start HTTP server, but it was not properly initialized.");
        }
    }

    /**
     * Stops the HTTP server and releases the port.
     */
    public static void stop() {
        if (httpServer != null) {
            httpServer.stop(0);
            logger.info("HTTP server stopped. Port " + PORT + " released.");
        } else {
            logger.warning("Attempted to stop HTTP server, but it was not running.");
        }
    }

    /**
     * Creates and configures a Gson instance with pretty-printing enabled.
     *
     * @return a configured Gson object
     */
    public static Gson getGson() {
        return GsonFactory.createGson();
    }

    /**
     * Configures the logger to output all log levels to the console using a simple format.
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
