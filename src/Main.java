import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.FileBackedTaskManager;
import service.Manager;
import service.TaskManager;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;


public class Main {

    public static void main(String[] args) {


        TaskManager taskManager = Manager.getDefaults();
        //Task
        Task task = taskManager.createTask(new Task(Status.NEW, "Новая задача", "описание", LocalDateTime.parse("2024-12-21T21:21:21"), Duration.ofMinutes(15)));
        System.out.println("Create task" + task);

        Task task1 = taskManager.createTask(new Task(Status.NEW, "учеба", "12341",
                LocalDateTime.parse("2025-12-20T21:21:21"), Duration.ofMinutes(105)));
        System.out.println("created " + task1);

        Task task2 = taskManager.createTask(new Task(Status.NEW, "работа", "1234",
                LocalDateTime.parse("2026-12-21T21:21:21"), Duration.ofMinutes(15)));
        System.out.println("created " + task2);

        Task taskFromManager = taskManager.getTaskById(task.getId());
        System.out.println("Get task" + taskFromManager);

        Task taskUpdated = new Task(taskFromManager.getId(), "новая задача", Status.IN_PROGRESS, "о");
        taskManager.updateTask(taskUpdated);
        System.out.println("Update task: " + taskUpdated);

        //Epic
        Epic epic = taskManager.createEpic(new Epic("Новый эпик", "описание"));
        System.out.println("Create epic: " + epic);

        Epic epic1 = taskManager.createEpic(new Epic("Эпик1", "Описание1"));
        System.out.println("epic1: " + epic1);

        Epic epic2 = taskManager.createEpic(new Epic("Эпик2", "Описание2"));
        System.out.println("epic2: " + epic2);

        //SubTask
        SubTask subTask = taskManager.createSubTask(new SubTask(Status.NEW, "Новая задача", "описание подзадачи", epic));
        taskManager.updateEpic(epic);
        taskManager.updateSubTask(subTask);
        SubTask subTask1 = taskManager.createSubTask(new SubTask(Status.DONE, "Подзадача 1", "Описание1", epic1));
        SubTask subTask2 = taskManager.createSubTask(new SubTask(Status.NEW, "Подзадача 2", "Описание2", epic1));
        SubTask subTask3 = taskManager.createSubTask(new SubTask(Status.IN_PROGRESS, "Подзадача 1", "Описание3", epic2));
        taskManager.updateEpic(epic);
        taskManager.updateEpic(epic1);
        taskManager.updateEpic(epic2);

        FileBackedTaskManager newTaskManager = FileBackedTaskManager.loadFromFile(new File("task.csv"));
        System.out.println(taskManager.getAllTasks().equals(newTaskManager.getAllTasks()));
        System.out.println(taskManager.getAllEpics().equals(newTaskManager.getAllEpics()));
        System.out.println(taskManager.getAllSubTasks().equals(newTaskManager.getAllSubTasks()));

    }
}