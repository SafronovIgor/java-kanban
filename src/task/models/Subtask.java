package task.models;

import java.util.ArrayList;

public class Subtask extends Task {
    private final ArrayList<Integer> idEpics = new ArrayList<>();

    public void addIdEpicToList(Integer id) {
        this.idEpics.add(id);
    }

    public ArrayList<Integer> getIdEpics() {
        return idEpics;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", idEpics{" + idEpics +
                '}';
    }
}