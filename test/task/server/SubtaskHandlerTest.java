package task.server;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.models.*;
import task.service.FileBackedTaskManager;
import task.service.InMemoryHistoryManager;
import task.service.Manager;
import task.service.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskHandlerTest {
    private static final InMemoryHistoryManager historyManager = Managers.getDefaultHistoryManager();
    private static final FileBackedTaskManager manager = Managers.getFileBackedTaskManager();
    private static final HttpServer httpServer;

    static {
        try {
            httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    public static void BeforeAll() {
        ArrayList<Manager> managers = new ArrayList<>();
        managers.add(manager);
        managers.add(historyManager);

        httpServer.createContext("/subtask", new SubtaskHandler<>(managers));
        httpServer.start();
    }

    @BeforeEach
    public void BeforeEach() {
        IntStream.range(0, 50).forEach(i -> {
            Task task = new Task();
            task.setStatus(Status.NEW);
            task.setTaskType(TaskType.TASK);
            task.setName("TEST_Task" + i);
            task.setDescription("Task for test");
            task.setStartTime(LocalDateTime.now());
            manager.addNewTask(task);
        });

        IntStream.range(0, 50).forEach(i -> {
            Epic task = new Epic();
            task.setStatus(Status.NEW);
            task.setTaskType(TaskType.EPIC);
            task.setName("TEST_Epic" + i);
            task.setDescription("Epic for test");
            task.setStartTime(LocalDateTime.now());
            manager.addNewEpic(task);

            IntStream.range(0, 2).forEach(j -> {
                Subtask subtask = new Subtask();
                subtask.setStatus(Status.NEW);
                subtask.setTaskType(TaskType.SUBTASK);
                subtask.setName("Subtask for test" + j);
                subtask.setDescription("Subtask for test");
                subtask.setStartTime(LocalDateTime.now());
                manager.addNewSubtask(subtask, task.getId());
            });
        });
    }

    @AfterAll
    public static void AfterAll() {
        httpServer.stop(1);
    }

    @Test
    void GETSubtasks() {
        URI uri = URI.create("http://localhost:8080/subtask");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpClient httpClient = HttpClient.newBuilder().build();

        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            int status = response.statusCode();

            System.out.println("Server responded with status code: " + status);
            System.out.println(response.body());
            assertFalse(response.body().isEmpty());
        } catch (IOException | InterruptedException e) {
            System.out.println("An error occurred while sending the request to the server.");
        }
    }

    @Test
    void GETSubtaskById() {
        int id = manager.getAllSubtasks().stream().findAny().get().getId();

        URI uri = URI.create("http://localhost:8080/subtask/" + id);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpClient httpClient = HttpClient.newBuilder().build();

        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            int status = response.statusCode();

            System.out.println("Server responded with status code: " + status + ", body: " + response.body());
            assertFalse(response.body().isEmpty());

        } catch (IOException | InterruptedException e) {
            System.out.println("An error occurred while sending the request to the server.");
        }
    }

}