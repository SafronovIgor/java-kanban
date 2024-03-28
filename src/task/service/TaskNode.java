package task.service;

import task.models.Task;

public class TaskNode {
    Task task;
    TaskNode next;
    TaskNode prev;

    TaskNode(Task task, TaskNode prev, TaskNode next) {
        this.task = task;
        this.prev = prev;
        this.next = next;
    }
}
