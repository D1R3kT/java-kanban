package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Задача")
public class TaskTest {

    @Test
    @DisplayName("Проверка задач по id")
    void shouldEqualsWithCopy(){
        Task task = new Task(Status.NEW, "task", "desc");
        Task taskExpected = new Task(Status.NEW, "task", "desc");

        assertEqualsTask(taskExpected, task, "Эпики должны совпадать");
    }

    private static void assertEqualsTask(Task expected, Task actual, String message) {
        assertEquals(expected.getId(), actual.getId(), message + ", id");
        assertEquals((expected.getName()), actual.getName(), message +", name");
    }
}
