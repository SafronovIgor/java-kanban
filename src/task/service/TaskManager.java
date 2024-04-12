package task.service;

import task.models.Epic;
import task.models.Status;
import task.models.Subtask;
import task.models.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface TaskManager {

    List<Task> getHistory();

    void changeStatus(Subtask subtask, Status status);

    ArrayList<Task> getAllTasks();

    ArrayList<Subtask> getAllSubtasks();

    ArrayList<Epic> getAllEpics();

    void deleteAllTask();

    void deleteAllSubtasks();

    void deleteAllEpics();

    Task getTaskById(Integer id);

    Subtask getSubtaskById(Integer id);

    Epic getEpicsById(Integer id);

    void addNewTask(Task newTask);

    void addNewSubtask(Subtask subtask, Integer idEpic);

    void addNewEpic(Epic epic);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    void deleteTaskByID(Integer id);

    void deleteSubtaskByID(Integer id);

    void deleteEpicByID(Integer id);

    ArrayList<Subtask> getListSubtaskByEpic(Epic epic);

    HashMap<Integer, Task> getTaskHashMap();

    HashMap<Integer, Subtask> getSubtaskHashMap();

    HashMap<Integer, Epic> getEpicHashMap();

    void recalculateDuration(Subtask subtask, int id);
}
