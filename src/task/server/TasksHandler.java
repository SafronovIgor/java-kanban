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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class TasksHandler<T extends Manager> implements HttpHandler {
    private final List<T> managers;

    public TasksHandler(List<T> managers) {
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
                    response = getTasks(exchange);
                } else {
                    String id = splitStrings[2];
                    response = getTaskById(exchange, Integer.valueOf(id));
                }
                break;
            case "POST":
                Task task = readTaskFromRequestBody(exchange);
                if (splitStrings.length == 2) {
                    response = createTask(exchange, task);
                } else {
                    String id = splitStrings[2];
                    task.setId(Integer.parseInt(id));
                    response= updateTask(exchange, task);
                }
                break;
            case "DELETE" :
                if (splitStrings.length == 3) {
                    String id = splitStrings[2];
                    response = deleteTask(exchange, Integer.valueOf(id));
                }
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

    private String getTasks(HttpExchange exchange) throws IOException {
        List<Task> tasks = managers.stream()
                .filter(manager -> manager instanceof TaskManager)
                .flatMap(manager -> ((TaskManager) manager).getAllTasks().stream())
                .collect(Collectors.toList());

        return toJson(exchange, tasks);
    }

    private String getTaskById(HttpExchange exchange, Integer id) throws IOException {
        Optional<Task> taskOptional = managers.stream()
                .filter(manager -> manager instanceof TaskManager)
                .map(manager -> ((TaskManager) manager).getTaskById(id))
                .filter(Objects::nonNull)
                .findFirst();

        return toJson(exchange, taskOptional.orElse(null));
    }

    public String createTask(HttpExchange exchange, Task task) throws IOException {
        boolean taskCreated = false;
        boolean validationError = false;

        for (T manager : managers) {
            if (manager instanceof TaskManager) {
                try {
                    ((TaskManager) manager).addNewTask(task);
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

    private String toJson(HttpExchange exchange, Object obj) throws IOException {
        Gson gson = new GsonBuilder()
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

    private Task readTaskFromRequestBody(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes());
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        return gson.fromJson(requestBody, Task.class);
    }

    private String updateTask(HttpExchange exchange, Task task) throws IOException {
        boolean taskUpdated = false;
        boolean validationError = false;

        for (T manager : managers) {
            if (manager instanceof TaskManager) {
                try {
                    ((TaskManager) manager).updateTask(task);
                    taskUpdated = true;
                } catch (RuntimeException e) {
                    validationError = true;
                    break;
                }
            }
        }

        if (validationError) {
            exchange.sendResponseHeaders(406, 0);
            return "Error code: 406";
        } else if (taskUpdated) {
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
                ((TaskManager) manager).deleteTaskByID(id);
            }
        }
        exchange.sendResponseHeaders(200, 0);
        return "OK";
    }
}
