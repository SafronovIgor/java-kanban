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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
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

        httpServer.createContext("/tasks", new TasksHandler<>(managers));
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
    void GETTasks() {
        URI uri = URI.create("http://localhost:8080/tasks");
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
    void GETTaskById() {
        URI uri = URI.create("http://localhost:8080/tasks/50");
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
    void POSTTask() {
        Task task = new Task();
        task.setStatus(Status.NEW);
        task.setTaskType(TaskType.TASK);
        task.setName("POST_Task");
        task.setDescription("Task for POST test");
        task.setStartTime(LocalDateTime.now());

        HttpResponse<String> response = sendTaskByPost(task);

        if (response != null) {
            int status = response.statusCode();
            System.out.println("Server responded with status code: " + status);
            assertEquals(201, status);
        } else {
            fail("Failed to send request to server.");
        }
    }

    @Test
    void POSTTask_ValidationFailure() {
        Task firstTask = new Task();
        firstTask.setStatus(Status.NEW);
        firstTask.setTaskType(TaskType.TASK);
        firstTask.setName("First_Task");
        firstTask.setDescription("First Task for POST test");
        firstTask.setStartTime(LocalDateTime.now());
        firstTask.setDuration(Duration.ofDays(2));

        HttpResponse<String> firstResponse = sendTaskByPost(firstTask);

        if (firstResponse == null || firstResponse.statusCode() != 201) {
            fail("Failed to create first task.");
        }

        Task secondTask = new Task();
        secondTask.setStatus(Status.NEW);
        secondTask.setTaskType(TaskType.TASK);
        secondTask.setName("Second_Task");
        secondTask.setDescription("Second Task for POST test");
        secondTask.setStartTime(firstTask.getStartTime());
        secondTask.setDuration(Duration.ofDays(2));

        try {
            HttpResponse<String> secondResponse = sendTaskByPost(secondTask);

            if (secondResponse != null) {
                int status = secondResponse.statusCode();
                System.out.println("Server responded with status code: " + status);
                assertEquals(406, status);
            } else {
                fail("Failed to send request to server for second task.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception occurred: " + e.getMessage());
        } finally {
            manager.deleteAllTask();
        }
    }

    private HttpResponse<String> sendTaskByPost(Task task) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        String json = gson.toJson(task);

        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(uri)
                .header("Content-Type", "application/json")
                .build();
        HttpClient httpClient = HttpClient.newBuilder().build();

        try {
            return httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.getMessage();
            return null;
        }
    }

    @Test
    void deleteTask() {
        ArrayList<Task> tasks = manager.getAllTasks();
        int countBefore = tasks.size();
        int id = tasks.stream().findAny().get().getId();

        URI uri = URI.create("http://localhost:8080/tasks/" + id);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();
        HttpClient httpClient = HttpClient.newBuilder().build();

        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            int countAfter = manager.getAllTasks().size();
            int status = response.statusCode();

            System.out.println("Server responded with status code: " + status);
            assertNotEquals(countBefore, countAfter);
        } catch (IOException | InterruptedException e) {
            e.getMessage();
        }
    }

    @AfterAll
    public static void AfterAll() {
        httpServer.stop(1);
    }

}
