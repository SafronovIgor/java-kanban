package task.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import task.service.Managers;
import task.service.TaskManager;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private static final TaskManager manager = Managers.getDefault();

    @Test
    public void TaskEqualsTask() {
        Task task1 = new Task();
        task1.setName("Согласовать время по ТЗ");
        task1.setDescription("ТЗ №7585 - доработка ws.");
        manager.addNewTask(task1);

        Task task2 = new Task();
        task2.setName("Погулять с собакой.");
        task2.setDescription("Сходить в парк, выгулять собаку.");
        manager.addNewTask(task2);

        ArrayList<Task> tasks = manager.getAllTasks();
        Task task1InMemory = tasks.get(0);
        Task task2InMemory = tasks.get(1);

        assertEquals(task1InMemory, task1InMemory);
        assertNotEquals(task1InMemory, task2InMemory);
    }

}