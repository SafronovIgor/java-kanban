package task.service;

public class Managers {

    public static TaskManager getDefaultTaskManager() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getFileBackedTaskManager(String pathToSaveFile) {
        return new FileBackedTaskManager(pathToSaveFile);
    }
}
