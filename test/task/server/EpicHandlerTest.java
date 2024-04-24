package task.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
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
import java.util.Comparator;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class EpicHandlerTest {
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

        httpServer.createContext("/epics", new EpicHandler<>(managers));
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

    @Test
    void getEpics() {
        URI uri = URI.create("http://localhost:8080/epics");
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
    void getEpicById() {
        int id = manager.getAllEpics().stream().findAny().get().getId();

        URI uri = URI.create("http://localhost:8080/epics/" + id);
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

    @Test
    void getEpicSubtask() {
        int id = manager.getAllEpics().stream().findAny().get().getId();

        URI uri = URI.create("http://localhost:8080/epics/" + id + "/subtasks");
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

    @Test
    void createEpic() {
        Epic task = new Epic();
        task.setStatus(Status.NEW);
        task.setTaskType(TaskType.EPIC);
        task.setName("xxxxxxxxxxx");
        task.setDescription("Epic for test");
        task.setStartTime(LocalDateTime.now());
        task.setEndTime(LocalDateTime.now());

        HttpResponse<String> response = sendTaskByPost(task);

        assertNotNull(response);
        Epic epicsById = manager.getEpicsById(manager.getAllEpics().stream()
                .max(Comparator.comparingInt(Task::getId))
                .orElseGet(() -> fail("=("))
                .getId()
        );
        assertEquals(task.getName(), epicsById.getName());
    }

    @Test
    void deleteEpic() {
        ArrayList<Epic> tasks = manager.getAllEpics();
        int countBefore = tasks.size();
        int id = tasks.stream().findAny().get().getId();

        URI uri = URI.create("http://localhost:8080/epics");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();
        HttpClient httpClient = HttpClient.newBuilder().build();

        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            int countAfter = manager.getAllEpics().size();
            int status = response.statusCode();

            System.out.println("Server responded with status code: " + status);
            assertNotEquals(countBefore, countAfter);
        } catch (IOException | InterruptedException e) {
            e.getMessage();
        }
    }

    private HttpResponse<String> sendTaskByPost(Epic task) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        String json = gson.toJson(task);

        URI uri = URI.create("http://localhost:8080/epics");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(uri)
                .header("Content-Type", "application/json")
                .build();
        HttpClient httpClient = HttpClient.newBuilder().build();

        try {
            return httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}