package task.service;

import task.models.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final TaskManager manager = Managers.getDefault();

    @Override
    public void add(Task task) {
        manager.addNewTask(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return manager.getHistory();
    }
}
