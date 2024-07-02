package service;

import exception.NotFoundException;
import exception.ValidationException;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("InMemoryHistoryManagerTest")
class InMemoryHistoryManagerTest {

    TaskManager taskManager;
    private Task task;
    private Epic epic;
    private SubTask subTask;


    @BeforeEach
    void init() throws ValidationException {
        taskManager = Manager.getDefaults();

        task = taskManager.createTask(new Task(Status.NEW, "task1", "описание задачи"));
        epic = taskManager.createEpic(new Epic("epic1", "описание эпика"));
        subTask = taskManager.createSubTask(new SubTask(Status.NEW, "Новая задача", "описание подзадачи"
                , epic, LocalDateTime.parse("2025-12-21T21:21:21"), Duration.ofMinutes(30)));
    }

    @Test
    @DisplayName("Добавление задачи в список просмотренных")
    void shouldAddViewedCorrectly() throws NotFoundException {

        taskManager.getTaskById(task.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getSubtaskById(subTask.getId());

        assertEquals(3, taskManager.getHistory().size(),
                "Неккоректное добавление просмотренных  задач");

        taskManager.getTaskById(task.getId());
        assertEquals(3, taskManager.getHistory().size(),
                "Неккоректное добавление повторно просмотренных задач");
    }

    @Test
    @DisplayName("Вывод всех просмотренных задач  + удаление задач их списка просмотренных")
    void shouldReturnCorrectListOfViewedTasks() throws NotFoundException {
        taskManager.getTaskById(task.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getSubtaskById(subTask.getId());

        taskManager.getTaskById(task.getId());


        List<Task> listOfViewedTasks = new ArrayList<>();
        listOfViewedTasks.add(epic);
        listOfViewedTasks.add(subTask);
        listOfViewedTasks.add(task);

        assertEquals(listOfViewedTasks, taskManager.getHistory(),
                "Неккоректное добавление просмотренных задач в список");

        taskManager.removeByIdEpic(epic.getId());
        listOfViewedTasks.remove(epic);
        listOfViewedTasks.remove(subTask);
        assertEquals(listOfViewedTasks, taskManager.getHistory(),
                "Неккоректное удаление просмотренных задач из списка");
    }

}