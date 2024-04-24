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

public class PrioritizedHandler <T extends Manager> implements HttpHandler {
    private final List<T> managers;

    public PrioritizedHandler(List<T> managers) {
        this.managers = managers;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "";
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                for (T manager : managers) {
                    if (manager instanceof TaskManager) {
                        Set<Task> prioritizedTasks = ((TaskManager) manager).getPrioritizedTasks();
                        response = toJson(exchange, prioritizedTasks);
                    }
                }
                break;
            default:
                response = "Method not found.";
        }

        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(200, response.getBytes().length);
            os.write(response.getBytes());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private String toJson(HttpExchange exchange, Object obj) throws IOException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();

        String json = gson.toJson(obj);

        if (obj != null) {
            exchange.sendResponseHeaders(200, json.getBytes().length);
        } else {
            exchange.sendResponseHeaders(404, 0);
        }

        return json;
    }
}
