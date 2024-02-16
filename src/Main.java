import task.tracker.models.Epic;
import task.tracker.models.Status;
import task.tracker.models.Subtask;
import task.tracker.models.Task;
import task.tracker.service.TaskManager;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        Task task1 = new Task();
        task1.setName("Согласовать время по ТЗ");
        task1.setDescription("ТЗ №7585 - доработка ws.");
        taskManager.addNewTask(task1);

        Task task2 = new Task();
        task2.setName("Погулять с собакой.");
        task2.setDescription("Сходить в парк, выгулять собаку.");
        taskManager.addNewTask(task2);

        Epic epic1 = new Epic();
        epic1.setName("Купить дом.");
        epic1.setDescription("Начать выбирать новое жильё.");
        taskManager.addNewEpic(epic1);

        Epic epic2 = new Epic();
        epic2.setName("Сделать паспорт.");
        epic2.setDescription("Отправить заявку на новый паспорт.");
        taskManager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask();
        subtask1.setName("Найти подходящий дом.");
        subtask1.setDescription("Найти сайты по продажам домой.");
        taskManager.addNewSubtask(subtask1, epic1.getId());

        Subtask subtask2 = new Subtask();
        subtask2.setName("Проверить можно ли купить участок.");
        subtask2.setDescription("Сравнить цены на дом и участок.");
        taskManager.addNewSubtask(subtask2, epic1.getId());

        Subtask subtask3 = new Subtask();
        subtask3.setName("Узнать в каком МФЦ.");
        subtask3.setDescription("Позвонить в МФЦ и уточнить время приема.");
        taskManager.addNewSubtask(subtask3, epic2.getId());

        System.out.println("Список всех map:");
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());

        System.out.println("\nИзменение статуса:");
        ArrayList<Subtask> listSubtasksByEpic = taskManager.getListSubtaskByEpic(epic1);
        System.out.println(listSubtasksByEpic); //до изм. статусов

        for (Subtask s : listSubtasksByEpic) {
            s.setStatus(Status.DONE);
            taskManager.updateSubtask(s);
            //break; Меняем один статус.
        }

        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);

        System.out.println(epic1);
        System.out.println(listSubtasksByEpic); //После изм. статуса

        System.out.println("\nУдаление:");
        System.out.println(taskManager.getAllTasks());
        taskManager.getTaskHashMap().remove(task1.getId());
        System.out.println(taskManager.getAllTasks() + "\n");

        System.out.println(taskManager.getAllSubtasks());
        taskManager.getSubtaskHashMap().remove(subtask1.getId());
        System.out.println(taskManager.getAllSubtasks());

    }
}
