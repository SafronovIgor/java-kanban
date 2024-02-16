package task.tracker.models;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> idSubtask = new ArrayList<>();

    public void addIdSubtaskToList(Integer id) {
        this.idSubtask.add(id);
    }

    public ArrayList<Integer> getListIdSubtasks() {
        return idSubtask;
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
