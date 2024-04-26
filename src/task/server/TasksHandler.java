package task.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import task.models.Task;
import task.service.Manager;
import task.service.TaskManager;

import java.io.IOException;
import java.util.List;

public class TasksHandler<T extends Manager> implements HttpHandler {
    private final List<T> managers;

    public TasksHandler(List<T> managers) {
        if (1 < managers.stream()
                .filter(m -> m instanceof TaskManager)
                .count()) {
            throw new RuntimeException("Кто-то передал больше одного менеджера для работы с тасками.");
        }
        this.managers = managers;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        BaseHttpHandler httpHandler = new BaseHttpHandler(exchange);
        String[] splitURI = httpHandler.getUriPath().split("/");

        switch (httpHandler.getMethod()) {
            case "GET":
                for (T manager : managers) {
                    if (manager instanceof TaskManager) {
                        TaskManager tm = (TaskManager) manager;
                        if (splitURI.length == 2) {
                            httpHandler.GETTasks(tm, Task.class);
                        } else {
                            int idTask = Integer.parseInt(splitURI[2]);
                            httpHandler.GETTaskById(tm, idTask, Task.class);
                        }
                    }
                }
                break;
            case "POST":
                for (T manager : managers) {
                    if (manager instanceof TaskManager) {
                        TaskManager tm = (TaskManager) manager;
                        if (splitURI.length == 2) {
                            httpHandler.POSTCreateTask(tm, Task.class);
                        } else {
                            int idTask = Integer.parseInt(splitURI[2]);
                            httpHandler.POSTUpdateTask(tm, idTask, Task.class);
                        }
                    }
                }
                break;
            case "DELETE":
                for (T manager : managers) {
                    if (manager instanceof TaskManager) {
                        TaskManager tm = (TaskManager) manager;
                        if (splitURI.length == 3) {
                            int idTask = Integer.parseInt(splitURI[2]);
                            httpHandler.DELETETask(tm, idTask, Task.class);
                        }
                    }
                }
                break;
            default:
                httpHandler.setResponseHeaders("Method not found.");
        }
    }

}
