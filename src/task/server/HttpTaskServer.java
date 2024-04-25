package task.server;

import com.sun.net.httpserver.HttpServer;
import task.service.Manager;
import task.service.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;

public final class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer httpServer;

    public HttpTaskServer() {
        ArrayList<Manager> managers = new ArrayList<>();
        managers.add(Managers.getDefaultHistoryManager());
        managers.add(Managers.getFileBackedTaskManager());

        try {
            this.httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
            this.httpServer.createContext("/tasks", new TasksHandler<>(managers));
            this.httpServer.createContext("/subtask", new SubtaskHandler<>(managers));
            this.httpServer.createContext("/epics", new EpicHandler<>(managers));
            this.httpServer.createContext("/history", new HistoryHandler<>(managers));
            this.httpServer.createContext("/prioritized", new PrioritizedHandler<>(managers));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void main(String[] args) {
        new HttpTaskServer().start();
    }

    public void start() {
        httpServer.start();
    }
}
