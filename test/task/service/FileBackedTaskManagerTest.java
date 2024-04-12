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
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FileBackedTaskManagerTest {
    private static final FileBackedTaskManager MANAGER = Managers.getFileBackedTaskManager();

    @Test
    @Order(1)
    public void checkLoadingFromEmptyFile() {
        File file = Paths.get(MANAGER.getPathToFile()).toFile();
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
        Task task1 = new Task();
        task1.setName("Согласовать время по ТЗ");
        task1.setDescription("ТЗ №7585 - доработка ws.");
        MANAGER.addNewTask(task1);

        Epic epic1 = new Epic();
        epic1.setName("Купить дом.");
        epic1.setDescription("Начать выбирать новое жильё.");
        MANAGER.addNewEpic(epic1);

        Subtask subtask1 = new Subtask();
        subtask1.setName("Найти подходящий дом.");
        subtask1.setDescription("Найти сайты по продажам домой.");
        MANAGER.addNewSubtask(subtask1, epic1.getId());

        LinkedList<Task> list = new LinkedList<>();

        list.addAll(MANAGER.getAllTasks());
        list.addAll(MANAGER.getAllEpics());
        list.addAll(MANAGER.getAllSubtasks());

        assertFalse(list.isEmpty());
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
            Files.delete(Path.of(MANAGER.getPathToFile()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}