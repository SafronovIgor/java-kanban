package task.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import task.models.Epic;
import task.models.Subtask;
import task.models.Task;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private static final TaskManager manager = Managers.getDefault();

    @BeforeAll
    public static void beforeAll() {
        Task task1 = new Task();
        task1.setName("Согласовать время по ТЗ");
        task1.setDescription("ТЗ №7585 - доработка ws.");
        manager.addNewTask(task1);

        Task task2 = new Task();
        task2.setName("Погулять с собакой.");
        task2.setDescription("Сходить в парк, выгулять собаку.");
        manager.addNewTask(task2);

        Epic epic1 = new Epic();
        epic1.setName("Купить дом.");
        epic1.setDescription("Начать выбирать новое жильё.");
        manager.addNewEpic(epic1);

        Epic epic2 = new Epic();
        epic2.setName("Сделать паспорт.");
        epic2.setDescription("Отправить заявку на новый паспорт.");
        manager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask();
        subtask1.setName("Найти подходящий дом.");
        subtask1.setDescription("Найти сайты по продажам домой.");
        manager.addNewSubtask(subtask1, epic1.getId());

        Subtask subtask2 = new Subtask();
        subtask2.setName("Проверить можно ли купить участок.");
        subtask2.setDescription("Сравнить цены на дом и участок.");
        manager.addNewSubtask(subtask2, epic1.getId());

        Subtask subtask3 = new Subtask();
        subtask3.setName("Узнать в каком МФЦ.");
        subtask3.setDescription("Позвонить в МФЦ и уточнить время приема.");
        manager.addNewSubtask(subtask3, epic2.getId());
    }

    @Test
    public void searchTaskById() {
        for (Task task : manager.getAllTasks()) {
            Task taskById = manager.getTaskById(task.getId());
            assertEquals(taskById, taskById);
        }

        for (Epic epic : manager.getAllEpics()) {
            Epic epicsById = manager.getEpicsById(epic.getId());
            assertEquals(epic, epicsById);
        }

        for (Subtask subtask : manager.getAllSubtasks()) {
            Subtask subtaskById = manager.getSubtaskById(subtask.getId());
            assertEquals(subtask, subtaskById);
        }
    }

    @Test
    public void checkTaskIdConflicts() {
        ArrayList<Task> tasks = manager.getAllTasks();
        Task task = tasks.get(0);
        System.out.println(task);
        task.setId(5);
        manager.addNewTask(task);
        System.out.println(task);
    }
}