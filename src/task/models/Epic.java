package task.models;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> idSubtask = new ArrayList<>();

    private LocalDateTime endTime;

    public Epic() {
        super.setTaskType(TaskType.EPIC);
    }

    public void addIdSubtaskToList(Integer id) {
        this.idSubtask.add(id);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public ArrayList<Integer> getListIdSubtasks() {
        return idSubtask;
    }

    @Override
    public TaskType getTaskType() {
        return super.getTaskType();
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
                ", startTime=" + getStartTime() +
                ", duration=" + getDuration() +
                ", endTime=" + getEndTime() +
                ", idSubtask{" + idSubtask +
                '}';
    }
}
