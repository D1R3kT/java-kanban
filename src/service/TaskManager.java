package service;

import model.Status;
import model.Task;
import model.SubTask;
import model.Epic;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();

    private int seq = 0;

    public int generateId() {
        return ++seq;
    }

    // методы Task
    public void getAllTasks() {
        System.out.println(tasks);
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
        Task saved = tasks.get(task.getId());
        saved.setName(task.getName());
        saved.setDescription(task.getDescription());
        saved.setStatus(task.getStatus());
        tasks.put(task.getId(),saved);
    }

    // методы SubTask
    public void getAllSubTask() {
        System.out.println(subTasks);
    }

    public void removeAllSubTasks() {
        for (Epic epic: epics.values()){
            epics.remove(epic.getId());
        }
        subTasks.clear();
    }

    public SubTask getSubTaskById(int id) {
        return subTasks.get(id);
    }

    public SubTask createSubTask(SubTask subTask) {
        Epic epic =  epics.get(subTask.getEpic().getId());
        subTask.setId(generateId());
        subTasks.put(subTask.getId(), subTask);
        epic.addSubTask(subTask);

        return subTask;
    }

    public void removeByIdSubTask(int id) {
        subTasks.remove(id);
    }

    public void updateSubTask(SubTask subTask) {
        SubTask saved = subTasks.get(subTask.getId());
        saved.setName(subTask.getName());
        saved.setDescription(subTask.getDescription());
        saved.setStatus(subTask.getStatus());
        saved.setEpic(subTask.getEpic());

        subTasks.put(subTask.getId(),saved);
    }



    // методы Epic
    public void getAllEpics() {
        System.out.println(epics);
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
        if (saved == null){
            return;
        }
        saved.setName(epic.getName());
        saved.setDescription(epic.getDescription());
        saved.setStatus(updateStatus(epic));
        epics.put(epic.getId(),saved);
    }

    public Status updateStatus(Epic epic) {
        ArrayList<SubTask> subTasks = getAllSubTasks(epic);
        Status status;
        int statusNew = 0;
        int statusDone = 0;
        int statusProgress = 0;
        for (SubTask subTask : subTasks){
            if (subTask.getStatus().equals(Status.NEW)) {
                statusNew++;
            } else if (subTask.getStatus().equals(Status.IN_PROGRESS)) {
                statusProgress++;
            } else if (subTask.getStatus().equals(Status.DONE)) {
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

    public ArrayList<SubTask> getAllSubTasks(Epic epic){
        Epic saved = epics.get(epic.getId());
        return saved.getSubTasks();
    }



}
