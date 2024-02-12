package task.tracker.models;

import java.util.ArrayList;

public class Subtask extends Task {
    private ArrayList<Integer> idEpics = new ArrayList<>();
    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                '}';
    }

    public void addIdEpicToList(Integer id) {
        this.idEpics.add(id);
    }

    public ArrayList<Integer> getIdEpics() {
        return idEpics;
    }
}