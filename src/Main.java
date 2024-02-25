import task.models.Epic;
import task.models.Status;
import task.models.Subtask;
import task.models.Task;
import task.service.InMemoryTaskManager;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

        Task task1 = new Task();
        task1.setName("Согласовать время по ТЗ");
        task1.setDescription("ТЗ №7585 - доработка ws.");
        inMemoryTaskManager.addNewTask(task1);

        Task task2 = new Task();
        task2.setName("Погулять с собакой.");
        task2.setDescription("Сходить в парк, выгулять собаку.");
        inMemoryTaskManager.addNewTask(task2);

        Epic epic1 = new Epic();
        epic1.setName("Купить дом.");
        epic1.setDescription("Начать выбирать новое жильё.");
        inMemoryTaskManager.addNewEpic(epic1);

        Epic epic2 = new Epic();
        epic2.setName("Сделать паспорт.");
        epic2.setDescription("Отправить заявку на новый паспорт.");
        inMemoryTaskManager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask();
        subtask1.setName("Найти подходящий дом.");
        subtask1.setDescription("Найти сайты по продажам домой.");
        inMemoryTaskManager.addNewSubtask(subtask1, epic1.getId());

        Subtask subtask2 = new Subtask();
        subtask2.setName("Проверить можно ли купить участок.");
        subtask2.setDescription("Сравнить цены на дом и участок.");
        inMemoryTaskManager.addNewSubtask(subtask2, epic1.getId());

        Subtask subtask3 = new Subtask();
        subtask3.setName("Узнать в каком МФЦ.");
        subtask3.setDescription("Позвонить в МФЦ и уточнить время приема.");
        inMemoryTaskManager.addNewSubtask(subtask3, epic2.getId());

        System.out.println("Список всех map:");
        System.out.println(inMemoryTaskManager.getAllTasks());
        System.out.println(inMemoryTaskManager.getAllEpics());
        System.out.println(inMemoryTaskManager.getAllSubtasks());

        System.out.println("\nИзменение статуса:");
        ArrayList<Subtask> listSubtasksByEpic = inMemoryTaskManager.getListSubtaskByEpic(epic1);
        System.out.println(listSubtasksByEpic); //до изм. статусов

        for (Subtask s : listSubtasksByEpic) {
            s.setStatus(Status.DONE);
            inMemoryTaskManager.updateSubtask(s);
            //break; Меняем один статус.
        }

        subtask1.setStatus(Status.DONE);
        inMemoryTaskManager.updateSubtask(subtask1);

        System.out.println(epic1);
        System.out.println(listSubtasksByEpic); //После изм. статуса

        System.out.println("\nУдаление:");
        System.out.println(inMemoryTaskManager.getAllTasks());
        inMemoryTaskManager.getTaskHashMap().remove(task1.getId());
        System.out.println(inMemoryTaskManager.getAllTasks() + "\n");

        System.out.println(inMemoryTaskManager.getAllSubtasks());
        inMemoryTaskManager.getSubtaskHashMap().remove(subtask1.getId());
        System.out.println(inMemoryTaskManager.getAllSubtasks());

        System.out.println("sssss");
        inMemoryTaskManager.getTaskById(1);
        System.out.println(inMemoryTaskManager.getHistory());

    }
}
