package http.handler;

import com.sun.net.httpserver.HttpServer;
import service.Manager;
import service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private HttpServer server;
    private final TaskManager taskManager;

    public HttpTaskServer(TaskManager manager) {
        taskManager = manager;
    }

    public static void main(String[] args) {
        HttpTaskServer taskServer = new HttpTaskServer(Manager.getDefaults());
        taskServer.start();

    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/tasks", new TasksHandler(taskManager));
            server.createContext("/subtasks", new SubtasksHandler(taskManager));
            server.createContext("/epics", new EpicsHandler(taskManager));
            server.createContext("/history", new HistoryHandler(taskManager));
            server.createContext("/prioritized", new PrioritizedHandler(taskManager));
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }
}