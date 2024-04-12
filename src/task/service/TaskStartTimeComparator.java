package task.service;

import task.models.Task;

import java.time.LocalDateTime;
import java.util.Comparator;

public class TaskStartTimeComparator implements Comparator<Task> {
    @Override
    public int compare(Task task1, Task task2) {
        LocalDateTime startTime1 = task1.getStartTime();
        LocalDateTime startTime2 = task2.getStartTime();

        if (startTime1 == null || startTime2 == null) {
            return 0;
        }

        return startTime1.compareTo(startTime2);
    }
}
