package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
            assertEquals((expected.getId()), actual.getId(), message +", id");
            assertEquals((expected.getName()), actual.getName(), message +", name");
        }

}
