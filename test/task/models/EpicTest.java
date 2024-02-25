package task.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import task.service.Managers;
import task.service.TaskManager;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private static final TaskManager manager = Managers.getDefault();

    @BeforeAll
    public static void beforeAll() {
        Epic epic1 = new Epic();
        epic1.setName("Купить дом.");
        epic1.setDescription("Начать выбирать новое жильё.");
        manager.addNewEpic(epic1);

        Epic epic2 = new Epic();
        epic2.setName("Сделать паспорт.");
        epic2.setDescription("Отправить заявку на новый паспорт.");
        manager.addNewEpic(epic2);
    }

    @Test
    public void EpicEqualsEpic() {

        ArrayList<Epic> epics = manager.getAllEpics();
        Task epic1InMemory = epics.get(0);
        Task epic2InMemory = epics.get(1);

        assertEquals(epic1InMemory, epic1InMemory);
        assertNotEquals(epic1InMemory, epic2InMemory);
    }

//    проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи;
//    тут у меня отдельный метод для добавления подзадачи, поэтому думаю этот тест не реализовывать.

}