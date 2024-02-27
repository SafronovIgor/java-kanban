package task.service;

import task.models.Epic;
import task.models.Subtask;
import task.models.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final TaskManager manager = Managers.getDefaultTaskManager();

    @Override
    public void add(Task task) {
        if (task != null) { // не привычно проверять на нал когда работаешь один и сам вызываешь)
            manager.addNewTask(task);
        }
    }

    public void add(Subtask subtask, Integer idEpic) {
        if (subtask != null && idEpic != null) {
            manager.addNewSubtask(subtask, idEpic);
        }
    }

    public void add(Epic epic) {
        if (epic != null) {
            manager.addNewEpic(epic);
        }
    }


    @Override
    public ArrayList<Task> getHistory() {
        return manager.getHistory();
    }
}
