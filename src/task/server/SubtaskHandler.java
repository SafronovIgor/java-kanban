package task.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import task.models.Subtask;
import task.service.Manager;
import task.service.TaskManager;

import java.io.IOException;
import java.util.List;

public class SubtaskHandler<T extends Manager> implements HttpHandler {
    private final List<T> managers;

    public SubtaskHandler(List<T> managers) {
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
                            httpHandler.GETTasks(tm, Subtask.class);
                        } else {
                            int idTask = Integer.parseInt(splitURI[2]);
                            httpHandler.GETTaskById(tm, idTask, Subtask.class);
                        }
                    }
                }
                break;
            case "POST":
                for (T manager : managers) {
                    if (manager instanceof TaskManager) {
                        TaskManager tm = (TaskManager) manager;
                        if (splitURI.length == 2) {
                            httpHandler.POSTCreateTask(tm, Subtask.class);
                        } else {
                            int idTask = Integer.parseInt(splitURI[2]);
                            httpHandler.POSTUpdateTask(tm, idTask, Subtask.class);
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
                            httpHandler.DELETETask(tm, idTask, Subtask.class);
                        }
                    }
                }
                break;
            default:
                httpHandler.setResponseHeaders("Method not found.");
        }
    }
}
