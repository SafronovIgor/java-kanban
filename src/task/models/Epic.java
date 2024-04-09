package task.models;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> idSubtask = new ArrayList<>();
    private TaskType taskType = TaskType.EPIC;

    public void addIdSubtaskToList(Integer id) {
        this.idSubtask.add(id);
    }

    public ArrayList<Integer> getListIdSubtasks() {
        return idSubtask;
    }

    @Override
    public TaskType getTaskType() {
        return taskType;
    }

    @Override
    public Epic fromString(String value, String delimiter) {
        return (Epic) super.fromString(value, delimiter);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", idSubtask{" + idSubtask +
                '}';
    }
}
