import model.Task;
import model.Epic;
import model.SubTask;
import model.Status;

import service.HistoryManager;
import service.InMemoryHistoryManager;
import service.InMemoryTaskManager;
import service.TaskManager;


public class Main {

    public static void main(String[] args) {

        HistoryManager historyManager = new InMemoryHistoryManager();
        TaskManager taskManager = new InMemoryTaskManager(historyManager);

        Task task1 =  taskManager.createTask(new Task(Status.NEW,"учеба", "12341"));
        System.out.println("created " + task1);

        Task task2 = taskManager.createTask(new Task(Status.NEW,"работа", "1234"));
        System.out.println("created " + task2);




        Epic epic1 = taskManager.createEpic(new Epic("Эпик1", "Описание1"));
        Epic epic2 = taskManager.createEpic(new Epic("Эпик2", "Описание2"));
        System.out.println("epic1: " + epic1);
        System.out.println("epic2: " + epic2);

        SubTask subTask1 = taskManager.createSubTask(new SubTask(Status.NEW, "Подзадача 1", "Описание1", epic1));
        SubTask subTask2 = taskManager.createSubTask(new SubTask(Status.NEW, "Подзадача 2", "Описание2", epic1));
        SubTask subTask3 = taskManager.createSubTask(new SubTask(Status.IN_PROGRESS, "Подзадача 1", "Описание3", epic2));
        taskManager.updateEpic(epic1);
        taskManager.updateEpic(epic2);

        System.out.println("subTask1: " + subTask1);
        System.out.println("subTask2: " + subTask2);
        System.out.println("subTask3: " + subTask3);
        System.out.println("epic1: " + epic1);
        System.out.println("epic2: " + epic2);


        Task task1Updated = new Task(task1.getId(), Status.IN_PROGRESS, "Новая задача обновление", task1.getDescription());
        taskManager.updateTask(task1Updated);
        System.out.println("update task " + task1Updated);

        Task task2Updated = new Task(task2.getId(), Status.DONE, "Новая задача обновление", task1.getDescription());
        taskManager.updateTask(task2Updated);
        System.out.println("update task " + task2Updated);

        SubTask subTask1Update = new SubTask(subTask1.getId(), Status.DONE, subTask1.getName(), subTask1.getDescription(),epic1);
        SubTask subTask2Update = new SubTask(subTask2.getId(), Status.NEW, subTask2.getName(), subTask2.getDescription(),epic1);
        SubTask subTask3Update = new SubTask(subTask3.getId(), Status.DONE, subTask3.getName(), subTask3.getDescription(),epic2);
        taskManager.updateSubTask(subTask1Update);
        taskManager.updateSubTask(subTask2Update);
        taskManager.updateSubTask(subTask3Update);
        taskManager.updateEpic(epic1);
        taskManager.updateEpic(epic2);

        System.out.println("subTask1Update: " + subTask1Update);
        System.out.println("subTask2Update: " + subTask2Update);
        System.out.println("subTask3Update: " + subTask3Update);

        System.out.println("epic1: " + epic1);
        System.out.println("epic2: " + epic2);

        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        historyManager.remove(1);

        System.out.println(historyManager.getHistory());

    }
}