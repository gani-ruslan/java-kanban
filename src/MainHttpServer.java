import kanban.http.server.TasksServer;
import kanban.managers.Managers;

public class MainHttpServer {
    public static void main(String[] args) {
        TasksServer httpTaskServer = new TasksServer(Managers.getDefault());
        TasksServer.start();
    }
}
