package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getAllTasks();

    void removeAllTasks();

    Task getTaskById(int id);

    Task createTask(Task task);

    void removeByIdTask(int id);

    void updateTask(Task task);

    List<SubTask> getAllSubTasks();

    void removeAllSubTasks();

    SubTask getSubtaskById(int id);

    SubTask createSubTask(SubTask subTask);

    void removeByIdSubTask(int id);

    void updateSubTask(SubTask subTask);

    List<Epic> getAllEpics();

    void removeAllEpics();

    Epic getEpicById(int id);

    Epic createEpic(Epic epic);

    void removeByIdEpic(int id);

    void updateEpic(Epic epic);

    Status updateStatus(Epic epic);

    SubTask getAllSubTasks(Epic epic);

}
