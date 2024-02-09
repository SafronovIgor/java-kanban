package task.tracker.models;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> idSubtask = new ArrayList<>();
    @Override
    public String toString() {
        return "Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                '}';
    }

    public void addIdSubtaskToList(Integer id) {
        this.idSubtask.add(id);
    }

    public ArrayList<Integer> getListIdSubtasks() {
        return idSubtask;
    }
}
