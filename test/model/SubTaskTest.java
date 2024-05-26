package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Подзадача")
public class SubTaskTest {

    @Test
    @DisplayName("Проверка подзадач по id")
    void shouldEqualsWithCopy(){
        Epic epic = new Epic("epic", "desc");
        SubTask subTask = new SubTask(Status.NEW, "subTask", "desc", epic);
        Task subTaskExpected = new SubTask(Status.NEW, "subTask", "desc", epic);

        assertEqualsTask(subTaskExpected, subTask, "Подзадачи должны совпадать");
    }

    private static void assertEqualsTask(Task expected, Task actual, String message) {
        assertEquals(expected.getId(), actual.getId(), message + ", id");
        assertEquals((expected.getName()), actual.getName(), message +", name");
    }

}
