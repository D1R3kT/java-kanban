package service;

import converter.TaskConverter;
import exception.ValidationException;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @Override
    public FileBackedTaskManager createManager() {
        return new FileBackedTaskManager(new InMemoryHistoryManager());
    }

    FileBackedTaskManager fileBackedTaskManager;
    Path file;
    TaskManager taskManagerLoader;


    @BeforeEach
    protected void init() throws ValidationException {
        super.init();

        fileBackedTaskManager = createManager();

        try {
            file = Files.createTempFile("task", ".csv");
        } catch (IOException exp) {
            throw new RuntimeException("Ошибка при создании файла", exp);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            writer.append("id,type,title,status,description,epic,duration,startTime");
            writer.newLine();

            writer.append(TaskConverter.toString(task));
            writer.newLine();
            writer.append(TaskConverter.toString(epic));
            writer.newLine();
            writer.append(TaskConverter.toString(subTask1));
            writer.newLine();
            writer.append(TaskConverter.toString(subTask2));
            writer.newLine();
        } catch (IOException exp) {
            throw new RuntimeException("Ошибка в файле: " + file.getFileName());
        }

        taskManagerLoader = FileBackedTaskManager.loadFromFile(file.toFile());
    }

    @Test
    @DisplayName("Проверка преобразования в строку")
    void shouldConvertTasksToString() {
        String convertTaskToLine = "1,TASK,Новая задача,NEW,описание,null," + task.getDuration()
                + "," + task.getStartTime();
        assertEquals(convertTaskToLine, TaskConverter.toString(task), "ошибка при преобразовании в строку");
    }

    @Test
    @DisplayName("Проверка загрузки данных из файла")
    void shouldLoadFromFile() {
        assertEqualsListOfTasks(taskManagerLoader.getAllTasks(), taskManager.getAllTasks());
        assertEqualsListOfTasks(taskManagerLoader.getAllEpics(), taskManager.getAllEpics());
        assertEqualsListOfTasks(taskManagerLoader.getAllSubTasks(), taskManager.getAllSubTasks());
    }


    private static <T extends Task> void assertEqualsListOfTasks(List<T> expected, List<T> actual) {

        assertEquals(expected.size(), actual.size(), "Размеры не совпадают");

        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getId(), actual.get(i).getId(), "id задач не совпадает");
        }
    }


}
