package service;

import model.Status;
import model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {
    EmptyHistoryManager historyManager = new EmptyHistoryManager();
    InMemoryTaskManager memoryTaskManager = new InMemoryTaskManager(historyManager);

    @Test
    @DisplayName("TaskManager")
    void TaskManagers() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager(historyManager);

        assertEqualsTaskManager(inMemoryTaskManager, memoryTaskManager, "Менеджеры должны совпадать");
    }

    private static void assertEqualsTaskManager(TaskManager expected, TaskManager actual, String message) {
        assertEquals(expected.getAllTasks(), actual.getAllTasks(), message + ", tasks");
    }

    @Test
    @DisplayName("Проверка удаления задач")
    void TaskShouldEqualsWithNull(){
        Task task1 =  memoryTaskManager.createTask(new Task(Status.NEW,"учеба", "12341"));
        Task task2 = memoryTaskManager.createTask(new Task(Status.NEW,"работа", "1234"));
        assertNotNull(memoryTaskManager.tasks);

        memoryTaskManager.removeAllTasks();
        int lenght = memoryTaskManager.tasks.size();
        assertEquals(lenght, 0);

    }

    private static class EmptyHistoryManager implements HistoryManager {

        @Override
       public void add(Task task) {
        }

        @Override
        public List<Task> getHistory() {
           return Collections.emptyList();
       }
    }


}
