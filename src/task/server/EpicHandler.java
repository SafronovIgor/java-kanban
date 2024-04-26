package task.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import task.models.Epic;
import task.service.Manager;
import task.service.TaskManager;

import java.io.IOException;
import java.util.List;

public class EpicHandler<T extends Manager> implements HttpHandler {
    private final List<T> managers;

    public EpicHandler(List<T> managers) {
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
                            httpHandler.GETTasks(tm, Epic.class);
                        } else if (splitURI.length == 3) {
                            int idTask = Integer.parseInt(splitURI[2]);
                            httpHandler.GETTaskById(tm, idTask, Epic.class);
                        } else {
                            int idTask = Integer.parseInt(splitURI[2]);
                            httpHandler.GETEpicSubtask(tm, idTask);
                        }
                    }
                }
                break;
            case "POST":
                for (T manager : managers) {
                    if (manager instanceof TaskManager) {
                        TaskManager tm = (TaskManager) manager;
                        if (splitURI.length == 2) {
                            httpHandler.POSTCreateTask(tm, Epic.class);
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
                            httpHandler.DELETETask(tm, idTask, Epic.class);
                        }
                    }
                }
                break;
            default:
                httpHandler.setResponseHeaders("Method not found.");
        }
    }
}
