package model;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    ArrayList<Integer> subTasksId = new ArrayList<>();

    public Epic (String name, String description) {
        super(Status.NEW, name, description);
    }

    public Epic (Status status, String name, String description) {
        super(status, name, description);
    }

    public ArrayList<Integer> getSubTasksId() {
        return subTasksId;
    }

    public void addSubTaskId(SubTask subTasks) {
        this.subTasksId.add(subTasks.getId());
    }

    public void removeSubTaskId(SubTask subTasks) {
        this.subTasksId.remove(subTasks.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subTasksId, epic.subTasksId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTasksId);
    }
}
