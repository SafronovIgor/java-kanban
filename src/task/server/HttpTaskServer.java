package task.server;

import com.sun.net.httpserver.HttpServer;
import task.service.Manager;
import task.service.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;

public final class HttpTaskServer {
    private final HttpServer httpServer;

    public HttpTaskServer(final int socket) {
        Manager[] array = new Manager[]{
                Managers.getDefaultHistoryManager(),
                Managers.getFileBackedTaskManager()
        };

        try {
            this.httpServer = HttpServer.create(new InetSocketAddress(socket), 0);
            this.httpServer.createContext("/tasks", new TasksHandler<>(array));
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
