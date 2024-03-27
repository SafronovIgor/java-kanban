package task.service;

import task.models.Epic;
import task.models.Status;
import task.models.Subtask;
import task.models.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {

    private static int taskCount = -1;
    private final HashMap<Integer, Task> taskHashMap = new HashMap<>();
    private final HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>();
    private final HashMap<Integer, Epic> epicHashMap = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistoryManager();

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }

    @Override
    public void changeStatus(Subtask subtask, Status status) {
        subtask.setStatus(status);
        updateSubtask(subtask);
    }

    private static int getNewId() {
        return ++taskCount;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        Collection<Task> values = taskHashMap.values();
        for (Task t : values){
            historyManager.add(t);
        }
        return new ArrayList<>(values);
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        Collection<Subtask> values = subtaskHashMap.values();
        for (Subtask t : values){
            historyManager.add(t);
        }
        return new ArrayList<>(values);
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        Collection<Epic> values = epicHashMap.values();
        for (Epic t : values){
            historyManager.add(t);
        }
        return new ArrayList<>(values);
    }

    @Override
    public void deleteAllTask() {
        taskHashMap.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtaskHashMap.clear();
    }

    @Override
    public void deleteAllEpics() {
        epicHashMap.clear();
    }

    @Override
    public Task getTaskById(Integer id) {
        Task task = taskHashMap.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        Subtask subtask = subtaskHashMap.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpicsById(Integer id) {
        Epic epic = epicHashMap.get(id);
        getHistory().add(epic);
        return epic;
    }

    @Override
    public void addNewTask(Task newTask) {
        final int NEW_ID = InMemoryTaskManager.getNewId();

        if (!taskHashMap.containsKey(NEW_ID)) {
            newTask.setId(NEW_ID);
            taskHashMap.put(NEW_ID, newTask);
            historyManager.add(newTask);
        } else {
            updateTask(newTask);
        }
    }

    @Override
    public void addNewSubtask(Subtask subtask, Integer idEpic) {
        final int NEW_ID = InMemoryTaskManager.getNewId();

        if (!subtaskHashMap.containsKey(NEW_ID)) {
            epicHashMap.get(idEpic).addIdSubtaskToList(NEW_ID);
            subtask.addIdEpicToList(idEpic);
            subtask.setId(NEW_ID);
            subtaskHashMap.put(NEW_ID, subtask);
            historyManager.add(subtask);
        } else {
            updateSubtask(subtask);
        }
    }

    @Override
    public void addNewEpic(Epic epic) {
        final int NEW_ID = InMemoryTaskManager.getNewId();

        if (!epicHashMap.containsKey(NEW_ID)) {
            epic.setId(NEW_ID);
            epicHashMap.put(NEW_ID, epic);
            historyManager.add(epic);
        } else {
            updateEpic(epic);
        }
    }

    @Override
    public void updateTask(Task task) {
        final int NEW_ID = task.getId();
        taskHashMap.put(NEW_ID, task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        final int NEW_ID = subtask.getId();
        ArrayList<Integer> idEpics = subtask.getIdEpics();

        for (int idEpic : idEpics) {
            Epic epic = epicHashMap.get(idEpic);
            updateEpic(epic);
        }

        subtaskHashMap.put(NEW_ID, subtask);
    }

    @Override
    public void updateEpic(Epic epic) {
        final int NEW_ID = epic.getId();
        ArrayList<Integer> listIdSubtasks = epic.getListIdSubtasks();
        boolean allSubtasksNew = true;
        boolean allSubtasksDone = true;

        for (int idSubtask : listIdSubtasks) {
            Subtask subtask = subtaskHashMap.get(idSubtask);
            Status status = subtask.getStatus();

            if (status != Status.NEW) {
                allSubtasksNew = false;
            }
            if (status != Status.DONE) {
                allSubtasksDone = false;
            }
        }

        if (allSubtasksNew) {
            epic.setStatus(Status.NEW);
        } else if (allSubtasksDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }

        epicHashMap.put(NEW_ID, epic);
    }

    @Override
    public void deleteTaskByID(Integer id) {
        historyManager.add(taskHashMap.get(id));
        taskHashMap.remove(id);
    }

    @Override
    public void deleteSubtaskByID(Integer id) {
        historyManager.add(subtaskHashMap.get(id));
        subtaskHashMap.remove(id);
    }

    @Override
    public void deleteEpicByID(Integer id) {
        historyManager.add(epicHashMap.get(id));
        epicHashMap.remove(id);
    }

    @Override
    public ArrayList<Subtask> getListSubtaskByEpic(Epic epic) {
        ArrayList<Subtask> result = new ArrayList<>();
        epic.getListIdSubtasks().forEach(
                id -> {
                    Subtask subtask = subtaskHashMap.get(id);
                    result.add(subtask);
                    historyManager.add(subtask);
                }
        );
        return result;
    }

    @Override
    public HashMap<Integer, Task> getTaskHashMap() {
        return taskHashMap;
    }

    @Override
    public HashMap<Integer, Subtask> getSubtaskHashMap() {
        return subtaskHashMap;
    }

    @Override
    public HashMap<Integer, Epic> getEpicHashMap() {
        return epicHashMap;
    }

}
