package task.service;

import task.models.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    public static final int SIZE_HISTORIC = 10;
    private final HashMap<Integer, TaskNode> taskMap;
    private final TaskLinkedList taskListManager;

    public InMemoryHistoryManager() {
        this.taskMap = new HashMap<>();
        this.taskListManager = new TaskLinkedList();
    }

    private class TaskLinkedList {
        private TaskNode head;
        private TaskNode tail;
        private int size;

        public TaskLinkedList() {
            this.head = null;
            this.tail = null;
            this.size = 0;
        }

        public void add(Task task) {
            if (task == null) {
                return;
            }

            if (taskMap.containsKey(task.getId())) {
                TaskNode existingNode = taskMap.get(task.getId());
                removeNode(existingNode);
            }

            TaskNode newNode = new TaskNode(task, null, null);
            if (size == 0) {
                head = tail = newNode;
            } else {
                newNode.prev = tail;
                tail.next = newNode;
                tail = newNode;
            }
            size++;

            taskMap.put(task.getId(), newNode);

            if (size > SIZE_HISTORIC) {
                TaskNode firstTask = head;
                removeNode(firstTask);
            }
        }

        public void remove(int id) {
            TaskNode current = head;
            while (current != null) {
                if (current.task.getId() == id) {
                    removeNode(current);
                    return;
                }
                current = current.next;
            }
        }

        public List<Task> getTasks() {
            ArrayList<Task> taskList = new ArrayList<>();
            TaskNode current = head;
            while (current != null) {
                taskList.add(current.task);
                current = current.next;
            }
            return taskList;
        }

        private void removeNode(TaskNode node) {
            if (node == null) {
                return;
            }
            if (node == head && node == tail) {
                head = tail = null;
            } else if (node == head) {
                head = head.next;
                head.prev = null;
            } else if (node == tail) {
                tail = tail.prev;
                tail.next = null;
            } else {
                node.prev.next = node.next;
                node.next.prev = node.prev;
            }
            size--;

            taskMap.remove(node.task.getId());
        }
    }

    public static String historyToString(FileBackedTaskManager manager) {
        StringBuilder stringBuilder = new StringBuilder();
        List<Task> history = manager.getHistory();
        for (Task task : history) {
            stringBuilder.append(task.getId()).append(",");
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }

    public static List<Integer> historyFromString(String value) {
        String[] parts = value.split(",");
        List<Integer> historyIds = new ArrayList<>();
        for (String part : parts) {
            historyIds.add(Integer.parseInt(part));
        }
        return historyIds;
    }

    @Override
    public void add(Task task) {
        taskListManager.add(task);
    }

    @Override
    public void remove(int id) {
        taskListManager.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return taskListManager.getTasks();
    }
}
