package kanban.http;

import com.google.gson.Gson;
import java.net.http.HttpClient;
import kanban.http.server.TasksServer;
import kanban.managers.Managers;
import kanban.managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class HttpBaseTest {

    protected TaskManager taskManager;
    protected Gson gson;
    protected TasksServer taskServer;
    protected HttpClient taskClient;

    @BeforeEach
    public void setUpBase() {
        taskManager = Managers.getDefault();
        gson = TasksServer.getGson();
        taskServer = new TasksServer(taskManager);
        taskClient = HttpClient.newHttpClient();
        taskManager.removeAllTask();
        taskManager.removeAllEpic();
        TasksServer.start();
    }

    @AfterEach
    public void tearDownBase() {
        TasksServer.stop();
        taskClient = null;
        taskServer = null;
        taskManager = null;
    }
}
