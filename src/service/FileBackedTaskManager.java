package service;

import converter.TaskConverter;
import exception.WriteFileException;
import model.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.APPEND;

public class FileBackedTaskManager extends InMemoryTaskManager {
    public static final String TASK_CSV = "task.csv";
    private final File file;
    int seq = 0;


    public FileBackedTaskManager(HistoryManager historyManager) {
        this(historyManager, new File(TASK_CSV));
    }

    public FileBackedTaskManager(File file) {
        this(Manager.getDefaultHistory(), file);
    }

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        manager.loadFromFile();
        return manager;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public Task createTask(Task task) {
        Task newTask = super.createTask(task);
        save();
        return newTask;
    }


    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        SubTask newSubTask = super.createSubTask(subTask);
        save();
        return newSubTask;
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }


    @Override
    public Epic createEpic(Epic epic) {
        Epic newEpic = super.createEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }


    // сохранение в файл
    private void save() {
        List<Task> tasks = new ArrayList<>();
        tasks.addAll(getAllTasks());
        tasks.addAll(getAllEpics());
        tasks.addAll(getAllSubTasks());

        try {
            Files.writeString(Path.of(TASK_CSV), "id,type,title,status,description,epic,duration,startTime\n");

            for (Task entry : tasks) {
                Files.writeString(Path.of(TASK_CSV), TaskConverter.toString(entry) + "\n", APPEND);
            }
        } catch (IOException exp) {
            throw new WriteFileException("Ошибка в файле: " + file.getAbsolutePath(), exp);
        }
    }

    private Task fromString(String value) {
        final String[] columns = value.split(",");

        int id = Integer.parseInt(columns[0]);
        TaskType type = TaskType.valueOf(columns[1]);
        String name = columns[2];
        Status status = Status.valueOf(columns[3]);
        String description = columns[4];
        Integer epicId = null;
        Task task = null;

        Duration duration = Duration.parse(columns[6]);

        LocalDateTime startTime = LocalDateTime.parse(columns[7]);

        switch (type) {
            case TASK:
                task = new Task(id, name, status, description, startTime, duration);
                createTask(task);
                break;
            case SUBTASK:
                epicId = Integer.parseInt(columns[5]);
                task = new SubTask(id, name, status, description, epicId, startTime, duration);
                createSubTask((SubTask) task);
                break;
            case EPIC:
                task = new Epic(id, name, status, description, startTime, duration);

                createEpic((Epic) task);

                break;
        }
        return task;
    }

    //Восстановление из файла
    public void loadFromFile() {

        int maxId = 0;
        try (final BufferedReader reader = new BufferedReader(new FileReader(file, UTF_8))) {
            reader.readLine(); // пропускаем заголовок
            while (reader.ready()) {
                String line = reader.readLine();
                final Task task = fromString(line);
                final int id = task.getId();
                if (task.getType() == TaskType.TASK) {
                    tasks.put(id, task);
                } else if (task.getType() == TaskType.SUBTASK) {
                    subTasks.put(id, (SubTask) task);
                } else if (task.getType() == TaskType.EPIC) {
                    epics.put(id, (Epic) task);
                }
                if (maxId < id) {
                    maxId = id;
                }
                if (line.isEmpty()) {
                    break;
                }
            }
            seq = maxId;
        } catch (IOException exp) {
            throw new RuntimeException(exp);
        }
    }


}
