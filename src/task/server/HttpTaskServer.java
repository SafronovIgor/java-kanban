package task.server;

import com.sun.net.httpserver.HttpServer;
import task.service.Manager;
import task.service.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Objects;

public final class HttpTaskServer {
    private final HttpServer httpServer;

    public HttpTaskServer(final int socket) {
        ArrayList<Manager> managers = new ArrayList<>();
        managers.add(Managers.getDefaultHistoryManager());
        managers.add(Managers.getFileBackedTaskManager());

        try {
            this.httpServer = HttpServer.create(new InetSocketAddress(socket), 0);
            this.httpServer.createContext("/tasks", new TasksHandler<>(managers));
            this.httpServer.createContext("/subtask", new SubtaskHandler<>(managers));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void main(String[] args) {
        HttpTaskServer httpTaskServer = new HttpTaskServer(8080);
        httpTaskServer.start();
    }

    public void start() {
        httpServer.start();
    }
}
