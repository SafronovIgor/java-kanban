package task.models;

import java.util.ArrayList;

public class Subtask extends Task {
    private final ArrayList<Integer> idEpics = new ArrayList<>();
    private final TaskType taskType = TaskType.SUBTASK;

    public void addIdEpicToList(Integer id) {
        this.idEpics.add(id);
    }

    public ArrayList<Integer> getIdEpics() {
        return idEpics;
    }

    public String toString(String delimiter) {
        final String[] properties = {
                String.valueOf(getId()),
                String.valueOf(getTaskType()),
                getName(),
                String.valueOf(getStatus()),
                getDescription(),
                String.valueOf(getStartTime()),
                getDuration().toString(),
                getEndTime().toString(),
                idEpics.toString()};
        return String.join(delimiter, properties);
    }

    @Override
    public TaskType getTaskType() {
        return taskType;
    }

    @Override
    public Subtask fromString(String value, String delimiter) {
        super.fromString(value, delimiter);

        int startIndex = value.indexOf('[');
        int endIndex = value.indexOf(']');
        String innerValue = value.substring(startIndex + 1, endIndex);
        String[] parts = innerValue.split(delimiter);

        for (int i = 0; i < parts.length; i++) {
            idEpics.add(Integer.parseInt(parts[i].trim()));
        }

        return this;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", startTime=" + getStartTime() +
                ", duration=" + getDuration() +
                ", endTime=" + getEndTime() +
                ", idEpics{" + idEpics +
                '}';
    }
}