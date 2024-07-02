package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Эпик")
class EpicTest {

    @Test
    @DisplayName("Должен совпадать со своей копией")
    void shouldEqualsWithCopy() {
        Epic epic = new Epic("name", "desc");
        Epic epicExpected = new Epic("name", "disc");
        assertEqualsTask(epicExpected, epic, "Эпики должны совпадать");
    }

    private static void assertEqualsTask(Task expected, Task actual, String message) {
        assertEquals((expected.getId()), actual.getId(), message + ", id");
        assertEquals((expected.getName()), actual.getName(), message + ", name");
    }

}
