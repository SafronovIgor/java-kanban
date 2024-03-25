package task.service;

import task.models.Task;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int SIZE_HISTORIC = 10;
    private Node head;
    private Node tail;
    private int size;
    private final LinkedHashMap<Integer, Node> taskMap;

    public InMemoryHistoryManager() {
        this.head = null;
        this.tail = null;
        this.size = 0;
        this.taskMap = new LinkedHashMap<>();
    }

    private static class Node {
        Task task;
        Node next;
        Node prev;

        Node(Task task, Node prev, Node next) {
            this.task = task;
            this.prev = prev;
            this.next = next;
        }
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }


        if (taskMap.containsKey(task.getId())) {
            Node existingNode = taskMap.get(task.getId());
            removeNode(existingNode);
        }

        Node newNode = new Node(task, null, null);
        if (size == 0) {
            head = tail = newNode;
        } else {
            newNode.prev = tail;
            tail.next = newNode;
            tail = newNode;
        }
        size++;

        taskMap.put(task.getId(), newNode);
    }

    public void linkLast(Task task) {
        add(task);
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> taskList = new ArrayList<>();
        Node current = head;
        while (current != null) {
            taskList.add(current.task);
            current = current.next;
        }
        return taskList;
    }

    @Override
    public void remove(int id) {
        Node current = head;
        while (current != null) {
            if (current.task.getId() == id) {
                removeNode(current);
                return;
            }
            current = current.next;
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> tasksHistoric = new ArrayList<>();
        Node current = tail;
        int count = 0;
        while (current != null && count < SIZE_HISTORIC) {
            tasksHistoric.add(current.task);
            current = current.prev;
            count++;
        }
        return tasksHistoric;
    }

    private void removeNode(Node node) {
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
