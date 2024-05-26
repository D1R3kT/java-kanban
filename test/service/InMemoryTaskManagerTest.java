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

    @Test
    @DisplayName("Удаление из начала списка")
    void testRemoveFirst() {
        InMemoryHistoryManager manager = new InMemoryHistoryManager();

        Task task1 = new Task(1,"task1");
        manager.add(task1);
        Task task2 = new Task(2,"task2");
        manager.add(task2);
        Task task3 = new Task(3, "task3");
        manager.add(task3);

        manager.remove(task1.getId());

        assertEquals(manager.getHistory(), List.of(task2, task3), "");
    }

    @Test
    @DisplayName("Удаление из конца списка")
    void testRemoveLast() {
        InMemoryHistoryManager manager = new InMemoryHistoryManager();

        Task task1 = new Task(1,"task1");
        manager.add(task1);
        Task task2 = new Task(2,"task2");
        manager.add(task2);
        Task task3 = new Task(3, "task3");
        manager.add(task3);

        manager.remove(task3.getId());

        assertEquals(manager.getHistory(), List.of(task1, task2), "");
    }


    private static class EmptyHistoryManager implements HistoryManager {

        @Override
        public void add(Task task) {
        }

        @Override
        public void remove(int id) {

        }

        @Override
        public List<Task> getHistory() {
           return Collections.emptyList();
       }
    }


}
