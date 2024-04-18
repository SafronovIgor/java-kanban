package task.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import task.models.Epic;
import task.models.Status;
import task.models.Subtask;
import task.models.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    final Random random = new Random();
    final T MANAGER;

    TaskManagerTest(T manager) {
        MANAGER = manager;
    }

    @Test
    void getHistory() {
        int countTask = createTasks(10);

        List<Task> history = MANAGER.getHistory();

        assertEquals(history.size(), countTask);
        assertFalse(history.isEmpty());

        Set<Task> uniqueTasks = new HashSet<>(history);
        assertEquals(history.size(), uniqueTasks.size());


    }

    @Test
    void changeStatus() {
        Task task = new Task();
        task.setName("TEST_Task1");
        task.setDescription("Task for test");
        task.setStartTime(LocalDateTime.now());
        MANAGER.addNewTask(task);

        Arrays.stream(Status.values()).forEach(status -> {
            task.setStatus(status);
            MANAGER.updateTask(task);
            assertEquals(task.getStatus(), status);
        });
    }

    @Test
    void getAllTasks() {
        assertEquals(createTasks(50), MANAGER.getAllTasks().size());
    }

    @Test
    void getAllEpics() {
        assertEquals(createEpics(50), MANAGER.getAllEpics().size());
    }

    @Test
    void getAllSubtasks() {
        assertEquals(createSubtasks(50), MANAGER.getAllSubtasks().size());
    }

    @Test
    void deleteAllTask() {
        createTasks(100);
        MANAGER.deleteAllTask();

        assertEquals(MANAGER.getAllTasks().size(), 0);
    }

    @Test
    void deleteAllEpics() {
        createEpics(100);
        MANAGER.deleteAllEpics();

        assertEquals(MANAGER.getAllEpics().size(), 0);
    }

    @Test
    void deleteAllSubtasks() {
        createSubtasks(100);

        MANAGER.deleteAllSubtasks();

        assertEquals(MANAGER.getAllSubtasks().size(), 0);
    }

    @Test
    void getTaskById() {
        Task task = new Task();
        task.setName("TEST_Task1");
        task.setDescription("Task for test");
        task.setStartTime(LocalDateTime.now());
        MANAGER.addNewTask(task);

        assertNotNull(MANAGER.getTaskById(task.getId()));
    }

    @Test
    void getEpicsById() {
        Epic task = new Epic();
        task.setName("TEST_Task1");
        task.setDescription("Task for test");
        task.setStartTime(LocalDateTime.now());
        MANAGER.addNewEpic(task);

        assertNotNull(MANAGER.getEpicsById(task.getId()));
    }

    @Test
    void getSubtaskById() {
        Epic task = new Epic();
        task.setName("TEST_Task1");
        task.setDescription("Task for test");
        task.setStartTime(LocalDateTime.now());
        MANAGER.addNewEpic(task);

        Subtask subtask = new Subtask();
        subtask.setName("TEST_Task1");
        subtask.setDescription("Task for test");
        subtask.setStartTime(LocalDateTime.now());
        MANAGER.addNewSubtask(subtask, task.getId());

        assertNotNull(MANAGER.getSubtaskById(subtask.getId()));
    }

    @Test
    void taskValidation() {
        Epic task = new Epic();
        task.setName("TEST_Epic1");
        task.setDescription("Task for test");
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.ofDays(1));
        MANAGER.addNewEpic(task);

        Epic finalTask = task;

        task = new Epic();
        task.setName("TEST_Epic2");
        task.setDescription("Task for test");
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.ofDays(1));

        Epic finalTask2 = task;
        assertThrows(
                RuntimeException.class,
                () -> MANAGER.addNewEpic(finalTask2),
                "Должна быть ошибка создания задачи с пересечением.");

        Stream.of(MANAGER.getAllTasks(), MANAGER.getAllEpics(), MANAGER.getAllSubtasks())
                .flatMap(List::stream)
                .forEach(t -> assertTrue(t.isIntersecting(finalTask)));
    }

    @Test
    void testRemovalFromBeginning() {
        testRemoval(createTaskList(3), 0);
    }

    @Test
    void testRemovalFromMiddle() {
        testRemoval(createTaskList(3), 1);
    }

    @Test
    void testRemovalFromEnd() {
        testRemoval(createTaskList(3), 2);
    }

    @Test
    public void testException() {
        assertThrows(ArithmeticException.class, () -> {
            int a = (10 / 0);
        }, "Деление на ноль должно приводить к исключению");
        assertDoesNotThrow(() -> {
            int a = 10 / 2;
        }, "Деление на два не должно вызывать исключение");
    }

    private void testRemoval(List<Task> tasks, int index) {
        Task taskToRemove = tasks.get(index);
        HistoryManager historyManager = MANAGER.getHistoryManager();

        historyManager.remove(taskToRemove.getId());

        assertEquals(tasks.size() - 1, historyManager.getHistory().size());
        assertFalse(historyManager.getHistory().contains(taskToRemove));
        for (int i = 0; i < tasks.size(); i++) {
            if (i != index) {
                assertTrue(historyManager.getHistory().contains(tasks.get(i)));
            }
        }
    }

    private List<Task> createTaskList(int count) {
        return IntStream.range(1, count + 1)
                .mapToObj(this::createTask)
                .collect(Collectors.toList());
    }

    public int createTasks(int bound) {
        final int b = random.nextInt(bound);

        IntStream.range(0, b).forEach(i -> {
            Task task = new Task();
            task.setStatus(Status.NEW);
            task.setName("TEST_Task" + i);
            task.setDescription("Task for test");
            task.setStartTime(LocalDateTime.now());
            MANAGER.addNewTask(task);
        });
        return b;
    }

    public int createEpics(int bound) {
        final int b = random.nextInt(bound);

        IntStream.range(0, b).forEach(i -> {
            Epic task = new Epic();
            task.setStatus(Status.NEW);
            task.setName("TEST_Task" + i);
            task.setDescription("Task for test");
            task.setStartTime(LocalDateTime.now());
            MANAGER.addNewEpic(task);
        });
        return b;
    }

    public int createSubtasks(int bound) {
        final int b = random.nextInt(bound);
        final int boundSubtask = random.nextInt(10);
        AtomicInteger countSubtask = new AtomicInteger();

        IntStream.range(0, b).forEach(i -> {
            Epic task = new Epic();
            task.setStatus(Status.NEW);
            task.setName("TEST_Epic" + i);
            task.setDescription("Epic for test");
            task.setStartTime(LocalDateTime.now());
            MANAGER.addNewEpic(task);

            IntStream.range(0, boundSubtask).forEach(j -> {
                Subtask subtask = new Subtask();
                subtask.setStatus(Status.NEW);
                subtask.setName("TEST_Subtask" + j);
                subtask.setDescription("Subtask for test");
                subtask.setStartTime(LocalDateTime.now());
                MANAGER.addNewSubtask(subtask, task.getId());
                countSubtask.getAndIncrement();
            });

        });
        return countSubtask.get();
    }

    private Task createTask(int id) {
        Task task = new Task();
        task.setId(id);
        task.setStatus(Status.NEW);
        task.setName("Test Task " + id);
        task.setDescription("Task for testing");
        task.setStartTime(LocalDateTime.now());
        MANAGER.addNewTask(task);
        return task;
    }

    @AfterEach
    public void clearTask() {
        MANAGER.deleteAllTask();
        MANAGER.deleteAllEpics();
        MANAGER.deleteAllSubtasks();
    }
}
