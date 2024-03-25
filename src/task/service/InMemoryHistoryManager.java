package task.service;

import task.models.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> tasksHistoric = new ArrayList<>();
    private static final int SIZE_HISTORIC = 10;

    @Override
    public void add(Task task) {
        if (task != null) { // не привычно проверять на нал когда работаешь один и сам вызываешь)
            tasksHistoric.add(task);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        int size = tasksHistoric.size();
        if (size <= SIZE_HISTORIC) {
            return tasksHistoric;
        } else {
            return new ArrayList<>(tasksHistoric.subList(size - SIZE_HISTORIC, size));
        }
    }


}
