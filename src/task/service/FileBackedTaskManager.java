package task.service;

import task.Exceptions.ManagerSaveException;
import task.models.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final String pathToFile;

    public FileBackedTaskManager() {
        try {
            this.pathToFile = Files.createTempFile("back_", ".txt").toString();
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    public FileBackedTaskManager(String pathToFile) {
        this.pathToFile = pathToFile;
    }

    public static ArrayList<Task> loadFromFile(File file) {
        ArrayList<Task> list = new ArrayList<>();
        try {
            String stringData = Files.readString(file.toPath());
            String[] lines = stringData.split("\n");
            String[] linesWithoutHeader = Arrays.copyOfRange(lines, 1, lines.length);
            String delimiter = ",";

            for (String line : linesWithoutHeader) {
                String[] dataTask = line.split(delimiter);
                switch (TaskType.valueOf(dataTask[1])) {
                    case TASK: {
                        Task task = new Task().fromString(line, delimiter);
                        list.add(task);
                        break;
                    }
                    case EPIC: {
                        Epic task = new Epic().fromString(line, delimiter);
                        list.add(task);
                        break;
                    }
                    case SUBTASK: {
                        Subtask subtask = new Subtask().fromString(line, delimiter);
                        list.add(subtask);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
        return list;
    }

    public void save() {
        Path filePath = Path.of(this.pathToFile);
        String[] columns = {"id", "type", "name", "status", "description", "epic", "startTime", "duration", "endTime"};
        String delimiter = ",";
        LinkedList<Task> list = new LinkedList<>();

        list.addAll(super.getAllTasks());
        list.addAll(super.getAllEpics());
        list.addAll(super.getAllSubtasks());

        if (!Files.isRegularFile(filePath)) {
            throw new ManagerSaveException("Произошла ошибка файл не найден.");
        }

        try (FileWriter fileWriter = new FileWriter(pathToFile, StandardCharsets.UTF_8)) {
            fileWriter.write(String.join(delimiter, columns));
            for (Task t : list) {
                fileWriter.write(System.lineSeparator());
                fileWriter.write(t.toString(delimiter));
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    public String getPathToFile() {
        return pathToFile;
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = super.getHistory();
        save();
        return history;
    }

    @Override
    public void changeStatus(Subtask subtask, Status status) {
        super.changeStatus(subtask, status);
        save();
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = super.getAllTasks();
        save();
        return allTasks;
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> allSubtasks = super.getAllSubtasks();
        save();
        return allSubtasks;
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> allEpics = super.getAllEpics();
        save();
        return allEpics;
    }

    @Override
    public void deleteAllTask() {
        super.deleteAllTask();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public Task getTaskById(Integer id) {
        Task taskById = super.getTaskById(id);
        save();
        return taskById;
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        Subtask subtaskById = super.getSubtaskById(id);
        save();
        return subtaskById;
    }

    @Override
    public Epic getEpicsById(Integer id) {
        Epic epicsById = super.getEpicsById(id);
        save();
        return epicsById;
    }

    @Override
    public void addNewTask(Task newTask) {
        super.addNewTask(newTask);
        save();
        super.addTaskInToPrioritizedTasks(newTask);
    }

    @Override
    public void addNewSubtask(Subtask subtask, Integer idEpic) {
        super.addNewSubtask(subtask, idEpic);
        save();
        super.addTaskInToPrioritizedTasks(subtask);
    }

    @Override
    public void addNewEpic(Epic epic) {
        super.addNewEpic(epic);
        save();
        super.addTaskInToPrioritizedTasks(epic);
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTaskByID(Integer id) {
        super.deleteTaskByID(id);
        save();
    }

    @Override
    public void deleteSubtaskByID(Integer id) {
        super.deleteSubtaskByID(id);
        save();
    }

    @Override
    public void deleteEpicByID(Integer id) {
        super.deleteEpicByID(id);
        save();
    }

    @Override
    public ArrayList<Subtask> getListSubtaskByEpic(Epic epic) {
        ArrayList<Subtask> listSubtaskByEpic = super.getListSubtaskByEpic(epic);
        save();
        return listSubtaskByEpic;
    }

    @Override
    public HashMap<Integer, Task> getTaskHashMap() {
        HashMap<Integer, Task> taskHashMap = super.getTaskHashMap();
        save();
        return taskHashMap;
    }

    @Override
    public HashMap<Integer, Subtask> getSubtaskHashMap() {
        HashMap<Integer, Subtask> subtaskHashMap = super.getSubtaskHashMap();
        save();
        return subtaskHashMap;
    }

    @Override
    public HashMap<Integer, Epic> getEpicHashMap() {
        HashMap<Integer, Epic> epicHashMap = super.getEpicHashMap();
        save();
        return epicHashMap;
    }

    @Override
    public void recalculateDuration(Subtask subtask, int id) {
        super.recalculateDuration(subtask, id);
        save();
    }
}
