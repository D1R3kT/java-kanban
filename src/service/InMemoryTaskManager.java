package service;

import exception.NotFoundException;
import exception.ValidationException;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    Map<Integer, Task> tasks;
    Map<Integer, SubTask> subTasks;
    Map<Integer, Epic> epics;
    HistoryManager historyManager;

    TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

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
        Task task = tasks.get(id);
        if (task == null) {
            throw new NotFoundException("зада с id = " + id);
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public Task createTask(Task task) throws ValidationException {
        task.setId(generateId());

        checkTaskTime(task);
        prioritizedTasks.add(task);

        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public void removeByIdTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void updateTask(Task task) {
        Task original = tasks.get(task.getId());

        if (original == null) {
            throw new NotFoundException("Task id = " + task.getId() + " не найдена");
        }
        checkTaskTime(task);
        prioritizedTasks.remove(original);
        prioritizedTasks.add(task);

        tasks.put(task.getId(), task);

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

        checkTaskTime(subTask);
        prioritizedTasks.add(subTask);

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
            throw new NotFoundException("Не найден эпик");
        }

        if (subTasks.containsKey(subTask.getId())) {
            saved.setName(subTask.getName());
            saved.setDescription(subTask.getDescription());
            saved.setStatus(subTask.getStatus());
            saved.setEpicId(subTask.getEpicId());

            checkTaskTime(saved);
            prioritizedTasks.remove(saved);
            prioritizedTasks.add(subTask);
            subTasks.put(subTask.getId(), saved);
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

        Epic deleteEpic = epics.get(id);
        ArrayList<Integer> subTaskDeleteList = deleteEpic.getSubTasksId();

        for (Integer i : subTaskDeleteList) {
            subTasks.remove(i);
        }

        epics.remove(id);
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic saved = epics.get(epic.getId());
        if (saved == null) {
            throw new NotFoundException("Не найден эпик: " + epic.getId());
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
            if (subTasks.get(id).getStatus().equals(Status.NEW)) {
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

    @Override
    public SubTask getAllSubTasks(Epic epic) {
        return subTasks.get(epic.getSubTasksId());
    }

    private void checkTaskTime(Task task) throws ValidationException {
        for (Task t : prioritizedTasks) {
            if (t.getId() == task.getId()) {
                continue;
            }
            if (task.getStartTime() == null || t.getStartTime() == null) {
                return;
            }
            if ((task.getStartTime().isBefore(t.getEndTime()) && task.getEndTime().isAfter(t.getStartTime())) ||
                    t.getStartTime().isBefore(task.getEndTime()) && t.getEndTime().isAfter(task.getStartTime())) {

                throw new ValidationException("Задача с id: " + task.getId() + " пересекается с задачей " + t.getId());
            }
        }
    }

}
