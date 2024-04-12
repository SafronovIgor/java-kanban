package task.models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private int id;
    private String name;
    private String description;
    private Status status = Status.NEW;
    private TaskType taskType = TaskType.TASK;
    private LocalDateTime startTime;
    private Duration duration = Duration.ZERO;

    public LocalDateTime getEndTime() {
        return startTime.plusSeconds(duration.toSeconds());
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Task fromString(String value, String delimiter) {
        final String[] taskData = value.split(delimiter);

        setId(Integer.parseInt(taskData[0]));
        setTaskType(TaskType.valueOf(taskData[1]));
        setName(taskData[2]);
        setStatus(Status.valueOf(taskData[3]));
        setDescription(taskData[4]);
        setStartTime(LocalDateTime.parse(taskData[5]));
        setDuration(Duration.parse(taskData[6]));
        return this;
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
                ""};

        return String.join(delimiter, properties);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", endTime=" + getEndTime() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
