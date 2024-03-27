import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import task.models.Epic;
import task.models.Status;
import task.models.Subtask;
import task.models.Task;
import task.service.Managers;
import task.service.TaskManager;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest {

    private static final TaskManager manager = Managers.getDefaultTaskManager();

    @BeforeAll
    public static void BeforeAll() {
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
    public void printAllTasks() {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getListSubtaskByEpic(epic)) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }

    }

    @Test
    public void testSubtaskDeletion() {
        Epic epic = manager.getAllEpics().get(0);
        Subtask subtask = manager.getListSubtaskByEpic(epic).get(0);

        manager.deleteSubtaskByID(subtask.getId());
        assertNull(manager.getSubtaskById(subtask.getId()));

        assertFalse(manager.getAllSubtasks().contains(subtask.getId()));
    }

    @Test
    public void testEpicSubtaskIds() {
        Epic epic = manager.getAllEpics().get(0);
        ArrayList<Subtask> listSubtasks = manager.getListSubtaskByEpic(epic);
        assertTrue(listSubtasks.size() != 0);
    }

    @Test
    public void testSubtaskStatusChange() {
        Subtask subtask = manager.getAllSubtasks().get(0);
        subtask.setStatus(Status.IN_PROGRESS);

        manager.changeStatus(subtask, Status.DONE);

        assertEquals(Status.DONE, subtask.getStatus());
    }

}
