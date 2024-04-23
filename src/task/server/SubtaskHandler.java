package task.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import task.models.Subtask;
import task.models.Task;
import task.service.Manager;
import task.service.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class SubtaskHandler <T extends Manager> implements HttpHandler {
    private final List<T> managers;

    public SubtaskHandler(List<T> managers) {
        this.managers = managers;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = null;
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] splitStrings = path.split("/");

        switch (method) {
            case "GET":
                if (splitStrings.length == 2) {
                    response = getSubtasks(exchange);
                } else {
                    String id = splitStrings[2];
                    response = getSubtaskById(exchange, Integer.valueOf(id));
                }
                break;
            case "POST":
                break;
            case "DELETE" :
                break;
            default:
                response = "Method not found.";
        }

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    private String toJson(HttpExchange exchange, Object obj) throws IOException {
        Gson gson = new GsonBuilder() //да дуьликат но на случай изменения условий десериализации.
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
        String json = gson.toJson(obj);

        if (obj != null) {
            exchange.sendResponseHeaders(200, json.getBytes().length);
        } else {
            exchange.sendResponseHeaders(404, 0);
        }

        return json;
    }

    private String getSubtasks(HttpExchange exchange) throws IOException {
        List<Subtask> subtasks = managers.stream()
                .filter(manager -> manager instanceof TaskManager)
                .flatMap(manager -> ((TaskManager) manager).getAllSubtasks().stream())
                .collect(Collectors.toList());

        return toJson(exchange, subtasks);
    }

    private String getSubtaskById(HttpExchange exchange, Integer id) throws IOException {
        Optional<Subtask> taskOptional = managers.stream()
                .filter(manager -> manager instanceof TaskManager)
                .map(manager -> ((TaskManager) manager).getSubtaskById(id))
                .filter(Objects::nonNull)
                .findFirst();

        return toJson(exchange, taskOptional.orElse(null));
    }
}
