package task.service;

import org.junit.jupiter.api.*;
import task.models.Epic;
import task.models.Subtask;
import task.models.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FileBackedTaskManagerTest {
    private final Random random = new Random();
    private static final FileBackedTaskManager MANAGER = Managers.getFileBackedTaskManager();

    @Test
    @Order(1)
    public void checkLoadingFromEmptyFile() {
        File file = Paths.get(MANAGER.getPathToFile()).toFile();
        System.out.println("File saving data:" + file);
        FileBackedTaskManager.loadFromFile(file);

        LinkedList<Task> list = new LinkedList<>();

        list.addAll(MANAGER.getAllTasks());
        list.addAll(MANAGER.getAllEpics());
        list.addAll(MANAGER.getAllSubtasks());

        assertTrue(list.isEmpty());
    }

    @Test
    @Order(2)
    public void savingTasks() {
        final int durationDaysEpic1 = random.nextInt(500);
        final int durationDaysSubtask1 = random.nextInt(durationDaysEpic1);
        final int durationDaysSubtask2 = (durationDaysEpic1 - durationDaysSubtask1);

        Task task1 = new Task();
        task1.setName("Согласовать время по ТЗ");
        task1.setDescription("ТЗ №7585 - доработка ws.");
        task1.setStartTime(LocalDateTime.now().minusDays(30));
        task1.setDuration(Duration.ofDays(5));
        MANAGER.addNewTask(task1);

        Epic epic1 = new Epic();
        epic1.setName("Купить дом.");
        epic1.setDescription("Начать выбирать новое жильё.");
        epic1.setStartTime(LocalDateTime.now().minusDays(9));
        MANAGER.addNewEpic(epic1);

        Subtask subtask1 = new Subtask();
        subtask1.setName("subtask1");
        subtask1.setDescription("Найти сайты по продажам домой.");
        subtask1.setStartTime(LocalDateTime.now().minusDays(8));
        subtask1.setDuration(Duration.ofDays(durationDaysSubtask1));
        MANAGER.addNewSubtask(subtask1, epic1.getId());

        Subtask subtask2 = new Subtask();
        subtask2.setName("subtask2");
        subtask2.setDescription("subtask2");
        subtask2.setStartTime(LocalDateTime.now().minusDays(5));
        subtask2.setDuration(Duration.ofDays(durationDaysSubtask2));
        MANAGER.addNewSubtask(subtask2, epic1.getId());

        LinkedList<Task> list = new LinkedList<>();
        list.addAll(MANAGER.getAllTasks());
        list.addAll(MANAGER.getAllEpics());
        list.addAll(MANAGER.getAllSubtasks());

        assertFalse(list.isEmpty());
        assertEquals(epic1.getEndTime(), epic1.getStartTime().plusDays(durationDaysEpic1));
    }

    @Test
    @Order(3)
    public void loadingTasks() {
        TaskManager defaultTaskManager = Managers.getDefaultTaskManager();
        defaultTaskManager.deleteAllTask();
        defaultTaskManager.deleteAllEpics();
        defaultTaskManager.deleteAllSubtasks();

        File file = Paths.get(MANAGER.getPathToFile()).toFile();
        FileBackedTaskManager.loadFromFile(file);

        LinkedList<Task> list = new LinkedList<>();

        list.addAll(MANAGER.getAllTasks());
        list.addAll(MANAGER.getAllEpics());
        list.addAll(MANAGER.getAllSubtasks());

        assertFalse(list.isEmpty());
    }

    @AfterAll
    public static void afterAll() {
        try {
            System.out.println("File saving data remove: " + MANAGER.getPathToFile());
            Files.delete(Path.of(MANAGER.getPathToFile()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}