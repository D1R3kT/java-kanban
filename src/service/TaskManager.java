package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, SubTask> subTasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();

    private int seq = 0;

    public int generateId() {
        return ++seq;
    }

    // методы Task

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    public void removeByIdTask(int id) {
        tasks.remove(id);
    }

    public void updateTask(Task task) {
        if (task == null) {
            return;
        }
        if (tasks.containsKey(task.getId())){
            tasks.put(task.getId(), task);
        } else {
            return;
        }

    }

    // методы SubTask

    public ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public void removeAllSubTasks() {
        for (Epic epic : epics.values()) {
            epics.remove(epic.getId());
        }
        subTasks.clear();
    }

    public SubTask getSubtaskById(int id) {
        return subTasks.get(id);
    }

    public SubTask createSubTask(SubTask subTask) {
        Epic epic = epics.get(subTask.getEpicId());
        subTask.setId(generateId());
        subTasks.put(subTask.getId(), subTask);
        epic.addSubTaskId(subTask);

        return subTask;
    }

    public void removeByIdSubTask(int id) {
        subTasks.remove(id);
    }

    public void updateSubTask(SubTask subTask) {
        SubTask saved = subTasks.get(subTask.getId());
        if (saved == null) {
            return;
        }
        if (subTasks.containsKey(subTask.getId())){
            saved.setName(subTask.getName());
            saved.setDescription(subTask.getDescription());
            saved.setStatus(subTask.getStatus());
            saved.setEpicId(subTask.getEpicId());
            subTasks.put(subTask.getId(), saved);
        } else {
            return;
        }
    }

    // методы Epic

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void removeAllEpics() {
        subTasks.clear();
        epics.clear();
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    public void removeByIdEpic(int id) {
        epics.remove(id);
    }

    public void updateEpic(Epic epic) {
        Epic saved = epics.get(epic.getId());
        if (saved == null) {
            return;
        }
        if (epics.containsKey(epic.getId())){
            saved.setName(epic.getName());
            saved.setDescription(epic.getDescription());
            saved.setStatus(updateStatus(epic));
            epics.put(epic.getId(), saved);
        } else{
            return;
        }

    }

    public Status updateStatus(Epic epic) {
        ArrayList<Integer> subTasksId = epic.getSubTasksId();

        Status status;
        int statusNew = 0;
        int statusDone = 0;
        int statusProgress = 0;

        for (Integer id : subTasksId) {
            if (subTasks.get(id).getStatus().equals(Status.NEW)){
                statusNew++;
            } else if (subTasks.get(id).getStatus().equals(Status.IN_PROGRESS)) {
                statusProgress++;
            } else if (subTasks.get(id).getStatus().equals(Status.DONE)) {
                statusDone++;
            }
        }

        if (statusDone == 0 && statusProgress == 0) {
            status = Status.NEW;
        } else if (statusNew == 0 && statusProgress == 0) {
            status = Status.DONE;
        } else {
            status = Status.IN_PROGRESS;
        }

        return status;
    }

    public SubTask getAllSubTasks(Epic epic) {
        return subTasks.get(epic.getSubTasksId());
    }
}
