package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.List;
import java.util.TreeSet;

public interface TaskManager {
    List<Task> getAllTasks();

    void removeAllTasks();

    Task getTaskById(int id);

    Task createTask(Task task);

    void removeByIdTask(int id);

    Task updateTask(Task task);

    List<SubTask> getAllSubTasks();

    void removeAllSubTasks();

    SubTask getSubtaskById(int id);

    SubTask createSubTask(SubTask subTask);

    void removeByIdSubTask(int id);

    SubTask updateSubTask(SubTask subTask);

    List<Epic> getAllEpics();

    void removeAllEpics();

    Epic getEpicById(int id);

    Epic createEpic(Epic epic);

    void removeByIdEpic(int id);

    Epic updateEpic(Epic epic);

    Status updateStatus(Epic epic);

    SubTask getAllSubTasks(Epic epic);

    List<Task> getHistory();

    void removeTaskFromViewed(int id);

    TreeSet<Task> getPrioritisedTasks();
}
