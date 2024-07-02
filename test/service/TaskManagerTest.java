package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("TaskManagerTest")
public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    abstract T createManager();

    Task task;
    Epic epic;
    SubTask subTask1;
    SubTask subTask2;

    private List<Task> tasks;
    private List<Epic> epics;
    private List<SubTask> subTasks;


    @BeforeEach
    protected void init() {
        taskManager = createManager();

        task = taskManager.createTask(new Task(Status.NEW, "Новая задача", "описание",
                LocalDateTime.parse("2024-12-21T21:21:21"), Duration.ofMinutes(15)));

        epic = taskManager.createEpic(new Epic("Новый эпик", "описание"));

        subTask1 = taskManager.createSubTask(new SubTask(Status.NEW, "Новая подзадача №1",
                "описание подзадачи", epic));
        subTask2 = taskManager.createSubTask(new SubTask(Status.NEW, "Новая подзадача №2 ",
                "описание подзадачи", epic,
                LocalDateTime.parse("2026-12-21T21:21:21"), Duration.ofMinutes(15)));
    }

    //TASK
    @Test
    @DisplayName("Добваление Task")
    void shouldGetTasks() {
        tasks = taskManager.getAllTasks();
        assertEquals(1, tasks.size(), "Количество задач не совпадает");
    }

    @Test
    @DisplayName("Удаление AllTask")
    void shouldRemoveAllTasks() {
        taskManager.removeAllTasks();
        tasks = taskManager.getAllTasks();
        assertEquals(0, tasks.size(), "Ошибка при удалении задач");
    }

    @Test
    @DisplayName("Создание Task")
    void shouldCreateTask() {
        assertEquals(1, task.getId(), "Неверный Id");
        assertEquals(Status.NEW, task.getStatus(), "Неверный статус");
        assertEquals("Новая задача", task.getName(), "Неверное название");
        assertEquals("описание", task.getDescription(), "Неверное описание");
    }

    @Test
    @DisplayName("Обновление Task")
    void shouldUpdateTask() {
        task.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task);
        Task updatedTask = taskManager.getTaskById(task.getId());
        assertEquals(Status.IN_PROGRESS, updatedTask.getStatus(), "Некорректное обновление задачи");
    }

    @Test
    @DisplayName("Удаление Task по ID")
    void shouldDeleteTask() {
        taskManager.removeByIdTask(task.getId());
        tasks = taskManager.getAllTasks();
        assertEquals(0, tasks.size(), "Некорректное удаление задачи по id");
    }

    //EPIC
    @Test
    @DisplayName("Удаление AllEpics")
    void shouldDeleteEpics() {
        taskManager.removeAllEpics();
        epics = taskManager.getAllEpics();
        assertEquals(0, epics.size(), "Некорректное удаление эпиков");
    }

    @Test
    @DisplayName("Создание Epic")
    void shouldCreateEpic() {
        assertEquals(2, epic.getId(), "Неверный id");
        assertEquals("Новый эпик", epic.getName(), "Неверное имя");
        assertEquals("описание", epic.getDescription(), "Неверное описание");
        assertEquals(Status.NEW, epic.getStatus(), "Неверный статус");
    }

    @Test
    @DisplayName("Получение Epic по id")
    void shouldGetEpicById() {
        Epic getEpic = taskManager.getEpicById(epic.getId());
        assertEquals(epic, getEpic, "Некорретный поиск эпика по id");
    }

    @Test
    @DisplayName("Удаление Task by Id")
    void shouldDeleteEpicById() {
        taskManager.removeByIdEpic(epic.getId());
        epics = taskManager.getAllEpics();
        assertEquals(0, epics.size(), "Некорректное удаление Epic по Id");
    }

    @Test
    @DisplayName("Обновление Epic")
    void shouldUpdateEpic() {
        epic.setDescription("Обновленное описание");

        taskManager.updateEpic(epic);
        Epic updatedEpic = taskManager.getEpicById(epic.getId());
        assertEquals("Обновленное описание", updatedEpic.getDescription(),
                "Некорректное обновление Epic");
    }

    @Test
    @DisplayName("Граничные условия по расчету статуса Epic'a")
    void shouldUpdateStatus() {

        //Все подзадачи со статусом NEW
        taskManager.updateEpic(epic);
        Epic updatedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(Status.NEW, updatedEpic.getStatus(), "Статус не совпадает с NEW");

        //Подзадачи со статусами DONE & NEW
        subTask1.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask1);
        taskManager.updateEpic(epic);
        updatedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(Status.IN_PROGRESS, updatedEpic.getStatus(), "Статус не совпадает с IN_PROGRESS");

        // Все подзадачи со статусом DONE
        subTask2.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask2);
        taskManager.updateEpic(epic);
        updatedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(Status.DONE, updatedEpic.getStatus(), "Статус не совпадает с DONE");

        //Подзадачи со статусом IN_PROGRESS
        subTask1.setStatus(Status.IN_PROGRESS);
        subTask2.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask2);
        taskManager.updateEpic(epic);
        updatedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(Status.IN_PROGRESS, updatedEpic.getStatus(), "Статус не совпадает с IN_PROGRESS");
    }

    //SubTask
    @Test
    @DisplayName("Создание SubTask")
    void shouldCreateSubTask() {
        assertEquals(3, subTask1.getId(), "Неверный id");
        assertEquals("Новая подзадача №1", subTask1.getName(), "Неверное описание");
        assertEquals("описание подзадачи", subTask1.getDescription(), "Неверное описание");
    }

    @Test
    @DisplayName("Удаление всех SubTasks")
    void shouldDeleteAllSubTasks() {
        taskManager.removeAllSubTasks();
        subTasks = taskManager.getAllSubTasks();
        assertEquals(0, subTasks.size(), "Некорректное удаление всех подзадач");
    }

    @Test
    @DisplayName("Возвращение SubTask по Id")
    void shouldGetSubTaskById() {
        SubTask getSubtask = taskManager.getSubtaskById(subTask1.getId());
        assertEquals(getSubtask, subTask1, "Некорректное получение подзадачи по Id");
    }

    @Test
    @DisplayName("Удаление SubTask по Id")
    void shouldBeDeleteSubTaskById() {
        taskManager.removeByIdSubTask(subTask1.getId());
        subTasks = taskManager.getAllSubTasks();
        assertEquals(1, subTasks.size(), "Некорректное удаление подзадачи по Id");
    }

    @Test
    @DisplayName("Проверка автоудаления подзадач после удаления эпика")
    void shouldRemoveEpicAndSubTasks() {
        taskManager.removeByIdEpic(epic.getId());
        assertNull(taskManager.getSubtaskById(subTask1.getId()), "Ошибка при автоудалении подзадач");
    }


}
