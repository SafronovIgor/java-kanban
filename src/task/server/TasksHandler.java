package task.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import javafx.util.Pair;
import task.models.Task;
import task.service.Manager;
import task.service.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class TasksHandler <T extends Manager> implements HttpHandler {
    private final T[] managers;

    public TasksHandler(T[] managers) {
        this.managers = managers;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Pair<Integer, String> response;
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] splitStrings = path.split("/");

        switch(method) {
            case "GET":
                if (splitStrings.length == 2) {
                    response = getTasks();
                } else {
                    String id = splitStrings[2];
                    response = getTaskById(Integer.valueOf(id));
                }
                break;
            case "POST" :

            default:
                response = new Pair<>(404,"error");
        }

        exchange.sendResponseHeaders(response.getKey(), 0);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getValue().getBytes());
        }
    }

    private Pair<Integer, String> getTasks() {
        int statusResponse = 200;
        AtomicReference<String> response = new AtomicReference<>("");

        Arrays.stream(managers).forEach(manager -> {
            if (manager instanceof TaskManager) {
                ArrayList<Task> tasks = ((TaskManager) manager).getAllTasks();
                Gson gson = getGson();
                response.set(gson.toJson(tasks));
            }
        });

        if (response.get().isEmpty()) statusResponse = 404;

        return new Pair<>(statusResponse, response.get());
    }

    private Pair<Integer, String> getTaskById(Integer id) {
        int statusResponse = 200;
        AtomicReference<String> response = new AtomicReference<>("");

        Arrays.stream(managers).forEach(manager -> {
            if (manager instanceof TaskManager) {
                Task task = ((TaskManager) manager).getTaskById(id);
                Gson gson = getGson();
                String json = gson.toJson(task);
                response.set(json);
            }
        });

        if (response.get().isEmpty()) statusResponse = 404;

        return new Pair<>(statusResponse, response.get());
    }

    private Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
    }
}
