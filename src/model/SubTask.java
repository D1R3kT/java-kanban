package model;

import java.util.Objects;

public class SubTask extends Task {

    private Integer epicId;

    public SubTask(Status status, String task, String description, Epic epic) {
        super(status, task, description);
        this.epicId = epic.getId();
    }

    public SubTask(int id, Status status, String task, String description, Epic epic) {
        super(id, task, status, description);
        this.epicId = epic.getId();
    }

    public SubTask(int id, String task, Status status, String description, Integer epicId) {
        super(id, task, status, description);
        this.epicId = epicId;
    }


    @Override
    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return epicId == subTask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}
