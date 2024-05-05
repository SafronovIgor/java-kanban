package task.service;

import task.models.Task;

import java.util.List;

public interface HistoryManager extends Manager {

    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}
