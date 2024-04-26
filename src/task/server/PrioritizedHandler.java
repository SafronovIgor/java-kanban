package task.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import task.models.Task;
import task.service.Manager;
import task.service.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class PrioritizedHandler<T extends Manager> implements HttpHandler {
    private final List<T> managers;

    public PrioritizedHandler(List<T> managers) {
        this.managers = managers;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        BaseHttpHandler httpHandler = new BaseHttpHandler(exchange);

        switch (exchange.getRequestMethod()) {
            case "GET":
                for (T manager : managers) {
                    if (manager instanceof TaskManager) {
                        Set<Task> prioritizedTasks = ((TaskManager) manager).getPrioritizedTasks();
                        httpHandler.setResponseHeaders(
                                httpHandler.toJson(prioritizedTasks)
                        );
                    }
                }
                break;
            default:
                httpHandler.setResponseHeaders("Method not found.");
        }
    }
}
