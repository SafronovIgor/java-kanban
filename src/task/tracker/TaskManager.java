package task.tracker;

import task.tracker.models.Epic;
import task.tracker.models.Subtask;
import task.tracker.models.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static int taskCount = -1;
    private final HashMap<Integer, Task> taskHashMap = new HashMap<>();
    private final HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>();
    private final HashMap<Integer, Epic> epicHashMap = new HashMap<>();

    private static int getNewId() {
        return ++taskCount;
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(taskHashMap.values());
    }

    public ArrayList<Task> getAllSubtasks() {
        return new ArrayList<>(subtaskHashMap.values());
    }

    public ArrayList<Task> getAllEpics() {
        return new ArrayList<>(epicHashMap.values());
    }

    public void deleteAllTask() {
        taskHashMap.clear();
    }

    public void deleteAllSubtasks() {
        subtaskHashMap.clear();
    }

    public void deleteAllEpics() {
        epicHashMap.clear();
    }

    public Task getTaskById(Integer id) {
        return taskHashMap.get(id);
    }

    public Subtask getSubtaskById(Integer id) {
        return subtaskHashMap.get(id);
    }

    public Epic getEpicsById(Integer id) {
        return epicHashMap.get(id);
    }

    public void addNewTask(Task newTask) {
        final int NEW_ID = TaskManager.getNewId();

        if (!taskHashMap.containsKey(NEW_ID)) {
            taskHashMap.put(NEW_ID, newTask);
        } else {
            System.out.println("Задача с таким id уже есть в спике.");
        }
    }

    public void addNewSubtask(Subtask subtask, Integer idEpic) {
        final int NEW_ID = TaskManager.getNewId();

        if (!subtaskHashMap.containsKey(NEW_ID)) {
            epicHashMap.get(idEpic).addIdSubtaskToList(NEW_ID);
            subtask.addIdEpicToList(idEpic);
            subtaskHashMap.put(NEW_ID, subtask);
        } else {
            System.out.println("Подзадача с таким id уже есть в спике.");
        }
    }

    public void addNewEpic(Epic epic) {
        final int NEW_ID = TaskManager.getNewId();

        if (!epicHashMap.containsKey(NEW_ID)) {
            epicHashMap.put(NEW_ID, epic);
        } else {
            System.out.println("Эпик с таким id уже есть в спике.");
        }
    }

    public void updateTask(Task task) {
        final int NEW_ID = task.getId();
        taskHashMap.put(NEW_ID, task);
    }

    public void updateSubtask(Subtask subtask) {
        final int NEW_ID = subtask.getId();
        subtaskHashMap.put(NEW_ID, subtask);
    }

    public void updateEpic(Epic epic) {
        final int NEW_ID = epic.getId();
        epicHashMap.put(NEW_ID, epic);
    }

    public void deleteTaskByID(Integer id) {
        taskHashMap.remove(id);
    }

    public void deleteSubtaskByID(Integer id) {
        subtaskHashMap.remove(id);
    }

    public void deleteEpicByID(Integer id) {
        epicHashMap.remove(id);
    }

    public ArrayList<Subtask> getListSubtaskByEpic(Epic epic) {
        ArrayList<Subtask> result = new ArrayList<>();
        epic.getListIdSubtasks().forEach(
                id -> result.add(subtaskHashMap.get(id))
        );
        return result;
    }

    //todo: Управление статусами
}