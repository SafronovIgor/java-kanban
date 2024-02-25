package task.service;

import task.models.Epic;
import task.models.Subtask;
import task.models.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final TaskManager manager = Managers.getDefault();

    @Override
    public void add(Task task) {
        manager.addNewTask(task);
    }

    public void add(Subtask subtask, Integer idEpic) {
        manager.addNewSubtask(subtask, idEpic);
    }

    public void add(Epic epic) {
        manager.addNewEpic(epic);
    }


    @Override
    public ArrayList<Task> getHistory() {
        return manager.getHistory();
    }
}
