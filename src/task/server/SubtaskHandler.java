package task.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import task.models.Subtask;
import task.service.Manager;
import task.service.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class SubtaskHandler<T extends Manager> implements HttpHandler {
    private final List<T> managers;

    public SubtaskHandler(List<T> managers) {
        this.managers = managers;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        AtomicReference<String> response = new AtomicReference<>("");
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] splitStrings = path.split("/");

        switch (method) {
            case "GET":
                if (splitStrings.length == 2) {
                    response.set(getSubtasks(exchange));
                } else {
                    String id = splitStrings[2];
                    response.set(getSubtaskById(exchange, Integer.valueOf(id)));
                }
                break;
            case "POST":
                Subtask task = readTaskFromRequestBody(exchange);
                if (splitStrings.length == 2) {
                    response.set(createSubtask(exchange, task, task.getIdEpics()));
                } else {
                    String id = splitStrings[2];
                    task.setId(Integer.parseInt(id));
                    response.set(updateSubtask(exchange, task));
                }
                break;
            case "DELETE":
                response.set(deleteTask(
                        exchange,
                        readTaskFromRequestBody(exchange).getId()
                ));
                break;
            default:
                response.set("Method not found.");
        }

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.get().getBytes());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    private Subtask readTaskFromRequestBody(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes());
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        return gson.fromJson(requestBody, Subtask.class);
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

    private String createSubtask(HttpExchange exchange, Subtask task, ArrayList<Integer> idEpics) throws IOException {
        boolean taskCreated = false;
        boolean validationError = false;

        for (T manager : managers) {
            if (manager instanceof TaskManager) {
                try {
                    idEpics.forEach(integer -> ((TaskManager) manager).addNewSubtask(task, integer));
                    taskCreated = true;
                } catch (RuntimeException e) {
                    validationError = true;
                    break;
                }
            }
        }

        if (validationError) {
            exchange.sendResponseHeaders(406, 0);
            return "Error code: 406";
        } else if (taskCreated) {
            exchange.sendResponseHeaders(201, 0);
            return "OK";
        } else {
            exchange.sendResponseHeaders(404, 0);
            return "Error code: 404";
        }
    }

    private String updateSubtask(HttpExchange exchange, Subtask task) throws IOException {
        boolean taskCreated = false;
        boolean validationError = false;

        for (T manager : managers) {
            if (manager instanceof TaskManager) {
                try {
                    ((TaskManager) manager).updateSubtask(task);
                    taskCreated = true;
                } catch (RuntimeException e) {
                    validationError = true;
                    break;
                }
            }
        }

        if (validationError) {
            exchange.sendResponseHeaders(406, 0);
            return "Error code: 406";
        } else if (taskCreated) {
            exchange.sendResponseHeaders(201, 0);
            return "OK";
        } else {
            exchange.sendResponseHeaders(404, 0);
            return "Error code: 404";
        }
    }

    private String deleteTask(HttpExchange exchange, Integer id) throws IOException {
        for (T manager : managers) {
            if (manager instanceof TaskManager) {
                ((TaskManager) manager).deleteSubtaskByID(id);
            }
        }
        exchange.sendResponseHeaders(200, 0);
        return "OK";
    }
}
