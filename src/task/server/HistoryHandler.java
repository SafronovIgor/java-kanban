package task.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import task.models.Task;
import task.service.Manager;
import task.service.TaskManager;

import java.io.IOException;
import java.util.List;

public class HistoryHandler<T extends Manager> implements HttpHandler {
    private final List<T> managers;

    public HistoryHandler(List<T> managers) {
        this.managers = managers;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        BaseHttpHandler httpHandler = new BaseHttpHandler(exchange);

        switch (exchange.getRequestMethod()) {
            case "GET":
                for (T manager : managers) {
                    if (manager instanceof TaskManager) {
                        List<Task> history = ((TaskManager) manager).getHistory();
                        httpHandler.setResponseHeaders(
                                httpHandler.toJson(history)
                        );
                    }
                }
                break;
            default:
                httpHandler.setResponseHeaders("Method not found.");
        }
    }
}
