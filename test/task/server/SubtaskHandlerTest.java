package task.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.util.Comparator;
import java.util.Optional;
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

    @Test
    void createSubtask() {
        Optional<Integer> epicID = manager.getAllEpics().stream()
                .max(Comparator.comparingInt(Task::getId))
                .map(Task::getId);
        Optional<Integer> subtaskID = manager.getAllSubtasks().stream()
                .max(Comparator.comparingInt(Task::getId))
                .map(Task::getId);

        Subtask subtask = new Subtask();
        subtask.setId(subtaskID.get() + 1);
        subtask.setStatus(Status.NEW);
        subtask.setTaskType(TaskType.SUBTASK);
        subtask.setName("Subtask for test");
        subtask.setDescription("Subtask for test");
        subtask.setStartTime(LocalDateTime.now());
        subtask.addIdEpicToList(epicID.get());

        HttpResponse<String> response = sendTaskByPost(subtask, null);

        assertNotNull(response);
        assertNotNull(manager.getSubtaskById(subtaskID.get() + 1));
    }

    @Test
    void updateSubtask() {
        Optional<Subtask> any = manager.getAllSubtasks().stream().findAny();
        if (any.isPresent()) {
            String oldNameSubtask = any.get().getName();

            Subtask subtask = new Subtask();
            subtask.setId(any.get().getId());
            subtask.setStatus(Status.NEW);
            subtask.setTaskType(TaskType.SUBTASK);
            subtask.setName("TTTTTTTTTTTTTTTTT");
            subtask.setDescription("Subtask for test");
            subtask.setStartTime(LocalDateTime.now());

            HttpResponse<String> response = sendTaskByPost(subtask, subtask.getId());
            int status = response.statusCode();

            System.out.println("Server responded with status code: " + status + ", body: " + response.body());

            assertNotEquals(oldNameSubtask, manager.getSubtaskById(subtask.getId()).getName());
        } else {
            fail("=(");
        }
    }

    private HttpResponse<String> sendTaskByPost(Subtask task, Integer id) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        String json = gson.toJson(task);

        URI uri = URI.create("http://localhost:8080/subtask" + (id == null ? "" : ("/" + task.getId())));
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