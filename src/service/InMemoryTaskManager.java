package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
     Map<Integer, Task> tasks;
     Map<Integer, SubTask> subTasks;
     Map<Integer, Epic> epics;
     HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
        this.tasks = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.epics = new HashMap<>();
    }

    private int seq = 0;

    public int generateId() {
        return ++seq;
    }

    // методы Task

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public void removeByIdTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void updateTask(Task task) {
        if (task == null) {
            return;
        }
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            return;
        }

    }

    // методы SubTask

    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void removeAllSubTasks() {
        for (Epic epic : epics.values()) {
            epics.remove(epic.getId());
        }
        subTasks.clear();
    }

    @Override
    public SubTask getSubtaskById(int id) {
        historyManager.add(subTasks.get(id));
        return subTasks.get(id);
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        Epic epic = epics.get(subTask.getEpicId());
        subTask.setId(generateId());
        subTasks.put(subTask.getId(), subTask);
        epic.addSubTaskId(subTask);

        return subTask;
    }

    @Override
    public void removeByIdSubTask(int id) {
        subTasks.remove(id);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        SubTask saved = subTasks.get(subTask.getId());
        if (saved == null) {
            return;
        }
        if (subTasks.containsKey(subTask.getId())) {
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

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeAllEpics() {
        subTasks.clear();
        epics.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public void removeByIdEpic(int id) {
        epics.remove(id);
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic saved = epics.get(epic.getId());
        if (saved == null) {
            return;
        }
        if (epics.containsKey(epic.getId())) {
            saved.setName(epic.getName());
            saved.setDescription(epic.getDescription());
            saved.setStatus(updateStatus(epic));
            epics.put(epic.getId(), saved);
        }

    }

    @Override
    public Status updateStatus(Epic epic) {
        ArrayList<Integer> subTasksId = epic.getSubTasksId();

        Status status;
        int statusNew = 0;
        int statusDone = 0;
        int statusProgress = 0;

        for (Integer id : subTasksId) {
            if (subTasks.get(id).getStatus().equals(Status.NEW)){
                statusNew++;
            }else if (subTasks.get(id).getStatus().equals(Status.IN_PROGRESS)){
                statusProgress++;
            }else if (subTasks.get(id).getStatus().equals(Status.DONE)){
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

    @Override
    public SubTask getAllSubTasks(Epic epic) {
        return subTasks.get(epic.getSubTasksId());
    }


}
