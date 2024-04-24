package task.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import task.models.Epic;
import task.models.Subtask;
import task.models.Task;
import task.service.Manager;
import task.service.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class EpicHandler<T extends Manager> implements HttpHandler {
    private final List<T> managers;

    public EpicHandler(List<T> managers) {
        this.managers = managers;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "";
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] splitStrings = path.split("/");

        switch (method) {
            case "GET":
                if (splitStrings.length == 2) {
                    response = getEpics(exchange);
                } else if (splitStrings.length == 3) {
                    String id = splitStrings[2];
                    response = getEpicById(exchange, Integer.valueOf(id));
                } else {
                    String id = splitStrings[2];
                    response = getEpicSubtask(exchange, Integer.valueOf(id));
                }
                break;
            case "POST":
                Epic task = readTaskFromRequestBody(exchange);
                if (splitStrings.length == 2) {
                    response = createEpic(exchange, task);
                }
                break;
            case "DELETE":
                response = deleteEpic(
                        exchange,
                        readTaskFromRequestBody(exchange).getId());
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

    private String deleteEpic(HttpExchange exchange, int id) throws IOException {
        for (T manager : managers) {
            if (manager instanceof TaskManager) {
                ((TaskManager) manager).deleteSubtaskByID(id);
            }
        }
        exchange.sendResponseHeaders(200, 0);
        return "OK";
    }

    private String getEpicSubtask(HttpExchange exchange, Integer id) throws IOException {
        ArrayList<Subtask> listSubtaskByEpic = new ArrayList<>();

        for (T manager : managers) {
            if (manager instanceof TaskManager) {
                Epic epic = ((TaskManager) manager).getEpicsById(id);
                listSubtaskByEpic = ((TaskManager) manager).getListSubtaskByEpic(epic);
            }
        }

        return toJson(exchange, listSubtaskByEpic);
    }

    private String createEpic(HttpExchange exchange, Epic task) throws IOException {
        boolean taskCreated = false;
        boolean validationError = false;

        for (T manager : managers) {
            if (manager instanceof TaskManager) {
                try {
                    ((TaskManager) manager).addNewEpic(task);
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

    private String getEpicById(HttpExchange exchange, Integer id) throws IOException {
        Optional<Epic> taskOptional = managers.stream()
                .filter(manager -> manager instanceof TaskManager)
                .map(manager -> ((TaskManager) manager).getEpicsById(id))
                .filter(Objects::nonNull)
                .findFirst();

        return toJson(exchange, taskOptional.orElse(null));
    }

    private String getEpics(HttpExchange exchange) throws IOException {
        List<Task> tasks = managers.stream()
                .filter(manager -> manager instanceof TaskManager)
                .flatMap(manager -> ((TaskManager) manager).getAllEpics().stream())
                .collect(Collectors.toList());

        return toJson(exchange, tasks);
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

    private Epic readTaskFromRequestBody(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes());
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        return gson.fromJson(requestBody, Epic.class);
    }
}
