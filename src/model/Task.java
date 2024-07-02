package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private int id;
    private String name;
    private String description;
    protected Status status;

    LocalDateTime startTime; //LocalDateTime
    Duration duration;
    LocalDateTime endTime;

    public Task(Status status, String name, String description) {
        this.status = status;
        this.name = name;
        this.description = description;
        this.startTime = LocalDateTime.now();
        this.duration = Duration.ofMinutes(15);
        this.endTime = startTime.plus(duration);
    }

    public Task(int id, String name, Status status, String description) {
        this.id = id;
        this.status = status;
        this.name = name;
        this.description = description;
        this.startTime = LocalDateTime.now();
        this.duration = Duration.ofMinutes(15);
        this.endTime = startTime.plus(duration);
    }

    public Task(int id, String name, Status status, String description, LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.status = status;
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
        this.endTime = startTime.plus(duration);
    }

    public Task(Status status, String name, String description, LocalDateTime startTime, Duration duration) {
        this.status = status;
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
        this.endTime = startTime.plus(duration);
    }


    public Task(int id, String name) {
        this.name = name;
        this.id = id;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getEpicId() {
        return null;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status);
    }
}
