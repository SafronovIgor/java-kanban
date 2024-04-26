package task.server;

import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import task.models.Epic;
import task.models.Subtask;
import task.models.Task;
import task.service.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class BaseHttpHandler {
    private HttpExchange exchange;

    private String method;

    private String uriPath;

    public BaseHttpHandler(HttpExchange exchange) {
        this.method = exchange.getRequestMethod();
        this.uriPath = exchange.getRequestURI().getPath();
        this.exchange = exchange;
    }

    public <T extends TaskManager> void GETEpicSubtask(T manager, int id) throws IOException {
        Epic epic = manager.getEpicsById(id);
        setResponseHeaders(toJson(manager.getListSubtaskByEpic(epic)));
    }

    public <T extends TaskManager> void DELETETask(T manager, int id, Class<? extends Task> type) throws IOException {
        if (type == Task.class) {
            manager.deleteTaskByID(id);
        } else if (type == Epic.class) {
            manager.deleteEpicByID(id);
        } else if (type == Subtask.class) {
            manager.deleteSubtaskByID(id);
        }
        setResponseHeaders(toJson("OK"));
    }

    public <T extends TaskManager> void POSTUpdateTask(
            T manager,
            int id,
            Class<? extends Task> type) throws IOException {
        Task task = readTaskFromRequestBody(exchange, type);
        task.setId(id);
        if (type == Task.class) {
            manager.updateTask(task);
        } else if (type == Subtask.class) {
            manager.updateSubtask((Subtask) task);
        }
        setResponseHeaders(toJson("OK"));
    }

    public <T extends TaskManager> void POSTCreateTask(T manager, Class<? extends Task> type) throws IOException {
        try {
            if (type == Task.class) {
                manager.addNewTask(readTaskFromRequestBody(exchange, type));
            } else if (type == Epic.class) {
                manager.addNewEpic((Epic) readTaskFromRequestBody(exchange, type));
            } else if (type == Subtask.class) {
                Subtask task = (Subtask) readTaskFromRequestBody(exchange, type);
                ArrayList<Integer> idEpics = new ArrayList<>(task.getIdEpics());
                for (Integer idEpic : idEpics) {
                    manager.addNewSubtask(task, idEpic);
                }
            }
            setResponseHeaders(toJson("Created"));
        } catch (RuntimeException e) {
            setResponseHeaders(e.getMessage(), RuntimeException.class);
        }
    }

    public <T extends TaskManager> void GETTaskById(T manager, int id, Class<? extends Task> type) throws IOException {
        Task taskById = null;
        if (type == Task.class) {
            taskById = manager.getTaskById(id);
        } else if (type == Epic.class) {
            taskById = manager.getEpicsById(id);
        } else if (type == Subtask.class) {
            taskById = manager.getSubtaskById(id);
        }
        setResponseHeaders(toJson(taskById));
    }

    public <T extends TaskManager> void GETTasks(T manager, Class<? extends Task> type) throws IOException {
        ArrayList<? extends Task> tasks = null;
        if (type == Task.class) {
            tasks = manager.getAllTasks();
        } else if (type == Epic.class) {
            tasks = manager.getAllEpics();
        } else if (type == Subtask.class) {
            tasks = manager.getAllSubtasks();
        }
        setResponseHeaders(toJson(tasks));
    }

    public String toJson(Object obj) {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create()
                .toJson(obj);
    }

    public void setResponseHeaders(String json) throws IOException {
        setResponseHeaders(json, null);
    }

    public void setResponseHeaders(String json, Class<? extends Throwable> errorClass) throws IOException {
        byte[] jsonBytes = json.getBytes();

        if (errorClass != null) {
            exchange.sendResponseHeaders(406, jsonBytes.length);
        } else if (json.toLowerCase().contains("created")) {
            exchange.sendResponseHeaders(201, jsonBytes.length);
        } else if (json.isEmpty() || json.equals("null")) {
            exchange.sendResponseHeaders(404, jsonBytes.length);
        } else {
            exchange.sendResponseHeaders(200, jsonBytes.length);
        }

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(json.getBytes());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private Task readTaskFromRequestBody(HttpExchange exchange, Class<? extends Task> taskClass) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes());
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create()
                .fromJson(requestBody, taskClass);
    }

    public String getUriPath() {
        return uriPath;
    }

    public String getMethod() {
        return method;
    }
}
