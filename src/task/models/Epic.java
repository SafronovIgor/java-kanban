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

    public String toString(String delimiter) {
        final String[] properties = {
                String.valueOf(getId()),
                String.valueOf(getTaskType()),
                getName(),
                String.valueOf(getStatus()),
                getDescription(),
                ""};
        return String.join(delimiter, properties);
    }

    @Override
    public TaskType getTaskType() {
        return taskType;
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
